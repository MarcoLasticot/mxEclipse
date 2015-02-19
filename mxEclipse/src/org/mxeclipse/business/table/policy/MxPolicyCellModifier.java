package org.mxeclipse.business.table.policy;

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;
import org.mxeclipse.model.MxTableColumn;
import org.mxeclipse.model.MxTreePolicy;

public class MxPolicyCellModifier implements ICellModifier {
	MxPolicyComposite composite;

	public MxPolicyCellModifier(MxPolicyComposite composite) {
		this.composite = composite;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}

	public Object getValue(Object element, String property) {
		Object result = "";
		MxTreePolicy policy = (MxTreePolicy)element;
		try {
			if (property.equals(MxTableColumn.FIELD_NAME)) {
				Iterator itPolicy = MxTreePolicy.getAllPolicies(false).iterator();
				result = Integer.valueOf(-1);
				int i = 0;
				while (itPolicy.hasNext()) {
					MxTreePolicy p = (MxTreePolicy)itPolicy.next();
					if (p.getName().equals(policy.getName())) {
						result = Integer.valueOf(i);
						break;
					}
					i++;
				}
			}
		} catch (Exception ex) {
			MessageDialog.openError(this.composite.getShell(), "Policy retrieval", "Error when retrieving a list of all policies in the system!");
		}

		return result;
	}

	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem)element;
		MxTreePolicy policy = (MxTreePolicy)item.getData();
		try {
			if (property.equals(MxTableColumn.FIELD_NAME)) {
				int nValue = ((Integer)value).intValue();
				if (nValue >= 0) {
					policy.setName(((MxTreePolicy)MxTreePolicy.getAllPolicies(false).get(nValue)).getName());
				}
			}
		} catch (Exception ex) {
			MessageDialog.openInformation(this.composite.getShell(), "Policy Configuration", "Error when editing value. Please give a correct value!");
		}
		this.composite.getType().propertyChanged(policy);
	}
}