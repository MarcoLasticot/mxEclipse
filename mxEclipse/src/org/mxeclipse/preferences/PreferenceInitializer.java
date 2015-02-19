package org.mxeclipse.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mxeclipse.MxEclipsePlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{
  public void initializeDefaultPreferences()
  {
    try
    {
      IPreferenceStore store = MxEclipsePlugin.getDefault().getPreferenceStore();
      store.setDefault("MatrixHost", "localhost");
      store.setDefault("MatrixUser", "creator");
      store.setDefault("MatrixUserPwd", "");
      store.setDefault("MatrixDefaultLogin", false);
      store.setDefault("AutomaticSilentLogin", false);

      store.setDefault("TriggerOff", false);
      store.setDefault("ObjectSearchLimit", 100);
      store.setDefault("HistoryLimit", 200);

      store.setDefault("JpoUpdateWarnOnRemote", true);
      store.setDefault("JpoImportProjectFolder", "programs");
      store.setDefault("JpoImportJavaSubfolder", "java");
      store.setDefault("JpoImportOthersSubfolder", "others");
      store.setDefault("JpoDialogJavaCodeStyle", true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}