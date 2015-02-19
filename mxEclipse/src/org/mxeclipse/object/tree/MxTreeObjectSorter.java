package org.mxeclipse.object.tree;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.logging.Logger;
import matrix.db.Context;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTableColumn;
import org.mxeclipse.model.MxTreeDomainObject;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeObjectSorter extends ViewerSorter
{
  private String columnName;

  public MxTreeObjectSorter(String columnName)
  {
    this.columnName = columnName;
  }

  public int compare(Viewer viewer, Object e1, Object e2)
  {
    MxTreeDomainObject do1 = (MxTreeDomainObject)e1;
    MxTreeDomainObject do2 = (MxTreeDomainObject)e2;

    if (this.columnName.equals(MxTableColumn.BASIC_TYPE))
      return do1.getType().compareToIgnoreCase(do2.getType());
    if (this.columnName.equals(MxTableColumn.BASIC_NAME))
      return do1.getName().compareToIgnoreCase(do2.getName());
    if (this.columnName.equals(MxTableColumn.BASIC_REVISION))
      return do1.getRevision().compareToIgnoreCase(do2.getRevision());
    if (this.columnName.equals(MxTableColumn.BASIC_RELATIONSHIP)) {
      if ((do1.getRelFromName() == null) || (do2.getRelToName() == null)) return -1;
      boolean from1 = do1.getRelFromName().equals(do1.getName());
      boolean from2 = do2.getRelFromName().equals(do2.getName());
      if ((from1) && (!from2))
        return 1;
      if ((!from1) && (from2)) {
        return -1;
      }
      return do1.getRelType().compareToIgnoreCase(do2.getRelType());
    }
    try
    {
      Context context = MxEclipsePlugin.getDefault().getContext();
      if (this.columnName.equals(MxTableColumn.BASIC_STATE)) {
        return do1.getDomainObject().getInfo(context, "current").compareToIgnoreCase(do2.getDomainObject().getInfo(context, "current"));
      }
    }
    catch (FrameworkException e)
    {
      MxEclipseLogger.getLogger().warning("Error in sorter, column " + this.columnName + ". Cause: " + e.getMessage());
    }

    return -1;
  }
}