package org.mxeclipse.configure.table;

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;
import org.mxeclipse.model.MxTableColumn;
import org.mxeclipse.utils.MxEclipseUtils;

public class MxTableColumnCellModifier implements ICellModifier {
	ConfigureTableComposite composite;

	public MxTableColumnCellModifier(ConfigureTableComposite composite) {
		this.composite = composite;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}

	public Object getValue(Object element, String property) {
		Object result = "";
		MxTableColumn column = (MxTableColumn)element;

		if (property.equals(MxTableColumn.FIELD_NAME)) {
			Iterator itAttribute = MxEclipseUtils.getAllAttributes().keySet().iterator();
			result = Integer.valueOf(-1);
			int i = 0;
			while (itAttribute.hasNext()) {
				String key = (String)itAttribute.next();
				if (key.equals(column.getName())) {
					result = Integer.valueOf(i);
					break;
				}
				i++;
			}
		} else if (property.equals(MxTableColumn.FIELD_TYPE)) {
			result = column.getType();
		} else if (property.equals(MxTableColumn.FIELD_VISIBLE)) {
			result = new Boolean(column.isVisible());
		} else if (property.equals(MxTableColumn.FIELD_ON_RELATIONSHIP)) {
			result = new Boolean(column.isOnRelationship());
		} else if (property.equals(MxTableColumn.FIELD_WIDTH)) {
			result = column.getWidth();
		}

		return result;
	}

	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem)element;
		MxTableColumn column = (MxTableColumn)item.getData();
		try {
			if (property.equals(MxTableColumn.FIELD_NAME)) {
				int nValue = ((Integer)value).intValue();
				if (nValue >= 0) {
					String[] allAttributes = (String[])MxEclipseUtils.getAllAttributes().keySet().toArray(new String[MxEclipseUtils.getAllAttributes().keySet().size()]);
					column.setName(allAttributes[nValue]);
				}
			} else if (property.equals(MxTableColumn.FIELD_TYPE)) {
				column.setType((String)value);
			} else if (property.equals(MxTableColumn.FIELD_VISIBLE)) {
				column.setVisible(((Boolean)value).booleanValue());
			} else if (property.equals(MxTableColumn.FIELD_ON_RELATIONSHIP)) {
				column.setOnRelationship(((Boolean)value).booleanValue());
			} else if (property.equals(MxTableColumn.FIELD_WIDTH)) {
				column.setWidth(Integer.parseInt((String)value));
			}
		} catch (Exception ex) {
			MessageDialog.openInformation(this.composite.getShell(), "Column Configuration", "Error when editing value. Please give a correct value!");
		}
		this.composite.getColumns().propertyChanged(column);
	}
}