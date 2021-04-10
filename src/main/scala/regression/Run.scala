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
import regression.ui._
import java.io.{File, InputStreamReader, FileInputStream, BufferedReader}
import regression.io._
import regression.models.Sheet
import scala.collection.mutable.Buffer
import regression.io._

object Run extends JFXApp {

  stage = new JFXApp.PrimaryStage {
    title.value = "Regression UI"
    width = 750
    height = 550
  }
  val root = new GridPane
  val scene = new Scene(root)
  stage.scene = scene


  // val menubarPH     = new HBox
  val menuB         = new ReMbar(stage, loadDataFile, switchFit)
  val tabbar        = new ReTabBar(removeSheet, selectSheet)
  val datapanel     = new ReDataPanel
  val coordinates   = new ReCoordinates

  val testSheet = new Sheet("yestsheet", 0, new Dataset(Map[Double, Double](
                                          2.1 -> 5.7,
                                          5.1 -> 13.9,
                                          7.88 -> 17.6,
                                          10.0 -> 20.96
                                        ), "XAX", "YAX"), "Linear Fit")
  var sheets = Buffer[Sheet]()
  var currentSheet = 0
  var currentFit : Option[RegressionFit] = None
  def getSheet(id: Int) : Option[Sheet] = sheets.filter(_.id == id).lift(0)
  /// Adds the sheet to the currently open sheets in memory
  def addSheet(sheet: Sheet) = {
    sheets.addOne(sheet)
    selectSheet(sheet.id)
    tabbar.refresh(sheets.toSeq, sheets.indexWhere(_.id == currentSheet))
  }
  def selectSheet(id: Int) = {
    currentSheet = id
    datapanel.refresh(getSheet(currentSheet))
    updateFit(if (currentFit.nonEmpty) currentFit.get.color else Color.DarkBlue)
  }
  def removeSheet(id: Int) = {
    sheets.remove(sheets.indexWhere(_.id == id))
    if (currentSheet == id) {
      val newId = if (sheets.nonEmpty) sheets(0).id else -1
      selectSheet(newId)
    }
  }
  def refreshCoordinates() : Unit = coordinates.refresh(getSheet(currentSheet), currentFit)
  def switchFit(newFit: String, color: Color) : Unit = {
    getSheet(currentSheet) match {
      case Some(sheet) => {
        sheet.fit = newFit
        updateFit(color)
      }
      case None =>
    }
  }
  def updateFit(color: Color) : Unit = {
    currentFit = getSheet(currentSheet) match {
      case None => None
      case Some(sheet) => sheet.fit match {
        case "Linear Fit" => Some(new LinearFit(sheet.dataset.data.toList, color))
        case "Quadratic Fit" => Some(new QuadraticFit(sheet.dataset.data.toList, color))
        case _ => None
      }
    }
    menuB.refresh(currentFit)
    refreshCoordinates()
  }
  menuB.refresh(None)
  tabbar.refresh(Seq(), -1)
  refreshCoordinates()


  val column0 = new ColumnConstraints
  val column1 = new ColumnConstraints

  column0.setPercentWidth(25.0)
  column1.setPercentWidth(75.0)

  val row0 = new RowConstraints
  val row1 = new RowConstraints
  val row2 = new RowConstraints

  row0.setMinHeight(26.0)
  row0.setMaxHeight(26.0)
  row1.setMinHeight(30.0)
  row1.setMaxHeight(30.0)
  row2.setMinHeight(250.0)
  row2.setVgrow(Priority.ALWAYS)

  root.columnConstraints = Array[ColumnConstraints](column0, column1)
  root.rowConstraints    = Array[RowConstraints](row0, row1, row2)

  root.add(menuB.node,                        0, 0, 2, 1)
  root.add(new VBox(tabbar.tabpanel),         0, 1, 2, 1)
  root.add(datapanel.table,                   0, 2, 1, 1)
  root.add(coordinates.chart,                     1, 2, 1, 1)

  // Initialize the first sheet id
  var newId = 0
  /// Loads a sheet from a file
  def loadDataFile(f: File) : Unit = {
    def showError(message: String) {
      val alert = new Alert(Alert.AlertType.Error)
      alert.title_=("Error")
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
          case Some(name) => {
            val sheetName = if (name.isEmpty) s"Sheet ${(sheets.length + 1).toString}" else name
            addSheet(new Sheet(name, newId, data, "Linear Fit"))
            newId = newId + 1
          }
          case None       =>
        }
      } catch {
        case dsE : CorruptedDatasetException => showError("Error loading dataset")
        case e   : Exception => showError("Error loading file: " + e.getMessage())
      }
    }
  }
}
