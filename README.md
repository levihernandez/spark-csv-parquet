# spark-csv-parquet with Apache Zeppelin
Ingest a CSV file and store it in Parquet format with SBT.

In the Big Data field, there are many ways of ingesting data. The following example gives us an idea of how the ETL process works with Spark as a stand alone task. The full tutorial is hosted at [my site](http://hernandezgraham.com)

I develop in MacOS and Linux for this tutorial.

## Know the data structure

Loading and transforming a small file is quite easy to do. I use my city's data to query parks with specific facilities. Follow these steps:

* Download the small set of 89 records from [City of Tallahassee city park points](http://talgov-tlcgis.opendata.arcgis.com/datasets/3f79ef4a4cc64b30ba314ea8004b4866_5) as CSV
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
$ cd zeppelin
$ bin/zeppelin-daemon.sh start
```
* Apache Spark 2.2
```shell
$ cd ${homedir}/project/bins
$ cd spark
```
* inbound and outbound directory for the data
```shell
$ mkdir -p ${homedir}/project/data/{inbound,outbound}
```
* 

## The Code
