aic-bigdata
===========

AIC Group Project Big Data

Architecture:
https://www.dropbox.com/s/lqbvh8k3ydv8mqv/AIC_Architektur_Vorschlag.pdf?dl=0

Start
-----------

- Install Gradle http://www.gradle.org/
- gradle build
- gradle main            <-- start Console version
- gradle analyze         <-- extract graph from mongodb/sql
- gradle userConvert     <-- convert existing Users from MongoDB into H2 Database
- gradle userClean       <-- clean and recreate the user table

Web
----------
- gradle appRun  <-- starts WebServer

Notes
----------
- Search and filtering should work case insensitive, if not  (H2 varchar_ignorecase is not set -> gradle userClean to recreate the user table).
- Dont forget to analyze your data : 
- <img src="https://raw.githubusercontent.com/syrenio/aic-bigdata/master/stuff/notsimply.jpg"/>

MongoDB
-----------

- Install MongoDB from: http://www.mongodb.org/downloads
- Installation information for your distribution can be found at: http://docs.mongodb.org/manual/ Installation
