package org.mxeclipse.business.basic;

import java.util.ArrayList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreePerson;
import org.mxeclipse.model.MxTreeSite;
import org.mxeclipse.model.MxTreeVault;
import org.mxeclipse.views.IModifyable;

public class MxPersonPrivilegesComposite extends MxBusinessBasicComposite {
	MxTreePerson businessType;
	IModifyable view;
	private Label lblAddress = null;
	private Text txtAddress = null;
	private Label lblPhone = null;
	private Text txtPhone = null;
	private Label lblFax = null;
	private Text txtFax = null;
	private Label lblSite = null;
	private Combo cmbSite = null;
	private Label lblEmail = null;
	private Text txtEmail = null;
	private Label lblVault = null;
	private Combo cmbVault = null;
	private List lstObjectAccess = null;

	public MxPersonPrivilegesComposite(Composite parent, int style, IModifyable view, MxTreeBusiness businessType) {
		super(parent, style);
		this.view = view;
		initialize();
		initializeContent(businessType);
	}

	private void initialize() {
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = 4;
		gridData11.verticalAlignment = 2;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = 4;
		gridData3.verticalAlignment = 2;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = 4;
		gridData2.verticalAlignment = 2;
		GridData gridData1 = new GridData();
		gridData1.widthHint = 70;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 2;
		gridData.horizontalAlignment = 4;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.lblAddress = new Label(this, 0);
		this.lblAddress.setText("Address");
		this.lblAddress.setLayoutData(gridData1);
		this.txtAddress = new Text(this, 2048);
		this.txtAddress.setLayoutData(gridData);
		this.txtAddress.addKeyListener(new ModifySetter(this.view));
		this.lblPhone = new Label(this, 0);
		this.lblPhone.setText("Phone");
		this.txtPhone = new Text(this, 2048);
		this.txtPhone.setLayoutData(gridData2);
		this.txtPhone.addKeyListener(new ModifySetter(this.view));
		this.lblFax = new Label(this, 0);
		this.lblFax.setText("Fax");
		this.txtFax = new Text(this, 2048);
		this.txtFax.setLayoutData(gridData3);
		this.txtFax.addKeyListener(new ModifySetter(this.view));
		this.lblEmail = new Label(this, 0);
		this.lblEmail.setText("Email");
		this.txtEmail = new Text(this, 2048);
		this.txtEmail.setLayoutData(gridData11);
		this.txtEmail.addKeyListener(new ModifySetter(this.view));
		this.lblSite = new Label(this, 0);
		this.lblSite.setText("Site");

		setLayout(gridLayout);
		createCmbSite();
		this.cmbSite.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxPersonPrivilegesComposite.this.view.setModified(true);
			}
		});
		setSize(new Point(300, 267));
		this.lblVault = new Label(this, 0);
		this.lblVault.setText("Vault");
		createCmbVault();
		Label filler = new Label(this, 0);
		this.lstObjectAccess = new List(this, 0);
		this.cmbVault.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxPersonPrivilegesComposite.this.view.setModified(true);
			}
		});
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		try {
			this.businessType = ((MxTreePerson)selectedBusiness);
			this.txtAddress.setText(this.businessType.getAddress());
			this.txtPhone.setText(this.businessType.getPhone());
			this.txtFax.setText(this.businessType.getFax());
			this.txtEmail.setText(this.businessType.getEmail());

			ArrayList allStores = MxTreeSite.getAllSites(false);
			String[] sAllSites = new String[allStores.size() + 1];
			sAllSites[0] = "";

			MxTreeSite site = this.businessType.getSite(true) != null ? this.businessType.getSite(false) : null;
			int selIndex = 0;

			for (int i = 1; i < sAllSites.length; i++) {
				sAllSites[i] = ((MxTreeSite)allStores.get(i - 1)).getName();
				if ((site != null) && (sAllSites[i].equals(site.getName()))) {
					selIndex = i;
				}
			}

			this.cmbSite.setItems(sAllSites);
			this.cmbSite.select(selIndex);

			ArrayList allVaults = MxTreeVault.getAllVaults(false);
			String[] sAllVaults = new String[allVaults.size() + 1];
			sAllVaults[0] = "";

			MxTreeVault vault = this.businessType.getVault(true) != null ? this.businessType.getVault(false) : null;
			selIndex = 0;

			for (int i = 1; i < sAllVaults.length; i++) {
				sAllVaults[i] = ((MxTreeVault)allVaults.get(i - 1)).getName();
				if ((vault != null) && (sAllVaults[i].equals(vault.getName()))) {
					selIndex = i;
				}
			}

			this.cmbVault.setItems(sAllVaults);
			this.cmbVault.select(selIndex);
		} catch (Exception e) {
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getShell(), 
					"Error when trying to initialize data in the basic form", 
					"Error when trying to initialize data in the basic form", 
					status);
		}
	}

	public void storeData() {
		try {
			this.businessType.setAddress(this.txtAddress.getText());
			this.businessType.setPhone(this.txtPhone.getText());
			this.businessType.setFax(this.txtFax.getText());
			this.businessType.setEmail(this.txtEmail.getText());
			this.businessType.setSite(this.cmbSite.getSelectionIndex() > 0 ? new MxTreeSite(this.cmbSite.getItem(this.cmbSite.getSelectionIndex())) : null);
			this.businessType.setVault(this.cmbVault.getSelectionIndex() > 0 ? new MxTreeVault(this.cmbVault.getItem(this.cmbVault.getSelectionIndex())) : null);
		} catch (Exception e) {
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getShell(), 
					"Error when trying to store the data to matrix", 
					"Error when trying to store the data to matrix", 
					status);
		}
	}

	private void createCmbSite() {
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = 4;
		gridData5.verticalAlignment = 2;
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = 4;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.verticalAlignment = 2;
		this.cmbSite = new Combo(this, 0);
		this.cmbSite.setLayoutData(gridData5);
		this.cmbSite.setLayoutData(gridData4);
	}

	private void createCmbVault() {
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = 4;
		gridData6.verticalAlignment = 2;
		this.cmbVault = new Combo(this, 0);
		this.cmbVault.setLayoutData(gridData6);
	}

	class ModifySetter implements KeyListener {
		private IModifyable view;

		public ModifySetter(IModifyable view) {
			this.view = view;
		}
		public void keyReleased(KeyEvent e) {
		}
		public void keyPressed(KeyEvent e) {
			this.view.setModified(true);
		}
	}
}