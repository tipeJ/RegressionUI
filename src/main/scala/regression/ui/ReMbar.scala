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
    fitComboBox.value.value_=(fitValue)
    val nSeq = Seq[Node](loadDataButton, fitComboBox)
    node.children_=(nSeq)
  }
}
