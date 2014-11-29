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
- gradle neo4jExtraction <-- extract graph from mongodb
- gradle tanalysis <-- add ads/topics and mine topics

Web
----------
- Install Node.js http://nodejs.org/
- Install Bower http://bower.io/  "npm install -g bower"
- cd src/main/webapp 
- "bower install"
- go back to project root-folder 
- gradle appRun  <-- starts WebServer

MongoDB
-----------

- Install MongoDB from: http://www.mongodb.org/downloads
- Installation information for your distribution can be found at: http://docs.mongodb.org/manual/ Installation
