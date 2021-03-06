import regression.io._

import java.io.{Reader, StringReader}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DatasetLoaderTest extends AnyFlatSpec with Matchers{

  private val dataFiles: Map[String, String] = Map(
    // A few tricks to make including the files in the scala source a bit neater
    // """Triple quotes""" allow us to put a string literal on multiple lines
    // stripMargin removes leading spaces from the start of each line until '|'
    // We also remove all control characters (which include \n) from ChunkIOExample
    // so we can use newlines when writing the test files.
    "standardDataFile" -> ("""t (s):v (m/s)
                          |0.0:0.0
                          |2.0: 1.7
                          |4.0: 3.5
                          | 6.0: 5.7
                          |9: 12
                          |""".stripMargin drop 1),
  )

  "DatasetLoader.loadStandardFile" should "be able to load a correctly formatted file" in {
    // Run the code we want to test. NOTE that you usually shouldn't try to
    // catch exceptions in tests, because we want the test to fail in that case
    val testInput: Reader = new StringReader(dataFiles("standardDataFile"))
    val dataSet = DatasetLoader.loadStandardFile(testInput)

    withClue("Loading data failed:") {
      dataSet.data.size should equal (5)
    }

  }
}
