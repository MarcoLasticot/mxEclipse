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
import org.mxeclipse.model.MxTreeWebTable;
import org.mxeclipse.views.IModifyable;

public class MxWebTableBasicComposite extends MxBusinessBasicComposite {
	MxTreeWebTable businessType;
	IModifyable view;
	private Label lblName = null;
	private Text txtName = null;
	private Label lblDescription = null;
	private Text txtDescription = null;
	private Label lblHidden = null;
	private Button chkHidden = null;

	public MxWebTableBasicComposite(Composite parent, int style, IModifyable view, MxTreeBusiness businessType) {
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
		this.lblHidden = new Label(this, 0);
		this.lblHidden.setText("Unique");
		this.chkHidden = new Button(this, 32);

		this.chkHidden.setLayoutData(gridData11);
		Label filler = new Label(this, 0);
		this.chkHidden.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxWebTableBasicComposite.this.view.setModified(true);
			}
		});
		setLayout(gridLayout);
		createCmbStore();
		setSize(new Point(300, 200));
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		try {
			this.businessType = ((MxTreeWebTable)selectedBusiness);
			this.txtName.setText(this.businessType.getName());
			this.txtDescription.setText(this.businessType.getDescription());
			this.chkHidden.setSelection(this.businessType.isHidden());
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
			MessageDialog.openInformation(getShell(), "Save not available!", "Table save is not implemented at the moment!");
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