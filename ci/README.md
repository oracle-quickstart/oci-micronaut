# MuShop CI

Project uses GitHub Actions for CI/CD.

## Java CI Workflow (`.github/workflows/java-ci.yml`)

This workflow contains step that pushes the build Docker images to the registry. The following environment variables need to be set on the pipeline.

| Environment variable name | Description | Example |
| --- | --- | --- |
| OCI_USERNAME | Docker registry username | `{tenancyName}/{myUserName}` |
| OCI_TOKEN | Docker registry password | `{myUserAuthToken}` |

### Build Job (`build`)

The build job runs test for all Micronaut services. The `docker` needs to be installed to successfully pass the tests.

### Build Docker Images For Docker Compose Test (`build-docker-compose-test-images`)

This job pushes the build Docker images tagged by `$GITHUB_SHA` to the registry for further docker compose test.

### Docker Compose Test (`docker-compose-test`)

This job starts the MuShop stack using the Docker compose with docker images from previous job.

### Push Docker Images (`push-docker-images`)

_Note: runs only for push events._

This job pushes the docker images to the registry.

### Deployment
There are no test deployment configured yet.
