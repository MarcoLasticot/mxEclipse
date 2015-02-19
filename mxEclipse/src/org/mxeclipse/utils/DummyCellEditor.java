package org.mxeclipse.utils;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DummyCellEditor extends CellEditor
{
  public DummyCellEditor()
  {
  }

  public DummyCellEditor(Composite parent)
  {
    super(parent);
  }

  public DummyCellEditor(Composite parent, int style) {
    super(parent, style);
  }

  protected Control createControl(Composite parent)
  {
    return null;
  }

  protected Object doGetValue()
  {
    return null;
  }

  protected void doSetFocus()
  {
  }

  protected void doSetValue(Object value)
  {
  }
}