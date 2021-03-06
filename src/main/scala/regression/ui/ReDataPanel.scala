package regression.ui

import scalafx.scene.control._
import scalafx.beans.property._
import scalafx.beans.value._
import regression.models.Sheet
import scalafx.collections.ObservableBuffer
import regression.RegressionController

// Helper class for TableView.
class DataCell(key_ : Double, value_ : Double) {
  val k = new ObjectProperty[Double](this, "k", key_)
  val v = new ObjectProperty[Double](this, "v", value_)
}

// An excel-like UI component for DataCell pairs.
class ReDataPanel() {
  val table = new TableView[DataCell]()

  table.columnResizePolicy = TableView.ConstrainedResizePolicy

  def init(controller: RegressionController) = {
    controller.currentSheet.addListener(
      (_, old, sheet) => {
        val obs = ObservableBuffer[DataCell]()
        var keysLabel = "X"
        var valuesLabel = "Y"
        if (sheet.nonEmpty) {
          sheet.get.dataset.data.toList.foreach(t => obs.addOne(new DataCell(t._1, t._2)))
          keysLabel = sheet.get.dataset.keysLabel
          valuesLabel = sheet.get.dataset.valuesLabel
        }
        val keyColumn = new TableColumn[DataCell, String]{
          text = keysLabel
          cellValueFactory = features => ReadOnlyDoubleWrapper.apply(features.value.k.value).asInstanceOf[ObservableValue[String, String]]
        }
        val valueColumn = new TableColumn[DataCell, String]{
          text = valuesLabel
          cellValueFactory = features => ReadOnlyDoubleWrapper.apply(features.value.v.value).asInstanceOf[ObservableValue[String, String]]
        }
        keyColumn.setSortType(TableColumn.SortType.Ascending)
        table.setItems(obs)
        table.columns.setAll(keyColumn, valueColumn)
        table.getSortOrder.add(keyColumn)
      }
    )
  }
}
