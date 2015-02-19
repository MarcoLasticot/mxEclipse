package org.mxeclipse.business.table.range;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;
import org.mxeclipse.model.MxTableColumn;
import org.mxeclipse.model.MxTreeRange;

public class MxRangeCellModifier implements ICellModifier {
	MxRangeComposite composite;

	public MxRangeCellModifier(MxRangeComposite composite) {
		this.composite = composite;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}

	public Object getValue(Object element, String property) {
		Object result = "";
		MxTreeRange attribute = (MxTreeRange)element;

		if (property.equals(MxTableColumn.FIELD_NAME)) {
			result = attribute.getName();
		}

		return result;
	}

	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem)element;
		MxTreeRange range = (MxTreeRange)item.getData();
		try {
			if (property.equals(MxTableColumn.FIELD_NAME)) {
				range.setName((String)value);
			}
		} catch (Exception ex) {
			MessageDialog.openInformation(this.composite.getShell(), "Attribute Range Configuration", "Error when editing value. Please give a correct value!");
		}
		this.composite.getAttribute().propertyChanged(range);
	}
}