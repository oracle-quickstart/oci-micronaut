#!/usr/bin/env bash

REGISTRY=phx.ocir.io
REPOSITORY=oraclelabs/micronaut-showcase/mushop
TAG=latest

docker build -t ${REGISTRY}/${REPOSITORY}/kafka:${TAG} -f Dockerfile.kafkaclient .
docker push ${REGISTRY}/${REPOSITORY}/kafka:${TAG}