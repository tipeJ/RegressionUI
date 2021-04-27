package regression

import scalafx.beans.property.ObjectProperty
import regression.models.Sheet
import regression.io._
import scalafx.scene.paint.Color
import scalafx.collections.ObservableBuffer
import regression.ui.AxisEndpoints
import scalafx.scene.control.Alert

// This class controls the UI with observable values and arrays.
class RegressionController {

  // All the sheets currently open in memory.
  val sheets        = new ObservableBuffer[Sheet]()
  // The currently selected Axis Endpoints.
  val axisEndPoints = new ObjectProperty[AxisEndpoints](){
    value = new AxisEndpoints
  }
  // The currently selected sheet.
  val currentSheet  = new ObjectProperty[Option[Sheet]](){
    value = None
  }
  // The currently selected fit.
  val currentFit    = new ObjectProperty[Option[RegressionFit]](){
    value = None
  }

  // Changes the currently selected sheet.
  def setCurrentSheet(sheet: Sheet) = currentSheet.value = Some(sheet)
  // Sets the fit to the given one.
  def setCurrentFit(fit: RegressionFit) = currentFit.value = Some(fit)
  // Switches the currently selected fit.
  def switchFit(newFit: String) : Unit = {
    val sheet = currentSheet.get()
    if (sheet.nonEmpty) {
      val color = if (currentFit.get().nonEmpty) currentFit.get().get.color else Color.Blue
      sheet.get.fit = newFit
      currentFit.value_=(
        newFit match {
          case "Linear Fit" => Some(new LinearFit(sheet.get.dataset.data.toList, color))
          case "Quadratic Fit" => Some(new QuadraticFit(sheet.get.dataset.data.toList, color))
          case _ => None
        }
      )
    }
  }
  // Switches the color of the current fit.
  def switchColor(color: Color) : Unit = {
    val cFit = currentFit.get
    if (cFit.nonEmpty) {
      val sheet = currentSheet.get()
      if (sheet.nonEmpty) {
        currentFit.value_=(
          if (cFit.get.isInstanceOf[LinearFit]) {
            Some(new LinearFit(sheet.get.dataset.data.toList, color))
          } else if (cFit.get.isInstanceOf[QuadraticFit]) {
            Some(new QuadraticFit(sheet.get.dataset.data.toList, color))
          } else {
            None
          }
        )
      }
    }
  }
  // Adds the given sheet to the sheets and selects it.
  def addSheet(sheet: Sheet) = {
    sheets.addOne(sheet)
    selectSheet(sheet)
  }
  // Select the given sheet.
  def selectSheet(sheet: Sheet) = {
    currentSheet.value_=(Some(sheet))
  }
  // Remove the given sheet and switch the current sheet if the removed one was the previously selected one.
  def removeSheet(id: Int) = {
    sheets.remove(sheets.indexWhere(_.id == id))
    if (currentSheet.get().get.id == id) {
      val newSheet : Option[Sheet] = if (sheets.nonEmpty) Some(sheets(0)) else None
      currentSheet.value_=(newSheet)
      if (newSheet.nonEmpty) {
        switchFit(newSheet.get.fit)
      } else {
        currentFit.value_=(None)
      }
    }
  }

  def getSheetIndex(sheet: Sheet) : Int = sheets.indexOf(sheet)

  def setAxisEndpoints(endpoints: AxisEndpoints) = axisEndPoints.value_=(endpoints)

  // Show an error dialog displaying the given message.
  def showError(message: String) = {
    val alert = new Alert(Alert.AlertType.Error) {
      title = "Error"
      contentText = message
    }
    alert.show()
  }
}
