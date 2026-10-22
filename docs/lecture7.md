# Lecture 7 – PDF docs with Pandoc

**Goal:** Generate a PDF from the existing Markdown notes and serve it with the docs (nginx) and the app.

## Pandoc

Pandoc converts Markdown → LaTeX → PDF. In the pipeline we use the `pandoc/latex` image (pandoc + TeX), same idea as Gradle/MkDocs: container in, file out.

Settings are in `docs-pdf.yaml` (title, table of contents, margins):

```yaml
title: "Team 6 – DevOps Docs"
toc: true
geometry: margin=2cm
colorlinks: true
header-includes: |
  ```{=latex}
  \DeclareUnicodeCharacter{2192}{\ensuremath{\rightarrow}}
  ```
```

The `DeclareUnicodeCharacter` line is needed because pdflatex can't print the arrow character otherwise and the build fails.

## Pipeline

Order matters: the PDF has to exist **before** the Gradle build (so it ends up in the JAR) and before the docs image.

1. Build docs (HTML) → `site/` (moved to the start, because `mkdocs build` wipes `site/`)
2. Build docs (PDF) → `site/documentation.pdf`
3. Copy PDF to `src/main/resources/static/`
4. Gradle build, app image, … (as before)
5. Docs image – `Dockerfile.docs` copies all of `site/`, so the PDF is included automatically

```bash
docker run --rm -u $(id -u):$(id -g) -v "$PWD":/data pandoc/latex:3.6 \
  --metadata-file=docs-pdf.yaml \
  docs/index.md $(ls docs/lecture*.md | sort -V) \
  -o site/documentation.pdf

mkdir -p src/main/resources/static
cp site/documentation.pdf src/main/resources/static/
```

- `sort -V` so lecture10 comes after lecture9, not after lecture1
- The copied PDF in `static/` is build output → in `.gitignore`
- Spring Boot serves everything in `static/` automatically, no code needed

## Check

- Docs: `http://10.0.40.168:8081/documentation.pdf`
- App: `http://10.0.40.168:8080/documentation.pdf`
- Pipeline step "Check PDF in app" does a `curl` on the app URL
