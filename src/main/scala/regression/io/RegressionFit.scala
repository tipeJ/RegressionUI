package regression.io

import scalafx.scene.paint.Color

// Parent class of different polynomial fits.
abstract class RegressionFit(val color: Color = Color.DarkBlue) {
  // Calculate the given x value for the polynomial y-value of this fit
  def polynomialValue(x: Double) : Double

  // Format this expression to a readable format (forex. -2x+3)
  def formattedExpression : String

  protected def coefFormat(coef: Double) : String = if (coef < 0) s"- ${coef.toString.substring(1)}" else s"+ $coef"
}
