package hr.fer.ztel.thesis.spark

import java.time.Instant

import hr.fer.ztel.thesis.measure.ItemPairSimilarityMeasure
import hr.fer.ztel.thesis.measure.ItemPairSimilarityMeasure._
import org.apache.spark.sql.SparkSession

class SparkSessionHandler(args: Array[String]) extends Serializable {

  if (args.length != 8) {
    println("Wrong args, should be: [folder] [user-item] [item-item] [measure] [normalize] [output] [k] [blocksize]")
    System exit 1
  }

  val folder: String = if (!args(0).endsWith("/")) args(0) + "/" else args(0)
  val userItemPath: String = folder + args(1)

  val itemItemPath: String = folder + args(2)
  val measureStr: String = args(3).toLowerCase
  val normalize: Boolean = args(4).toLowerCase.toBoolean
  val measure: ItemPairSimilarityMeasure = parseMeasure(measureStr).get

  val time: String = Instant.ofEpochMilli(System.currentTimeMillis)
    .toString.replace(":", "-").replace(".", "-")

  val recommendationsPath: String = folder + args(5) + "_" + time
  val topK: Int = args(6).toInt

  val usersSizePath: String = userItemPath + ".lookup.size"
  val itemsSizePath: String = itemItemPath + ".lookup.size"

  val blockSize: Int = args(7).toInt

  lazy val getSparkSession: SparkSession = {

    val sparkBuilder = if (folder.startsWith("hdfs:///")) SparkSession.builder
    else SparkSession.builder.master("local[*]")

    sparkBuilder
      .appName("SparkRecommenderThesis")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .config("spark.kryo.registrator", "hr.fer.ztel.thesis.spark.SparkKryoRegistrator")
      .getOrCreate
  }

}