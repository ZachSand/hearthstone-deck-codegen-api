# hearthstone-deck-generator

API for generating hearthstone deck codes that can be imported into the Blizzard Hearthstone application.

[![CircleCI](https://circleci.com/gh/ZachSand/hearthstone-deck-codegen-api.svg?style=svg)](https://circleci.com/gh/ZachSand/hearthstone-deck-codegen-api)

**This project is currently just a personal sandbox for testing and using the Hearthtstone Battlenet API, however it is
usable**

## Prerequisites

- Obtain Battlenet client ID and secret from [Battlenet API](https://develop.battle.net/)
- Set system environment variables:
- BATTLENET_CLIENT_ID=ac8a2f7b17a44b26beaca3cbb928be97
- BATTLENET_CLIENT_SECRET=DDuLK4n2OyMpbRgjUqLUPy4YFC3euBdt

## Quick Start

If you have docker installed on your system, run:

```
./startup.sh
```

This will build a docker image from the Spring Boot application and the Postgres database. They will be wired up
together using the docker compose file. The application will be available on localhost:8080

Otherwise, a Postgres database will need to be configured with the details from
the [application.yml](https://github.com/ZachSand/hearthstone-deck-codegen-api/blob/master/src/main/resources/local/application.yml)
. Then create the role and the database. Run the following commands to set up the default user to use for connecting to
the database.

```
psql postgres
create role root with superuser;
alter role root with password 'deckgen';
alter role root with login;
\q
```

Create the DB

```
createdb hearthstone_db
```

Once the database is set up, build the project with Maven and run:

```
mvn spring-boot:run
```

## API Example

Simple example JSON request to the application (POST to localhost:8080/deckgenerator/api/deck)

```
{
  "className": "warrior",
  "gameFormat": "standard",
  "deckSets": [
    {
      "setName": "all",
      "classSetCount": 10,
      "neutralSetCount": 20
    }
  ]
}
```

More detailed example JSON request (POST to localhost:8080/deckgenerator/api/deck)

```
{
  "className": "warrior",
  "gameFormat": "wild",
  "deckSets": [
    {
      "setName": "naxxramas",
      "classSetCount": 1,
      "neutralSetCount": 9
    },
    {
      "setName": "saviors-of-uldum",
      "classSetCount": 2,
      "neutralSetCount": 8
    },
    {
      "setName": "madness-at-the-darkmoon-faire",
      "classSetCount": 4,
      "neutralSetCount": 4
    },
    {
      "setName": "galakronds-awakening",
      "classSetCount": 1,
      "neutralSetCount": 1
    }
  ]
}
```

### Application Info

- Java 11
- Spring Boot, JPA
- Postgres database
- Battlenet OAuth
- Blizzard API for Hearthstone

## Disclaimer

This application most likely has many bugs as it doesn't have many tests yet due to just throwing things together to
test the Hearthstone API.

### To Do

Among other things...

- Create Swagger for API
- Write more tests
- Add logging
- Handle exceptions better than naive approach



