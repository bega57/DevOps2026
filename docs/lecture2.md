# Lecture 2 – Runner & erste Pipeline

**Ziel:** Self-hosted Runner mit GitHub verbinden, erste Pipeline laufen lassen.

- Runner auf dem Build-Agent eingerichtet und mit dem Repo verbunden
- Dozent hat den Runner als Service installiert
- Erste Pipeline über *Actions → New workflow → Simple workflow*

Test-Step:

```yaml
- run: docker run --rm hello-world
```

Den Hello-World-Step haben wir später durch den richtigen Build-Workflow ersetzt.

Ob der Runner funktioniert, sieht man in `~/actions-runner/_work` – dort landen die ausgecheckten Repos.
