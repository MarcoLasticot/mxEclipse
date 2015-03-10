package org.mxeclipse.dialogs;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mxeclipse.model.MxAttribute;
import org.mxeclipse.model.MxObjectSearchCriteria;
import org.mxeclipse.model.MxTreeDomainObject;

public class SearchMatrixBusinessObjectsDialog extends Dialog {
	SearchMatrixBusinessObjectsComposite inner;
	MxObjectSearchCriteria searchCriteria;

	public SearchMatrixBusinessObjectsDialog(Shell parent, MxObjectSearchCriteria searchCriteria) {
		super(parent);
		setShellStyle(getShellStyle() | 0x10);
		this.searchCriteria = searchCriteria;
	}

	public static void main(String[] args) {
		Display display = Display.getDefault();
		SearchMatrixBusinessObjectsDialog thisClass = new SearchMatrixBusinessObjectsDialog(null, null);
		thisClass.createDialogArea(null);
		thisClass.open();

		while (!thisClass.getShell().isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	protected void cancelPressed() {
		super.cancelPressed();
	}

	protected void okPressed() {
		this.inner.okPressed();
		super.okPressed();
	}

	public List<MxTreeDomainObject> getTreeObjectList() {
		return this.inner.getTreeObjectList();
	}

	public void setSearchCriteria(MxObjectSearchCriteria searchCriteria) {
		this.inner.setSearchCriteria(searchCriteria);
	}

	public MxObjectSearchCriteria getSearchCriteria() {
		return this.inner.getSearchCriteria();
	}

	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite)super.createDialogArea(parent);
		GridData gridDataComp = new GridData();
		gridDataComp.grabExcessHorizontalSpace = true;
		gridDataComp.horizontalAlignment = 4;
		gridDataComp.grabExcessVerticalSpace = true;
		gridDataComp.verticalAlignment = 4;
		comp.setLayoutData(gridDataComp);

		GridLayout layout = (GridLayout)comp.getLayout();
		layout.numColumns = 1;

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = 4;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = 4;
		this.inner = new SearchMatrixBusinessObjectsComposite(comp, 0);
		this.inner.setLayoutData(gridData);
		this.inner.setSearchCriteria(this.searchCriteria);
		return comp;
	}

	public static List<MxTreeDomainObject> findObjects(String id, String type, String name, String rev, ArrayList<MxAttribute> alAttributes) throws Exception {
		MxObjectSearchCriteria criteria = new MxObjectSearchCriteria(id, type, name, rev, null, null, null, false, "standard", alAttributes);
		return SearchFindLikeComposite.findObjects(criteria);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Search Business Objects");
		newShell.setMinimumSize(600, 600);
	}
}