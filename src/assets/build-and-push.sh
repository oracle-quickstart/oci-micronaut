#!/usr/bin/env bash

NAMESPACE="phx.ocir.io/oraclelabs/micronaut-showcase/mushop/"
TAG=$(<VERSION)

IMAGE=${NAMESPACE}$(basename $(pwd)):"${TAG}"

docker build -t "$IMAGE" .
docker push "$IMAGE"

