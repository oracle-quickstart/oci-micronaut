#!/usr/bin/env bash

REGISTRY=phx.ocir.io
REPOSITORY=oraclelabs/micronaut-showcase/mushop

OPENJDK_TAG=16-alpine
GRAALVM_TAG=java11-21.1.0
NATIVE_TAG=ol7-java11-22.2.0


docker build -t ${REGISTRY}/${REPOSITORY}/base/openjdk:${OPENJDK_TAG} -f Dockerfile.base-openjdk .
docker push ${REGISTRY}/${REPOSITORY}/base/openjdk:${OPENJDK_TAG}

docker build -t ${REGISTRY}/${REPOSITORY}/base/graalvm-ce:${GRAALVM_TAG} -f Dockerfile.base-graalvm .
docker push ${REGISTRY}/${REPOSITORY}/base/graalvm-ce:${GRAALVM_TAG}

docker build -t ${REGISTRY}/${REPOSITORY}/base/native:${NATIVE_TAG} -f Dockerfile.base-native .
docker push ${REGISTRY}/${REPOSITORY}/base/native:${NATIVE_TAG}
