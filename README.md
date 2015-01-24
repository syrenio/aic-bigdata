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
1. install software
2. get source
3. "gradle build" should build without problems
4. "gradle startStream" will collect data (skip this step if you already have a dataset in your mongodb and users in the H2 database), task need to be stopped manually.
5. "gradle analyze" extract the graph from the databases. (skip this step if you already have the extracted neo4j database(e.g. VM) )
6. "gradle appRun" starts the web server and explore the collected data and queries.

### VM
0. developers will provide you with the password*
1. open terminal and change directory to ~/aic-bigdata/aic-bigdata-server
2. execute "sudo gradle appRun"
3. connect with your browser to http://localhost:8080/aic-bigdata-server

<span style="color:red"> corrupt Neo4j:</span>
1. if your Neo4j database is corrupt please delete the folder /data/neo/aicDB
2. execute "sudo gradle analyze" in the "aic-bigdata-server" directory and the neo4j should be restored.




Notes
----------
- Search and filtering should work case insensitive, if not  (H2 varchar_ignorecase is not set -> gradle userClean to recreate the user table).
- Dont forget to analyze your data :
- <img src="https://raw.githubusercontent.com/syrenio/aic-bigdata/master/stuff/notsimply.jpg"/>
