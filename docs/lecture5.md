# Lecture 5 – Image-Tag, SonarQube, JaCoCo

**Ziel:** Eindeutige Image-Tags, Code-Analyse mit SonarQube.

## Image-Tag

Tag = die ersten 7 Zeichen vom Commit-Hash, damit jedes Image in der Registry eindeutig ist.

```bash
echo "IMAGE_TAG=${GITHUB_SHA::7}" >> "$GITHUB_ENV"
```

## SonarQube

- Läuft als Container auf unserem Agent: `http://10.0.40.168:9000`
- Admin-Passwort geändert, lokales Projekt `devops-app-team6` angelegt, Token als GitHub-Secret `SONAR_TOKEN` gespeichert

## JaCoCo

Plugin in `build.gradle` hinzugefügt, Report läuft nach den Tests. XML-Report muss aktiv sein, sonst kann Sonar die Coverage nicht lesen (`build/reports/jacoco/test/jacocoTestReport.xml`).

## Erster Scan (manuell)

```bash
sonar-scanner \
  -Dsonar.projectKey=devops-app-team6 \
  -Dsonar.sources=. \
  -Dsonar.host.url=http://10.0.40.168:9000 \
  -Dsonar.token=<SONAR_TOKEN>
```

## Scan in der Pipeline

Läuft über das `sonarsource/sonar-scanner-cli`-Image mit `SONAR_HOST_URL` und `SONAR_TOKEN` als Env-Variablen. Ergebnisse dann im Sonar-Dashboard.
