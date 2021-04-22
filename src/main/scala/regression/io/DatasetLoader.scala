package regression.io

import java.io.{BufferedReader, IOException, Reader}
import org.json4s._
import org.json4s.jackson.JsonMethods._

object DatasetLoader {
  /*
   Loads a JSON file,
   */
  def loadJSONFile(input: Reader) : Dataset = {
    try {
      val bfr = new BufferedReader(input)
      var jsonString : String = ""
      var line: String = null
      // Get all contents of the reader
      while({line = bfr.readLine; line != null}) jsonString += line

      implicit val formats = org.json4s.DefaultFormats
      val map = parse(jsonString).extract[Map[String, Any]]
      val data = map("data").asInstanceOf[Map[String, Double]].map(g => (g._1.toDouble, g._2))
      val keysL = map("keysLabel").asInstanceOf[String]
      val valuesL = map("valuesLabel").asInstanceOf[String]
      new Dataset(data, keysL, valuesL)
    } catch {
      case e: Exception =>
        val dataExc = new CorruptedDatasetException("Reading the dataset failed.")
        throw dataExc
    }

  }

  def loadStandardFile(input: Reader) : Dataset = {
    try {
      val bfr = new BufferedReader(input)
      val labels = bfr.readLine.split(":")
      if (labels.length != 2) {
        throw new CorruptedDatasetException("Dataset has incorrect label formatting")
      } else {
        val keysLabel   = labels(0).trim
        val valuesLabel = labels(1).trim

        // Read the dataset
        def parseDatasetLine(r: String) : (Double, Double) = {
          val values = r.split(":")
          if (values.length != 2) throw new CorruptedDatasetException("Faulty dataset formatting")
          try {
            val key = values(0).trim.toDouble
            val value = values(1).trim.toDouble
            (key, value)
          } catch {
            case e: Exception => throw new CorruptedDatasetException("Integer parsing error")
          }
        }
        var data: Map[Double, Double] = Map()
        var line: String = null
        while ({line = bfr.readLine; line != null}) {
          val parsed = parseDatasetLine(line)
          data += (parsed._1 -> parsed._2)
        }
        new Dataset(data, keysLabel, valuesLabel)
      }
    } catch {
      case e: IOException =>

        val dataExc = new CorruptedDatasetException("Reading the dataset failed.")

        // Append the information about the initial cause for use in
        // debugging. Otherwise the programmer cannot know the method or
        // line number causing the problem.

        dataExc.initCause(e)

        throw dataExc
    }
  }
}
