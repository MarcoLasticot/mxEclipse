package org.mxeclipse.business.table.state;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mxeclipse.model.MxTreeState;
import org.mxeclipse.views.MxEclipseBusinessView;

public class MxStateCellModifier implements ICellModifier {
	MxStateComposite composite;

	public MxStateCellModifier(MxStateComposite composite) {
		this.composite = composite;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}

	public Object getValue(Object element, String property) {
		Object result = "";
		MxTreeState state = (MxTreeState)element;
		try {
			if (property.equals("Name")) {
				result = state.getName();
			} else {
				String stateName = state.getName();
				if (stateName.length() > 0) {
					IWorkbench workbench = PlatformUI.getWorkbench();
					IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
					IWorkbenchPage page = window.getActivePage();
					try {
						MxEclipseBusinessView view = (MxEclipseBusinessView)page.showView("org.mxeclipse.views.MxEclipseBusinessView");
						TreeItem[] selected = view.treObjects.getSelection();
						if (selected.length > 0) {
							view.expandItem(selected[0]);
							for (TreeItem subItem : selected[0].getItems()) {
								if ((subItem.getData() instanceof MxTreeState)) {
									MxTreeState subState = (MxTreeState)subItem.getData();
									if (subState.getName().equals(stateName)) {
										view.nodeSelected(subItem);
										view.treObjects.setSelection(subItem);
										break;
									}
								}
							}
						}
					} catch (PartInitException e) {
						MessageDialog.openError(this.composite.getShell(), "State selection", "Error when trying to select state! " + e.getMessage());
					}
				}

				result = "";
			}
		} catch (Exception ex) {
			MessageDialog.openError(this.composite.getShell(), "Trigger retrieval", "Error when retrieving a trigger properties for editing!");
		}

		return result;
	}

	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem)element;
		MxTreeState state = (MxTreeState)item.getData();
		try {
			if (property.equals("Name")) {
				state.setName((String)value);
			}
		} catch (Exception ex) {
			MessageDialog.openInformation(this.composite.getShell(), "Trigger Configuration", "Error when editing value. Please give a correct value!");
		}
		if (!property.equals("Link")) {
			this.composite.getBusiness().propertyChanged(state);
		}
	}

}
