package org.mxeclipse.business.basic;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreePerson;
import org.mxeclipse.views.IModifyable;

public class MxPersonRightsComposite extends MxBusinessBasicComposite {
	MxTreePerson businessType;
	MxPersonOneRightComposite accessComposite;
	MxPersonOneRightComposite adminComposite;
	IModifyable view;

	public MxPersonRightsComposite(Composite parent, int style, IModifyable view, MxTreeBusiness businessType) {
		super(parent, style);
		this.view = view;
		initialize();
		initializeContent(businessType);
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		setLayout(gridLayout);
		this.accessComposite = new MxPersonOneRightComposite(this, 0, this.view, false);
		this.accessComposite.initCheckboxes();

		this.adminComposite = new MxPersonOneRightComposite(this, 0, this.view, true);
		this.adminComposite.initCheckboxes();

		setSize(new Point(300, 267));
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		try {
			this.businessType = ((MxTreePerson)selectedBusiness);

			this.accessComposite.setRights(this.businessType.getAccessRights(false));
			this.adminComposite.setRights(this.businessType.getAdminRights(false));
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
			this.businessType.setAccessRights((String[])this.accessComposite.getRights().toArray(new String[this.accessComposite.getRights().size()]));
			this.businessType.setAdminRights((String[])this.adminComposite.getRights().toArray(new String[this.adminComposite.getRights().size()]));
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