package regression

import scalafx.application.JFXApp
import scalafx.scene.layout._
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.geometry.Insets
import scalafx.scene.control._
import scalafx.collections._
import scalafx.scene.control.TableColumn._
import scalafx.beans.property._
import scalafx.beans.value.ObservableValue
import scalafx.stage.FileChooser
import scala.collection.mutable.Buffer
import scalafx.scene.text.Text
import java.io.{File, InputStreamReader, FileInputStream, BufferedReader}
import java.io.FileNotFoundException
import regression.models.Sheet
import regression.ui._
import regression.io._
import regression.RegressionController

object App extends JFXApp {

  stage = new JFXApp.PrimaryStage {
    title.value = "Regression Visualization"
    width = 750
    height = 550
  }
  val root = new GridPane
  val scene = new Scene(root)
  stage.scene = scene
  val controller = new RegressionController

  // Initialize the main UI components:
  val menuB         = new ReMenubar(stage, loadDataFile)
  val tabbar        = new ReTabBar
  val datapanel     = new ReDataPanel
  val equationText  = new Label {
    padding = Insets.apply(0, 0, 0, 25)
  }
  val coordinates   = new ReCoordinates

  menuB.init(controller)
  datapanel.init(controller)
  tabbar.init(controller)
  coordinates.init(controller)

  // Add a listener for current equation:
  controller.currentFit.addListener(
    (_, __, fit) => {
      val text = fit match {
        case Some(f) => f.formattedExpression
        case None    => ""
      }
      equationText.text_=(text)
    }
  )

  // Set the UI Grid layout:
  val column0 = new ColumnConstraints
  val column1 = new ColumnConstraints

  column0.setPercentWidth(25.0)
  column1.setPercentWidth(75.0)

  val row0 = new RowConstraints
  val row1 = new RowConstraints
  val row2 = new RowConstraints
  val row3 = new RowConstraints

  row0.setMinHeight(26.0)
  row0.setMaxHeight(26.0)
  row1.setMinHeight(30.0)
  row1.setMaxHeight(30.0)
  row2.setMinHeight(25.0)
  row2.setMaxHeight(25.0)
  row3.setMinHeight(250.0)
  row3.setVgrow(Priority.ALWAYS)

  root.columnConstraints = Array[ColumnConstraints](column0, column1)
  root.rowConstraints    = Array[RowConstraints](row0, row1, row2, row3)

  root.add(menuB.node,                            0, 0, 2, 1)
  root.add(new VBox(tabbar.tabpanel),             0, 1, 2, 1)
  root.add(datapanel.table,                       0, 2, 1, 2)
  root.add(equationText,                          1, 2, 1, 1)
  root.add(coordinates.chart,                     1, 3, 1, 1)

  // Initialize the first sheet id
  var newId = 0
  // Loads a sheet from a file
  def loadDataFile(f: File) : Unit = {
    if (f != null && f.canRead()) {
      try {
        val reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))
        // Gets the extension type
        val ext = f.getName().substring(f.getName().lastIndexOf('.')+1).toLowerCase()
        val data = ext match {
          case "json" => DatasetLoader.loadJSONFile(reader)
          case "txt"  => DatasetLoader.loadStandardFile(reader)
          case _      => throw new FileNotFoundException
        }
        val nameDialog = new TextInputDialog
        nameDialog.setContentText("Sheet name")
        val nameResult = nameDialog.showAndWait()
        nameResult match {
          case Some(name) => {
            val sheetName = if (name.isEmpty) s"Sheet $newId" else name
            controller.addSheet(new Sheet(name, newId, data, "Linear Fit"))
            controller.switchFit("Linear Fit")
            newId = newId + 1
          }
          case None       =>
        }
      } catch {
        case dsE : CorruptedDatasetException => controller.showError("Error loading dataset.")
        case fE  : FileNotFoundException => controller.showError("Invalid file selected.")
        case e   : Exception => controller.showError("Error loading file: " + e.getMessage())
      }
    }
  }
}
