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

/// Class for regression menubar.
class ReMbar(val stage: Stage, loadFileCallback: (File) => Unit, switchFit: (String, Color) => Unit, refreshEndpoints: (AxisEndpoints) => Unit) {

  val node = new HBox

  private var endpointsOptionsVisible = false

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
        val alert = new Alert(Alert.AlertType.None)
        alert.title_=("Equation")
        // alert.setContentText(fit.get.formattedExpression)
        val eqText = new TextArea(fit.get.formattedExpression)
        eqText.editable_=(false)
        eqText.wrapText_=(true)
        alert.getDialogPane().setContent(eqText)
        alert.buttonTypes.addOne(ButtonType.Close)
        alert.show()
      }
      colorPicker.value_=(fit.get.color)
      colorPicker.onAction = (event) => this.switchFit(fitComboBox.value.value, new Color(colorPicker.getValue))
    }

    // Button for changing axis endpoints.
    val axisEndpointsButton = new Button("Axis endpoints")
    axisEndpointsButton.onAction = (event) => {
      // Show the dialog for axis endpoints.
      val dialog = new Dialog[Option[AxisEndpoints]]() {
        initOwner(stage)
        title = "Axis Endpoints"
        headerText = "Set Axis Endpoints"
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

      autoCheckBox.onAction = (ev) => checkValues()
      xAxisStart.text.onChange{(_, _, _) => checkValues()}
      yAxisStart.text.onChange{(_, _, _) => checkValues()}
      xAxisEnd.text.onChange{(_, _, _) => checkValues()}
      yAxisEnd.text.onChange{(_, _, _) => checkValues()}

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
            Some(new AxisEndpoints(true))
          } else {
            try {
              // Try to return an AxisEndpoints object with custom ranges. Return None if Exception met (Either double conversion error or an invalid parameters error).
              Some(new AxisEndpoints(false, xAxisStart.text.value.toDouble, xAxisEnd.text.value.toDouble, yAxisStart.text.value.toDouble, yAxisEnd.text.value.toDouble))
            } catch {
              case e: Exception => None
            }
          }
        } else {
          null
        }
      }

      val result = dialog.showAndWait()

      // Show the dialog and wait for the result.
      result match {
        case Some(op: Option[AxisEndpoints]) => op match {
          case Some(endpoints: AxisEndpoints) => refreshEndpoints(endpoints)
          case None => {
            val alert = new Alert(Alert.AlertType.Error)
            alert.title_=("Error")
            alert.setContentText("Invalid parameters. The given start values should be lower than the corresponding end values.")
            alert.show()
          }
        }
        case _ =>
      }
    }

    val nSeq = Seq[Node](loadDataButton, fitComboBox, fitInfoButton, colorPicker, axisEndpointsButton)
    node.children_=(nSeq)
  }
}
