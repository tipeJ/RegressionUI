package regression.ui

import scalafx.scene.Node
import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.HBox
import scalafx.scene.control._
import scalafx.stage._
import java.io.File
import regression.io._
import scalafx.scene.paint.Color
import javafx.util.converter.IntegerStringConverter
import javafx.scene.control
import scalafx.scene.layout.GridPane
import scalafx.geometry.Insets
import regression.RegressionController

/// Class for regression menubar.
class ReMenubar(val stage: Stage, loadFileCallback: (File) => Unit) {

  val node = new HBox

  val fitComboBox = new ComboBox[String]
  fitComboBox.items.get().addAll("Linear Fit", "Quadratic Fit", "No Fit")

  // Button for displaying the equation
  val fitInfoButton = new Button("Show equation")

  // Button for changing the regression color
  val colorPicker = new ColorPicker()

  def init(controller: RegressionController) = {
    // Button for loading a new datasheet
    val loadDataButton = new Button("Open file")
    loadDataButton.onAction = (event) => {
      val fileChooser = new FileChooser(){
        extensionFilters.addAll(
          new FileChooser.ExtensionFilter("Dataset files (.json or .txt)", Seq("*.txt", "*.json")),
          new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"),
          new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"),
        )
      }
      fileChooser.setTitle("Open data file")
      val file = fileChooser.showOpenDialog(stage)
      this.loadFileCallback(file)
    }
    // Dropdown menu for switching regression type
    val fitComboBox = new ComboBox[String]
    fitComboBox.items.get().addAll("Linear Fit", "Quadratic Fit", "No Fit")
    fitComboBox.onAction = (_) => controller.switchFit(fitComboBox.value.value)
    controller.currentFit.addListener(
      (_, _, fit) => {
        val fitValue = fit match {
          case Some(fit: LinearFit)    => "Linear Fit"
          case Some(fit: QuadraticFit) => "Quadratic Fit"
          case _                       => "No Fit"
        }
        fitComboBox.value.value_=(fitValue)
        fitInfoButton.onAction = (event) => {
          if (fit.nonEmpty) {
            val alert = new Alert(Alert.AlertType.None)
            alert.title_=("Equation")
            val eqText = new TextArea(fit.get.formattedExpression)
            eqText.editable_=(false)
            eqText.wrapText_=(true)
            alert.getDialogPane().setContent(eqText)
            alert.buttonTypes.addOne(ButtonType.Close)
            alert.show()
          }
        }
        if (fit.nonEmpty) {
          colorPicker.value_=(fit.get.color)
        }
      }
    )


    colorPicker.onAction = (_) => controller.switchColor(new Color(colorPicker.getValue))

    // Button for changing axis endpoints.
    val axisEndpointsButton = new Button("Axis endpoints")
    axisEndpointsButton.onAction = (event) => showAxisRangeDialog(controller)

    val nSeq = Seq[Node](loadDataButton, fitComboBox, fitInfoButton, colorPicker, axisEndpointsButton)
    node.children_=(nSeq)
  }

  private def showAxisRangeDialog(controller: RegressionController) = {
    // Show the dialog for axis endpoints.
    val dialog = new Dialog[AxisEndpoints]() {
      initOwner(stage)
      title = "Axis Ranges"
      headerText = "Set Axis Ranges"
    }
    val applyButtonType = new ButtonType("Apply", ButtonBar.ButtonData.OKDone)
    dialog.getDialogPane().getButtonTypes().addAll(applyButtonType, ButtonType.Cancel)

    val applyButton = dialog.getDialogPane.lookupButton(applyButtonType).asInstanceOf[javafx.scene.control.Button]
    applyButton.setDisable(true)

    val autoCheckBox = new CheckBox
    val xAxisStart = new TextField() {
      promptText = "X-Axis start"
    }
    val yAxisStart = new TextField() {
      promptText = "Y-Axis start"
    }
    val xAxisEnd = new TextField() {
      promptText = "X-Axis end"
    }
    val yAxisEnd = new TextField() {
      promptText = "Y-Axis end"
    }

    def checkValues() {
      def checkTextValue(tf: TextField) : Boolean = {
        try {
          tf.text.value.toDouble
          true
        } catch {
          case e: Exception => false
        }
      }
      applyButton.setDisable(if ((checkTextValue(xAxisStart) && checkTextValue(xAxisEnd) && checkTextValue(yAxisStart) && checkTextValue(yAxisEnd)) || autoCheckBox.selected.value) false else true)
    }

    autoCheckBox.onAction = (_) => checkValues()
    xAxisStart.text.onChange{(_, _, _) => checkValues()}
    yAxisStart.text.onChange{(_, _, _) => checkValues()}
    xAxisEnd.text.onChange{(_, _, _) => checkValues()}
    yAxisEnd.text.onChange{(_, _, _) => checkValues()}

    // Create the layout for the dialog contents
    val grid = new GridPane() {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      add(new Label("X-Start:"), 0, 0)
      add(xAxisStart, 1, 0)
      add(new Label("X-End:"), 0, 1)
      add(xAxisEnd, 1, 1)

      add(new Label("Y-Start:"), 0, 2)
      add(yAxisStart, 1, 2)
      add(new Label("Y-End:"), 0, 3)
      add(yAxisEnd, 1, 3)

      add(new Label("Fit into view"), 0, 4)
      add(autoCheckBox, 1, 4)
    }
    dialog.getDialogPane.setContent(grid)
    dialog.resultConverter = dialogButton => {
      if (dialogButton == applyButtonType) {
        if (autoCheckBox.selected.value) {
          // Return AxisEndpoints with automatic scaling.
          new AxisEndpoints(true)
        } else {
          // Try to return an AxisEndpoints object with custom ranges
          try {
            new AxisEndpoints(false, xAxisStart.text.value.toDouble, xAxisEnd.text.value.toDouble, yAxisStart.text.value.toDouble, yAxisEnd.text.value.toDouble)
          } catch {
            case e: Exception => {
              controller.showError("Invalid parameters. The given start values should be lower than the corresponding end values.")
              new AxisEndpoints(true)
            }
          }
        }
      } else {
        null
      }
    }


    // Show the dialog and wait for the result.
    val result = dialog.showAndWait()
    result match {
      case Some(endpoints: AxisEndpoints) => controller.setAxisEndpoints(endpoints)
      // Handle cancellation
      case _ =>
    }
  }
}
