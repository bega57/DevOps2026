# Lecture 5 – Image tag, SonarQube, JaCoCo

**Goal:** Unique image tags, code analysis with SonarQube.

## Image tag

Tag = first 7 characters of the commit hash, so every image in the registry is unique.

```bash
echo "IMAGE_TAG=${GITHUB_SHA::7}" >> "$GITHUB_ENV"
```

## SonarQube

- Runs as a container on our agent: `http://10.0.40.168:9000`
- Changed the admin password, created a local project `devops-app-team6`, stored the token as GitHub secret `SONAR_TOKEN`

## JaCoCo

Added the plugin to `build.gradle`, report runs after the tests. The XML report has to be enabled, otherwise Sonar can't read the coverage (`build/reports/jacoco/test/jacocoTestReport.xml`).

## First scan (manual)

```bash
sonar-scanner \
  -Dsonar.projectKey=devops-app-team6 \
  -Dsonar.sources=. \
  -Dsonar.host.url=http://10.0.40.168:9000 \
  -Dsonar.token=<SONAR_TOKEN>
```

## Scan in the pipeline

Runs with the `sonarsource/sonar-scanner-cli` image, `SONAR_HOST_URL` and `SONAR_TOKEN` passed as env variables. Results show up in the Sonar dashboard.
