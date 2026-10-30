# Lecture 8 – Deployment to production

**Goal:** After a successful build, deploy app and docs automatically to the production server `10.0.40.173` via SSH (Option 1).

## Deploy job

New job `deploy` with `needs: build` – only runs if the build succeeded. No checkout needed, it runs on the same runner and uses the images `build` just created.

## SSH key

```bash
ssh-keygen -t ed25519 -f ~/team6_deploy -N "" -C "team6-deploy"
```

- **Public key** → `~/.ssh/authorized_keys` of `team6@10.0.40.173`
- **Private key** → GitHub secret `DEPLOY_SSH_KEY` (complete, incl. `BEGIN`/`END` lines)
- `authorized_keys` belongs to `root`, so `ssh-copy-id` failed. Since `.ssh` belongs to `team6`, we replaced the file with a copy that includes our key.

In the pipeline the key is written to `$RUNNER_TEMP` (`chmod 600`), the host key is fetched with `ssh-keyscan` (nobody can type `yes`), and the key is deleted at the end.

## Deployment

Our registry only speaks HTTP and production Docker doesn't accept it, so the images are sent directly through SSH:

```bash
docker save team6-app:$IMAGE_TAG | $SSH 'docker load'
```

On production (lifecycle):

```bash
docker stop team6-app team6-docs || true
docker rm team6-app team6-docs || true
# remove old images, docker load ...
docker run -d --name team6-app  --restart always -p 1861:8080 team6-app:$IMAGE_TAG
docker run -d --name team6-docs --restart always -p 1862:80   team6-docs:0.0.1
```

Images are named `team6-...` because all teams share the same Docker on production.

## Check

- App: `http://10.0.40.173:1861/actuator/health`
- Docs: `http://10.0.40.173:1862`
- Pipeline step "Check production" tests both with `curl`
