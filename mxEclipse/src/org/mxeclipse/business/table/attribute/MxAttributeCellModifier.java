package org.mxeclipse.business.table.attribute;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;
import org.mxeclipse.model.MxTableColumn;
import org.mxeclipse.model.MxTreeAttribute;
import org.mxeclipse.model.MxTreeBasic;
import org.mxeclipse.model.MxTreeIndex;

public class MxAttributeCellModifier implements ICellModifier {
	MxAttributeComposite composite;

	public MxAttributeCellModifier(MxAttributeComposite composite) {
		this.composite = composite;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}

	public Object getValue(Object element, String property) {
		Object result = "";
		MxTreeAttribute attribute = (MxTreeAttribute)element;
		try {
			if (property.equals(MxTableColumn.FIELD_NAME)) {
				ArrayList alAttributes = null;
				if ((this.composite.getBusiness() instanceof MxTreeIndex)) {
					alAttributes = MxTreeBasic.getAllBasics();
					alAttributes.addAll(MxTreeAttribute.getAllAttributes(false));
				} else {
					alAttributes = MxTreeAttribute.getAllAttributes(false);
				}

				Iterator itAttribute = alAttributes.iterator();
				result = Integer.valueOf(-1);
				int i = 0;
				while (itAttribute.hasNext()) {
					MxTreeAttribute a = (MxTreeAttribute)itAttribute.next();
					if (a.getName().equals(attribute.getName())) {
						result = Integer.valueOf(i);
						break;
					}
					i++;
				}
			}
		} catch (Exception ex) {
			MessageDialog.openError(this.composite.getShell(), "Attribute retrieval", "Error when retrieving a list of all attributes in the system!");
		}

		return result;
	}

	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem)element;
		MxTreeAttribute attribute = (MxTreeAttribute)item.getData();
		try {
			if (property.equals(MxTableColumn.FIELD_NAME)) {
				int nValue = ((Integer)value).intValue();
				if (nValue >= 0) {
					ArrayList alAttributes = null;
					if ((this.composite.getBusiness() instanceof MxTreeIndex)) {
						alAttributes = MxTreeBasic.getAllBasics();
						alAttributes.addAll(MxTreeAttribute.getAllAttributes(false));
						attribute.setType(((MxTreeAttribute)alAttributes.get(nValue)).getType());
					} else {
						alAttributes = MxTreeAttribute.getAllAttributes(false);
					}
					attribute.setName(((MxTreeAttribute)alAttributes.get(nValue)).getName());
				}
			}
		} catch (Exception ex) {
			MessageDialog.openInformation(this.composite.getShell(), "Attribute Configuration", "Error when editing value. Please give a correct value!");
		}
		this.composite.getBusiness().propertyChanged(attribute);
	}
}