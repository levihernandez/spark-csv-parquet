package sparkIngest
/*
 Author: Levi Hernandez
 Date: 2018/01/09
 Description: Capture [parks.csv] flat file and convert to Parquet
 Comment: 
*/

import org.apache.spark.sql.Encoders
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object Parks{
	def main(args: Array[String]) {
		val spark = SparkSession.builder.appName("Talgov-Parks").getOrCreate()

		import spark.implicits._
		val tsm = "yyyy-MM-dd'T'HH:mm:ss.F'Z'" // 	2006-01-03T00:00:00.000Z

		val table = "parks"
		val datasr = "/Users/jhernandez/Work/temp/data" 
		val datain = datasr+"/inbound/"
		val dataout = datasr+"/outbound/"+table
		val dfparktwo = spark.read.format("csv")
			.option("header","true")
			.option("delimiter",",")
			.option("inferSchema", "true")
			.load(datain)
			.withColumn("EDITED", unix_timestamp($"EDITED", tsm).cast("timestamp"))

		dfparktwo.printSchema()

		dfparktwo.write.mode("append").parquet(dataout + ".parquet")

		// Read the parquet table
		val dfread = spark.read.parquet(dataout+".parquet")
		dfread.printSchema()
		// Creates a temporary view using the DataFrame
		dfread.createOrReplaceTempView("park")
		dfread.select("PARKNUM","ACREAGE").filter("LLFIELD = 'Y'").show()
		}
}


