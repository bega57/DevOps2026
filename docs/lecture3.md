# Lecture 3 – App in der Pipeline bauen

**Ziel:** App per Gradle-Container bauen, Tests laufen lassen, Dockerfile schreiben.

- Kein SDK auf dem Agent installieren, alles läuft über Docker
- `gradle clean build` erzeugt das Fat-JAR in `build/libs/` und führt die Unit-Tests aus

```bash
docker run --rm \
  -u $(id -u):$(id -g) \
  -v "$PWD":/project -w /project \
  gradle:jdk17 gradle clean build --no-daemon
```

`-u` ist wichtig: Docker läuft als root, ohne das gehören die Build-Files root und der nächste Checkout schlägt fehl.

## Dockerfile

```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY build/libs/devops-app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```
