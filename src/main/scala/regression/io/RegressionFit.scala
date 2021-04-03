package regression.io

import scalafx.scene.paint.Color

abstract class RegressionFit(val color: Color = Color.AliceBlue) {
  def polynomialValue(x: Double) : Double
  def formattedExpression : String
}
