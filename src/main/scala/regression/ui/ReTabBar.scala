package regression.ui

import scalafx.scene.control._
import regression.models.Sheet
import regression.RegressionController

class ReTabBar() {
  val tabpanel = new TabPane

  def init(controller: RegressionController) = {
    controller.sheets.onChange(
      (source, change) => {
        tabpanel.getTabs().setAll()
        for (s <- source) {
          val tab = new Tab
          tab.text = s.name
          tab.onClosed = (event) => {
            controller.removeSheet(s.id)
          }
          tab.onSelectionChanged = (event) => {
            if (tab.isSelected()) {
              controller.selectSheet(s)
              controller.switchFit(s.fit)
            }
          }
          tabpanel.getTabs().add(tab)
        }
      }
    )
    controller.currentSheet.addListener(
      (_, old, sheet) => {
        if (sheet.nonEmpty){
          tabpanel.selectionModel().select(controller.getSheetIndex(sheet.get))
        }
      }
    )
  }
}
