package org.mxeclipse.business.table.type;

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;
import org.mxeclipse.model.MxTableColumn;
import org.mxeclipse.model.MxTreeType;

public class MxTypeCellModifier implements ICellModifier {
	MxTypeComposite composite;

	public MxTypeCellModifier(MxTypeComposite composite) {
		this.composite = composite;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}

	public Object getValue(Object element, String property) {
		Object result = "";
		MxTreeType type = (MxTreeType)element;
		try {
			if (property.equals(MxTableColumn.FIELD_NAME)) {
				Iterator itAttribute = MxTreeType.getAllTypes(false).iterator();
				result = Integer.valueOf(-1);
				int i = 0;
				while (itAttribute.hasNext()) {
					MxTreeType a = (MxTreeType)itAttribute.next();
					if (a.getName().equals(type.getName())) {
						result = Integer.valueOf(i);
						break;
					}
					i++;
				}
			}
		} catch (Exception ex) {
			MessageDialog.openError(this.composite.getShell(), "Type retrieval", "Error when retrieving a list of all types in the system!");
		}

		return result;
	}

	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem)element;
		MxTreeType type = (MxTreeType)item.getData();
		try {
			if (property.equals(MxTableColumn.FIELD_NAME)) {
				int nValue = ((Integer)value).intValue();
				if (nValue >= 0){
					type.setName(((MxTreeType)MxTreeType.getAllTypes(false).get(nValue)).getName());
				}
			}
		} catch (Exception ex) {
			MessageDialog.openInformation(this.composite.getShell(), "Type Configuration", "Error when editing value. Please give a correct value!");
		}
		this.composite.getBusiness().propertyChanged(type);
	}
}