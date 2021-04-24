package regression.io

import scalafx.scene.paint.Color

class QuadraticFit(private val data : Seq[(Double, Double)], c: Color = Color.AliceBlue) extends RegressionFit(c) {

  /// Calculate the coefficients for the Quadratic Fit with the Least Squares method (https://en.wikipedia.org/wiki/Least_squares)
  private def quadraticFit() : (Double, Double, Double) = {
    val Sx        = data.map(_._1).sum // Sum of x-values from the data map
    val Sy        = data.map(_._2).sum // Sum of y-values from the data map
    val Sx4       = data.map(d => math.pow(d._1, 4)).sum // Sum of x^4
    val Sx3       = data.map(d => math.pow(d._1, 3)).sum // Sum of x^3
    val Sx2       = data.map(d => math.pow(d._1, 2)).sum // Sum of x^2
    val Sxy       = data.map(d => d._1 * d._2).sum       // Sum of x
    val Sx2y      = data.map(d => math.pow(d._1, 2) * d._2).sum // Sum of x^2y

    val n = data.size // Length of the data

    // First coefficient
    val coef1 = (Sx2y * (Sx2 * n - Sx * Sx) - Sxy * (Sx3 * n - Sx * Sx2) + Sy * (Sx3 * Sx - Sx2 * Sx2)) / (Sx4 * (Sx2 * n - Sx * Sx) - Sx3 * (Sx3 * n - Sx * Sx2) + Sx2 * (Sx3 * Sx - Sx2 * Sx2))

    // Second coefficient
    val coef2 = (Sx4 * (Sxy * n - Sy * Sx) - Sx3 * (Sx2y * n - Sy * Sx2) + Sx2 * (Sx2y * Sx - Sxy * Sx2)) / (Sx4 * (Sx2 * n - Sx * Sx) - Sx3 * (Sx3 * n - Sx * Sx2) + Sx2 * (Sx3 * Sx - Sx2 * Sx2))

    // Third coefficient
    val coef3 = (Sx4 * (Sx2 * Sy - Sx * Sxy) - Sx3 * (Sx3 * Sy - Sx * Sx2y) + Sx2 * (Sx3 * Sxy - Sx2 * Sx2y)) / (Sx4 * (Sx2 * n - Sx * Sx) - Sx3 * (Sx3 * n - Sx * Sx2) + Sx2 * (Sx3 * Sx - Sx2 * Sx2))

    (coef1, coef2, coef3)
  }


  val (coef1, coef2, coef3) = quadraticFit()
  override def polynomialValue(x: Double): Double = (coef1 * x * x) + (coef2 * x) + coef3
  override def formattedExpression: String = coef1 + s"x^2 ${coefFormat(coef2)}x ${coefFormat(coef3)}"

}
