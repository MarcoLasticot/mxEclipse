package org.mxeclipse.preferences;

import matrix.db.Context;
import matrix.util.MatrixException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.utils.MxEclipseUtils;
import org.mxeclipse.views.MxEclipseObjectView;

public class MxEclipsePreference extends FieldEditorPreferencePage
  implements IWorkbenchPreferencePage
{
  Label lblChangedContext;
  private String oldHostName;
  private String newHostName;
  private String oldUsername;
  private String newUsername;
  private String oldPassword;
  private String newPassword;

  public MxEclipsePreference()
  {
    super(1);
    setPreferenceStore(MxEclipsePlugin.getDefault().getPreferenceStore());
    setDescription("For access over HTTP, use http://<server>:<port>/<context> as Matrix Host. \nSample: http://localhost/ematrix");
    this.oldHostName = getPreferenceStore().getString("MatrixHost");
    this.oldUsername = getPreferenceStore().getString("MatrixUser");
    this.oldPassword = getPreferenceStore().getString("MatrixUserPwd");
  }

  public void createFieldEditors()
  {
    addField(
      new StringFieldEditor("MatrixHost", 
      "Matrix Host:", 
      getFieldEditorParent()));

    addField(
      new StringFieldEditor("MatrixUser", 
      "Matrix User:", 
      getFieldEditorParent()));
    addField(
      new StringFieldEditor("MatrixUserPwd", 
      "Matrix User Password:", 
      getFieldEditorParent()));

    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalSpan = 2;
    gridData.horizontalAlignment = 4;
    this.lblChangedContext = new Label(getFieldEditorParent(), 0);
    this.lblChangedContext.setForeground(Display.getCurrent().getSystemColor(3));
    this.lblChangedContext.setText("Host name, username or password changed. Old connection will be closed when you click OK!");
    this.lblChangedContext.setVisible(false);
    this.lblChangedContext.setLayoutData(gridData);

    addField(
      new BooleanFieldEditor("MatrixDefaultLogin", 
      "Always login with the specified user credentials", 
      getFieldEditorParent()));
    addField(
      new BooleanFieldEditor("AutomaticSilentLogin", 
      "Automatically log in without any dialogs", 
      getFieldEditorParent()));

    Button cmdClearCache = new Button(getFieldEditorParent(), 0);
    cmdClearCache.setText("Clear Cache");
    cmdClearCache.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent arg0) {
      }

      public void widgetSelected(SelectionEvent arg0) {
        MxEclipseUtils.clearCache();
        MessageDialog.openInformation(MxEclipsePreference.this.getFieldEditorParent().getShell(), "Clear Cache", "MxEclipse cache has been cleared!");
      }
    });
  }

  public void init(IWorkbench workbench)
  {
  }

  protected IPreferenceStore doGetPreferenceStore()
  {
    return super.doGetPreferenceStore();
  }

  public boolean performOk()
  {
    boolean retVal = super.performOk();
    if (((this.newHostName != null) && (!this.newHostName.equals(this.oldHostName))) || ((this.newUsername != null) && (!this.newUsername.equals(this.oldUsername))) || (
      (this.newPassword != null) && (!this.newPassword.equals(this.oldPassword)))) {
      Context ctx = MxEclipsePlugin.getDefault().getContext();
      if ((ctx != null) && (ctx.isConnected())) {
        try {
          ctx.closeContext();
          ctx.disconnect();
          MxEclipsePlugin.getDefault().setHost(null);
          MxEclipsePlugin.getDefault().setUser(null);
          MxEclipseObjectView.refreshViewStatusBar(null);
        } catch (MatrixException e) {
          String message = e.getMessage();
          Status status = new Status(4, "MxEclipse", 0, message, e);
          ErrorDialog.openError(null, 
            MxEclipseUtils.getString("MxEclipseAction.error.header.DisconnectFailed"), 
            MxEclipseUtils.getString("MxEclipseAction.error.message.DisconnectFailed") + 
            ctx.getUser(), 
            status);
        } finally {
          ctx = null;
          MxEclipsePlugin.getDefault().setContext(ctx);
        }
      }
    }
    return retVal;
  }

  public void propertyChange(PropertyChangeEvent event)
  {
    super.propertyChange(event);
    if (((FieldEditor)event.getSource()).getPreferenceName().equals("MatrixHost"))
      this.newHostName = ((StringFieldEditor)event.getSource()).getStringValue();
    else if (((FieldEditor)event.getSource()).getPreferenceName().equals("MatrixUser"))
      this.newUsername = ((StringFieldEditor)event.getSource()).getStringValue();
    else if (((FieldEditor)event.getSource()).getPreferenceName().equals("MatrixUserPwd")) {
      this.newPassword = ((StringFieldEditor)event.getSource()).getStringValue();
    }

    this.lblChangedContext.setVisible(((this.newHostName != null) && (!this.newHostName.equals(this.oldHostName))) || ((this.newUsername != null) && (!this.newUsername.equals(this.oldUsername))) || (
      (this.newPassword != null) && (!this.newPassword.equals(this.oldPassword))));
    getFieldEditorParent().redraw();
  }
}