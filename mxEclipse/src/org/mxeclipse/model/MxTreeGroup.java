package org.mxeclipse.model;

import java.util.ArrayList;
import java.util.Collections;
import matrix.db.Group;
import matrix.db.GroupItr;
import matrix.db.GroupList;
import matrix.util.MatrixException;
import org.mxeclipse.exception.MxEclipseException;

public class MxTreeGroup extends MxTreeAssignment {
	private static ArrayList<MxTreeGroup> allGroups;

	public MxTreeGroup(String name) throws MxEclipseException, MatrixException {
		super("Group", name);
	}

	public static ArrayList<MxTreeGroup> getAllGroups(boolean refresh) throws MatrixException, MxEclipseException {
		if ((refresh) || (allGroups == null)) {
			GroupList pl = Group.getGroups(getContext(), true);
			allGroups = new ArrayList();
			GroupItr pi = new GroupItr(pl);
			while (pi.next()) {
				Group p = pi.obj();
				MxTreeGroup role = new MxTreeGroup(p.getName());
				allGroups.add(role);
			}
			Collections.sort(allGroups);
		}
		return allGroups;
	}

	public static void clearCache() {
		allGroups = null;
	}
}