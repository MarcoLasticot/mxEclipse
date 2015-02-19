package org.mxeclipse.model;

import java.util.ArrayList;
import java.util.Collections;
import matrix.db.Role;
import matrix.db.RoleItr;
import matrix.db.RoleList;
import matrix.util.MatrixException;
import org.mxeclipse.exception.MxEclipseException;

public class MxTreeRole extends MxTreeAssignment
{
  private static ArrayList<MxTreeRole> allRoles;

  public MxTreeRole(String name)
    throws MxEclipseException, MatrixException
  {
    super("Role", name);
  }

  public static ArrayList<MxTreeRole> getAllRoles(boolean refresh)
    throws MatrixException, MxEclipseException
  {
    if ((refresh) || (allRoles == null)) {
      RoleList pl = Role.getRoles(getContext(), true);
      allRoles = new ArrayList();
      RoleItr pi = new RoleItr(pl);
      while (pi.next()) {
        Role p = pi.obj();
        MxTreeRole role = new MxTreeRole(p.getName());
        allRoles.add(role);
      }
      Collections.sort(allRoles);
    }
    return allRoles;
  }

  public static void clearCache() {
    allRoles = null;
  }
}