package org.mxeclipse.dialogs;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.model.MxTreeAttribute;
import org.mxeclipse.utils.MxEclipseUtils;

public class CreateNewAdminComposite extends Composite {
	private Label lblType = null;
	private Combo cmbType = null;
	private Label lblName = null;
	private Text txtName = null;
	private String adminType;
	private String adminName;
	private String attributeType;
	private Label lblAttributeType = null;
	private Combo cmbAttributeType = null;

	public CreateNewAdminComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
		initializeContent();
	}

	private void initialize() {
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = 4;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = 2;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 2;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.lblType = new Label(this, 0);
		this.lblType.setText("Type");
		createCmbType();
		this.lblName = new Label(this, 0);
		this.lblName.setText("Name");
		this.txtName = new Text(this, 2048);
		this.txtName.setLayoutData(gridData1);
		this.lblAttributeType = new Label(this, 0);
		this.lblAttributeType.setText("Attribute Type");
		setLayout(gridLayout);
		createCmbAttributeType();
		setSize(new Point(300, 78));
	}

	private void initializeContent() {
		try {
			List types = MxEclipseUtils.getAdminTypes();
			Collections.sort(types);
			String[] sTypes = new String[types.size() - 1];
			int i = 0;
			for(Iterator iterator = types.iterator(); iterator.hasNext();) {
				String t = (String)iterator.next();
				if(!t.equals("All"))
					sTypes[i++] = t;
			}
			this.cmbType.setItems(sTypes);

			this.cmbAttributeType.setItems(MxTreeAttribute.ATTRIBUTE_TYPES);
			if (this.cmbType.getSelectionIndex() > -1) {
				boolean attributeSelected = this.cmbType.getItem(this.cmbType.getSelectionIndex()).equalsIgnoreCase("Attribute");
				this.cmbAttributeType.setVisible(attributeSelected);
				this.lblAttributeType.setVisible(attributeSelected);
			} else {
				this.cmbAttributeType.setVisible(false);
				this.lblAttributeType.setVisible(false);
			}
		} catch (Exception ex) {
			MessageDialog.openInformation(getShell(), "Create New", "Error when trying to get combo boxes values " + ex.getMessage());
		}
	}

	private void createCmbType() {
		GridData gridData4 = new GridData();
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.verticalAlignment = 2;
		gridData4.horizontalAlignment = 4;
		this.cmbType = new Combo(this, 0);
		this.cmbType.setLayoutData(gridData4);
		this.cmbType.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				boolean attributeSelected = CreateNewAdminComposite.this.cmbType.getItem(CreateNewAdminComposite.this.cmbType.getSelectionIndex()).equalsIgnoreCase("Attribute");
				CreateNewAdminComposite.this.cmbAttributeType.setVisible(attributeSelected);
				CreateNewAdminComposite.this.lblAttributeType.setVisible(attributeSelected);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public boolean okPressed() {
		if (this.cmbType.getSelectionIndex() < 0) {
			MessageDialog.openInformation(getShell(), "Create New", "Please select a type!");
			return false;
		}
		if (this.txtName.getText().equals("")) {
			MessageDialog.openInformation(getShell(), "Create New", "Please select type in name!");
			return false;
		}
		if ((this.cmbType.getItem(this.cmbType.getSelectionIndex()).equalsIgnoreCase("Attribute")) && (this.cmbAttributeType.getSelectionIndex() < 0)) {
			MessageDialog.openInformation(getShell(), "Create New", "Attribute type is mandatory for attributes!");
			return false;
		}

		this.adminName = this.txtName.getText();
		this.adminType = this.cmbType.getText();
		this.attributeType = this.cmbAttributeType.getText();
		return true;
	}

	public String getAdminName() {
		return this.adminName;
	}

	public void setAdminName(String name) {
		this.adminName = name;
	}

	public String getAdminType() {
		return this.adminType;
	}

	public void setAdminType(String type) {
		this.adminType = type;
	}

	public String getAttributeType() {
		return this.attributeType;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	private void createCmbAttributeType() {
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = 4;
		gridData2.verticalAlignment = 2;
		this.cmbAttributeType = new Combo(this, 0);
		this.cmbAttributeType.setLayoutData(gridData2);
	}
}