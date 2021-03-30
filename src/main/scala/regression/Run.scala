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
import scalafx.beans.property.DoubleProperty
import scalafx.beans.property.ObjectProperty
import scalafx.beans.property.ReadOnlyDoubleWrapper
import scalafx.beans.value.ObservableValue
import scalafx.stage.FileChooser
import regression.ui._
import java.io.{File, InputStreamReader, FileInputStream, BufferedReader}
import regression.io.DatasetLoader
import regression.io.CorruptedDatasetException
import regression.models.Sheet
import scala.collection.mutable.Buffer

class DataCell(key_ : Double, value_ : Double) {
  val k = new ObjectProperty[Double](this, "k", key_)
  val v = new ObjectProperty[Double](this, "v", value_)
}
object Run extends JFXApp {

  lazy val obs = ObservableBuffer(
    new DataCell(1.2, 5.6),
    new DataCell(1.6, 128.2),
    new DataCell(2.0, 129.2)
  )
  stage = new JFXApp.PrimaryStage {
    title.value = "Regression UI"
    width = 750
    height = 550
  }
  val root = new GridPane
  val scene = new Scene(root)
  stage.scene = scene


  // Placeholders
  // val menubarPH     = new HBox
  val menuB         = new ReMbar(stage, loadDataFile)
  val tabbar        = new ReTabBar(removeSheet, selectSheet)
  val datapanel     = new ReDataPanel
  val coordinatesPH = new Canvas(600, 600)

  var sheets = Buffer[Sheet]()
  var currentSheet = -1
  /// Adds the sheet to the currently open sheets in memory
  def addSheet(sheet: Sheet) = {
    sheets.addOne(sheet)
    currentSheet = sheets.length - 1
    tabbar.refresh(sheets.toSeq)
    datapanel.refresh(sheets(currentSheet))
  }
  def selectSheet(index: Int) = {
    currentSheet = index
    datapanel.refresh(sheets(currentSheet))
  }
  def removeSheet(index: Int) = {
    sheets.remove(index)
    if (currentSheet == index) {
      currentSheet = currentSheet match {
        case 0 => sheets.length - 1
        case _ => currentSheet - 1
      }
    }
    datapanel.refresh(sheets(currentSheet))
  }
  /// Loads a sheet from a file
  def loadDataFile(f: File) = {
    def showError(message: String) {
      val alert = new Alert(Alert.AlertType.Error)
      alert.setContentText(message)
      alert.show()
    }
    if (f != null && f.canRead()) {
      try {
        val reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))
        // Gets the extension type
        val ext = f.getName().substring(f.getName().lastIndexOf('.')+1).toLowerCase()
        val data = ext match {
          case "json" => DatasetLoader.loadJSONFile(reader)
          case "txt"  => DatasetLoader.loadStandardFile(reader)
        }
        val nameDialog = new TextInputDialog
        nameDialog.setContentText("Sheet name")
        val nameResult = nameDialog.showAndWait()
        nameResult match {
          case Some("")   => addSheet(new Sheet(f.getName(), data))
          case Some(name) => addSheet(new Sheet(name, data))
          case None       => addSheet(new Sheet(s"Sheet ${(sheets.length + 1).toString}", data))
        }
      } catch {
        case dsE : CorruptedDatasetException => showError("Error loading dataset")
        case e   : Exception => showError("Error loading file: " + e.getMessage())
      }
    }
  }
  menuB.refresh()
  tabbar.refresh(Seq())
  coordinatesPH          .graphicsContext2D.fillText("Coordinates", 10, 100)

  root.add(menuB.node,        0, 0, 2, 1)
  root.add(new HBox(tabbar.tabpanel),         0, 1, 2, 1)
  root.add(datapanel.table,        0, 2, 1, 1)
  root.add(coordinatesPH,    1, 2, 1, 1)

  val column0 = new ColumnConstraints
  val column1 = new ColumnConstraints

  column0.setPercentWidth(25.0)
  column1.setPercentWidth(75.0)

  val row0 = new RowConstraints
  val row1 = new RowConstraints
  val row2 = new RowConstraints

  row0.setMinHeight(30.0)
  row0.setMaxHeight(30.0)
  row1.setMinHeight(35.0)
  row1.setMaxHeight(35.0)
  row2.setMinHeight(250.0)

  root.columnConstraints = Array[ColumnConstraints](column0, column1)
  root.rowConstraints    = Array[RowConstraints](row0, row1, row2)

  // tabbarPH.setBackground(new Background(Array(new BackgroundFill(Color.Beige, new CornerRadii(0), Insets.Empty)))) //Set sideBox background color
  menuB.node.setBackground(new Background(Array(new BackgroundFill(Color.Cyan, new CornerRadii(0), Insets.Empty)))) //Set sideBox background color
  datapanel.table.setBackground(new Background(Array(new BackgroundFill(Color.Gray, new CornerRadii(0), Insets.Empty)))) //Set sideBox background color

}
