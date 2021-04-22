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

class ReCoordinates extends Pane{
  val xAxis = new NumberAxis()
  val yAxis = new NumberAxis()

  var chart = new LineChart(xAxis, yAxis)
  chart.setLegendVisible(false)

  def refresh(sheet: Option[Sheet], fitOption: Option[RegressionFit]) {
    val series = new XYChart.Series[Number, Number]()
    val trendline = new XYChart.Series[Number, Number]()

    val (r, g, b) = fitOption match {
      case Some(fit) => (fit.color.red * 255, fit.color.green * 255, fit.color.blue * 255)
      case None      => (0, 0, 0)
    }
    if (sheet.nonEmpty) {
      val set = sheet.get.dataset
      chart.XAxis.label_=(set.keysLabel)
      chart.YAxis.label_=(set.valuesLabel)

      // Draw the data points
      for (point <- set.data) {
        val chartPoint = new XYChart.Data[Number, Number](new javafx.scene.chart.XYChart.Data(point._1, point._2))
        val pointNode = new Circle(new javafx.scene.shape.Circle(3.0f))
        pointNode.setStrokeWidth(2.5f)
        pointNode.setStyle(s"-fx-stroke: rgba($r, $g, $b);")
        pointNode.setFill(Color.WHITE)
        chartPoint.setNode(pointNode)
        series.getData().add(chartPoint)
      }
      // Draw the regression trendline
      if (fitOption.nonEmpty) {
        val fit = fitOption.get

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

    series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: transparent;") // Hide data points line that is visible by default
    trendline.getNode().lookup(".chart-series-line").setStyle(s"-fx-stroke: rgba($r, $g, $b, 1.0);")
  }
}
