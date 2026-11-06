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

Production pulls the images from our registry (`10.0.40.171:5000`), the `build` job already pushed them there.

At first production Docker didn't accept our HTTP registry, so we sent the images with `docker save | ssh docker load`. After the registry was allowed on production we switched to `docker pull` (the old lines are still commented out in the workflow).

On production (lifecycle):

```bash
docker stop team6-app team6-docs || true
docker rm team6-app team6-docs || true
# remove old images
docker pull $REGISTRY_IMAGE:$IMAGE_TAG
docker pull 10.0.40.171:5000/team6-docs:0.0.1
docker run -d --name team6-app  --restart always -p 1861:8080 $REGISTRY_IMAGE:$IMAGE_TAG
docker run -d --name team6-docs --restart always -p 1862:80   10.0.40.171:5000/team6-docs:0.0.1
```

Containers are named `team6-...` because all teams share the same Docker on production.

## Check

- App: `http://10.0.40.173:1861/actuator/health`
- Docs: `http://10.0.40.173:1862`
- Pipeline step "Check production" tests both with `curl`
