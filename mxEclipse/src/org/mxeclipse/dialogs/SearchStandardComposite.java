package org.mxeclipse.dialogs;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.model.MxAttribute;
import org.mxeclipse.model.MxObjectSearchCriteria;
import org.mxeclipse.model.MxTreeDomainObject;
import org.mxeclipse.utils.MxEclipseUtils;

public class SearchStandardComposite extends Composite implements ISearchComposite {
	private Text txtType = null;
	private Label lblName = null;
	private Text txtName = null;
	private Label lblId = null;
	private Text txtId = null;
	private Label lblRevision = null;
	private Text txtRevision = null;
	private Label lblDescription = null;
	private Text txtDescription = null;
	private Label lblPolicy = null;
	private Text txtPolicy = null;
	private Label lblState = null;
	private Text txtState = null;
	private MxObjectSearchCriteria searchCriteria;
	private Composite pnlAttributes = null;
	private ScrolledComposite scPanel = null;

	private ArrayList<AttributeRow> lstAttributes = new ArrayList();

	private List<MxTreeDomainObject> treeObjectList = new ArrayList();

	public SearchStandardComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 5;
		setLayout(gridLayout1);
		setSize(new Point(380, 107));
		GridData gridData9 = new GridData();
		gridData9.verticalAlignment = 2;
		gridData9.horizontalSpan = 4;
		gridData9.grabExcessHorizontalSpace = true;
		gridData9.horizontalAlignment = 4;
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = 4;
		gridData4.verticalAlignment = 2;
		gridData4.horizontalSpan = 4;
		gridData4.grabExcessHorizontalSpace = true;
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = 2;
		gridData2.horizontalSpan = 4;
		gridData2.horizontalAlignment = 4;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 2;
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = 4;
		GridData gridDataDesc = new GridData();
		gridDataDesc.grabExcessHorizontalSpace = true;
		gridDataDesc.verticalAlignment = 2;
		gridDataDesc.horizontalSpan = 4;
		gridDataDesc.horizontalAlignment = 4;
		GridData gridDataPolicy = new GridData();
		gridDataPolicy.grabExcessHorizontalSpace = true;
		gridDataPolicy.verticalAlignment = 2;
		gridDataPolicy.horizontalSpan = 4;
		gridDataPolicy.horizontalAlignment = 4;
		GridData gridDataState = new GridData();
		gridDataState.grabExcessHorizontalSpace = true;
		gridDataState.verticalAlignment = 2;
		gridDataState.horizontalSpan = 4;
		gridDataState.horizontalAlignment = 4;

		this.lblId = new Label(this, 0);
		this.lblId.setText("Id");
		this.txtId = new Text(this, 2048);
		this.txtId.setLayoutData(gridData4);
		Label lblType = new Label(this, 0);
		lblType.setText("Type");
		this.txtType = new Text(this, 2048);
		this.txtType.setLayoutData(gridData);
		this.lblName = new Label(this, 0);
		this.lblName.setText("Name");
		this.txtName = new Text(this, 2048);
		this.txtName.setLayoutData(gridData2);
		this.lblRevision = new Label(this, 0);
		this.lblRevision.setText("Revision");
		this.txtRevision = new Text(this, 2048);
		this.txtRevision.setLayoutData(gridData9);
		this.lblDescription = new Label(this, 0);
		this.lblDescription.setText("Description");
		this.txtDescription = new Text(this, 2048);
		this.txtDescription.setLayoutData(gridDataDesc);
		this.lblPolicy = new Label(this, 0);
		this.lblPolicy.setText("Policy");
		this.txtPolicy = new Text(this, 2048);
		this.txtPolicy.setLayoutData(gridDataPolicy);
		this.lblState = new Label(this, 0);
		this.lblState.setText("State");
		this.txtState = new Text(this, 2048);
		this.txtState.setLayoutData(gridDataState);

		this.scPanel = new ScrolledComposite(this, 2560);
		this.scPanel.setLayoutData(new GridData(4, 4, true, true, 5, 1));

		GridLayout layAttributes = new GridLayout();
		layAttributes.numColumns = 4;
		this.pnlAttributes = new Composite(this.scPanel, 0);
		GridData grdAttributes = new GridData(4, 4, true, true);
		this.pnlAttributes.setLayoutData(grdAttributes);
		this.pnlAttributes.setLayout(layAttributes);

		this.scPanel.setContent(this.pnlAttributes);
		this.scPanel.setExpandHorizontal(true);
		this.scPanel.setExpandVertical(true);

		new AttributeRow(this.pnlAttributes, this.lstAttributes);
	}

	public MxObjectSearchCriteria getSearchCriteria() {
		return this.searchCriteria;
	}

	public void setSearchCriteria(MxObjectSearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
		if (searchCriteria != null) {
			this.txtId.setText(searchCriteria.getId());
			this.txtType.setText(searchCriteria.getType());
			this.txtName.setText(searchCriteria.getName());
			this.txtRevision.setText(searchCriteria.getRevision());
			this.txtPolicy.setText(searchCriteria.getPolicy());
			this.txtState.setText(searchCriteria.getState());
			this.txtDescription.setText(searchCriteria.getDescription());
			this.lstAttributes.clear();
			for (Control ctl : this.pnlAttributes.getChildren()) {
				ctl.dispose();
			}

			if (searchCriteria.getAttributes() != null) {
				for (MxAttribute attribute : searchCriteria.getAttributes()) {
					AttributeRow arow = new AttributeRow(this.pnlAttributes, this.lstAttributes);
					arow.setCondition(attribute);
				}
			}
			this.scPanel.setMinSize(this.pnlAttributes.computeSize(-1, -1));
			this.scPanel.layout();
		}
	}

	public void fillSearchCriteria(boolean appendResults) {
		ArrayList attributes = new ArrayList();
		for (int i = 0; i < this.lstAttributes.size(); i++) {
			MxAttribute attribute = ((AttributeRow)this.lstAttributes.get(i)).getCondition();
			if (attribute != null) {
				attributes.add(attribute);
			}
		}

		this.searchCriteria = 
				new MxObjectSearchCriteria(this.txtId.getText(), this.txtType.getText(), this.txtName.getText(), 
						this.txtRevision.getText(), this.txtPolicy.getText(), this.txtState.getText(), this.txtDescription.getText(), appendResults, "standard", attributes);
	}

	public void okPressed(boolean appendResults) {
		fillSearchCriteria(appendResults);
		try {
			this.treeObjectList = SearchFindLikeComposite.findObjects(this.searchCriteria);
		} catch (Exception e) {
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getShell(), 
					MxEclipseUtils.getString("SearchMatrixBusinessObjectsDialog.error.header.SearchAdminObjectsFailed"), 
					MxEclipseUtils.getString("SearchMatrixBusinessObjectsDialog.error.message.SearchAdminObjectsFailed"), 
					status);
		}
	}

	public List<MxTreeDomainObject> getTreeObjectList() {
		return this.treeObjectList;
	}
}