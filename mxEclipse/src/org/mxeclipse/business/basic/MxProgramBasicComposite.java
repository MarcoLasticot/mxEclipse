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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreePerson;
import org.mxeclipse.model.MxTreeProgram;
import org.mxeclipse.views.IModifyable;

public class MxProgramBasicComposite extends MxBusinessBasicComposite {
	MxTreeProgram businessType;
	IModifyable view;
	private Label lblName = null;
	private Text txtName = null;
	private Label lblFullName = null;
	private Text txtDescription = null;
	private Label lblHidden = null;
	private Button chkHidden = null;
	private Label lblProgramType = null;
	private Button radProgramTypeMql = null;
	private Button radProgramTypeJava = null;
	private Button radProgramTypeExternal = null;
	private Button[] radProgramTypes = null;
	private Label lblNeedsBusinessObject = null;
	private Button chkNeedsBusinessObject = null;
	private Label lblExecute = null;
	private Button radImmediate = null;
	private Button radDeferred = null;
	private Label lblDownloadable = null;
	private Button chkDownloadable = null;
	private Label lblPiped = null;
	private Button chkPiped = null;
	private Label lblPooled = null;
	private Button chkPooled = null;
	private Composite grpExecute = null;
	private Label lblUser = null;
	private Combo cmbUser = null;

	public MxProgramBasicComposite(Composite parent, int style, IModifyable view, MxTreeBusiness businessType) {
		super(parent, style);
		this.view = view;
		initialize();
		initializeContent(businessType);
	}

