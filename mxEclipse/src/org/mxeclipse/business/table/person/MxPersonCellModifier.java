package org.mxeclipse.business.table.person;

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mxeclipse.model.MxTreePerson;
import org.mxeclipse.views.MxEclipseBusinessView;

public class MxPersonCellModifier implements ICellModifier {
	MxPersonComposite composite;

	public MxPersonCellModifier(MxPersonComposite composite) {
		this.composite = composite;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}

	public Object getValue(Object element, String property) {
		Object result = "";
		MxTreePerson person = (MxTreePerson)element;
		try {
			if (property.equals("Name")) {
				Iterator itAttribute = MxTreePerson.getAllPersons(false).iterator();
				result = Integer.valueOf(-1);
				int i = 0;
				while (itAttribute.hasNext()) {
					MxTreePerson a = (MxTreePerson)itAttribute.next();
					if (a.getName().equals(person.getName())) {
						result = Integer.valueOf(i);
						break;
					}
					i++;
				}
			} else {
				String personName = person.getName();
				if (personName.length() > 0) {
					IWorkbench workbench = PlatformUI.getWorkbench();
					IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
					IWorkbenchPage page = window.getActivePage();
					try {
						MxEclipseBusinessView view = (MxEclipseBusinessView)page.showView("org.mxeclipse.views.MxEclipseBusinessView");
						TreeItem[] selected = view.treObjects.getSelection();
						if (selected.length > 0) {
							view.expandItem(selected[0]);
							for (TreeItem subItem : selected[0].getItems()) {
								if ((subItem.getData() instanceof MxTreePerson)) {
									MxTreePerson subPerson = (MxTreePerson)subItem.getData();
									if (subPerson.getName().equals(personName)) {
										view.nodeSelected(subItem);
										view.treObjects.setSelection(subItem);
										break;
									}
								}
							}
						}
					} catch (PartInitException e) {
						MessageDialog.openError(this.composite.getShell(), "Person selection", "Error when trying to select a person! " + e.getMessage());
					}
				}

				result = "";
			}
		} catch (Exception ex) {
			MessageDialog.openError(this.composite.getShell(), "Person retrieval", "Error when retrieving person properties for editing!");
		}

		return result;
	}

	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem)element;
		MxTreePerson person = (MxTreePerson)item.getData();
		try {
			if (property.equals("Name")) {
				int nValue = ((Integer)value).intValue();
				if (nValue >= 0) {
					person.setName(((MxTreePerson)MxTreePerson.getAllPersons(false).get(nValue)).getName());
				}
			}
		} catch (Exception ex) {
			MessageDialog.openInformation(this.composite.getShell(), "Trigger Configuration", "Error when editing value. Please give a correct value!");
		}
		if (!property.equals("Link")) {
			this.composite.getBusiness().propertyChanged(person);
		}
	}
}