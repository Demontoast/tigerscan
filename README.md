# TigerScan - Email Security Scanner
*Software Engineering Project - Rowan University*

## Requirements
### Manual (Drag & Drop)
- [SQLite JDBC Driver](https://github.com/xerial/sqlite-jdbc) - JDBC library for interfacing with SQLite database files in Java. Download the latest compiled version from the [download page](https://bitbucket.org/xerial/sqlite-jdbc/downloads). (TigerScan currently uses version 3.14.2.1)
- [Apache Lucene](http://lucene.apache.org/) - Open-source project featuring advanced text searching, indexing, analysis, spell-checking and more. Downloads can be found at the [Apache archives](http://archive.apache.org/dist/lucene/java/). (TigerScan currently uses version 6.2.1) Currently required dependencies are as follows:
 - core/lucene-core-6.2.1.jar
 - queryparser/lucene-queryparser-6.2.1.jar
 - analysis/common/lucene-analyzers-common-6.2.1.jar

Place .jar files in the "lib" directory. These dependencies are required to properly compile the source code. Fully compiled versions can be found on the [releases](https://github.com/nickschillaci1/SWENG_EmailSecurityScanner/releases) page.