package regression.ui

import scalafx.scene.control._
import regression.models.Sheet

class ReTabBar(onClosed: (Int) => Unit, onSelected: (Int) => Unit) {
  val tabpanel = new TabPane

  def refresh(sheets: Seq[Sheet], selectedIndex: Int) = {
    tabpanel.getTabs().setAll()
    for (s <- sheets) {
      val tab = new Tab
      tab.text = s.name
      tab.onClosed = (event) => {
        this.onClosed(s.id)
      }
      tab.onSelectionChanged = (event) => {
        if (tab.isSelected()) onSelected(s.id)
      }
      tabpanel.getTabs().add(tab)
    }
    if (selectedIndex >= 0) tabpanel.selectionModel().select(selectedIndex)
  }
}
