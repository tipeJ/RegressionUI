package regression.io

import scalafx.scene.paint.Color

abstract class RegressionFit(val color: Color = Color.DarkBlue) {
  def polynomialValue(x: Double) : Double
  def formattedExpression : String

  protected def coefFormat(coef: Double) : String = if (coef < 0) s"- ${coef.toString.substring(1)}" else s"+ $coef"
}
