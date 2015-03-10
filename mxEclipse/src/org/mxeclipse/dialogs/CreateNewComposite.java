package org.mxeclipse.dialogs;

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
import org.mxeclipse.model.MxTreeDomainObject;
import org.mxeclipse.model.MxTreePolicy;
import org.mxeclipse.model.MxTreeType;
import org.mxeclipse.model.MxTreeVault;

public class CreateNewComposite extends Composite {
	private Label lblType = null;
	private Combo cmbType = null;
	private Label lblName = null;
	private Text txtName = null;
	private Label lblRevision = null;
	private Text txtRevision = null;
	private Label lblPolicy = null;
	private Combo cmbPolicy = null;
	private Label lblVault = null;
	private Combo cmbVault = null;
	private MxTreeDomainObject treeObject;
	private Label lblSequence = null;

	public CreateNewComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
		initializeContent();
	}

	private void initialize() {
		GridData gridData11 = new GridData();
		gridData11.grabExcessHorizontalSpace = true;
		gridData11.verticalAlignment = 2;
		gridData11.horizontalAlignment = 4;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = 4;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.horizontalSpan = 2;
		gridData1.verticalAlignment = 2;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 2;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		this.lblType = new Label(this, 0);
		this.lblType.setText("Type");
		createCmbType();
		this.lblName = new Label(this, 0);
		this.lblName.setText("Name");
		this.txtName = new Text(this, 2048);
		this.txtName.setLayoutData(gridData1);
		this.lblRevision = new Label(this, 0);
		this.lblRevision.setText("Revision");
		this.txtRevision = new Text(this, 2048);
		this.lblSequence = new Label(this, 0);
		this.lblSequence.setText("");
		this.lblSequence.setLayoutData(gridData11);
		this.lblPolicy = new Label(this, 0);
		this.lblPolicy.setText("Policy");
		setLayout(gridLayout);
		createCmbPolicy();
		setSize(new Point(300, 142));
		this.lblVault = new Label(this, 0);
		this.lblVault.setText("Vault");
		createCmbVault();
	}

	private void initializeContent() {
		try {
			this.cmbType.setItems(MxTreeType.getAllTypeNames(false));
			this.cmbVault.setItems(MxTreeVault.getAllVaultNames(false));
			for (int i = 0; i < this.cmbVault.getItems().length; i++) {
				if (this.cmbVault.getItem(i).equals("eService Production")) {
					this.cmbVault.select(i);
					this.cmbVault.setSelection(new Point(0, -1));
				}
			}
		} catch (Exception ex) {
			MessageDialog.openInformation(getShell(), "Create New", "Error when trying to get combo boxes values " + ex.getMessage());
		}
	}

	private void fillInPolicyCombo() {
		try {
			String type = this.cmbType.getText();
			MxTreeType oType = new MxTreeType(type);
			this.cmbPolicy.setItems(oType.getPolicyNames(false));
			if (this.cmbPolicy.getItemCount() > 0) {
				this.cmbPolicy.select(0);
			}
		} catch (Exception ex) {
			MessageDialog.openInformation(getShell(), "Create New", "Error when trying to get policy combo boxes values " + ex.getMessage());
		}
	}

	private void createCmbType() {
		GridData gridData4 = new GridData();
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.verticalAlignment = 2;
		gridData4.horizontalSpan = 2;
		gridData4.horizontalAlignment = 4;
		this.cmbType = new Combo(this, 0);
		this.cmbType.setLayoutData(gridData4);
		this.cmbType.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				CreateNewComposite.this.fillInPolicyCombo();
				CreateNewComposite.this.fillInRevision();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createCmbPolicy() {
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = 2;
		gridData2.horizontalSpan = 2;
		gridData2.horizontalAlignment = 4;
		this.cmbPolicy = new Combo(this, 0);
		this.cmbPolicy.setLayoutData(gridData2);
		this.cmbPolicy.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				CreateNewComposite.this.fillInRevision();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createCmbVault() {
		GridData gridData3 = new GridData();
		gridData3.grabExcessVerticalSpace = false;
		gridData3.horizontalAlignment = 4;
		gridData3.verticalAlignment = 2;
		gridData3.horizontalSpan = 2;
		gridData3.grabExcessHorizontalSpace = true;
		this.cmbVault = new Combo(this, 0);
		this.cmbVault.setLayoutData(gridData3);
	}

	private void fillInRevision() {
		try {
			if (this.cmbPolicy.getSelectionIndex() < 0) {
				return;
			}
			MxTreePolicy policy = new MxTreePolicy(this.cmbPolicy.getItem(this.cmbPolicy.getSelectionIndex()));
			policy.fillBasics();
			String seq = policy.getSequence();
			this.lblSequence.setText(seq);
			if (seq.indexOf(",") > 0) {
				this.txtRevision.setText(seq.substring(0, seq.indexOf(',')));
			}
			else {
				this.txtRevision.setText(seq);
			}
		} catch (Exception ex) {
			MessageDialog.openInformation(getShell(), "Create New", "Error when trying to get revision sequence: " + ex.getMessage());
		}
	}

	public boolean okPressed() {
		try {
			if (this.cmbType.getSelectionIndex() < 0) {
				MessageDialog.openInformation(getShell(), "Create New", "Please select a type!");
				return false;
			}
			if (this.txtName.getText().equals("")) {
				MessageDialog.openInformation(getShell(), "Create New", "Please select type in name!");
				return false;
			}
			if (this.cmbPolicy.getSelectionIndex() < 0) {
				MessageDialog.openInformation(getShell(), "Create New", "Please select a policy!");
				return false;
			}
			if (this.cmbVault.getSelectionIndex() < 0) {
				MessageDialog.openInformation(getShell(), "Create New", "Please select a vault!");
				return false;
			}
			this.treeObject = MxTreeDomainObject.createNewObject(this.cmbType.getItem(this.cmbType.getSelectionIndex()), this.txtName.getText(), this.txtRevision.getText(), this.cmbPolicy.getItem(this.cmbPolicy.getSelectionIndex()), this.cmbVault.getItem(this.cmbVault.getSelectionIndex()));
			return true;
		} catch (Exception ex) {
			MessageDialog.openInformation(getShell(), "Create New", "Error when trying to get create a new object: " + ex.getMessage());
		}
		return false;
	}

	public MxTreeDomainObject getNewObject() {
		return this.treeObject;
	}

	public void setTypes(String[] types) {
		this.cmbType.setItems(types);
		this.cmbPolicy.removeAll();
		this.txtRevision.setText("");
	}
}