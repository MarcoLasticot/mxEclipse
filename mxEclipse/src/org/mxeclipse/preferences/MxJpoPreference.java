package org.mxeclipse.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mxeclipse.MxEclipsePlugin;

public class MxJpoPreference extends FieldEditorPreferencePage
  implements IWorkbenchPreferencePage
{
  public MxJpoPreference()
  {
    super(1);
    setPreferenceStore(MxEclipsePlugin.getDefault().getPreferenceStore());
  }

  public void createFieldEditors()
  {
    StringFieldEditor programFolderEditor = new StringFieldEditor("JpoImportProjectFolder", 
      "Default JPO import folder:", getFieldEditorParent());
    programFolderEditor.setEmptyStringAllowed(false);
    addField(programFolderEditor);
    StringFieldEditor javaFolderEditor = new StringFieldEditor("JpoImportJavaSubfolder", 
      "Default subfolder for java programs:", getFieldEditorParent());
    addField(javaFolderEditor);
    StringFieldEditor othersFolderEditor = new StringFieldEditor("JpoImportOthersSubfolder", 
      "Default subfolder for non-java programs:", getFieldEditorParent());
    addField(othersFolderEditor);
    addField(
      new BooleanFieldEditor("JpoUpdateWarnOnRemote", 
      "Warn when about to update remote JPO:", getFieldEditorParent()));
    addField(
      new BooleanFieldEditor("JpoDialogJavaCodeStyle", 
      "Java Code Style in the Program Code tab:", getFieldEditorParent()));
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
    return retVal;
  }

  public void propertyChange(PropertyChangeEvent event)
  {
    super.propertyChange(event);
  }
}