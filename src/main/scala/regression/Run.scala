package regression

import scalafx.application.JFXApp
import scalafx.scene.layout.VBox
import scalafx.scene.layout.ColumnConstraints
import scalafx.scene.layout.RowConstraints
import scalafx.scene.layout.Background
import scalafx.scene.layout.GridPane
import scalafx.scene.Scene
import scalafx.scene.layout.HBox
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.BackgroundFill
import scalafx.scene.paint.Color
import scalafx.scene.layout.CornerRadii
import scalafx.geometry.Insets
import scalafx.scene.control.Label

object Run extends JFXApp {

  stage = new JFXApp.PrimaryStage {
    title.value = "Regression UI"
    width = 750
    height = 550
  }
  val root = new GridPane
  val scene = new Scene(root)
  stage.scene = scene

  // Placeholders
  val menubarPH     = new HBox
  val tabbarPH      = new HBox
  val sidebarPH     = new VBox
  val coordinatesPH = new Canvas(600, 600)

  val menubarPHLabel = new Label("Menubar")
  val tabbarPHLabel = new Label("Tabbar")
  val sidebarPHLabel = new Label("sidebar")

  menubarPH              .children_=(menubarPHLabel)
  tabbarPH               .children_=(tabbarPHLabel)
  coordinatesPH          .graphicsContext2D.fillText("Coordinates", 10, 100)
  sidebarPH              .children_=(sidebarPHLabel)

  root.add(menubarPH,        0, 0, 2, 1)
  root.add(tabbarPH,         0, 1, 2, 1)
  root.add(sidebarPH,        0, 2, 1, 1)
  root.add(coordinatesPH,    1, 2, 1, 1)

  val column0 = new ColumnConstraints
  val column1 = new ColumnConstraints

  column0.setPercentWidth(25.0)
  column1.setPercentWidth(75.0)

  val row0 = new RowConstraints
  val row1 = new RowConstraints
  val row2 = new RowConstraints

  row0.setPercentHeight(5.0)
  row1.setPercentHeight(5.0)
  row2.setPercentHeight(90.0)

  root.columnConstraints = Array[ColumnConstraints](column0, column1)
  root.rowConstraints    = Array[RowConstraints](row0, row1, row2)

  tabbarPH.setBackground(new Background(Array(new BackgroundFill(Color.Beige, new CornerRadii(0), Insets.Empty)))) //Set sideBox background color
  menubarPH.setBackground(new Background(Array(new BackgroundFill(Color.Cyan, new CornerRadii(0), Insets.Empty)))) //Set sideBox background color
  sidebarPH.setBackground(new Background(Array(new BackgroundFill(Color.Gray, new CornerRadii(0), Insets.Empty)))) //Set sideBox background color
}
