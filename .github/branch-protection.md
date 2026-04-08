# Branch Protection Policy – main

## How to apply (one-time setup in GitHub)

Go to: **GitHub repo → Settings → Branches → Add branch ruleset**

### Rules to enable for `main`:

| Rule | Setting |
|------|---------|
| Restrict pushes that create the branch | ✅ Enabled |
| Require a pull request before merging | ✅ Enabled |
| Required approvals | **1** (minimum) |
| Dismiss stale reviews on new commits | ✅ Enabled |
| Require status checks to pass before merging | ✅ Enabled |
| Status checks required | `SonarCloud Scan`, `Build Package` |
| Block direct pushes to main | ✅ Enabled |
| Include administrators | ✅ Enabled (admins also must use PRs) |

## Secrets required in repo

Go to: **GitHub repo → Settings → Secrets and variables → Actions**

| Secret name | Value |
|-------------|-------|
| `SONAR_TOKEN` | SonarCloud token from sonarcloud.io |
| `DOCKERHUB_USERNAME` | `bhavyaprabhaditya` |
| `DOCKERHUB_TOKEN` | Docker Hub access token (not password) |

> Generate a Docker Hub token at: https://hub.docker.com → Account Settings → Security → New Access Token
