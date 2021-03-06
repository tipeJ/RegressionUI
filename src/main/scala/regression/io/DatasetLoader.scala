package regression.io

import java.io.{BufferedReader, IOException, Reader}

object DatasetLoader {
  def loadStandardFile(input: Reader) = {
    try {
      val bfr = new BufferedReader(input)
      val labels = bfr.readLine.split(":")
      if (labels.length != 2) {
        throw new CorruptedDatasetException("Dataset has incorrect label formatting")
      } else {
        val keysLabel   = labels(0).trim
        val valuesLabel = labels(1).trim

        // Read the dataset
        def parseDatasetLine(r: String) : (Int, Int) = {
          val values = r.split(":")
          if (values.length != 2) throw new CorruptedDatasetException("Faulty dataset formatting")
          try {
            val key = values(0).trim.toInt
            val value = values(1).trim.toInt
            (key, value)
          } catch {
            case e: Exception => throw new CorruptedDatasetException("Integer parsing error")
          }
        }
        var data: Map[Int, Int] = Map()
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
