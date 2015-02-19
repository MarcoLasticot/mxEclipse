package org.mxeclipse.business.basic;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeWebNavigation;
import org.mxeclipse.views.IModifyable;

public class MxWebLinkComposite extends MxBusinessBasicComposite {
	MxTreeWebNavigation businessType;
	IModifyable view;
	private Label lblHref = null;
	private Text txtHref = null;
	private Label lblAlt = null;
	private Text txtAlt = null;

	public MxWebLinkComposite(Composite parent, int style, IModifyable view, MxTreeBusiness businessType) {
		super(parent, style);
		this.view = view;
		initialize();
		initializeContent(businessType);
	}

	private void initialize() {
		GridData gridData31 = new GridData();
		gridData31.horizontalAlignment = 4;
		gridData31.grabExcessHorizontalSpace = true;
		gridData31.verticalAlignment = 2;
		GridData gridData21 = new GridData();
		gridData21.horizontalAlignment = 4;
		gridData21.grabExcessHorizontalSpace = true;
		gridData21.verticalAlignment = 4;
		gridData21.grabExcessVerticalSpace = true;

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		this.lblHref = new Label(this, 0);
		this.lblHref.setText("Href");
		this.txtHref = new Text(this, 2626);
		this.txtHref.setLayoutData(gridData21);
		this.txtHref.addKeyListener(new ModifySetter(this.view));

		this.lblAlt = new Label(this, 0);
		this.lblAlt.setText("Alt");
		this.txtAlt = new Text(this, 2048);
		this.txtAlt.setLayoutData(gridData31);
		this.txtAlt.addKeyListener(new ModifySetter(this.view));

		setLayout(gridLayout);
		setSize(new Point(300, 200));
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		try {
			this.businessType = ((MxTreeWebNavigation)selectedBusiness);
			this.txtHref.setText(this.businessType.getHref());
			this.txtAlt.setText(this.businessType.getAlt());
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
			this.businessType.setHref(this.txtHref.getText());
			this.businessType.setAlt(this.txtAlt.getText());
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