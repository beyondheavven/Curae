# Contributing

## Branching

`main` is the deployable branch. Never push to it directly.

1. Start from an up-to-date `main`:

   ```bash
   git checkout main
   git pull --ff-only origin main
   ```

2. Create a branch per feature/fix:

   ```bash
   git checkout -b feature/<short-name>
   git checkout -b fix/<short-name>
   git checkout -b chore/<short-name>
   ```

3. Commit, push the branch and open a pull request to `main`:

   ```bash
   git push -u origin feature/<short-name>
   ```

4. CI (`ci.yml`) must pass. After review and merge, `cd.yml` builds images and deploys to Kubernetes.

## Branch protection (GitHub settings)

Enable on `main` (Settings → Branches → Branch rulesets):

- Require a pull request before merging — at least 1 approval
- Require status checks to pass — `CI / build`
- Block force pushes and branch deletion
- Require branches to be up to date before merging

These settings are server-side and cannot be stored in the repository. Configure them once for the repo.

## Secrets

Never commit `.env`, kubeconfigs, tokens or passwords. Use GitHub repository secrets:

- `KUBE_CONFIG` — base64-encoded kubeconfig used by `cd.yml`
- `GITHUB_TOKEN` — provided automatically for GHCR pushes

In Kubernetes, secrets come from `curae-secrets` (see `README.md`).
