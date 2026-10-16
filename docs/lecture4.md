# Lecture 4 – Deployment & integration test

**Goal:** Run the app on the agent, test it, push the image to the registry.

## Deploy

The old container gets stopped and removed, then a new one is started. `|| true` so the step doesn't fail if nothing is running yet.

```bash
docker stop devops-app || true
docker rm devops-app || true
docker run -d --name devops-app --restart unless-stopped -p 8080:8080 devops-app:$IMAGE_TAG
```

## Integration test

Separate step after the deploy. The expected response is in `integration-test/health-expected.json` and gets compared with a `curl` to `/actuator/health`. If they differ → `exit 1` → pipeline fails, no push.

## Registry

Registry runs on `10.0.40.171:5000`.

```bash
docker tag devops-app:$IMAGE_TAG 10.0.40.171:5000/team6/devops-app:$IMAGE_TAG
docker push 10.0.40.171:5000/team6/devops-app:$IMAGE_TAG
```
