package org.mxeclipse.business.table.range;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreeRange;

public class MxRangeLabelProvider extends LabelProvider implements ITableLabelProvider {
	public static final String RANGE_IMAGE = "range";
	public static final String CHECKED_IMAGE = "checked";
	public static final String UNCHECKED_IMAGE = "unchecked";
	private static ImageRegistry imageRegistry = new ImageRegistry();

	static {
		imageRegistry.put("range", MxEclipsePlugin.getImageDescriptor("iconSmallAttribute.gif"));
		imageRegistry.put("checked", MxEclipsePlugin.getImageDescriptor("checked.gif"));
		imageRegistry.put("unchecked", MxEclipsePlugin.getImageDescriptor("unchecked.gif"));
	}

	private Image getTypeImage(String type) {
		return imageRegistry.get("range");
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		MxTreeRange range = (MxTreeRange)element;
		switch (columnIndex) {
		case 0:
			result = range.getName();
		}

		return result;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		Image result = null;
		MxTreeRange range = (MxTreeRange)element;
		switch (columnIndex) {
		case 0:
			result = getTypeImage(range.getName());
		}

		return result;
	}
}