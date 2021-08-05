#!/usr/bin/env bash

REGISTRY=iad.ocir.io
REPOSITORY=cloudnative-devrel/micronaut-showcase/mushop
TAG=latest

docker build -t ${REGISTRY}/${REPOSITORY}/kafka:${TAG} -f Dockerfile.kafkaclient .
docker push ${REGISTRY}/${REPOSITORY}/kafka:${TAG}