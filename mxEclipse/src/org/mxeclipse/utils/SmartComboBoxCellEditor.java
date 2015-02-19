package org.mxeclipse.utils;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;

public class SmartComboBoxCellEditor extends ComboBoxCellEditor
{
  public SmartComboBoxCellEditor()
  {
  }

  public SmartComboBoxCellEditor(Composite parent, String[] items)
  {
    super(parent, items);
  }

  public SmartComboBoxCellEditor(Composite parent, String[] items, int style)
  {
    super(parent, items, style);
  }

  protected void keyReleaseOccured(KeyEvent keyEvent)
  {
    super.keyReleaseOccured(keyEvent);
  }
}