package regression.ui

import scalafx.scene.Node
import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.HBox
import scalafx.scene.control._
import scalafx.stage._
import java.io.File
import regression.io._
import scalafx.scene.paint.Color


class ReMbar(val stage: Stage, loadFileCallback: (File) => Unit, switchFit: (String, Color) => Unit) {

  val node = new HBox

  def refresh(fit: Option[RegressionFit]) : Unit = {
    // Button for loading a new datasheet
    val loadDataButton = new Button("Open file")
    loadDataButton.onAction = (event) => {
      val fileChooser = new FileChooser()
      fileChooser.setTitle("Open data file")
      val file = fileChooser.showOpenDialog(stage)
      this.loadFileCallback(file)
    }

    val fitValue = fit match {
      case Some(fit: LinearFit)    => "Linear Fit"
      case Some(fit: QuadraticFit) => "Quadratic Fit"
      case _                       => "No Fit"
    }
    // Dropdown menu for switching regression type
    val fitComboBox = new ComboBox[String]
    fitComboBox.items.get().addAll("Linear Fit", "Quadratic Fit", "No Fit")
    fitComboBox.onAction = (event) => {
      val color = if (fit.nonEmpty) fit.get.color else Color.DarkBlue
      this.switchFit(fitComboBox.value.value, color)
    }
    fitComboBox.value.value_=(fitValue)

    // Button for displaying the equation
    val fitInfoButton = new Button("Show equation")

    // Button for changing the regression color
    val colorPicker = new ColorPicker()

    if (fit.nonEmpty) {
      fitInfoButton.onAction = (event) => {
        val alert = new Alert(Alert.AlertType.Information)
        alert.title_=("Equation")
        alert.setContentText(fit.get.formattedExpression)
        alert.show()
      }
      colorPicker.value_=(fit.get.color)
      colorPicker.onAction = (event) => this.switchFit(fitComboBox.value.value, new Color(colorPicker.getValue))
    }

    val nSeq = Seq[Node](loadDataButton, fitComboBox, fitInfoButton, colorPicker)
    node.children_=(nSeq)
  }
}
