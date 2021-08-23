#!/usr/bin/env bash

REGISTRY=iad.ocir.io
REPOSITORY=cloudnative-devrel/micronaut-showcase/mushop

OPENJDK_TAG=16-alpine
GRAALVM_TAG=java11-21.1.0


docker build -t ${REGISTRY}/${REPOSITORY}/base/openjdk:${OPENJDK_TAG} -f Dockerfile.base-openjdk .
docker push ${REGISTRY}/${REPOSITORY}/base/openjdk:${OPENJDK_TAG}

docker build -t ${REGISTRY}/${REPOSITORY}/base/graalvm-ce:${GRAALVM_TAG} -f Dockerfile.base-openjdk .
docker push ${REGISTRY}/${REPOSITORY}/base/graalvm-ce:${GRAALVM_TAG}