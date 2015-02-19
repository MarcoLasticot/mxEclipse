package org.mxeclipse.business.basic;

import java.util.ArrayList;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.business.table.user.MxUserComposite;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeState;
import org.mxeclipse.model.MxTreeStateUserAccess;
import org.mxeclipse.views.IModifyable;

public class MxStateRightsComposite extends MxBusinessBasicComposite {
	MxTreeState businessType;
	MxUserComposite lstUsers;
	MxPersonOneRightComposite accessComposite;
	Text txtFilter;
	IModifyable view;
	Composite pnlUser;
	ArrayList<MxTreeStateUserAccess> userAccess;
	SashForm top;

	public MxStateRightsComposite(Composite parent, int style, IModifyable view, MxTreeBusiness businessType) {
		super(parent, style);
		this.view = view;
		this.businessType = ((MxTreeState)businessType);
		initialize();
	}

	private void initialize() {
		setLayout(new FillLayout());
		setLayoutData(new GridData(1808));

		this.top = new SashForm(this, 65792);

		this.lstUsers = new MxUserComposite(this.top, 84736, this.businessType, this.view);
		GridData grdUsers = new GridData(4, 4, false, true);
		grdUsers.widthHint = 120;
		this.lstUsers.setLayoutData(grdUsers);
		this.lstUsers.tblObjects.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Table lstUsers = (Table)e.getSource();

				MxTreeStateUserAccess selectedUser = (MxTreeStateUserAccess)lstUsers.getItem(lstUsers.getSelectionIndex()).getData();
				MxStateRightsComposite.this.accessComposite.setRights(selectedUser.getAccessRights());
				MxStateRightsComposite.this.txtFilter.setText(selectedUser.getFilter() != null ? selectedUser.getFilter() : "");
				MxStateRightsComposite.this.accessComposite.setVisible(true);
				MxStateRightsComposite.this.txtFilter.setVisible(true);
			}
		});
		this.pnlUser = new Composite(this.top, 198912);
		GridLayout layUser = new GridLayout();
		layUser.numColumns = 2;
		this.pnlUser.setLayout(layUser);
		this.pnlUser.setLayoutData(new GridData(4, 4, true, true));

		this.accessComposite = new MxPersonOneRightComposite(this.pnlUser, 0, this.view, false);
		this.accessComposite.initCheckboxes();
		this.accessComposite.setVisible(false);

		this.txtFilter = new Text(this.pnlUser, 2880);
		this.txtFilter.setLayoutData(new GridData(4, 4, true, true));
		this.txtFilter.addKeyListener(new ModifySetter(this.view));
		this.txtFilter.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
			}

			public void keyReleased(KeyEvent arg0) {
				if (MxStateRightsComposite.this.lstUsers.tblObjects.getSelectionIndex() >= 0) {
					MxTreeStateUserAccess selectedUser = (MxTreeStateUserAccess)MxStateRightsComposite.this.lstUsers.tblObjects.getItem(MxStateRightsComposite.this.lstUsers.tblObjects.getSelectionIndex()).getData();
					selectedUser.setFilter(MxStateRightsComposite.this.txtFilter.getText());
				}
			}
		});
		this.txtFilter.setVisible(false);

		setSize(new Point(300, 267));
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		try {
			this.businessType = ((MxTreeState)selectedBusiness);

			this.lstUsers.setData(this.businessType);
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
			MxTreeState stateType = this.businessType;
			ArrayList alUserAccess = new ArrayList();
			for (int i = 0; i < this.lstUsers.tblObjects.getItemCount(); i++) {
				alUserAccess.add((MxTreeStateUserAccess)this.lstUsers.tblObjects.getItem(i).getData());
			}
			stateType.setUserAccess(alUserAccess);
			this.accessComposite.setVisible(false);
			this.txtFilter.setVisible(false);
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