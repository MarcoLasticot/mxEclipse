package org.mxeclipse.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class MxEclipseMenuAction
  implements IObjectActionDelegate
{
  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
  }

  public void run(IAction action)
  {
    Shell shell = new Shell();
    MessageDialog.openInformation(shell, "MxEclipse Plug-in", "Connect to Matrix was executed.");
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
  }
}