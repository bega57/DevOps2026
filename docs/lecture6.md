# Lecture 6 – Doku mit MkDocs

**Ziel:** Diese Doku als HTML bauen und über nginx ausliefern.

- `mkdocs.yml` + `docs/`-Ordner im Repo, Notizen als Markdown
- HTML wird in der Pipeline mit dem `squidfunk/mkdocs-material`-Image gebaut → Ordner `site/`
- Eigenes Image auf Basis von `nginx`, das den `site/`-Ordner ausliefert
- Läuft auf einem anderen Port als die App
- Image wird am Ende in die Registry gepusht

Gleiches Prinzip wie bei der App: Gradle-Container baut das JAR → JRE-Image. Hier baut der MkDocs-Container das HTML → nginx-Image.
