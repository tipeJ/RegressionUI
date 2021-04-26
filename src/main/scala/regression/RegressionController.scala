package regression

import scalafx.beans.property.ObjectProperty
import regression.models.Sheet
import regression.io._
import scalafx.scene.paint.Color
import scalafx.collections.ObservableBuffer
import regression.ui.AxisEndpoints

class RegressionController {
  val sheets        = new ObservableBuffer[Sheet]()
  val axisEndPoints = new ObjectProperty[AxisEndpoints](){
    value_=(new AxisEndpoints)
  }
  val currentSheet  = new ObjectProperty[Option[Sheet]](){
    value_=(None)
  }
  val currentFit    = new ObjectProperty[Option[RegressionFit]](){
    value_=(None)
  }

  def setCurrentSheet(sheet: Sheet) = currentSheet.value_=(Some(sheet))
  def setCurrentFit(fit: RegressionFit) = currentFit.value_=(Some(fit))
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
  def addSheet(sheet: Sheet) = {
    sheets.addOne(sheet)
    selectSheet(sheet)
  }
  def selectSheet(sheet: Sheet) = {
    currentSheet.value_=(Some(sheet))
  }
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
}
