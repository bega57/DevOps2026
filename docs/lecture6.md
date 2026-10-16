# Lecture 6 – Docs with MkDocs

**Goal:** Build these docs as HTML and serve them with nginx.

Same idea as the app: Gradle container builds the JAR → JRE image. Here the MkDocs container builds the HTML → nginx image.

## Setup

```powershell
docker run --rm -v "${PWD}:/docs" squidfunk/mkdocs-material new .
```

Creates `mkdocs.yml` and `docs/index.md`. Notes go into `docs/`, menu order is set with `nav` in `mkdocs.yml`. New pages only show up in the menu if they're listed in `nav`.

Extra settings in `mkdocs.yml`:

- `use_directory_urls: false` + `offline` plugin → `site/index.html` also works without a server (e.g. to send the folder to a customer)
- `palette` with `prefers-color-scheme` → light/dark follows the system setting

## Local testing

```powershell
docker run --rm -p 8000:8000 -v "${PWD}:/docs" squidfunk/mkdocs-material        # live preview on localhost:8000
docker run --rm -v "${PWD}:/docs" squidfunk/mkdocs-material build                # creates site/
```

`site/` is build output → in `.gitignore`.

## Dockerfile.docs

```dockerfile
FROM nginx:alpine
COPY site/ /usr/share/nginx/html/
EXPOSE 80
```

## Pipeline

Added at the end of the workflow:

```bash
# build HTML
docker run --rm -u $(id -u):$(id -g) -v "$PWD":/docs squidfunk/mkdocs-material:9 build

# build image
docker build -f Dockerfile.docs -t team6-docs:0.0.1 .

# deploy
docker stop team6-docs || true
docker rm team6-docs || true
docker run -d --name team6-docs --restart unless-stopped -p 8081:80 team6-docs:0.0.1

# push
docker tag team6-docs:0.0.1 10.0.40.171:5000/team6-docs:0.0.1
docker push 10.0.40.171:5000/team6-docs:0.0.1
```

- `-u` again so `site/` isn't owned by root
- `:9` instead of `latest`, because MkDocs 2.0 will break plugins/themes
- Port 8081, since 8080 (app) and 9000 (Sonar) are taken

## Check

- Docs: `http://10.0.40.168:8081`
- Registry: `curl http://10.0.40.171:5000/v2/team6-docs/tags/list` → `{"name":"team6-docs","tags":["0.0.1"]}`
