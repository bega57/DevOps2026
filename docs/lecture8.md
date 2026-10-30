# Lecture 8 – Deployment to production

**Goal:** After a successful build, deploy the app and the docs automatically to the production server `10.0.40.173` (Option 1: deploy via SSH).

## Separate deploy job

The workflow now has two jobs. `deploy` only starts when `build` succeeded:

```yaml
deploy:
  runs-on: self-hosted
  needs: build
```

- No checkout needed – the job only works with the images that `build` created.
- Runs on the same self-hosted runner as `build`, so the images are already there locally.
- `IMAGE_TAG` is set again, because env variables are not passed between jobs.

## SSH key

A separate key pair just for deployment, created on the build agent:

```bash
ssh-keygen -t ed25519 -f ~/team6_deploy -N "" -C "team6-deploy"
```

- `-N ""` = no passphrase, otherwise the pipeline can't use it
- **Public key** → production server, `~team6/.ssh/authorized_keys`
- **Private key** → GitHub secret `DEPLOY_SSH_KEY` (the whole file, including the `BEGIN`/`END` lines)

`ssh-copy-id` didn't work, because `authorized_keys` belongs to `root`. The `.ssh` folder belongs to `team6`, so we can replace the file with a copy:

```bash
cat ~/team6_deploy.pub | ssh team6@10.0.40.173 \
  'cd ~/.ssh && cp authorized_keys authorized_keys.bak && cp authorized_keys ak.new \
   && cat >> ak.new && chmod 600 ak.new && mv ak.new authorized_keys'
```

Test: `ssh -i ~/team6_deploy team6@10.0.40.173 'docker ps'` – works without password.

## Using the key in the pipeline

```bash
mkdir -p "$RUNNER_TEMP/ssh"
echo "$DEPLOY_SSH_KEY" | tr -d '\r' > "$RUNNER_TEMP/ssh/id_deploy"
chmod 600 "$RUNNER_TEMP/ssh/id_deploy"
ssh-keygen -y -f "$RUNNER_TEMP/ssh/id_deploy" > /dev/null
ssh-keyscan -H 10.0.40.173 > "$RUNNER_TEMP/ssh/known_hosts"
```

- Key is written to a temporary file, `chmod 600` because ssh refuses keys that others can read
- `tr -d '\r'` removes Windows line endings (copy & paste from Windows)
- `ssh-keygen -y` checks the key early, so a broken secret fails with a clear error
- **Host key:** in the pipeline nobody can type `yes`. `ssh-keyscan` gets the host key, ssh uses it via `-o UserKnownHostsFile=...`
- `-o BatchMode=yes`: never ask for a password, fail immediately instead
- Last step `Remove SSH key` (`if: always()`) deletes the key again

## Getting the images to production

Our registry `10.0.40.171:5000` only speaks HTTP. The production server's Docker is not configured for it (`server gave HTTP response to HTTPS client`) and we have no root rights to change that.

Solution: send the images directly through SSH:

```bash
docker save team6-app:$IMAGE_TAG | $SSH 'docker load'
docker save team6-docs:0.0.1 | $SSH 'docker load'
```

`docker save` writes the image as a tar stream, the pipe sends it through the SSH connection, `docker load` imports it on production.

The app image is retagged from `devops-app` to `team6-app`, because all teams share the same Docker on the production server.

## Lifecycle on production

```bash
docker stop team6-app team6-docs || true
docker rm team6-app team6-docs || true
docker images -q team6-app | xargs -r docker rmi -f
docker rmi -f team6-docs:0.0.1 || true
# docker load ... (see above)
docker run -d --name team6-app  --restart always -p 1861:8080 team6-app:$IMAGE_TAG
docker run -d --name team6-docs --restart always -p 1862:80   team6-docs:0.0.1
```

- `|| true` so the first deployment doesn't fail when nothing exists yet
- `--restart always` so the containers come back after a server reboot

## Check

- App: `http://10.0.40.173:1861/actuator/health`
- Docs: `http://10.0.40.173:1862`
- Pipeline step "Check production" waits for the health endpoint and checks the docs with `curl`

## Problems we had

| Error | Cause | Fix |
|---|---|---|
| `cannot create .ssh/authorized_keys: Permission denied` | file belongs to `root` | replace it with a copy (see above) |
| `server gave HTTP response to HTTPS client` | production Docker doesn't trust our HTTP registry | `docker save` / `docker load` via SSH |
| `Load key ...: error in libcrypto` | secret without the `BEGIN`/`END` lines | paste the complete private key into the secret |
