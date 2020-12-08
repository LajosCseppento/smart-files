# SmartFiles / Design / Data Store

Prototype file scanning, indexing and duplicate finding was carried out
using in-memory data structures. The data was persisted in a single JSON file.
The test data set included 4.5 million records (complete file system of my machine).
Reading and parsing the 1.1 GB JSON took 11 seconds on average (100% CPU usage) and 
yielded a 1.5 GB memory footprint.

System:
 * Intel Core i7-6700 @ 3.4 GHz
 * 32 GB RAM
 * NVMe SSD
 * Windows 10 64-bit Pro
 * OpenJDK 64-Bit Server VM (11.0.9+11, mixed mode), AdoptOpenJDK

The prototype did not account for hashing and duplicate finding. After thorough
consideration I realised that the simplicity of the one JSON file comes with too
many drawbacks:
 * read/write lag might disturb end-user experience
 * all data has to be in the memory to be able to work with
 * hashing, duplicate finding, GUI need system resources on-top of data store

Although the suboptimal text JSON could be replaced by a compact binary format,
it would not decrease memory usage. Thus, I decided to leverage a database which
supports graph-like data structures.

## The Candidates

My initial research reduced the scope to five candidates:

 * [ArangoDB](https://www.arangodb.com)
 * [JanusGraph](https://janusgraph.org)
 * [Neo4j](https://neo4j.com)
 * [OrientDB](https://www.orientdb.org)
 * [RedisGraph](https://oss.redislabs.com/redisgraph/)

Main requirements:

 * Supports graph-like data structures
 * Nodes can be annotated with extra information (such as hash)
 * Easy to embed with SmartFiles (no separate installation required)
 * Multi-platform support (Linux, OS X, Windows)
 * License allows usage as part of SmartFiles up to 50M records
 * Stable, learning resources are available

Other aspects:
 * I used Neo4j before, it would be nice to try something new

This lead to elimination of three candidates:
 * JanusGraph: latest version is 0.5.2 at the time writing 
 * Neo4j: complicated licensing, subscriptions, GPLv3
 * RedisGraph: does not target Windows

ArangoDB and OrientDB comes with an Apache 2 License. 

## ArangoDB vs. OrientDB

First of all, I found [this thread](https://stackoverflow.com/questions/28553942/what-factors-to-consider-when-choosing-a-multi-model-dbms-orientdb-vs-arangodb)
very useful.
