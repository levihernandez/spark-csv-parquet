# spark-csv-parquet with Apache Zeppelin
Ingest a CSV file and store it in Parquet format with SBT.

In the Big Data field, there are many ways of ingesting data. The following example gives us an idea of how the ETL process works with Spark as a stand alone task. The full tutorial is hosted at [my site](http://hernandezgraham.com)

I develop in MacOS and Linux for this tutorial.

## Know the data structure

Loading and transforming a small file is quite easy to do. I use my city's data to query parks with specific facilities. Follow these steps:

* Download the small set of 89 records from [City of Tallahassee city park points](http://talgov-tlcgis.opendata.arcgis.com/datasets/3f79ef4a4cc64b30ba314ea8004b4866_5.csv) as CSV
* Discover data types, this is optional for now
  * headers
  * date, time, timestamps
  * integers, decimals, etc
* Download [Spark 2](https://spark.apache.org/downloads.html)
* Use a prototyping notebook such as [Apache Zeppelin](https://zeppelin.apache.org/download.html) (install not covered here)
* Create a new project with SBT (not covered in this tutorial)
* 


## Prepare Environment
Assuming you Java 8 exists in the environment, the list of tools to have handy are:

* SBT (install via Brew)
  * create new project, mine is named sparkIngest 
  ```shell
  $ mkdir -p ${homedir}/project/{bins,sbtc,data}
  $ cd ${homedir}/project/sbtc
  $ sbt new scala/scala-seed.g8
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
First we begin with a prototype of what we want to do in Zeppelin notebooks.
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

