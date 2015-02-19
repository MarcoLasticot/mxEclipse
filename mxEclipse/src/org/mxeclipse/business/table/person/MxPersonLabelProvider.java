package org.mxeclipse.business.table.person;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreePerson;

public class MxPersonLabelProvider extends LabelProvider implements ITableLabelProvider {
	public static final String PERSON_IMAGE = "person";
	public static final String LINK_TO_OBJECT_IMAGE = "link";
	public static final String CHECKED_IMAGE = "checked";
	public static final String UNCHECKED_IMAGE = "unchecked";
	private static ImageRegistry imageRegistry = new ImageRegistry();

	static {
		imageRegistry.put("person", MxEclipsePlugin.getImageDescriptor("person.gif"));
		imageRegistry.put("link", MxEclipsePlugin.getImageDescriptor("link_to_object.gif"));
		imageRegistry.put("checked", MxEclipsePlugin.getImageDescriptor("checked.gif"));
		imageRegistry.put("unchecked", MxEclipsePlugin.getImageDescriptor("unchecked.gif"));
	}

	private Image getTypeImage(MxTreeBusiness obj) {
		return imageRegistry.get("person");
	}

	private Image getLinkImage() {
		return imageRegistry.get("link");
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		MxTreePerson state = (MxTreePerson)element;
		String columnName = MxPersonComposite.columnNames[columnIndex];
		if (columnName.equals("Name")) {
			result = state.getName();
		}

		return result;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		Image result = null;
		MxTreePerson state = (MxTreePerson)element;
		String columnName = MxPersonComposite.columnNames[columnIndex];
		if (columnName.equals("Name")) {
			result = getTypeImage(state);
		} else if (columnName.equals("Link")) {
			result = getLinkImage();
		}
		return result;
	}
}