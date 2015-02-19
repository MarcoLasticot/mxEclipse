package org.mxeclipse.business.tree;

import matrix.db.Context;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTableColumn;
import org.mxeclipse.model.MxTreeBusiness;

public class MxBusinessSorter extends ViewerSorter
{
  private String columnName;

  public MxBusinessSorter(String columnName)
  {
    this.columnName = columnName;
  }

  public int compare(Viewer viewer, Object e1, Object e2)
  {
    MxTreeBusiness do1 = (MxTreeBusiness)e1;
    MxTreeBusiness do2 = (MxTreeBusiness)e2;

    if (this.columnName.equals(MxTableColumn.BASIC_TYPE)) {
      if (do1.getType().compareToIgnoreCase(do2.getType()) != 0) {
        return do1.getType().compareToIgnoreCase(do2.getType());
      }
      return do1.getName().compareTo(do2.getName());
    }
    if (this.columnName.equals(MxTableColumn.BASIC_NAME)) {
      return do1.getName().compareToIgnoreCase(do2.getName());
    }

    Context localContext = MxEclipsePlugin.getDefault().getContext();

    return -1;
  }
}