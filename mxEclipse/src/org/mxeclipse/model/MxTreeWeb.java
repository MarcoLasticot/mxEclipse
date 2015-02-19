package org.mxeclipse.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public abstract class MxTreeWeb extends MxTreeBusiness
{
  protected static String MQL_INFO_SETTING = "print {0} \"{1}\" select setting dump |;";
  protected static String MQL_INFO_USER = "print {0} \"{1}\" select user dump |;";
  protected String realType;
  protected ArrayList<MxTreeWebSetting> settings;
  protected ArrayList<MxTreeUser> users;
  protected static final String SETTING = "setting";
  protected static final String VALUE = "value";

  public MxTreeWeb(String type, String name)
    throws MxEclipseException, MatrixException
  {
    super(type, name);
    this.realType = type.toLowerCase();
  }

  public void addSetting() throws MxEclipseException, MatrixException {
    MxTreeWebSetting newSetting = new MxTreeWebSetting("", this);
    addSetting(newSetting);
  }

  public void addSetting(MxTreeWebSetting newMenu) {
    getSettings(false).add(newMenu);
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext()) {
      IMxBusinessViewer contentProvider = (IMxBusinessViewer)iterator.next();
      contentProvider.addProperty(newMenu);
    }
  }

  public void removeSetting(MxTreeWebSetting menu) {
    if (this.settings == null) {
      getSettings(false);
    }
    this.settings.remove(menu);
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext()) {
      IMxBusinessViewer contentProvider = (IMxBusinessViewer)iterator.next();
      contentProvider.removeProperty(menu);
    }
  }

  public static ArrayList<MxTreeWebSetting> getSettings(MxTreeWeb web)
  {
    ArrayList retSettings = new ArrayList();
    try {
      Context context = getContext();

      MQLCommand command = new MQLCommand();
      command.executeCommand(context, MessageFormat.format(MQL_SIMPLE_PRINT, new Object[] { web.getType(), web.getName() }));

      String[] lines = command.getResult().split("\n");
      String myLineBeginning = "setting";
      for (int i = 0; i < lines.length; i++) {
        lines[i] = lines[i].trim();
        if (lines[i].startsWith(myLineBeginning)) {
          lines[i] = lines[i].substring(myLineBeginning.length());
          int indexOfValue = lines[i].indexOf("value");
          String setName = lines[i].substring(0, indexOfValue).trim();
          String setValue = lines[i].substring(indexOfValue + "value".length()).trim();

          MxTreeWebSetting child = MxTreeWebSetting.createInstance(web, setName);
          child.setValue(setValue);

          child.setFrom(true);
          child.setRelType("contains");
          child.setParent(web);
          retSettings.add(child);
        }
      }
      return retSettings;
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }return null;
  }

  public ArrayList<MxTreeWebSetting> getSettings(boolean forceRefresh)
  {
    if ((forceRefresh) || (this.settings == null)) {
      this.settings = getSettings(this);
    }
    return this.settings;
  }

  protected String prepareSaveSettings(MxTreeWeb oldWeb)
  {
      ArrayList mySettings = getSettings(false);
      ArrayList oldSettings = oldWeb.getSettings(false);
      String sAdded = "";
      String sRemoved = "";
      for(Iterator iterator = oldSettings.iterator(); iterator.hasNext();)
      {
          MxTreeWebSetting oldItem = (MxTreeWebSetting)iterator.next();
          sRemoved = (new StringBuilder(String.valueOf(sRemoved))).append(" remove setting \"").append(oldItem.getName()).append("\"").toString();
      }

      for(Iterator iterator1 = mySettings.iterator(); iterator1.hasNext();)
      {
          MxTreeWebSetting item = (MxTreeWebSetting)iterator1.next();
          sAdded = (new StringBuilder(String.valueOf(sAdded))).append(" add setting \"").append(item.getName()).append("\" \"").append(item.getValue()).append("\"").toString();
      }

      return (new StringBuilder(String.valueOf(sRemoved))).append(sAdded).toString();
  }

  public void addUser() throws MxEclipseException, MatrixException
  {
    MxTreeUser newUser = new MxTreePerson("");
    addUser(newUser);
  }

  public void addUser(MxTreeUser newUser) {
    getUsers(false).add(newUser);
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext()) {
      IMxBusinessViewer contentProvider = (IMxBusinessViewer)iterator.next();
      contentProvider.addProperty(newUser);
    }
  }

  public void removeUser(MxTreeUser user) {
    if (this.users == null) {
      getUsers(false);
    }
    this.users.remove(user);
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext()) {
      IMxBusinessViewer contentProvider = (IMxBusinessViewer)iterator.next();
      contentProvider.removeProperty(user);
    }
  }

  public static ArrayList<MxTreeUser> getUsers(MxTreeWeb web)
  {
    ArrayList retSettings = new ArrayList();
    try {
      Context context = getContext();

      MQLCommand command = new MQLCommand();

      command.executeCommand(context, MessageFormat.format(MQL_INFO_USER, new Object[] { web.getType(), web.getName() }));

      String res = command.getResult().trim();
      if (res.length() != 0) {
        String[] sUsers = res.split("\\|");

        for (String sUser : sUsers) {
          MxTreeUser user = MxTreeUser.createInstance(sUser);
          user.setFrom(false);
          user.setRelType("access");
          user.setParent(web);
          retSettings.add(user);
        }
      }
      return retSettings;
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }return null;
  }

  public ArrayList<MxTreeUser> getUsers(boolean forceRefresh)
  {
    if ((forceRefresh) || (this.users == null)) {
      this.users = getUsers(this);
    }
    return this.users;
  }

  protected String prepareSaveUsers(MxTreeWeb oldWeb)
  {
      ArrayList myUsers = getUsers(false);
      ArrayList oldUsers = oldWeb.getUsers(false);
      String sAdded = "";
      String sRemoved = "";
      for(Iterator iterator = oldUsers.iterator(); iterator.hasNext();)
      {
          MxTreeUser oldUser = (MxTreeUser)iterator.next();
          sRemoved = (new StringBuilder(String.valueOf(sRemoved))).append(" remove user \"").append(oldUser.getName()).append("\"").toString();
      }

      for(Iterator iterator1 = myUsers.iterator(); iterator1.hasNext();)
      {
          MxTreeUser user = (MxTreeUser)iterator1.next();
          sAdded = (new StringBuilder(String.valueOf(sAdded))).append(" add user \"").append(user.getName()).append("\"").toString();
      }

      return (new StringBuilder(String.valueOf(sRemoved))).append(sAdded).toString();
  }

  public void refresh()
    throws MxEclipseException, MatrixException
  {
    super.refresh();
    this.settings = null;
  }

  public static void clearCache()
  {
  }
}