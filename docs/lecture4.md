# Lecture 4 – Deployment & Integrationstest

**Ziel:** App auf dem Agent starten, testen, Image in die Registry pushen.

## Deploy

Alter Container wird gestoppt und gelöscht, dann neu gestartet. `|| true`, damit der Step nicht failt, wenn noch keiner läuft.

```bash
docker stop devops-app || true
docker rm devops-app || true
docker run -d --name devops-app --restart unless-stopped -p 8080:8080 devops-app:$IMAGE_TAG
```

## Integrationstest

Eigener Step nach dem Deploy. Erwartete Antwort steht in `integration-test/health-expected.json` und wird mit `curl` gegen `/actuator/health` verglichen. Bei Unterschied `exit 1` → Pipeline failt, kein Push.

## Registry

Registry läuft auf `10.0.40.171:5000`.

```bash
docker tag devops-app:$IMAGE_TAG 10.0.40.171:5000/team6/devops-app:$IMAGE_TAG
docker push 10.0.40.171:5000/team6/devops-app:$IMAGE_TAG
```