	private void initialize() {
		GridData gridData3 = new GridData();
		GridData gridData21 = new GridData();
		GridData gridData11 = new GridData();
		gridData11.horizontalSpan = 2;
		setSize(new Point(412, 200));
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = 4;
		gridData2.horizontalSpan = 3;
		gridData2.verticalAlignment = 2;
		gridData2.horizontalAlignment = 4;
		gridData2.verticalAlignment = 2;
		GridData gridData1 = new GridData();
		gridData1.widthHint = 70;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 3;
		gridData.verticalAlignment = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 2;
		gridData.horizontalAlignment = 4;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		this.lblName = new Label(this, 0);
		this.lblName.setText("Name");
		this.lblName.setLayoutData(gridData1);
		this.txtName = new Text(this, 2048);
		this.txtName.setLayoutData(gridData);
		this.txtName.addKeyListener(new ModifySetter(this.view));
		this.lblFullName = new Label(this, 0);
		this.lblFullName.setText("Description");
		this.txtDescription = new Text(this, 2048);
		this.txtDescription.setLayoutData(gridData2);
		this.txtDescription.addKeyListener(new ModifySetter(this.view));
		this.lblHidden = new Label(this, 0);
		this.lblHidden.setText("Hidden");
		this.chkHidden = new Button(this, 32);
		Label filler5 = new Label(this, 0);
		Label filler14 = new Label(this, 0);
		this.chkHidden.addSelectionListener(new ModifySelectionSetter(this.view));

		this.lblProgramType = new Label(this, 0);
		this.lblProgramType.setText("Program Type");
		this.radProgramTypeJava = new Button(this, 16);
		this.radProgramTypeJava.setText("Java");
		this.radProgramTypeMql = new Button(this, 16);
		this.radProgramTypeMql.setText("Mql");
		this.radProgramTypeExternal = new Button(this, 16);
		this.radProgramTypeExternal.setText("External");
		this.radProgramTypeJava.addSelectionListener(new ModifySelectionSetter(this.view));
		this.radProgramTypeMql.addSelectionListener(new ModifySelectionSetter(this.view));
		this.radProgramTypeExternal.addSelectionListener(new ModifySelectionSetter(this.view));
		this.lblExecute = new Label(this, 0);
		this.lblExecute.setText("Execute");
		createGrpExecute();
		this.radImmediate = new Button(this.grpExecute, 16);
		this.radImmediate.setText("Immediate");
		this.radImmediate.setLayoutData(gridData11);
		this.radDeferred = new Button(this.grpExecute, 16);
		this.radDeferred.setText("Deferred");
		this.lblNeedsBusinessObject = new Label(this, 0);
		this.lblNeedsBusinessObject.setText("Needs Business Object");

		this.chkNeedsBusinessObject = new Button(this, 32);

		this.lblDownloadable = new Label(this, 0);
		this.lblDownloadable.setText("Downloadable");
		this.chkDownloadable = new Button(this, 32);
		this.lblPiped = new Label(this, 0);
		this.lblPiped.setText("Piped");
		this.chkPiped = new Button(this, 32);

		this.lblPooled = new Label(this, 0);
		this.lblPooled.setText("Pooled");
		this.lblPooled.setLayoutData(gridData3);
		this.chkPooled = new Button(this, 32);
		this.chkPooled.setLayoutData(gridData21);
		this.lblUser = new Label(this, 0);
		this.lblUser.setText("User");
		this.radImmediate.addSelectionListener(new ModifySelectionSetter(this.view));
		this.radDeferred.addSelectionListener(new ModifySelectionSetter(this.view));

		this.radProgramTypeMql.addSelectionListener(new ModifySelectionSetter(this.view));
		this.radProgramTypeExternal.addSelectionListener(new ModifySelectionSetter(this.view));
		this.chkNeedsBusinessObject.addSelectionListener(new ModifySelectionSetter(this.view));
		this.chkDownloadable.addSelectionListener(new ModifySelectionSetter(this.view));
		this.chkPiped.addSelectionListener(new ModifySelectionSetter(this.view));
		this.chkPooled.addSelectionListener(new ModifySelectionSetter(this.view));
		this.radProgramTypes = new Button[] { this.radProgramTypeJava, this.radProgramTypeMql, this.radProgramTypeExternal };
		setLayout(gridLayout);
		createCmbUser();
		setSize(new Point(300, 200));
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		try {
			this.businessType = ((MxTreeProgram)selectedBusiness);
			this.txtName.setText(this.businessType.getName());
			this.txtDescription.setText(this.businessType.getDescription());
			this.chkHidden.setSelection(this.businessType.isHidden());
			this.radImmediate.setSelection(this.businessType.isImmediate());
			this.radDeferred.setSelection(!this.businessType.isImmediate());
			this.chkNeedsBusinessObject.setSelection(this.businessType.isNeedsBusinessObject());
			this.chkDownloadable.setSelection(this.businessType.isDownloadable());
			this.chkPooled.setSelection(this.businessType.isPooled());
			this.chkPiped.setSelection(this.businessType.isPiped());

			for (int i = 0; i < this.radProgramTypes.length; i++) {
				this.radProgramTypes[i].setSelection(this.businessType.getProgramType().contains(MxTreeProgram.PROGRAM_TYPES[i]));
			}

			ArrayList allUsers = MxTreePerson.getAllPersons(false);
			String[] sAllUsers = new String[allUsers.size() + 1];
			sAllUsers[0] = "";

			String userName = this.businessType.getUser() != null ? this.businessType.getUser().getName() : "";
			int selIndex = 0;
			for (int i = 1; i < sAllUsers.length; i++) {
				sAllUsers[i] = ((MxTreePerson)allUsers.get(i - 1)).getName();
				if (sAllUsers[i].equals(userName)) {
					selIndex = i;
				}
			}
			this.cmbUser.setItems(sAllUsers);
			this.cmbUser.select(selIndex);
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
			this.businessType.setDescription(this.txtDescription.getText());
			this.businessType.setHidden(this.chkHidden.getSelection());
			this.businessType.setImmediate(!this.radDeferred.getSelection());
			this.businessType.setNeedsBusinessObject(this.chkNeedsBusinessObject.getSelection());
			this.businessType.setDownloadable(this.chkDownloadable.getSelection());
			this.businessType.setPooled(this.chkPooled.getSelection());
			this.businessType.setPiped(this.chkPiped.getSelection());

			for (int i = 0; i < this.radProgramTypes.length; i++) {
				if (this.radProgramTypes[i].getSelection()) {
					this.businessType.setProgramType(MxTreeProgram.PROGRAM_TYPES[i]);
					break;
				}
			}

			this.businessType.setUser(this.cmbUser.getSelectionIndex() > 0 ? new MxTreePerson(this.cmbUser.getItem(this.cmbUser.getSelectionIndex())) : null);
		} catch (Exception e) {
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getShell(), 
					"Error when trying to store the data to matrix", 
					"Error when trying to store the data to matrix", 
					status);
		}
	}

	private void createGrpExecute() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 3;
		GridData gridData4 = new GridData();
		gridData4.horizontalSpan = 3;

		this.grpExecute = new Composite(this, 0);
		this.grpExecute.setLayoutData(gridData4);
		this.grpExecute.setLayout(gridLayout1);
	}

	private void createCmbUser() {
		GridData gridData5 = new GridData();
		gridData5.horizontalSpan = 3;
		gridData5.verticalAlignment = 2;
		gridData5.horizontalAlignment = 4;
		this.cmbUser = new Combo(this, 0);
		this.cmbUser.setLayoutData(gridData5);
		this.cmbUser.addSelectionListener(new ModifySelectionSetter(this.view));
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