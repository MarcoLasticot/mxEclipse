package org.mxeclipse.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import matrix.db.Context;
import matrix.db.Store;
import matrix.db.StoreItr;
import matrix.db.StoreList;
import matrix.util.MatrixException;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeStore extends MxTreeBusiness
{
  Store store;
  private static ArrayList<MxTreeStore> allStores;
  protected String description;

  public MxTreeStore(String name)
    throws MxEclipseException, MatrixException
  {
    super("Vault", name);
    this.store = new Store(name);
  }

  public void refresh() throws MxEclipseException, MatrixException
  {
    super.refresh();
    this.store = new Store(getName());
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
      this.store.open(context);
      try {
        this.name = this.store.getName();
        this.description = this.store.getDescription(context);
      } finally {
        this.store.close(context);
      }
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
  }

  public static ArrayList<MxTreeStore> getAllStores(boolean refresh) throws MatrixException, MxEclipseException {
    if ((refresh) || (allStores == null)) {
      Context context = getContext();
      StoreList vl = Store.getStores(context, true);
      allStores = new ArrayList();
      StoreItr pi = new StoreItr(vl);
      while (pi.next()) {
        Store p = pi.obj();
        MxTreeStore vault = new MxTreeStore(p.getName());
        allStores.add(vault);
      }
      Collections.sort(allStores);
    }
    return allStores;
  }

  public static String[] getAllVaultNames(boolean refresh) throws MatrixException, MxEclipseException {
    ArrayList allTypes = getAllStores(refresh);

    String[] retStore = new String[allTypes.size()];
    for (int i = 0; i < retStore.length; i++) {
      retStore[i] = ((MxTreeStore)allTypes.get(i)).getName();
    }
    return retStore;
  }

  public void save()
  {
  }

  public static void clearCache() {
    allStores = null;
  }
}