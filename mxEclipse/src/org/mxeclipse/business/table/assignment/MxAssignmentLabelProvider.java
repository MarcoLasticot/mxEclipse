package org.mxeclipse.business.table.assignment;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreeAssignment;
import org.mxeclipse.model.MxTreeBusiness;

public class MxAssignmentLabelProvider extends LabelProvider implements ITableLabelProvider {
	public static final String ROLE_IMAGE = "role";
	public static final String GROUP_IMAGE = "group";
	private static ImageRegistry imageRegistry = new ImageRegistry();

	static {
		imageRegistry.put("role", MxEclipsePlugin.getImageDescriptor("role.gif"));
		imageRegistry.put("group", MxEclipsePlugin.getImageDescriptor("group.gif"));
	}

	private Image getTypeImage(MxTreeBusiness obj) {
		if (obj.getType().equals("Role")) {
			return imageRegistry.get("role");
		}
		return imageRegistry.get("group");
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		MxTreeAssignment type = (MxTreeAssignment)element;
		switch (columnIndex) {
		case 0:
			result = type.getName();
		}

		return result;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		Image result = null;
		MxTreeAssignment type = (MxTreeAssignment)element;
		switch (columnIndex) {
		case 0:
			result = getTypeImage(type);
		}

		return result;
	}
}