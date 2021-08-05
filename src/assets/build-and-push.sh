#!/usr/bin/env bash

NAMESPACE="iad.ocir.io/cloudnative-devrel/micronaut-showcase/mushop/"
TAG=$(<VERSION)

IMAGE=${NAMESPACE}$(basename $(pwd)):"${TAG}"

docker build -t "$IMAGE" .
docker push "$IMAGE"

