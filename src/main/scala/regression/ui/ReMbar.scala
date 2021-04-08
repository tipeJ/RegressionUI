package regression.ui

import scalafx.scene.Node
import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.HBox
import scalafx.scene.control._
import scalafx.stage._
import java.io.File
import regression.io._


class ReMbar(val stage: Stage, loadFileCallback: (File) => Unit) {

  val node = new HBox

  def refresh(fit: Option[RegressionFit]) : Unit = {
    val loadDataButton = new Button("Open file")
    loadDataButton.onAction = (event) => {
      val fileChooser = new FileChooser()
      fileChooser.setTitle("Open data file")
      val file = fileChooser.showOpenDialog(stage)
      loadFileCallback(file)
    }
    val fitValue = fit match {
      case Some(fit: LinearFit)    => "Linear Fit"
      case Some(fit: QuadraticFit) => "Quadratic Fit"
      case None                    => "No Fit"
    }
    val fitComboBox = new ComboBox[String]
    fitComboBox.items.get().addAll("Linear Fit", "Quadratic Fit", "No Fit")
    fitComboBox.onAction = (event) => {

    }
    fitComboBox.value.value_=(fitValue)

    val fitInfoButton = new Button("Show equation")
    if (fit.nonEmpty) {
      fitInfoButton.onAction = (event) => {
        val alert = new Alert(Alert.AlertType.Information)
        alert.title_=("Equation")
        alert.setContentText(fit.get.formattedExpression)
        alert.show()
      }
    }
    val nSeq = Seq[Node](loadDataButton, fitComboBox, fitInfoButton)
    node.children_=(nSeq)
  }
}
