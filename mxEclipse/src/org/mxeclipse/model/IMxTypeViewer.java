package org.mxeclipse.model;

public abstract interface IMxTypeViewer
{
  public abstract void addType(MxTreeType paramMxTreeType);

  public abstract void removeType(MxTreeType paramMxTreeType);

  public abstract void updateType(MxTreeType paramMxTreeType);
}