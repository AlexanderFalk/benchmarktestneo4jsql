# Technical Comparison of an SQL and GraphDatabase

#### Author: Alexander Falk
#### Date: 24/04/2018
#### Course: Database by Developers

1. NEO4j: 

   1. **all persons that a person endorses, i.e., endorsements of depth one.**

      ```cypher
      MATCH(p)-[:ENDORSES]->(endorsement) WHERE ID(p) = 20653 RETURN p;
      ```

      28 records,

   2. **All persons that are endorsed by endorsed persons of a person, i.e., endorsements of depth two**

      ```cypher
      MATCH (:Person { name: 'Shayne Cronauer' })-[:ENDORSES*2]-(p)
       RETURN p.name
      ```

      638 records

   3. **endorsements of depth three.**

      ```cypher
      MATCH (:Person { name: 'Shayne Cronauer' })-[:ENDORSES*3]-(p)
       RETURN p.name
      ```

      13954 record

   4. **endorsements of depth four.**

      ```cypher
      MATCH (:Person { name: 'Shayne Cronauer' })-[:ENDORSES*4]-(p)
       RETURN p.name
      ```

      314233 records

   5. **endorsements of depth five**

      ```cypher
      MATCH (:Person { name: 'Shayne Cronauer' })-[:ENDORSES*5]-(p)
       RETURN p.name
      ```

      7073032 records

2. SQL

   1. **all persons that a person endorses, i.e., endorsements of depth one.**

      ```sql
      SELECT count(*) FROM nodes AS a 
      INNER JOIN edges as b 
      ON a.node_id=b.source_node_id::INTEGER 
      WHERE a.node_id = 20653;
      ```

      28 records,	

   2. **All persons that are endorsed by endorsed persons of a person, i.e., endorsements of depth two**

      ```sql
      SELECT count(*) FROM nodes AS a 
      INNER JOIN edges as b 
      ON a.node_id=b.source_node_id::INTEGER 
      INNER JOIN edges c ON c.source_node_id=b.target_node_id
      WHERE a.node_id = 20653;
      ```

      638 records

   3. **endorsements of depth three.**

      ```sql
      SELECT count(*) FROM nodes AS a 
      INNER JOIN edges as b ON a.node_id=b.source_node_id::INTEGER 
      INNER JOIN edges c ON c.source_node_id=b.target_node_id
      INNER JOIN edges d ON d.source_node_id=c.target_node_id
      WHERE a.node_id = 20653;
      ```

      13954 records

   4. **endorsements of depth four.**

      ```cypher
      SELECT count(*) FROM nodes AS a 
      INNER JOIN edges as b ON a.node_id=b.source_node_id::INTEGER 
      INNER JOIN edges c ON c.source_node_id=b.target_node_id
      INNER JOIN edges d ON d.source_node_id=c.target_node_id
      INNER JOIN edges e ON e.source_node_id=d.target_node_id
      WHERE a.node_id = 20653;
      ```

      314233 records

   5. **endorsements of depth five**

      ```cypher
      SELECT count(*) FROM nodes AS a 
      INNER JOIN edges as b ON a.node_id=b.source_node_id::INTEGER 
      INNER JOIN edges c ON c.source_node_id=b.target_node_id
      INNER JOIN edges d ON d.source_node_id=c.target_node_id
      INNER JOIN edges e ON e.source_node_id=d.target_node_id
      INNER JOIN edges f ON f.source_node_id=e.target_node_id
      WHERE a.node_id = 20653;
      ```

      7073032 records

3. **Write a program in a programming language of your choice, such as Java, C#, etc., where the program executes the above queries for twenty random nodes against the two respective databases. That is, you run each query on the same twenty random nodes.**

   The program has been written in Java. 
   ​

4. **Extend your program, so that it measures the average and median execution times of each query. That is, you run a benchmark for the two databases.**

   Everything is done at first depth.

   Neo4j Test #1 - 20 random queries

   ```sql
   Time Spent: 0.047
   Time Spent: 0.024
   Time Spent: 0.012
   Time Spent: 0.011
   Time Spent: 0.011
   Time Spent: 0.013
   Time Spent: 0.011
   Time Spent: 0.011
   Time Spent: 0.019
   Time Spent: 0.016
   Time Spent: 0.011
   Time Spent: 0.011
   Time Spent: 0.01
   Time Spent: 0.014
   Time Spent: 0.013
   Time Spent: 0.02
   Time Spent: 0.012
   Time Spent: 0.013
   Time Spent: 0.011
   Time Spent: 0.012
   Median Time: 0.011
   Average Time: 0.015100000000000006
   ```

   ​

   SQL Test #1 - 20 random queries

   ```sql
   # 20 - Time spent: 1.569
   # 19 - Time spent: 1.125
   # 18 - Time spent: 1.168
   # 17 - Time spent: 1.205
   # 16 - Time spent: 1.121
   # 15 - Time spent: 1.106
   # 14 - Time spent: 1.377
   # 13 - Time spent: 1.113
   # 12 - Time spent: 1.144
   # 11 - Time spent: 1.137
   # 10 - Time spent: 1.112
   # 9 - Time spent: 1.113
   # 8 - Time spent: 1.106
   # 7 - Time spent: 1.11
   # 6 - Time spent: 1.125
   # 5 - Time spent: 1.129
   # 4 - Time spent: 1.122
   # 3 - Time spent: 1.15
   # 2 - Time spent: 1.171
   # 1 - Time spent: 1.142
   Median: 1.112
   Average Time: 1.16725
   ```

   ​

   ​

5.  **Describe the setup of your experiment. That is, what does someone has to do/install/setup to reproduce your experiment?**

   You need to setup a local neo4j and postgres database. Also you need gradle to activate the project to get the right dependencies. If you have gradle, you can just pull the project from github, locate it at your destination folder and execute: 'gradle idea' (if you use intellij, which is recommended for Java development)

6. **Give an explanation of the differences in your time measurements.**

   Neo4j is a lot faster than postgres due to neo4j just checks the nearest neighbors and return those back. It doesn't have to filter anything out, since it just checks the relationships of the node id. For postgres it has to join two tables, find the matches, and then filter out those who are not needed in the return. This is a litle time consuming and which is why it will take a longer time. The test is only made on depth 1, but if we took this to the next level, 4 as example, then we would really see the strength of neo4j. 

7. **Conclude which database is better suited for this kind of queries and explain why.**

   For these kind of queries, where we want to find relations of a node and relations of a relation, neo4j is the optimal choice. The reason for this is: it is build on the graph theory, where only nodes who know about each other, will be looked at. In postgres everything is tables combined with attributes and here we have to look through the whole table each time to make sure that we get every result we need. 

   A small thing to notice here is that none of the databases have been using indices. This could have had an effect on the results. 





   ​

   ​



