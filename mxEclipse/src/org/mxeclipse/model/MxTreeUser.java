package org.mxeclipse.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import matrix.util.MatrixException;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public abstract class MxTreeUser extends MxTreeBusiness
{
  private static ArrayList<MxTreeUser> allUsers;
  public static String[] ALL_USER_TYPES = { "Person", "Role", 
    "Group", "Association" };

  public MxTreeUser(String type, String name) throws MxEclipseException, MatrixException
  {
    super(type, name);
  }

  public void setType(String type) {
    this.type = type;
  }

  public static MxTreeUser createInstance(String name) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, CloneNotSupportedException {
    for (MxTreeUser u : getAllUsers(false)) {
      if (u.getName().equals(name)) {
        return (MxTreeUser)u.clone();
      }
    }

    return null;
  }

  public static MxTreeUser getInstance(String name) {
    for (MxTreeUser u : getAllUsers(false)) {
      if (u.getName().equals(name)) {
        return u;
      }
    }
    return null;
  }

  public static ArrayList<MxTreeUser> getAllUsers(boolean forceRefresh)
  {
    if ((forceRefresh) || (allUsers == null)) {
      try {
        allUsers = new ArrayList();
        List lstUsers = new ArrayList();
        lstUsers.addAll(MxTreePerson.getAllPersons(false));
        lstUsers.addAll(MxTreeRole.getAllRoles(false));
        lstUsers.addAll(MxTreeGroup.getAllGroups(false));
        lstUsers.addAll(MxTreeAssociation.getAllAssociations(false));
        for (Iterator localIterator = lstUsers.iterator(); localIterator.hasNext(); ) { Object person = localIterator.next();
          allUsers.add((MxTreeUser)person);
        }
        Collections.sort(allUsers);
      } catch (Exception ex) {
        MxEclipseLogger.getLogger().severe(ex.getMessage());
        return null;
      }
    }
    return allUsers;
  }

  public static String[] getAllUserNames(boolean refresh)
    throws MatrixException, MxEclipseException
  {
    ArrayList allTypes = getAllUsers(refresh);

    String[] retVal = new String[allTypes.size()];
    for (int i = 0; i < retVal.length; i++) {
      retVal[i] = ((MxTreeUser)allTypes.get(i)).getName();
    }
    return retVal;
  }

  public static ArrayList<MxTreeUser> getAllUsers(boolean forceRefresh, String userType)
  {
    if ((forceRefresh) || (allUsers == null)) {
      getAllUsers(forceRefresh);
    }
    ArrayList alSpecificUsers = new ArrayList();
    for (MxTreeUser u : allUsers) {
      if (u.getType().equals(userType)) {
        alSpecificUsers.add(u);
      }
    }
    return alSpecificUsers;
  }

  public static String[] getAllUserNames(boolean refresh, String userType)
    throws MatrixException, MxEclipseException
  {
    ArrayList allTypes = getAllUsers(refresh, userType);

    if (userType.equals("")) {
      return new String[] { "owner", "public" };
    }
    String[] retVal = new String[allTypes.size()];
    for (int i = 0; i < retVal.length; i++) {
      retVal[i] = ((MxTreeUser)allTypes.get(i)).getName();
    }
    return retVal;
  }

  public static void clearCache() {
    allUsers = null;
  }
}