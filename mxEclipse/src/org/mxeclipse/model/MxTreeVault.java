package org.mxeclipse.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import matrix.db.Context;
import matrix.db.Vault;
import matrix.db.VaultItr;
import matrix.db.VaultList;
import matrix.util.MatrixException;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeVault extends MxTreeBusiness
{
  Vault vault;
  private static ArrayList<MxTreeVault> allVaults;
  protected String description;

  public MxTreeVault(String name)
    throws MxEclipseException, MatrixException
  {
    super("Vault", name);
    this.vault = new Vault(name);
  }

  public void refresh() throws MxEclipseException, MatrixException
  {
    super.refresh();
    this.vault = new Vault(getName());
    fillBasics();
  }

  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void fillBasics() {
    try {
      Context context = getContext();
      this.vault.open(context);
      try {
        this.name = this.vault.getName();
        this.description = this.vault.getDescription(context);
      } finally {
        this.vault.close(context);
      }
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
  }

  public static ArrayList<MxTreeVault> getAllVaults(boolean refresh) throws MatrixException, MxEclipseException {
    if ((refresh) || (allVaults == null)) {
      Context context = getContext();
      VaultList vl = Vault.getVaults(context, true);
      allVaults = new ArrayList();
      VaultItr pi = new VaultItr(vl);
      while (pi.next()) {
        Vault p = pi.obj();
        MxTreeVault vault = new MxTreeVault(p.getName());
        allVaults.add(vault);
      }
      Collections.sort(allVaults);
    }
    return allVaults;
  }

  public static String[] getAllVaultNames(boolean refresh) throws MatrixException, MxEclipseException {
    ArrayList allTypes = getAllVaults(refresh);

    String[] retVal = new String[allTypes.size()];
    for (int i = 0; i < retVal.length; i++) {
      retVal[i] = ((MxTreeVault)allTypes.get(i)).getName();
    }
    return retVal;
  }

  public void save()
  {
  }

  public static void clearCache() {
    allVaults = null;
  }
}