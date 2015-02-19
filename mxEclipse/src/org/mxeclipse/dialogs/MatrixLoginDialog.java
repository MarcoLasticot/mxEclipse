package org.mxeclipse.dialogs;

import java.lang.reflect.InvocationTargetException;
import matrix.db.Context;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.matrix.MatrixOperations;
import org.mxeclipse.utils.MxEclipseUtils;

public class MatrixLoginDialog extends Dialog
{
  private Label hostLabel;
  private Text hostText;
  private Label userLabel;
  private Label passLabel;
  private Text userText;
  private Text passText;
  private Button chkBox;
  private static IPreferenceStore store = MxEclipsePlugin.getDefault().getPreferenceStore();

  public MatrixLoginDialog(Shell parentShell)
  {
    super(parentShell);
  }

  public MatrixLoginDialog(IShellProvider parentShell) {
    super(parentShell);
  }

  protected Control createDialogArea(Composite parent)
  {
    Composite comp = (Composite)super.createDialogArea(parent);
    GridLayout layout = (GridLayout)comp.getLayout();
    layout.numColumns = 2;
    layout.marginLeft = 5;
    layout.marginRight = 5;
    layout.marginTop = 5;
    layout.marginBottom = 5;

    GridData labelData = new GridData(4, 
      4, 
      true, 
      true);
    labelData.widthHint = 75;
    GridData fieldData = new GridData(4, 
      4, 
      true, 
      true);
    fieldData.horizontalIndent = 5;
    fieldData.widthHint = 150;
    GridData chkBoxData = new GridData(4, 
      4, 
      true, 
      true);
    chkBoxData.horizontalSpan = 2;

    this.hostLabel = 
      new Label(comp, 
      16384);
    this.hostLabel.setText(MxEclipseUtils.getString("label.MatrixHost"));
    this.hostLabel.setLayoutData(labelData);

    this.hostText = 
      new Text(comp, 
      2048);
    this.hostText.setLayoutData(fieldData);
    this.hostText.setText(store.getString("MatrixHost"));
    this.hostText.setFocus();

    this.userLabel = 
      new Label(comp, 
      16384);
    this.userLabel.setText(MxEclipseUtils.getString("label.MatrixUser"));
    this.userLabel.setLayoutData(labelData);

    this.userText = 
      new Text(comp, 
      2048);
    this.userText.setLayoutData(fieldData);
    this.userText.setText(store.getString("MatrixUser"));

    this.passLabel = 
      new Label(comp, 
      16384);
    this.passLabel.setText(MxEclipseUtils.getString("label.MatrixPassword"));
    this.passLabel.setLayoutData(labelData);

    this.passText = 
      new Text(comp, 
      4196352);
    this.passText.setLayoutData(fieldData);
    this.passText.setText(store.getString("MatrixUserPwd"));

    this.chkBox = 
      new Button(comp, 
      32);
    this.chkBox.setText(MxEclipseUtils.getString("checkbox.SaveHostDetails"));
    this.chkBox.setLayoutData(chkBoxData);

    return comp;
  }

  protected void okPressed()
  {
    ProgressMonitorDialog pmd = null;
    try {
      MatrixOperations mxops = new MatrixOperations();
      mxops.setHost(this.hostText.getText());
      mxops.setUser(this.userText.getText());
      mxops.setPassword(this.passText.getText());
      boolean saveData = this.chkBox.getSelection();

      pmd = new ProgressMonitorDialog(getShell());
      pmd.open();
      pmd.run(true, true, mxops);

      Context context = MxEclipsePlugin.getDefault().getContext();
      if (context.isConnected()) {
        MessageDialog.openInformation(getShell(), 
          MxEclipseUtils.getString("message.ConnectionSuccessful"), 
          MxEclipseUtils.getString("message.Connected"));
        pmd.close();

        if (saveData) {
          store.setValue("MatrixHost", this.hostText.getText());
          store.setValue("MatrixUser", this.userText.getText());
          store.setValue("MatrixUserPwd", this.passText.getText());
          store.setValue("MatrixDefaultLogin", saveData);
        }
        MxEclipsePlugin.getDefault().setHost(this.hostText.getText());
        MxEclipsePlugin.getDefault().setUser(this.userText.getText());

        super.okPressed();
      }
    } catch (InvocationTargetException e) {
      if (pmd != null) {
        pmd.close();
      }
      String message = e.getCause().getMessage();
      Status status = new Status(4, 
        MxEclipseUtils.getString("pluginName"), 
        0, 
        message, 
        e);
      ErrorDialog.openError(getShell(), 
        MxEclipseUtils.getString("message.error.ConnectionFailed"), 
        MxEclipseUtils.getString("message.error.UnableToConnect"), 
        status);
    } catch (InterruptedException e) {
      if (pmd != null) {
        pmd.close();
      }
      String message = e.getCause().getMessage();
      Status status = new Status(4, 
        MxEclipseUtils.getString("pluginName"), 
        0, 
        message, 
        e);
      ErrorDialog.openError(getShell(), 
        MxEclipseUtils.getString("message.error.ConnectionFailed"), 
        MxEclipseUtils.getString("message.error.UnableToConnect"), 
        status);
    }
  }

  protected void cancelPressed()
  {
    super.cancelPressed();
  }

  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(MxEclipseUtils.getString("label.ConnectToMatrix"));
  }

  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, 0, MxEclipseUtils.getString("button.Connect"), true);
    createButton(parent, 1, IDialogConstants.CANCEL_LABEL, false);
  }
}