package org.mxeclipse.business.basic;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeState;
import org.mxeclipse.views.IModifyable;

public class MxStateBasicComposite extends MxBusinessBasicComposite {
	MxTreeState businessType;
	IModifyable view;
	private Label lblName = null;
	private Text txtName = null;
	private Label lblVersionable = null;
	private Button chkVersionable = null;
	private Label lblRevisionable = null;
	private Button chkRevisionable = null;
	private Label lblPromote = null;
	private Button chkPromote = null;
	private Label lblCheckoutHistory = null;
	private Button chkCheckoutHistory = null;

	public MxStateBasicComposite(Composite parent, int style, IModifyable view, MxTreeBusiness businessType) {
		super(parent, style);
		this.view = view;
		initialize();
		initializeContent(businessType);
	}

	private void initialize() {
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
		this.lblVersionable = new Label(this, 0);
		this.lblVersionable.setText("Versionable");
		this.chkVersionable = new Button(this, 32);
		this.chkVersionable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxStateBasicComposite.this.view.setModified(true);
			}
		});
		this.lblRevisionable = new Label(this, 0);
		this.lblRevisionable.setText("Revisionable");
		this.chkRevisionable = new Button(this, 32);
		this.chkRevisionable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxStateBasicComposite.this.view.setModified(true);
			}
		});
		this.lblPromote = new Label(this, 0);
		this.lblPromote.setText("Promote");
		this.chkPromote = new Button(this, 32);
		this.chkPromote.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxStateBasicComposite.this.view.setModified(true);
			}
		});
		this.lblCheckoutHistory = new Label(this, 0);
		this.lblCheckoutHistory.setText("Checkout history");
		this.chkCheckoutHistory = new Button(this, 32);
		this.chkCheckoutHistory.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxStateBasicComposite.this.view.setModified(true);
			}
		});
		setLayout(gridLayout);
		setSize(new Point(300, 200));
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		try {
			this.businessType = ((MxTreeState)selectedBusiness);
			this.txtName.setText(this.businessType.getName());
			this.chkVersionable.setSelection(this.businessType.isVersionable());
			this.chkRevisionable.setSelection(this.businessType.isRevisionable());
			this.chkPromote.setSelection(this.businessType.isPromote());
			this.chkCheckoutHistory.setSelection(this.businessType.isCheckoutHistory());
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
			this.businessType.setVersionable(this.chkVersionable.getSelection());
			this.businessType.setRevisionable(this.chkRevisionable.getSelection());
			this.businessType.setPromote(this.chkPromote.getSelection());
			this.businessType.setCheckoutHistory(this.chkCheckoutHistory.getSelection());
		} catch (Exception e) {
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getShell(), 
					"Error when trying to store the data to matrix", 
					"Error when trying to store the data to matrix", 
					status);
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