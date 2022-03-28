#!/usr/bin/env bash

for DIR in $(find ./src -mindepth 1 -maxdepth 1 -type d ); do
  pushd $DIR || exit
  if [ -f "mvnw" ]; then
    ./mvnw clean package -Dpackaging=docker
  elif [ -f "gradlew" ]; then
    ./gradlew clean dockerBuild
  elif [ -f "build-and-push.sh" ]; then
    echo "Skipping $(pwd)"
  fi
  popd
done
