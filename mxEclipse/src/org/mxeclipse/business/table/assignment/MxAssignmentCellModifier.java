package org.mxeclipse.business.table.assignment;

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;
import org.mxeclipse.model.MxTableColumn;
import org.mxeclipse.model.MxTreeAssignment;
import org.mxeclipse.model.MxTreeGroup;
import org.mxeclipse.model.MxTreeRole;

public class MxAssignmentCellModifier implements ICellModifier {
	MxAssignmentComposite composite;

	public MxAssignmentCellModifier(MxAssignmentComposite composite) {
		this.composite = composite;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}

	public Object getValue(Object element, String property) {
		Object result = "";
		MxTreeAssignment type = (MxTreeAssignment)element;
		try {
			if (property.equals(MxTableColumn.FIELD_NAME)) {
				Iterator itAttribute = null;
				if ((type instanceof MxTreeRole)) {
					itAttribute = MxTreeRole.getAllRoles(false).iterator();
				} else {
					itAttribute = MxTreeGroup.getAllGroups(false).iterator();
				}
				result = Integer.valueOf(-1);
				int i = 0;
				while (itAttribute.hasNext()) {
					MxTreeAssignment a = (MxTreeAssignment)itAttribute.next();
					if (a.getName().equals(type.getName())) {
						result = Integer.valueOf(i);
						break;
					}
					i++;
				}
			}
		} catch (Exception ex) {
			MessageDialog.openError(this.composite.getShell(), "Role/Group retrieval", "Error when retrieving a list of all roles/groups in the system!");
		}

		return result;
	}

	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem)element;
		MxTreeAssignment assignment = (MxTreeAssignment)item.getData();
		try {
			if (property.equals(MxTableColumn.FIELD_NAME)) {
				int nValue = ((Integer)value).intValue();
				if (nValue >= 0) {
					assignment.setName(((MxTreeAssignment)MxTreeAssignment.getAllAssignments(false, assignment.getType()).get(nValue)).getName());
				}
			}
		} catch (Exception ex) {
			MessageDialog.openInformation(this.composite.getShell(), "Assignment Configuration", "Error when editing value. Please give a correct value!");
		}
		this.composite.getBusiness().propertyChanged(assignment);
	}
}