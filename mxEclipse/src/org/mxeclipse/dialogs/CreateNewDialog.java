package org.mxeclipse.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mxeclipse.model.MxTreeDomainObject;

public class CreateNewDialog extends Dialog
{
  CreateNewComposite inner;
  public static String DIRECTION_FROM = "→";
  public static String DIRECTION_TO = "<";

  public CreateNewDialog(Shell parent)
  {
    super(parent);
    setShellStyle(getShellStyle() | 0x10);
  }

  public static void main(String[] args)
  {
    Display display = Display.getDefault();
    CreateNewDialog thisClass = new CreateNewDialog(null);
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
    if (this.inner.okPressed())
      super.okPressed();
  }

  public MxTreeDomainObject getNewObject()
  {
    return this.inner.getNewObject();
  }

  protected Control createDialogArea(Composite parent)
  {
    Composite comp = (Composite)super.createDialogArea(parent);
    GridLayout layout = (GridLayout)comp.getLayout();
    layout.numColumns = 1;

    Composite comRel = new Composite(comp, 0);
    GridLayout layRel = new GridLayout();
    layRel.numColumns = 3;
    comRel.setLayout(layRel);

    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = 4;
    this.inner = new CreateNewComposite(comp, 0);
    this.inner.setLayoutData(gridData);

    return comp;
  }

  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText("Create New Object");
    newShell.setMinimumSize(400, 270);
  }
}