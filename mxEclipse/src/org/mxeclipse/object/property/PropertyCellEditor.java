package org.mxeclipse.object.property;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class PropertyCellEditor extends TextCellEditor
{
  protected MxObjectProperyTable table;

  public PropertyCellEditor()
  {
  }

  public PropertyCellEditor(Composite parent, MxObjectProperyTable table)
  {
    super(parent);
    this.table = table;
  }

  public PropertyCellEditor(Composite parent, int style)
  {
    super(parent, style);
  }

  protected Control createControl(Composite parent)
  {
    return super.createControl(parent);
  }

  protected Object doGetValue()
  {
    return super.doGetValue();
  }

  protected void doSetFocus()
  {
    super.doSetFocus();
  }

  protected void doSetValue(Object value)
  {
    super.doSetValue(value);
  }
}