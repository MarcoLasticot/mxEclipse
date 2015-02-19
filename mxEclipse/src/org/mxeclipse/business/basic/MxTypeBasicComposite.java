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
import org.mxeclipse.model.MxTreeType;
import org.mxeclipse.views.IModifyable;

public class MxTypeBasicComposite extends MxBusinessBasicComposite {
	MxTreeType businessType;
	IModifyable view;
	private Label lblName = null;
	private Text txtName = null;
	private Label lblDescription = null;
	private Text txtDescription = null;
	private Label lblType = null;
	private Text txtType = null;
	private Label lblHidden = null;
	private Button chkHidden = null;
	private Button chkAbstract = null;
	private Label lblAbstract = null;
	private Label lblParentType = null;
	private Combo cmbParentType = null;

	public MxTypeBasicComposite(Composite parent, int style, IModifyable view, MxTreeBusiness businessType) {
		super(parent, style);
		this.view = view;
		initialize();
		initializeContent(businessType);
	}

	private void initialize() {
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
		this.lblName = new Label(this, 0);
		this.lblName.setText("Name");
		this.lblName.setLayoutData(gridData1);
		this.txtName = new Text(this, 2048);
		this.txtName.setLayoutData(gridData);
		this.txtName.addKeyListener(new ModifySetter(this.view));
		this.lblDescription = new Label(this, 0);
		this.lblDescription.setText("Description");
		this.txtDescription = new Text(this, 2048);
		this.txtDescription.setLayoutData(gridData2);
		this.txtDescription.addKeyListener(new ModifySetter(this.view));
		this.lblType = new Label(this, 0);
		this.lblType.setText("Type");
		this.txtType = new Text(this, 2048);
		this.txtType.setEnabled(false);
		this.txtType.setLayoutData(gridData3);
		this.txtType.addKeyListener(new ModifySetter(this.view));
		this.lblHidden = new Label(this, 0);
		this.lblHidden.setText("Hidden");
		this.chkHidden = new Button(this, 32);
		this.lblAbstract = new Label(this, 0);
		this.lblAbstract.setText("Abstract");
		this.chkAbstract = new Button(this, 32);
		this.lblParentType = new Label(this, 0);
		this.lblParentType.setText("Parent Type");
		this.chkAbstract.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxTypeBasicComposite.this.view.setModified(true);
			}
		});
		this.chkHidden.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxTypeBasicComposite.this.view.setModified(true);
			}
		});
		setLayout(gridLayout);
		createCmbParentType();
		setSize(new Point(300, 200));
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		try {
			this.businessType = ((MxTreeType)selectedBusiness);
			this.txtName.setText(this.businessType.getName());
			this.txtDescription.setText(this.businessType.getDescription());

			this.chkHidden.setSelection(this.businessType.isHidden());
			this.chkAbstract.setSelection(this.businessType.isAbstractType());
			ArrayList allTypes = MxTreeType.getAllTypes(false);

			String[] sAllTypes = new String[allTypes.size() + 1];
			sAllTypes[0] = "";

			String parentName = this.businessType.getParentType(false) != null ? this.businessType.getParentType(false).getName() : "";
			int selIndex = 0;
			for (int i = 1; i < sAllTypes.length; i++) {
				sAllTypes[i] = ((MxTreeType)allTypes.get(i - 1)).getName();
				if (sAllTypes[i].equals(parentName)) {
					selIndex = i;
				}
			}
			this.cmbParentType.setItems(sAllTypes);
			this.cmbParentType.select(selIndex);
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
			this.businessType.setAbstractType(this.chkAbstract.getSelection());
			this.businessType.setParentType(this.cmbParentType.getSelectionIndex() > 0 ? this.cmbParentType.getItem(this.cmbParentType.getSelectionIndex()) : null);
		} catch (Exception e) {
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getShell(), 
					"Error when trying to store the data to matrix", 
					"Error when trying to store the data to matrix", 
					status);
		}
	}

	private void createCmbParentType() {
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = 4;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.verticalAlignment = 2;
		this.cmbParentType = new Combo(this, 0);
		this.cmbParentType.setLayoutData(gridData4);
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