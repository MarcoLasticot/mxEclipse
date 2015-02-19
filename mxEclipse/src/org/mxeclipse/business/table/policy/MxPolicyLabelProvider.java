package org.mxeclipse.business.table.policy;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreePolicy;

public class MxPolicyLabelProvider extends LabelProvider implements ITableLabelProvider {
	public static final String POLICY_IMAGE = "policy";
	public static final String CHECKED_IMAGE = "checked";
	public static final String UNCHECKED_IMAGE = "unchecked";
	private static ImageRegistry imageRegistry = new ImageRegistry();

	static {
		imageRegistry.put("policy", MxEclipsePlugin.getImageDescriptor("policy.gif"));
		imageRegistry.put("checked", MxEclipsePlugin.getImageDescriptor("checked.gif"));
		imageRegistry.put("unchecked", MxEclipsePlugin.getImageDescriptor("unchecked.gif"));
	}

	private Image getTypeImage(String type) {
		return imageRegistry.get("policy");
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		MxTreePolicy policy = (MxTreePolicy)element;
		switch (columnIndex) {
		case 0:
			result = policy.getName();
		}

		return result;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		Image result = null;
		MxTreePolicy policy = (MxTreePolicy)element;
		switch (columnIndex) {
		case 0:
			result = getTypeImage(policy.getName());
		}

		return result;
	}
}