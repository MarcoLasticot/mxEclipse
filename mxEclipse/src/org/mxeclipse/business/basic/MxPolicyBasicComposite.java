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
import org.mxeclipse.model.MxTreePolicy;
import org.mxeclipse.model.MxTreeStore;
import org.mxeclipse.views.IModifyable;

public class MxPolicyBasicComposite extends MxBusinessBasicComposite {
	MxTreePolicy businessType;
	IModifyable view;
	private Label lblName = null;
	private Text txtName = null;
	private Label lblDescription = null;
	private Text txtDescription = null;
	private Label lblSequence = null;
	private Text txtSequence = null;
	private Label lblHidden = null;
	private Button chkHidden = null;
	private Label lblStore = null;
	private Combo cmbStore = null;

	public MxPolicyBasicComposite(Composite parent, int style, IModifyable view, MxTreeBusiness businessType) {
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
		this.lblSequence = new Label(this, 0);
		this.lblSequence.setText("Sequence");
		this.txtSequence = new Text(this, 2048);
		this.txtSequence.setLayoutData(gridData3);
		this.txtSequence.addKeyListener(new ModifySetter(this.view));
		this.lblHidden = new Label(this, 0);
		this.lblHidden.setText("Hidden");
		this.chkHidden = new Button(this, 32);

		this.lblStore = new Label(this, 0);
		this.lblStore.setText("Store");

		this.chkHidden.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxPolicyBasicComposite.this.view.setModified(true);
			}
		});
		setLayout(gridLayout);
		createCmbStore();
		this.cmbStore.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxPolicyBasicComposite.this.view.setModified(true);
			}
		});
		setSize(new Point(300, 200));
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		try {
			this.businessType = ((MxTreePolicy)selectedBusiness);
			this.txtName.setText(this.businessType.getName());
			this.txtDescription.setText(this.businessType.getDescription());

			this.chkHidden.setSelection(this.businessType.isHidden());
			ArrayList allStores = MxTreeStore.getAllStores(false);
			this.txtSequence.setText(this.businessType.getSequence());

			String[] sAllStores = new String[allStores.size() + 1];
			sAllStores[0] = "";

			MxTreeStore store = this.businessType.getStore(true) != null ? this.businessType.getStore(false) : null;
			int selIndex = 0;

			for (int i = 1; i < sAllStores.length; i++) {
				sAllStores[i] = ((MxTreeStore)allStores.get(i - 1)).getName();
				if ((store != null) && (sAllStores[i].equals(store.getName()))) {
					selIndex = i;
				}
			}

			this.cmbStore.setItems(sAllStores);
			this.cmbStore.select(selIndex);
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

			this.businessType.setSequence(this.txtSequence.getText());
			this.businessType.setHidden(this.chkHidden.getSelection());

			this.businessType.setStore(this.cmbStore.getSelectionIndex() > 0 ? new MxTreeStore(this.cmbStore.getItem(this.cmbStore.getSelectionIndex())) : null);
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
		this.cmbStore = new Combo(this, 0);
		this.cmbStore.setLayoutData(gridData4);
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