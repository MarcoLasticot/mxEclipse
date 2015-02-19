package org.mxeclipse.business.basic;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeWebColumn;
import org.mxeclipse.views.IModifyable;

public class MxWebColumnExpressionComposite extends MxBusinessBasicComposite {
	MxTreeWebColumn businessType;
	IModifyable view;
	private Combo cmbColumnType = null;
	private Label lblColumnType = null;
	private Label lblExpression = null;
	private Text txtExpression = null;

	public MxWebColumnExpressionComposite(Composite parent, int style, IModifyable view, MxTreeBusiness businessType) { 
		super(parent, style);
		this.view = view;
		initialize();
		initializeContent(businessType);
	}

	private void initialize() {
		GridData gridData12 = new GridData();
		gridData12.horizontalAlignment = 4;
		gridData12.grabExcessHorizontalSpace = true;
		gridData12.grabExcessVerticalSpace = true;
		gridData12.verticalAlignment = 4;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = 4;
		gridData3.verticalAlignment = 2;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		setLayout(gridLayout);
		this.lblColumnType = new Label(this, 0);
		this.lblColumnType.setText("Column Type");
		createCmbColumnType();
		setSize(new Point(300, 168));
		this.lblExpression = new Label(this, 0);
		this.lblExpression.setText("Expression");
		this.txtExpression = new Text(this, 2626);
		this.txtExpression.setLayoutData(gridData12);
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		try {
			this.businessType = ((MxTreeWebColumn)selectedBusiness);

			this.cmbColumnType.setItems(MxTreeWebColumn.COLUMN_TYPES);
			for (int i = 0; i < this.cmbColumnType.getItemCount(); i++) {
				if (this.cmbColumnType.getItem(i).equals(this.businessType.getColumnType())) {
					this.cmbColumnType.select(i);
					break;
				}
			}
			this.txtExpression.setText(this.businessType.getExpression());
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
			this.businessType.setColumnType(this.cmbColumnType.getText());
			this.businessType.setExpression(this.txtExpression.getText());
		} catch (Exception e) {
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getShell(), 
					"Error when trying to store the data to matrix", 
					"Error when trying to store the data to matrix", 
					status);
		}
	}

	private void createCmbColumnType() {
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = 1;
		gridData4.grabExcessVerticalSpace = false;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.verticalAlignment = 2;
		this.cmbColumnType = new Combo(this, 0);
		this.cmbColumnType.setLayoutData(gridData4);
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