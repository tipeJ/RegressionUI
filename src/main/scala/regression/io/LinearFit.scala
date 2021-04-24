package regression.io

import scalafx.scene.paint.Color

class LinearFit(private val data : Seq[(Double, Double)], c: Color = Color.AliceBlue) extends RegressionFit(c) {

  private def leastSquaresFit(data: Seq[(Double, Double)]) : (Double, Double) = {
    val n = data.length
    val vX = data.map(_._1).reduce((p, p2) => p + p2)/n // AVG of x-values
    val vY = data.map(_._2).reduce((p, p2) => p + p2)/n // AVG of y-values
    var sXX = 0.0 // Sum of x-AVG(x) squared
    var sXY = 0.0 // Sum of (x-AVG(x))(y-AVG(y))
    for (p <- data) {
      sXY += (p._1 - vX) * (p._2 - vY)
      sXX += math.pow(p._1 - vX, 2)
    }
    val m = sXY/sXX
    val b = vY - m*vX
    (m, b)
  }

  val (coef, yintercept) = leastSquaresFit(data)
  override def polynomialValue(x: Double): Double = coef * x + yintercept
  override def formattedExpression: String = coef + s"x ${coefFormat(yintercept)}"
}
