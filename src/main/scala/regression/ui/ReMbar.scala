package regression.ui

import scalafx.scene.Node
import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.HBox
import scalafx.scene.control.Label
import scalafx.scene.control.Button
import scalafx.stage.FileChooser
import scalafx.stage.Stage
import java.io.File


class ReMbar(val stage: Stage, loadFileCallback: (File) => Unit) {

  val node = new HBox

  def refresh() : Unit = {
    val loadDataButton = new Button("Open file")
    loadDataButton.onAction = (event) => {
      val fileChooser = new FileChooser()
      fileChooser.setTitle("Open data file")
      val file = fileChooser.showOpenDialog(stage)
      loadFileCallback(file)
    }
    val nSeq = Seq[Node](loadDataButton)
    node.children_=(nSeq)
  }
}
