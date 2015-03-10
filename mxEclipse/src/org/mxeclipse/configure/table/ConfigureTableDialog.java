package org.mxeclipse.configure.table;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mxeclipse.model.MxTreeDomainObject;
import org.mxeclipse.utils.MxPersistUtils;

public class ConfigureTableDialog extends Dialog {
	ConfigureTableComposite inner;
	MxTableColumnList initialColumns;
	protected MxTreeDomainObject selectedObject;

	public ConfigureTableDialog(Shell parent, MxTableColumnList initialColumns) {
		super(parent);
		setShellStyle(getShellStyle() | 0x10);
		this.initialColumns = initialColumns;
	}

	public static void main(String[] args) {
		Display display = Display.getDefault();
		ConfigureTableDialog thisClass = new ConfigureTableDialog(null, null);
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
		List lst = new ArrayList();
		lst.add(this.inner.getTableColumns());
		MxPersistUtils.persistObjects(lst);
		super.okPressed();
	}

	public MxTableColumnList getTableColuns() {
		return this.inner.getTableColumns();
	}

	public void setTableColumns(MxTableColumnList columns) {
		this.inner.setTableColumns(columns);
	}

	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite)super.createDialogArea(parent);
		GridLayout layout = (GridLayout)comp.getLayout();
		layout.numColumns = 1;

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = 4;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = 4;
		this.inner = new ConfigureTableComposite(comp, 0, this.initialColumns);
		this.inner.setLayoutData(gridData);

		return comp;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Search Business Objects");
		newShell.setMinimumSize(600, 600);
	}
}