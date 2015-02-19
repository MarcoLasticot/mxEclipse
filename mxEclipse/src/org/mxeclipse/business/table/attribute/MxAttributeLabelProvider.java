package org.mxeclipse.business.table.attribute;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreeAttribute;
import org.mxeclipse.model.MxTreeBusiness;

public class MxAttributeLabelProvider extends LabelProvider implements ITableLabelProvider {
	public static final String ATTRIBUTE_IMAGE = "attribute";
	public static final String ATTRIBUTE_IMAGE_GRAY = "attributegray";
	public static final String CHECKED_IMAGE = "checked";
	public static final String UNCHECKED_IMAGE = "unchecked";
	public static final String BASIC_IMAGE = "basic";
	private static ImageRegistry imageRegistry = new ImageRegistry();

	static {
		imageRegistry.put("attribute", MxEclipsePlugin.getImageDescriptor("attrib.gif"));
		imageRegistry.put("attributegray", MxEclipsePlugin.getImageDescriptor("attrib_gray.gif"));
		imageRegistry.put("checked", MxEclipsePlugin.getImageDescriptor("checked.gif"));
		imageRegistry.put("unchecked", MxEclipsePlugin.getImageDescriptor("unchecked.gif"));
		imageRegistry.put("basic", MxEclipsePlugin.getImageDescriptor("basic.gif"));
	}

	private Image getTypeImage(MxTreeBusiness obj) {
		if (obj.getType().equals("Basic")) {
			return imageRegistry.get("basic");
		}
		if (obj.isInherited()) {
			return imageRegistry.get("attributegray");
		}
		return imageRegistry.get("attribute");
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		MxTreeAttribute attribute = (MxTreeAttribute)element;
		switch (columnIndex) {
		case 0:
			result = attribute.getName();
		}

		return result;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		Image result = null;
		MxTreeAttribute attribute = (MxTreeAttribute)element;
		switch (columnIndex) {
		case 0:
			result = getTypeImage(attribute);
		}

		return result;
	}
}