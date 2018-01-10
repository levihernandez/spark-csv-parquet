# spark-csv-parquet with Apache Zeppelin
Ingest a CSV file and store it in Parquet format with SBT.

In the Big Data field, there are many ways of ingesting data. The following example gives us an idea of how the ETL process works with Spark as a stand alone task. The full tutorial is hosted at [my site](http://hernandezgraham.com)

I develop in MacOS and Linux for this tutorial.

## Know the data structure

Loading and transforming a small file is quite easy to do. I use my city's data to query parks with specific facilities such as baseball fields, bathrooms, grills, etc. Follow these steps:

* Download the small set of 89 records from [City of Tallahassee park points](http://talgov-tlcgis.opendata.arcgis.com/datasets/3f79ef4a4cc64b30ba314ea8004b4866_5.csv) as CSV
* Discover data types, this is optional for now
  * headers
  * date, time, timestamps
  * integers, decimals, etc

Decide if the file is small enough to auto-load it with Spark or define the table structure manually with data types for large data sets.


## Prepare Environment
Assuming you Java 8 exists in the environment, the list of tools to have handy are:

* SBT (install via Brew), 
  * for Mac OS:
  
  ```shell
  $ brew install sbt
  ```
  * configure Eclipse plugins (this is optional) to import the SBT project into Eclipse
  
  ```shell
  $ vi ~/.sbt/0.13/plugins/plugins.sbt
  addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.3")
  addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")
  ```

  * create new project, mine is named **sparkIngest** 
  
  ```shell
  $ mkdir -p ${homedir}/project/{bins,sbtc,data}
  $ cd ${homedir}/project/sbtc
  $ sbt new scala/scala-seed.g8
  A minimal Scala project.
  name [Scala Seed Project]: sparkIngest
  Template applied in ./sparkingest
  ```
  * configure the build.sbt file, **NOTE:** right now only Scala 2.11 is the max version that works with SBT.
  
  ```json
  name := "sparkIngest"
  version := "1.0"
  scalaVersion := "2.11.11"
  val sparkVersion = "2.2.0"
  resolvers ++= Seq(
    "apache-snapshots" at "http://repository.apache.org/snapshots/"
  )
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.3.1",
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-sql" % sparkVersion,
    "org.apache.spark" %% "spark-mllib" % sparkVersion,
    "org.apache.spark" %% "spark-streaming" % sparkVersion,
    "org.apache.spark" %% "spark-hive" % sparkVersion
  )
  ```
  
* Apache Zeppelin, in background mode

  ```shell
  $ cd ${homedir}/project/bins
  $ curl -L http://apache.cs.utah.edu/zeppelin/zeppelin-0.7.3/zeppelin-0.7.3-bin-all.tgz | tar xvf -
  $ mv zeppelin-0.7.3-bin-all zeppelin
  $ cd zeppelin
  $ bin/zeppelin-daemon.sh start
  Zeppelin start                                             [  OK  ]
  ```
* Apache Spark 2.2

  ```shell
  $ cd ${homedir}/project/bins
  $ curl -L https://www.apache.org/dyn/closer.lua/spark/spark-2.2.1/spark-2.2.1-bin-hadoop2.7.tgz | tar xvf -
  $ mv spark-2.2.1-bin-hadoop2.7 spark
  ```
* inbound and outbound directory for the data

  ```shell
  $ mkdir -p ${homedir}/project/data/{inbound,outbound}
  $ cd ${homedir}/project/data/inbound
  $ curl -O http://talgov-tlcgis.opendata.arcgis.com/datasets/3f79ef4a4cc64b30ba314ea8004b4866_5.csv
  ```
* Java 8

  ```shell
  $ java -version
  java version "1.8.0_152"
  Java(TM) SE Runtime Environment (build 1.8.0_152-b16)
  Java HotSpot(TM) 64-Bit Server VM (build 25.152-b16, mixed mode)
  ```

## The Code

First we begin with a prototype of what we want to do in [Zeppelin notebooks: spark-csv-parquet ](https://github.com/levihernandez/spark-csv-parquet/tree/master/notebooks).

* Import basic libraries
* create a 'case class' for the table structure OR let Spark autodetect the schema
* declare variables for the location of the file
* define timestamp formatting
* load the CSV file and have Spark read it
* transform columns to the datatypes we need such as timestamps, decimals, etc
* save the data to a Parquet, snappy compressed file
* open the Parquet file
* create a temp table
* query the temp table

## SBT Jar

Venturing into the world of Spark can be challenging for begginers. It was the case with me when I tried to setup the SBT environment for the first time. Here are some generic steps to take in order to transfer the Zeppelin notebook prototype into a JAR package.

* create a new SBT project, as shown in the 'Prepare Environment' section
* configure the build.sbt file inside the project, as shown in the 'Prepare Environment' section
* create a Scala package structure as sparkIngest
* create a Scala Object file for the code
* copy and paste the code from our Zeppelin prototype to the Scala Object file as parks.scala. See dir structure:

  ```shell
  $ ls -R
  build.sbt	project		src

  ./project:
  Dependencies.scala	build.properties

  ./src:
  main	test

  ./src/main:
  scala

  ./src/main/scala:
  sparkIngest

  ./src/main/scala/sparkIngest:
  parks.scala

  ./src/test:
  scala

  ./src/test/scala:
  sparkIngest

  ./src/test/scala/sparkIngest:
  parksSpec.scala
  ```

* modify the code in the parks.scala file to include a main class and object name
The SBT project is included in this tutorial inside the sbtc directory.
* compile the code and find the new JAR file

```shell
$ sbt package
...
[info] Packaging sparkingest/target/scala-2.11/sparkingest_2.11-1.0.jar ...
[info] Done packaging.
[success] Total time: 3 s, completed Jan 9, 2018 09:39:58 AM
```

## Prototyping
While it is fun to prototype data, it is always more challenging in real life to transform and load terabytes of data into production systems. It is imperative that we know the use case first, know our data, and have a plan to achieve our goals.

In the samples included, I approached the prototypes as an autodiscovery of the data and as a manual structure of the tables. On one hand, autodiscovery of data is the 'lazy' way of having Spark read a CSV. The caveat with this approach is the number of columns (for Spark 1) and the number of rows Spark needs to scan in a file. If we scan a small file in the megabytes, then autodiscovery might make sense, assuming we are not too worried about data types.

The second approach is to define the table structure, tell Spark we have a first row as a header, and map the table structure to the dataframe. This approach is more painful if we have a lot of columns. Once the table structure is defined with the data types taken care of, then scanning of rows is not going to take a long time when we read gigabytes of data.

The protyping for this project is done as a local task. Running the JAR in a Hadoop cluster requires a more strict configuration to ensure we don't overload our nodes by resending the bulk of data over and over again.


## Running the Code

Now that the code has been compiled with sbt, it is the time to run our JAR file by directly calling our new packaged class. It is important to call the class by its name **sparkIntest.Parks**, not by the file name with .scala extension.

```shell
$ ${SPARK_HOME}/bin/spark-submit --class sparkIngest.Parks --master local ${homedir}/project/sbtc/sparkIngest/target/scala-2.11/sparkingest_2.11-1.0.jar 
root
 |-- X: double (nullable = true)
 |-- Y: double (nullable = true)
 |-- OBJECTID: integer (nullable = true)
 |-- PARKNUM: integer (nullable = true)
 ...
 +-------+-------+
|PARKNUM|ACREAGE|
+-------+-------+
|    218|  20.77|
|    219|   8.61|
|    217|   1.89|
|    228|  31.96|
|    225|  31.96|
|    257|  19.78|
|    237|  12.44|
|    215|  13.29|
|    277|  16.94|
|    205| 676.34|
|    201| 304.05|
+-------+-------+
...
```

Soon after the spark-submit job has been processed, the new parks.parquet file is ready to be shipped to a Hadoop cluster or AWS.

Best luck to you on your learning!
