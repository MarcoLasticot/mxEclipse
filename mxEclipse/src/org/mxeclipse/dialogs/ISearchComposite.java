package org.mxeclipse.dialogs;

import java.util.List;
import org.mxeclipse.model.MxObjectSearchCriteria;
import org.mxeclipse.model.MxTreeDomainObject;

public abstract interface ISearchComposite
{
  public abstract List<MxTreeDomainObject> getTreeObjectList();

  public abstract void okPressed(boolean paramBoolean);

  public abstract void setSearchCriteria(MxObjectSearchCriteria paramMxObjectSearchCriteria);

  public abstract MxObjectSearchCriteria getSearchCriteria();

  public abstract void fillSearchCriteria(boolean paramBoolean);
}