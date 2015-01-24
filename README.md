aic-bigdata
===========

AIC Group Project Big Data

Architecture:
https://www.dropbox.com/s/lqbvh8k3ydv8mqv/AIC_Architektur_Vorschlag.pdf?dl=0


# General Stuff
### Software
- Gradle 2+ (http://www.gradle.org)
- Java 7+
- MongoDB (http://www.mongodb.org/downloads & http://docs.mongodb.org/manual/)


# Instructions
### General Gradle Tasks Descriptions
    gradle build        // default build task
    gradle startStream  // start stream and write to mongodb/sql
    gradle analyze      // extract graph from mongodb/sql
    gradle userConver   // convert existing Users from mognodb into H2 Database
    gradle userClean    // clean and recreate H2 user table
    gradle appRun       // starts WebApplication
### Config files
    mongo.properties    // configure MongoDB & Collection names
    server.properties   // configure streaming options (terms,inital users, ...)
    neo4j.properties    // configure Neo4j location
    sql.properties      // configure H2 DB location
    twitter.properties  // twitter credentials
### Database locations
    Neo4J: /data/neo/aicDB
    SQL:   /data/sql/userDB
### From Source
### VM




Notes
----------
- Search and filtering should work case insensitive, if not  (H2 varchar_ignorecase is not set -> gradle userClean to recreate the user table).
- Dont forget to analyze your data :
- <img src="https://raw.githubusercontent.com/syrenio/aic-bigdata/master/stuff/notsimply.jpg"/>
