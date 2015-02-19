package org.mxeclipse.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mxeclipse.model.MxFilter;

public class FilterObjectsDialog extends Dialog
{
  FilterObjectsComposite inner;
  MxFilter filter;

  public FilterObjectsDialog(Shell parent, MxFilter initialFilter)
  {
    super(parent);
    this.filter = initialFilter;
    setShellStyle(getShellStyle() | 0x10);
  }

  public static void main(String[] args)
  {
    Display display = Display.getDefault();
    FilterObjectsDialog thisClass = new FilterObjectsDialog(null, null);
    thisClass.createDialogArea(null);
    thisClass.open();

    while (!thisClass.getShell().isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }

  protected void cancelPressed()
  {
    super.cancelPressed();
  }

  protected void okPressed()
  {
    this.inner.okPressed();
    super.okPressed();
  }

  public MxFilter getFilter() {
    return this.inner.getFilter();
  }

  protected Control createDialogArea(Composite parent)
  {
    Composite comp = (Composite)super.createDialogArea(parent);

    GridLayout layout = (GridLayout)comp.getLayout();
    layout.numColumns = 1;
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = 4;
    gridData.verticalAlignment = 4;
    this.inner = new FilterObjectsComposite(comp, 0, this.filter);
    this.inner.setLayoutData(gridData);
    return this.inner;
  }

  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText("Filter Objects");
  }
}