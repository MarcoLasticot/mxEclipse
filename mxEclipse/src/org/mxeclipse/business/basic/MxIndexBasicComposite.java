package org.mxeclipse.business.basic;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeIndex;
import org.mxeclipse.views.IModifyable;

public class MxIndexBasicComposite extends MxBusinessBasicComposite {
	MxTreeIndex businessType;
	IModifyable view;
	private Label lblName = null;
	private Text txtName = null;
	private Label lblDescription = null;
	private Text txtDescription = null;
	private Label lblUnique = null;
	private Button chkUnique = null;
	private Button cmdEnableIndex = null;
	private Button cmdValidateIndex = null;

	public MxIndexBasicComposite(Composite parent, int style, IModifyable view, MxTreeBusiness businessType) {
		super(parent, style);
		this.view = view;
		initialize();
		initializeContent(businessType);
	}

	private void initialize() {
		GridData gridData11 = new GridData();
		gridData11.horizontalSpan = 2;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = 4;
		gridData3.verticalAlignment = 2;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = 4;
		gridData2.horizontalSpan = 2;
		gridData2.verticalAlignment = 2;
		GridData gridData1 = new GridData();
		gridData1.widthHint = 70;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 2;
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = 4;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
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
		this.lblUnique = new Label(this, 0);
		this.lblUnique.setText("Unique");
		this.chkUnique = new Button(this, 32);

		this.chkUnique.setLayoutData(gridData11);
		Label filler = new Label(this, 0);
		this.cmdEnableIndex = new Button(this, 0);
		this.cmdEnableIndex.setText("Enable Index");
		this.cmdEnableIndex.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					if (MxIndexBasicComposite.this.view.isModified()) {
						MessageDialog.openInformation(MxIndexBasicComposite.this.getShell(), "Save needed", "Please save the index first");
						return;
					}
					MxIndexBasicComposite.this.businessType.setEnabled(!MxIndexBasicComposite.this.businessType.isEnabled());
					MxIndexBasicComposite.this.cmdEnableIndex.setText(MxIndexBasicComposite.this.businessType.isEnabled() ? "Disable Index" : "Enable Index");
					MxIndexBasicComposite.this.cmdValidateIndex.setEnabled(MxIndexBasicComposite.this.businessType.isEnabled());
					MessageDialog.openInformation(MxIndexBasicComposite.this.getShell(), "Index " + (MxIndexBasicComposite.this.businessType.isEnabled() ? "enabled" : "disabled"), "Index has been successfully " + (MxIndexBasicComposite.this.businessType.isEnabled() ? "enabled" : "disabled") + "!");
				} catch (Exception ex) {
					Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
					ErrorDialog.openError(MxIndexBasicComposite.this.getShell(), 
							"Error when trying to enable index", 
							"Error when trying to enable index: " + ex.getMessage(), 
							status);
				}
			}
		});
		this.cmdValidateIndex = new Button(this, 0);
		this.cmdValidateIndex.setText("Validate Index");
		this.cmdValidateIndex.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					MxIndexBasicComposite.this.businessType.validate();
					MessageDialog.openInformation(MxIndexBasicComposite.this.getShell(), "Index validated", "Index has been successfully validated!");
				} catch (Exception ex) {
					Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
					ErrorDialog.openError(MxIndexBasicComposite.this.getShell(), 
							"Error when trying to validate index", 
							"Error when trying to validate index: " + ex.getMessage(), 
							status);
				}
			}
		});
		this.chkUnique.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxIndexBasicComposite.this.view.setModified(true);
			}
		});
		setLayout(gridLayout);
		createCmbStore();
		setSize(new Point(300, 200));
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		try {
			this.businessType = ((MxTreeIndex)selectedBusiness);
			this.txtName.setText(this.businessType.getName());
			this.txtDescription.setText(this.businessType.getDescription());

			this.chkUnique.setSelection(this.businessType.isUnique());
			this.cmdEnableIndex.setText(this.businessType.isEnabled() ? "Disable Index" : "Enable Index");
			this.cmdValidateIndex.setEnabled(this.businessType.isEnabled());
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

			this.businessType.setUnique(this.chkUnique.getSelection());
		} catch (Exception e) {
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getShell(), 
					"Error when trying to store the data to matrix", 
					"Error when trying to store the data to matrix", 
					status);
		}
	}

	private void createCmbStore() {
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = 4;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.verticalAlignment = 2;
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