package regression.ui

import scalafx.scene.control._
import regression.models.Sheet

class ReTabBar(onClosed: (Int) => Unit, onSelected: (Int) => Unit) {
  val tabpanel = new TabPane

  def refresh(sheets: Seq[Sheet]) = {
    tabpanel.getTabs().setAll()
    for (i <- sheets.indices) {
      val s = sheets(i)
      val tab = new Tab
      tab.text = s.name
      tab.onClosed = (event) => {
        this.onClosed(i)
        tabpanel.getTabs().remove(i)
      }
      tab.onSelectionChanged = (event) => {
        if (tab.isSelected()) onSelected(i)
      }
      tabpanel.getTabs().add(tab)
    }
  }
}
