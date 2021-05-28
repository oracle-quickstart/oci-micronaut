# Micronaut MuShop Docker

The complete **Micronaut MuShop** application can be run using `docker-compose` locally.

## Default Configuration

The default configuration that the `docker-compose` file uses is to be offline.

When using `docker-compose` some services are not available, such as publishing the newsletter which uses an Oracle Cloud Function, however the remaining services are functional.

## Connecting to Oracle Cloud Infrastructure Services when running locally

Where relevant, you can configure the container images to leverage cloud services as well. 
For instance, you can configure the locally running application to read and write data on 
to an Oracle ATP database.

### Prerequisites

You need Docker installed locally and at least 8GB of resources assigned to Docker in order to run instances of Oracle database within containers.

## Quick Start

```shell
# From this directory
docker-compose up -d

# From the mushop root
docker-compose -f deploy/docker-compose/docker-compose.yml up -d
```

Note that it may take a little while to download all the images and spin up the containers (in particular the Oracle Database image is 2GB to download).

You can track the progress of services starting up with `docker-compose ps` and once all services have a status of `Up` then open [http://localhost:81](http://localhost:81) in your browser to access MuShop.

## Shutdown

```shell
# From this directory
docker-compose down
# From the mushop root
docker-compose -f deploy/docker-compose/docker-compose.yml down
```
