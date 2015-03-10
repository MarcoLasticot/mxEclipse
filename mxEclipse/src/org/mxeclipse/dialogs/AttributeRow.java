package org.mxeclipse.dialogs;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.model.MxAttribute;
import org.mxeclipse.model.MxTreeAttribute;

public class AttributeRow {
	private Combo cmbAttributes = null;
	private Text txtAttributeValue = null;
	private Button cmdAddNew = null;
	private Button cmdDelete = null;
	private Composite parent;
	private ArrayList<AttributeRow> lst;

	public AttributeRow(Composite parent, ArrayList<AttributeRow> lst) {
		this.parent = parent;
		this.lst = lst;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = 4;
		gridData2.verticalAlignment = 2;

		GridData gridDataButton = new GridData();
		gridDataButton.horizontalAlignment = 4;
		gridDataButton.verticalAlignment = 2;
		this.cmbAttributes = new Combo(parent, 0);
		this.cmbAttributes.setVisibleItemCount(8);
		this.cmbAttributes.setLayoutData(gridData2);
		try {
			ArrayList allAttributes = MxTreeAttribute.getAllAttributes(false);
			MxTreeAttribute attr;
			for(Iterator iterator = allAttributes.iterator(); iterator.hasNext(); cmbAttributes.add(attr.getName())) {
				attr = (MxTreeAttribute)iterator.next();
			}
		} catch (Exception ex) {
			Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
			ErrorDialog.openError(parent.getShell(), 
					"Error when trying to initialize attribute related form part", 
					"Error when trying to initialize attribute related form part", 
					status);
		}

		GridData gridData12 = new GridData();
		gridData12.horizontalAlignment = 4;
		gridData12.widthHint = 200;
		gridData12.verticalAlignment = 2;
		gridData12.grabExcessHorizontalSpace = true;
		this.txtAttributeValue = new Text(parent, 2048);
		this.txtAttributeValue.setLayoutData(gridData12);

		this.cmdAddNew = new Button(parent, 2048);
		this.cmdAddNew.setLayoutData(gridDataButton);
		this.cmdAddNew.setText("+");

		this.cmdAddNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AttributeRow.this.addNew();
			}
		});
		this.cmdDelete = new Button(parent, 2048);
		this.cmdDelete.setLayoutData(gridDataButton);
		this.cmdDelete.setText("-");

		this.cmdDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AttributeRow.this.delete();
			}
		});
		if (lst.size() == 0) {
			this.cmdDelete.setVisible(false);
		}

		lst.add(this);
	}

	private void addNew() {
		new AttributeRow(this.parent, this.lst);
		this.parent.redraw();
		this.parent.update();
		this.parent.pack(true);
		this.parent.redraw();
		this.parent.update();
		this.parent.pack(true);
		this.cmdAddNew.setVisible(false);
	}

	private void delete() {
		this.cmbAttributes.dispose();
		this.txtAttributeValue.dispose();
		this.cmdAddNew.dispose();
		this.cmdDelete.dispose();
		if (this.lst.indexOf(this) == this.lst.size() - 1) {
			((AttributeRow)this.lst.get(this.lst.size() - 2)).cmdAddNew.setVisible(true);
		}
		this.lst.remove(this);
	}

	public MxAttribute getCondition() {
		String name = this.cmbAttributes.getText();
		String value = this.txtAttributeValue.getText();
		if ((name != null) && (!name.equals("")) && (value != null) && (!value.equals(""))) {
			return new MxAttribute(this.cmbAttributes.getText(), this.txtAttributeValue.getText());
		}
		return null;
	}

	public void setCondition(MxAttribute attribute) {
		if (attribute != null) {
			this.cmbAttributes.setText(attribute.getName());
			this.txtAttributeValue.setText(attribute.getValue());
		}
	}
}