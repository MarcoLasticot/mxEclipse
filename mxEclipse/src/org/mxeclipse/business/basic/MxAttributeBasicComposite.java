package org.mxeclipse.business.basic;

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
import org.mxeclipse.model.MxTreeAttribute;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.views.IModifyable;

public class MxAttributeBasicComposite extends MxBusinessBasicComposite
{
	MxTreeAttribute attribute;
	IModifyable view;
	private Label lblName = null;
	private Text txtName = null;
	private Label lblDescription = null;
	private Text txtDescription = null;
	private Label lblType = null;
	private Text txtType = null;
	private Label lblDefault = null;
	private Text txtDefault = null;
	private Label lblHidden = null;
	private Button chkHidden = null;

	public MxAttributeBasicComposite(Composite parent, int style, IModifyable view, MxTreeBusiness attribute) {
		super(parent, style);
		this.view = view;
		initialize();
		initializeContent(attribute);
	}

	private void initialize() {
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = 4;
		gridData4.verticalAlignment = 2;
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
		this.lblDefault = new Label(this, 0);
		this.lblDefault.setText("Default");
		this.txtDefault = new Text(this, 2048);
		this.txtDefault.setLayoutData(gridData4);
		this.txtDefault.addKeyListener(new ModifySetter(this.view));
		this.lblHidden = new Label(this, 0);
		this.lblHidden.setText("Hidden");
		this.chkHidden = new Button(this, 32);
		this.chkHidden.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MxAttributeBasicComposite.this.view.setModified(true);
			}
		});
		setLayout(gridLayout);
		setSize(new Point(300, 200));
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		this.attribute = ((MxTreeAttribute)selectedBusiness);
		this.txtName.setText(this.attribute.getName());
		this.txtDescription.setText(this.attribute.getDescription());
		this.txtType.setText(this.attribute.getAttributeType());
		this.txtDefault.setText(this.attribute.getDefaultValue());
		this.chkHidden.setSelection(this.attribute.isHidden());
	}

	public void storeData() {
		this.attribute.setName(this.txtName.getText());
		this.attribute.setDescription(this.txtDescription.getText());
		this.attribute.setDefaultValue(this.txtDefault.getText());
		this.attribute.setAttributeType(this.txtType.getText());
		this.attribute.setHidden(this.chkHidden.getSelection());
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