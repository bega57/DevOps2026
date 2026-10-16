# Lecture 2 – Runner & first pipeline

**Goal:** Connect a self-hosted runner to GitHub and run a first pipeline.

- Set up the runner on the build agent and connected it to the repo
- Lecturer installed the runner as a service
- First pipeline via *Actions → New workflow → Simple workflow*

Test step:

```yaml
- run: docker run --rm hello-world
```

We later replaced the hello-world step with the real build workflow.

To check that the runner works, look in `~/actions-runner/_work` – that's where the checked-out repos end up.
