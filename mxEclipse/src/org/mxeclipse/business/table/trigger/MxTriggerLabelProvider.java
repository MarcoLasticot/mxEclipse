package org.mxeclipse.business.table.trigger;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeTrigger;

public class MxTriggerLabelProvider extends LabelProvider implements ITableLabelProvider {
	public static final String TRIGGER_IMAGE = "trigger";
	public static final String TRIGGER_IMAGE_GRAY = "triggergray";
	public static final String LINK_TO_OBJECT_IMAGE = "link";
	public static final String CHECKED_IMAGE = "checked";
	public static final String UNCHECKED_IMAGE = "unchecked";
	private static ImageRegistry imageRegistry = new ImageRegistry();

	static {
		imageRegistry.put("trigger", MxEclipsePlugin.getImageDescriptor("trigger.gif"));
		imageRegistry.put("triggergray", MxEclipsePlugin.getImageDescriptor("trigger_gray.gif"));
		imageRegistry.put("link", MxEclipsePlugin.getImageDescriptor("link_to_object.gif"));
		imageRegistry.put("checked", MxEclipsePlugin.getImageDescriptor("checked.gif"));
		imageRegistry.put("unchecked", MxEclipsePlugin.getImageDescriptor("unchecked.gif"));
	}

	private Image getTypeImage(MxTreeBusiness obj) {
		if (obj.isInherited()) {
			return imageRegistry.get("triggergray");
		}
		return imageRegistry.get("trigger");
	}

	private Image getLinkImage() {
		return imageRegistry.get("link");
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		MxTreeTrigger trigger = (MxTreeTrigger)element;
		String columnName = MxTriggerComposite.columnNames[columnIndex];
		if (columnName.equals("EventType")) {
			result = trigger.getEventType();
		} else if (columnName.equals("TriggerType")) {
			result = trigger.getTriggerType();
		} else if (columnName.equals("Program")) {
			result = trigger.getMainProgramName();
		} else if (columnName.equals("Args")) {
			result = trigger.getArgs();
		}

		return result;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		Image result = null;
		MxTreeTrigger trigger = (MxTreeTrigger)element;
		String columnName = MxTriggerComposite.columnNames[columnIndex];
		if (columnName.equals("EventType")) {
			result = getTypeImage(trigger);
		} else if (columnName.equals("Link")) {
			result = getLinkImage();
		}
		return result;
	}

}
