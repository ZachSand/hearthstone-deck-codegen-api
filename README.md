# hearthstone-deck-generator
API for generating hearthstone deck codes that can be imported into the Blizzard Hearthstone
application.

**This project is currently just a personal sandbox for testing and using the Hearthtstone
Blizzard API, however it is somewhat usable**

## Prerequisites
- Obtain Blizzard client ID and secret from [Blizzard API](https://develop.battle.net/)
- Set system environment variables:
- BLIZZARD_CLIENT_ID
- BLIZZARD_CLIENT_SECRET

## Quick Start
If you have docker installed on your system, run:
```
./startup.sh
```
This will build a docker image from the Spring Boot application and the MySQL database. 
They will be wired up together using the docker compose file. The application will be 
available on localhost:8080

Otherwise, a MySQL database will need to be configured with the details from the application.yml
in the src/main/resources/local folder. Once the database is setup, build the projec with Maven
and run:
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
```$xslt
{
  "className": "warrior",
  "gameFormat": "wild",
  "deckSets": [
    {
      "setName": "naxxramas",
      "classSetCount": 2,
      "neutralSetCount": 8
    },
    {
      "setName": "saviors-of-uldum",
      "classSetCount": 2,
      "neutralSetCount": 8
    },
    {
      "setName": "basic",
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
  - MySQL database
  - Blizzard API for Hearthstone

## Disclaimer
This application most likely has many bugs as it doesn't have any test because I was just
testing the Blizzard API. 

### To Do
Among other things...
- Create Swagger for API
- Write tests, add CI for tests
- Add logging
- Add more verification of request data
- Handle exceptions better than naive approach
- Optimize; current response time is rather slow due to the brute force nature of everything



