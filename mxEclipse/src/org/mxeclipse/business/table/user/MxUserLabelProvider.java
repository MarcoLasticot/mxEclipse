package org.mxeclipse.business.table.user;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeStateUserAccess;
import org.mxeclipse.model.MxTreeUser;

public class MxUserLabelProvider extends LabelProvider implements ITableLabelProvider {
	public static final String PERSON_IMAGE = "person";
	public static final String ROLE_IMAGE = "role";
	public static final String GROUP_IMAGE = "group";
	public static final String ASSOCIATION_IMAGE = "association";
	public static final String LINK_TO_OBJECT_IMAGE = "link";
	public static final String CHECKED_IMAGE = "checked";
	public static final String UNCHECKED_IMAGE = "unchecked";
	private static ImageRegistry imageRegistry = new ImageRegistry();

	static {
		imageRegistry.put("person", MxEclipsePlugin.getImageDescriptor("person.gif"));
		imageRegistry.put("role", MxEclipsePlugin.getImageDescriptor("role.gif"));
		imageRegistry.put("group", MxEclipsePlugin.getImageDescriptor("group.gif"));
		imageRegistry.put("association", MxEclipsePlugin.getImageDescriptor("association.gif"));
		imageRegistry.put("link", MxEclipsePlugin.getImageDescriptor("link_to_object.gif"));
		imageRegistry.put("checked", MxEclipsePlugin.getImageDescriptor("checked.gif"));
		imageRegistry.put("unchecked", MxEclipsePlugin.getImageDescriptor("unchecked.gif"));
	}

	private Image getTypeImage(MxTreeBusiness obj) {
		MxTreeUser user = null;
		if ((obj instanceof MxTreeStateUserAccess)) {
			MxTreeStateUserAccess userAccess = (MxTreeStateUserAccess)obj;
			user = userAccess.getUser();
		} else if ((obj instanceof MxTreeUser)) {
			user = (MxTreeUser)obj;
		}
		if (user != null) {
			if (user.getType().equals("Person")) {
				return imageRegistry.get("person");
			}
			if (user.getType().equals("Role")) {
				return imageRegistry.get("role");
			}
			if (user.getType().equals("Group")) {
				return imageRegistry.get("group");
			}
			if (user.getType().equals("Association")) {
				return imageRegistry.get("association");
			}
			return null;
		}

		return null;
	}

	private Image getLinkImage() {
		return imageRegistry.get("link");
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		if ((element instanceof MxTreeStateUserAccess)) {
			MxTreeStateUserAccess userAccess = (MxTreeStateUserAccess)element;
			String columnName = MxUserComposite.columnNames[columnIndex];
			if (columnName.equals("Type")) {
				result = userAccess.getUserType();
			} else if (columnName.equals("Name")) {
				result = userAccess.getName();
			}
		} else if ((element instanceof MxTreeUser)) {
			MxTreeUser user = (MxTreeUser)element;
			String columnName = MxUserComposite.columnNames[columnIndex];
			if (columnName.equals("Type")) {
				result = user.getType();
			}
			else if (columnName.equals("Name")) {
				result = user.getName();
			}
		}

		return result;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		Image result = null;
		MxTreeBusiness userAccess = (MxTreeBusiness)element;
		String columnName = MxUserComposite.columnNames[columnIndex];
		if (columnName.equals("Type")) {
			result = getTypeImage(userAccess);
		} else if (!columnName.equals("Name")) {
			if (columnName.equals("Select")) {
				result = getLinkImage();
			}
		}
		return result;
	}
}