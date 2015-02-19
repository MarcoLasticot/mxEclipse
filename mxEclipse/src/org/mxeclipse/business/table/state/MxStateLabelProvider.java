package org.mxeclipse.business.table.state;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeState;

public class MxStateLabelProvider extends LabelProvider implements ITableLabelProvider {
	public static final String STATE_IMAGE = "trigger";
	public static final String LINK_TO_OBJECT_IMAGE = "link";
	public static final String CHECKED_IMAGE = "checked";
	public static final String UNCHECKED_IMAGE = "unchecked";
	private static ImageRegistry imageRegistry = new ImageRegistry();

	static {
		imageRegistry.put("trigger", MxEclipsePlugin.getImageDescriptor("state.gif"));
		imageRegistry.put("link", MxEclipsePlugin.getImageDescriptor("link_to_object.gif"));
		imageRegistry.put("checked", MxEclipsePlugin.getImageDescriptor("checked.gif"));
		imageRegistry.put("unchecked", MxEclipsePlugin.getImageDescriptor("unchecked.gif"));
	}

	private Image getTypeImage(MxTreeBusiness obj) {
		return imageRegistry.get("trigger");
	}

	private Image getLinkImage() {
		return imageRegistry.get("link");
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		MxTreeState state = (MxTreeState)element;
		String columnName = MxStateComposite.columnNames[columnIndex];
		if (columnName.equals("Name")) {
			result = state.getName();
		}

		return result;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		Image result = null;
		MxTreeState state = (MxTreeState)element;
		String columnName = MxStateComposite.columnNames[columnIndex];
		if (columnName.equals("Name")) {
			result = getTypeImage(state);
		} else if (columnName.equals("Link")) {
			result = getLinkImage();
		}
		return result;
	}

}
