package org.mxeclipse.business.basic;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreePerson;
import org.mxeclipse.views.IModifyable;

public class MxPersonBasicComposite extends MxBusinessBasicComposite {
	MxTreePerson businessType;
	IModifyable view;
	private Label lblName = null;
	private Text txtName = null;
	private Label lblFullName = null;
	private Text txtFullName = null;
	private Label lblComment = null;
	private Text txtComment = null;
	private Label lblHidden = null;
	private Button chkHidden = null;
	private Label lblUserType = null;
	private Button chkApplicationUser = null;
	private Button chkFullUser = null;
	private Button chkBusinessAdministrator = null;
	private Button chkSystemAdministrator = null;
	private Button chkInactive = null;
	private Button chkTrusted = null;
	private Button[] chkPersonTypes = null;
	private Label lblPassword = null;
	private Combo cmbPasswordOptions = null;
	private Text txtPassword = null;

	public MxPersonBasicComposite(Composite parent, int style, IModifyable view, MxTreeBusiness businessType) {
		super(parent, style);
		this.view = view;
		initialize();
		initializeContent(businessType);
	}

	private void initialize() {
		setSize(new Point(329, 182));
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = 4;
		gridData11.verticalAlignment = 2;
		setSize(new Point(306, 188));
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = 4;
		gridData3.horizontalSpan = 2;
		gridData3.verticalAlignment = 2;
		gridData3.horizontalAlignment = 4;
		gridData3.verticalAlignment = 2;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = 4;
		gridData2.horizontalSpan = 2;
		gridData2.verticalAlignment = 2;
		gridData2.horizontalAlignment = 4;
		gridData2.verticalAlignment = 2;
		GridData gridData1 = new GridData();
		gridData1.widthHint = 70;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		gridData.verticalAlignment = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 2;
		gridData.horizontalAlignment = 4;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		this.lblName = new Label(this, 0);
		this.lblName.setText("Name");
		this.lblName.setLayoutData(gridData1);
		this.txtName = new Text(this, 2048);
		this.txtName.setLayoutData(gridData);
		this.txtName.addKeyListener(new ModifySetter(this.view));
		this.lblFullName = new Label(this, 0);
		this.lblFullName.setText("Full Name");
		this.txtFullName = new Text(this, 2048);
		this.txtFullName.setLayoutData(gridData2);
		this.lblPassword = new Label(this, 0);
		this.lblPassword.setText("Password");
		createCmbPasswordOptions();
		this.txtPassword = new Text(this, 2048);
		this.txtPassword.setEchoChar('*');
		this.txtPassword.setLayoutData(gridData11);
		this.txtFullName.addKeyListener(new ModifySetter(this.view));
		this.lblComment = new Label(this, 0);
		this.lblComment.setText("Comment");
		this.txtComment = new Text(this, 2048);
		this.txtComment.setLayoutData(gridData3);
		this.txtComment.addKeyListener(new ModifySetter(this.view));
		this.lblHidden = new Label(this, 0);
		this.lblHidden.setText("Hidden");
		this.chkHidden = new Button(this, 32);
		this.chkHidden.addSelectionListener(new ModifySelectionSetter(this.view));

		Label filler11 = new Label(this, 0);
		this.lblUserType = new Label(this, 0);
		this.lblUserType.setText("User Type");
		this.chkFullUser = new Button(this, 32);
		this.chkFullUser.setText("Full User");
		this.chkFullUser.addSelectionListener(new ModifySelectionSetter(this.view));
		this.chkApplicationUser = new Button(this, 32);
		this.chkApplicationUser.setText("Application User");
		this.chkApplicationUser.addSelectionListener(new ModifySelectionSetter(this.view));
		Label filler4 = new Label(this, 0);
		this.chkBusinessAdministrator = new Button(this, 32);
		this.chkBusinessAdministrator.setText("Business Admin");
		this.chkBusinessAdministrator.addSelectionListener(new ModifySelectionSetter(this.view));
		this.chkSystemAdministrator = new Button(this, 32);
		this.chkSystemAdministrator.setText("System Admin");
		this.chkSystemAdministrator.addSelectionListener(new ModifySelectionSetter(this.view));
		Label filler6 = new Label(this, 0);
		this.chkInactive = new Button(this, 32);
		this.chkInactive.setText("Inactive");
		this.chkInactive.addSelectionListener(new ModifySelectionSetter(this.view));
		this.chkTrusted = new Button(this, 32);
		this.chkTrusted.setText("Trusted");
		this.chkTrusted.addSelectionListener(new ModifySelectionSetter(this.view));

		this.chkPersonTypes = new Button[] { this.chkApplicationUser, this.chkFullUser, this.chkBusinessAdministrator, this.chkSystemAdministrator, this.chkInactive, this.chkTrusted };
		setLayout(gridLayout);
		setSize(new Point(300, 200));
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		try {
			this.businessType = ((MxTreePerson)selectedBusiness);
			this.txtName.setText(this.businessType.getName());
			this.txtPassword.setText("");
			this.txtFullName.setText(this.businessType.getFullName());
			this.txtComment.setText(this.businessType.getComment());
			this.chkHidden.setSelection(this.businessType.isHidden());

			for (int i = 0; i < this.chkPersonTypes.length; i++) {
				this.chkPersonTypes[i].setSelection(this.businessType.getPersonTypes(false).contains(MxTreePerson.PERSON_TYPES[i]));
			}

			this.cmbPasswordOptions.setItems(MxTreePerson.PASSWORD_OPTIONS);
			for (int i = 0; i < MxTreePerson.PASSWORD_OPTIONS.length; i++) {
				if (this.cmbPasswordOptions.getItem(i).equals(this.businessType.getPasswordOption())) {
					this.cmbPasswordOptions.select(i);
					break;
				}
			}
			passwordEnabling();
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
			this.businessType.setName(this.txtName.getText());
			this.businessType.setFullName(this.txtFullName.getText());
			this.businessType.setPassword(this.txtPassword.getText());
			this.businessType.setComment(this.txtComment.getText());
			this.businessType.setHidden(this.chkHidden.getSelection());

			for (int i = 0; i < this.chkPersonTypes.length; i++) {
				if (this.chkPersonTypes[i].getSelection()) {
					this.businessType.getPersonTypes(false).add(MxTreePerson.PERSON_TYPES[i]);
				} else {
					this.businessType.getPersonTypes(false).remove(MxTreePerson.PERSON_TYPES[i]);
				}
			}

			this.businessType.setPasswordOption(this.cmbPasswordOptions.getItem(this.cmbPasswordOptions.getSelectionIndex()));
		} catch (Exception e) {
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getShell(), 
					"Error when trying to store the data to matrix", 
					"Error when trying to store the data to matrix", 
					status);
		}
	}

	private void createCmbPasswordOptions() {
		GridData gridData4 = new GridData();
		gridData4.horizontalIndent = 0;
		gridData4.widthHint = 80;
		gridData4.heightHint = -1;
		this.cmbPasswordOptions = new Combo(this, 0);
		this.cmbPasswordOptions.setLayoutData(gridData4);
		this.cmbPasswordOptions.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				MxPersonBasicComposite.this.passwordEnabling();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			} } );
	}

	private void passwordEnabling() {
		String selOption = this.cmbPasswordOptions.getItem(this.cmbPasswordOptions.getSelectionIndex());
		if ((selOption.equals("No Password")) || (selOption.equals("Disabled")) || (selOption.equals("Change Required"))) {
			this.txtPassword.setEnabled(false);
			this.txtPassword.setText("");
		} else {
			this.txtPassword.setEnabled(true);
		}
	}

	class ModifySelectionSetter extends SelectionAdapter {
		private IModifyable view;

		public ModifySelectionSetter(IModifyable view) {
			this.view = view;
		}

		public void widgetSelected(SelectionEvent e) {
			this.view.setModified(true);
		}
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