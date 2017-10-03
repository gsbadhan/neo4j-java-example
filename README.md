## download Neo4j:
wget http://dist.neo4j.org/neo4j-enterprise-3.2.5-unix.tar.gz


## Neo4j Browser URL:
http://localhost:7474/


##Eclipse IDE settings (~/eclipse-settings):
 1. import cleanup.xml and formatter.xml in Eclipse IDE.
 2. set ON save actions in Eclipse.
 3. install sonarlint tool in Eclipse.
 4. install morejunit tool in Eclipse.
 5. install Ecma code coverage tool in Eclipse.

 
## DB scripts (~/db-scripts) 
 1.lg-schema.db 
 
 

##Basic Neo4j commands:
 1. connect to cyher-shell: ./cypher-shell -u neo4j -p test
 

## Enable Neo4j Procedures in neo4j.conf:
dbms.security.procedures.unrestricted=apoc.*,org.digi.* 
 

## Library Integration instructions:
 1. graph-config.json file must be in target Application class path.
 
 
## Change neo4j password:
 CALL dbms.changePassword('new password');
 

 
## DB Export/Import:
#### Export DB:
$neo4j-home> bin/neo4j stop
$neo4j-home> bin/neo4j-admin dump --database=graph.db --to=/backups/graph.db/2017-sep-22.dump
$neo4j-home> ls /backups/graph.db
$neo4j-home> 2016-10-02.dump

#### Import DB:
$neo4j-home> bin/neo4j stop
$neo4j-home> bin/neo4j-admin load --from=/backups/graph.db/2017-sep-22.dump --database=graph.db --force   
  

