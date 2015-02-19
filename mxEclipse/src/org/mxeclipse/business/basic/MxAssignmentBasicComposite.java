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
import org.mxeclipse.model.MxTreeAssignment;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeSite;
import org.mxeclipse.views.IModifyable;

public class MxAssignmentBasicComposite extends MxBusinessBasicComposite {
	MxTreeAssignment businessType;
	IModifyable view;
	private Label lblName = null;
	private Text txtName = null;
	private Label lblDescription = null;
	private Text txtDescription = null;
	private Label lblSite = null;
	private Label lblHidden = null;
	private Button chkHidden = null;
	private Combo cmbSite = null;

	public MxAssignmentBasicComposite(Composite parent, int style, IModifyable view, MxTreeBusiness businessType) {
		super(parent, style);
		this.view = view;
		initialize();
		initializeContent(businessType);
	}

	private void initialize() {
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = 4;
		gridData2.verticalAlignment = 2;
		gridData2.horizontalAlignment = 4;
		gridData2.verticalAlignment = 2;
		GridData gridData1 = new GridData();
		gridData1.widthHint = 70;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 2;
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
		this.lblSite = new Label(this, 0);
		this.lblSite.setText("Site");
		createCmbSite();
		this.cmbSite.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxAssignmentBasicComposite.this.view.setModified(true);
			}
		});
		this.lblHidden = new Label(this, 0);
		this.lblHidden.setText("Hidden");
		this.chkHidden = new Button(this, 32);

		this.chkHidden.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxAssignmentBasicComposite.this.view.setModified(true);
			}
		});
		setLayout(gridLayout);
		setSize(new Point(300, 200));
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		try {
			this.businessType = ((MxTreeAssignment)selectedBusiness);
			this.txtName.setText(this.businessType.getName());
			this.txtDescription.setText(this.businessType.getDescription());

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
			this.businessType.setName(this.txtName.getText());
			this.businessType.setDescription(this.txtDescription.getText());
			this.businessType.setSite(this.cmbSite.getSelectionIndex() > 0 ? new MxTreeSite(this.cmbSite.getItem(this.cmbSite.getSelectionIndex())) : null);
			this.businessType.setHidden(this.chkHidden.getSelection());
		} catch (Exception e) {
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getShell(), 
					"Error when trying to store the data to matrix", 
					"Error when trying to store the data to matrix", 
					status);
		}
	}

	private void createCmbSite() {
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = 4;
		gridData3.verticalAlignment = 2;
		this.cmbSite = new Combo(this, 0);
		this.cmbSite.setLayoutData(gridData3);
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