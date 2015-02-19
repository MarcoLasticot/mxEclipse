package org.mxeclipse.configure.table;

import org.mxeclipse.model.MxTableColumn;

public abstract interface IMxTableColumnViewer
{
  public abstract void addTask(MxTableColumn paramMxTableColumn);

  public abstract void removeTask(MxTableColumn paramMxTableColumn);

  public abstract void updateProperty(MxTableColumn paramMxTableColumn);
}