package org.mxeclipse.object.property;

import java.util.List;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

public class MxObjectPropertyCellModifier
  implements ICellModifier
{
  private MxObjectProperyTable tableViewerExample;
  private String[] columnNames;

  public MxObjectPropertyCellModifier(MxObjectProperyTable tableViewerExample)
  {
    this.tableViewerExample = tableViewerExample;
  }

  public boolean canModify(Object element, String property)
  {
    return true;
  }

  public Object getValue(Object element, String property)
  {
    int columnIndex = this.tableViewerExample.getColumnNames().indexOf(property);

    Object result = null;
    MxObjectProperty task = (MxObjectProperty)element;

    switch (columnIndex) {
    case 0:
      result = task.getName();
      break;
    case 1:
      result = task.getValue();
      break;
    default:
      result = "";
    }
    return result;
  }

  public void modify(Object element, String property, Object value)
  {
    int columnIndex = this.tableViewerExample.getColumnNames().indexOf(property);

    TableItem item = (TableItem)element;
    MxObjectProperty mxProperty = (MxObjectProperty)item.getData();

    boolean bModified = false;

    switch (columnIndex)
    {
    case 0:
      break;
    case 1:
      String valueString = ((String)value).trim();
      mxProperty.setValue(valueString);
      bModified = mxProperty.isModified();
    }

    if (bModified)
      this.tableViewerExample.getProperties().propertyChanged(mxProperty);
  }
}