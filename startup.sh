#!/bin/bash

mvn clean install -P docker
mvn compile jib:dockerBuild -P docker
docker-compose -f ./src/main/resources/docker/docker-compose.yml up -d
