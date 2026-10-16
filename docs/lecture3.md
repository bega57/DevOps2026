# Lecture 3 – Building the app in the pipeline

**Goal:** Build the app with a Gradle container, run tests, write a Dockerfile.

- No SDK installed on the agent, everything runs through Docker
- `gradle clean build` creates the fat JAR in `build/libs/` and runs the unit tests

```bash
docker run --rm \
  -u $(id -u):$(id -g) \
  -v "$PWD":/project -w /project \
  gradle:jdk17 gradle clean build --no-daemon
```

`-u` matters: Docker runs as root, so without it the build files belong to root and the next checkout fails.

## Dockerfile

```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY build/libs/devops-app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```
