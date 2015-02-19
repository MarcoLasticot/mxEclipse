package org.mxeclipse.preferences;

import matrix.util.MatrixException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.utils.MxEclipseUtils;

public class MxObjectPreference extends FieldEditorPreferencePage
  implements IWorkbenchPreferencePage
{
  private boolean triggerOffChanged;

  public MxObjectPreference()
  {
    super(1);
    setPreferenceStore(MxEclipsePlugin.getDefault().getPreferenceStore());
  }

  public void createFieldEditors()
  {
    addField(
      new BooleanFieldEditor("TriggerOff", 
      "Global trigger off", 
      getFieldEditorParent()));
    addField(
      new IntegerFieldEditor("ObjectSearchLimit", 
      "Object Search Limit (0 for unlimitted):", 
      getFieldEditorParent()));
    addField(
      new IntegerFieldEditor("HistoryLimit", 
      "History Limit (0 for unlimitted):", 
      getFieldEditorParent()));
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
    if (this.triggerOffChanged) {
      try {
        MxEclipseUtils.triggerOnOff();
      } catch (MatrixException e) {
        Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
        ErrorDialog.openError(null, 
          "Error when creating actions", 
          e.getMessage(), 
          status);
        return false;
      }
    }

    return retVal;
  }

  public void propertyChange(PropertyChangeEvent event)
  {
    super.propertyChange(event);
  }
}