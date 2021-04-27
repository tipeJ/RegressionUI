package regression.ui

import regression.models.Sheet
import scalafx.scene.layout.Pane
import scalafx.scene.chart._
import scalafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle
import regression.io._
import javafx.scene.paint.Paint
import scalafx.scene.shape.Circle
import javafx.scene.paint.Color
import regression.RegressionController
import scalafx.scene.control._

class ReCoordinates extends Pane{
  val xAxis = new NumberAxis()
  val yAxis = new NumberAxis()

  var chart = new LineChart(xAxis, yAxis) {
    val series = new XYChart.Series[Number, Number]()
    val trendline = new XYChart.Series[Number, Number]()

    data.get().setAll(series, trendline)
  }
  chart.setLegendVisible(false)

  def init(controller: RegressionController) {
    controller.axisEndPoints.addListener(
      (_, old, points) => {
        if (!points.auto) {
          xAxis.setAutoRanging(false)
          xAxis.setLowerBound(points.xStart)
          xAxis.setUpperBound(points.xEnd)
          xAxis.setTickUnit(points.getXTickUnit)

          yAxis.setAutoRanging(false)
          yAxis.setLowerBound(points.yStart)
          yAxis.setUpperBound(points.yEnd)
          yAxis.setTickUnit(points.getYTickUnit)
        } else {
          xAxis.setAutoRanging(true)
          yAxis.setAutoRanging(true)
        }
      }
    )
    // Listen for changes in the selected fit.
    controller.currentFit.addListener(
      (obs, old, newFit) => {
        val (r, g, b) = newFit match {
          case Some(fit) => (fit.color.red * 255, fit.color.green * 255, fit.color.blue * 255)
          case None      => (0, 0, 0)
        }
        var series = new XYChart.Series[Number, Number]()
        val trendline = new XYChart.Series[Number, Number]()

        val sheet = controller.currentSheet.get
        if (sheet.nonEmpty) {
          val set = sheet.get.dataset
          chart.XAxis.label_=(set.keysLabel)
          chart.YAxis.label_=(set.valuesLabel)
          series = redrawSeriesPoints(set, r, g, b)

          // Draw the regression trendline
          if (newFit.nonEmpty) {
            val fit = newFit.get

            val xMax = set.data.keys.max
            val drawStep = xMax / 125 // Step between two data dots
            var currentX = math.min(0.0, set.data.keys.min)
            while (currentX <= xMax) {
              val y = fit.polynomialValue(currentX) // Calculate the Y-value of the given polynomial
              val chartPoint = new XYChart.Data[Number, Number](new javafx.scene.chart.XYChart.Data(currentX, y))
              val pointNode = new Rectangle(0.0, 0.0)
              chartPoint.setNode(pointNode)
              trendline.getData().add(chartPoint)
              currentX += drawStep
            }
          }
        }
        chart.data.get().setAll(series, trendline)

        trendline.getNode().lookup(".chart-series-line").setStyle(s"-fx-stroke: rgba($r, $g, $b, 1.0);")
        series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: transparent;") // Hide data points line that is visible by default
      })

    // Listen for changes in the selected sheet.
    controller.currentSheet.addListener(
      (obs, old, newSheet) => {
        var series = new XYChart.Series[Number, Number]()
        val (r, g, b) = controller.currentFit.get match {
          case Some(fit) => (fit.color.red * 255, fit.color.green * 255, fit.color.blue * 255)
          case None      => (0, 0, 0)
        }
        if (newSheet.nonEmpty) {
          val set = newSheet.get.dataset
          // Draw the data points
          series = redrawSeriesPoints(set, r, g, b)
        }
        chart.data.get().set(0, series)

        series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: transparent;") // Hide data points line that is visible by default
      }
    )
  }

  private def redrawSeriesPoints(set: Dataset, r: AnyVal, g: AnyVal, b: AnyVal) : XYChart.Series[Number, Number] = {
    val series = new XYChart.Series[Number, Number]()
    for (point <- set.data) {
      val chartPoint = new XYChart.Data[Number, Number](new javafx.scene.chart.XYChart.Data(point._1, point._2))
      val pointNode = new Circle(new javafx.scene.shape.Circle(3.0f))
      pointNode.setStrokeWidth(2.5f)
      pointNode.setStyle(s"-fx-stroke: rgba($r, $g, $b);")
      pointNode.setFill(Color.WHITE)
      pointNode.onMouseClicked = (_) => openDataPointDialog(point, set.keysLabel, set.valuesLabel)
      chartPoint.setNode(pointNode)
      series.getData().add(chartPoint)
    }
    series
  }
  private def openDataPointDialog(point: (Double, Double), keysLabel: String, valuesLabel: String) = {
    val alert = new Alert(Alert.AlertType.None)
    alert.title_=("Point")
    alert.setContentText(s"${point._1} $keysLabel \n${point._2} $valuesLabel")
    alert.buttonTypes.addOne(ButtonType.Close)
    alert.show()
  }

}

case class AxisEndpoints (auto: Boolean = false, xStart: Double = 0, xEnd: Double = 1, yStart: Double = 0, yEnd: Double = 1) {
  if (!auto && (xStart >= xEnd || yStart >= yEnd)) throw new IllegalArgumentException("Lower bounds of axes must not be greater than the upper bounds")

  def getXTickUnit : Double = (xEnd - xStart) / 10.0
  def getYTickUnit : Double = (yEnd - yStart) / 10.0
}
