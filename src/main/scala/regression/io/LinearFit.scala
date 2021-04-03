package regression.io

import scalafx.scene.paint.Color

class LinearFit(private val data : Seq[(Double, Double)], c: Color = Color.AliceBlue) extends RegressionFit() {

  private def leastSquaresFit(data: Seq[(Double, Double)]) : (Double, Double) = {
    val n = data.length
    var vX = data.map(_._1).reduce((p, p2) => p + p2)/n
    var vY = data.map(_._2).reduce((p, p2) => p + p2)/n
    var sXX = 0.0
    var sXY = 0.0
    for (p <- data) {
      sXY += (p._1 - vX) * (p._2 - vY)
      sXX += math.pow(p._1 - vX, 2)
    }
    var m = sXY/sXX
    var b = vY - m*vX
    (m, b)
  }

  val (coef, yintercept) = leastSquaresFit(data)
  override def polynomialValue(x: Double): Double = coef * x + yintercept
  override def formattedExpression: String = coef + s"x + $yintercept"
}
