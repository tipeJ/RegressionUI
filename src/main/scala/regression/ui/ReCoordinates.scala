package regression.ui

import regression.models.Sheet
import scalafx.scene.layout.Pane
import scalafx.scene.chart._
import scalafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle
import regression.io._
import javafx.scene.paint.Paint

class ReCoordinates extends Pane{
  val xAxis = new NumberAxis()
  val yAxis = new NumberAxis()

  val chart = new LineChart(xAxis, yAxis)

  def refresh(set: Dataset, fit: RegressionFit) {
    val series = new XYChart.Series[Number, Number]()
    val seriesY = new XYChart.Series[Number, Number]()

    chart.data.get().setAll(series, seriesY)
    series.getNode().setStyle("-fx-stroke: transparent;")

    for (point <- set.data) {
      val chartPoint = new XYChart.Data[Number, Number](new javafx.scene.chart.XYChart.Data(point._1, point._2))
      series.getData().add(chartPoint)
    }
    val xMax = set.data.keys.max
    var currentX = 0.0
    while (currentX <= xMax) {
      val y = fit.polynomialValue(currentX)
      val chartPoint = new XYChart.Data[Number, Number](new javafx.scene.chart.XYChart.Data(currentX, y))
      val pointNode = new Rectangle(2.0, 2.0)
      pointNode.setFill(fit.color)
      chartPoint.setNode(new Rectangle(2.0, 2.0))
      series.getData().add(chartPoint)
      currentX += 0.03
    }

  }
}
