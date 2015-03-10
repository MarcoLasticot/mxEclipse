package org.mxeclipse.configure.table;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTableColumn;

public class MxTableColumnLabelProvider extends LabelProvider implements ITableLabelProvider {
	public static final String ATTRIBUTE_IMAGE = "attribute";
	public static final String BASIC_IMAGE = "basic";
	public static final String CHECKED_IMAGE = "checked";
	public static final String UNCHECKED_IMAGE = "unchecked";
	private static ImageRegistry imageRegistry = new ImageRegistry();

	static {
		imageRegistry.put("attribute", MxEclipsePlugin.getImageDescriptor("iconSmallAttribute.gif"));
		imageRegistry.put("basic", MxEclipsePlugin.getImageDescriptor("history.gif"));
		imageRegistry.put("checked", MxEclipsePlugin.getImageDescriptor("checked.gif"));
		imageRegistry.put("unchecked", MxEclipsePlugin.getImageDescriptor("unchecked.gif"));
	}

	private Image getBooleanImage(boolean isSelected) {
		String key = isSelected ? "checked" : "unchecked";
		return imageRegistry.get(key);
	}

	private Image getTypeImage(String type) {
		if (type.equals(MxTableColumn.TYPE_ATTRIBUTE)) {
			return imageRegistry.get("attribute");
		}
		if (type.equals(MxTableColumn.TYPE_ATTRIBUTE)) {
			return imageRegistry.get("basic");
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		MxTableColumn task = (MxTableColumn)element;
		switch (columnIndex) {
		case 0:
			result = task.getName();
			break;
		case 1:
			result = task.getType();
			break;
		case 2:
			break;
		case 4:
			result = (new StringBuilder()).append(task.getWidth()).toString();
			break;
		case 3:
		}

		return result;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		Image result = null;
		MxTableColumn task = (MxTableColumn)element;
		switch (columnIndex) {
		case 0:
			result = getTypeImage(task.getType());
			break;
		case 1:
			break;
		case 2:
			result = getBooleanImage(((MxTableColumn)element).isVisible());
			break;
		case 3:
			result = getBooleanImage(((MxTableColumn)element).isOnRelationship());
			break;
		case 4:
			break;
		}

		return result;
	}
}