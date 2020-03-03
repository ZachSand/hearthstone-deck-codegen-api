#!/bin/bash

mvn clean install -P docker
mvn compile jib:dockerBuild -P docker
docker-compose up -d