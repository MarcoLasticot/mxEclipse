package org.mxeclipse.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import matrix.db.Context;
import matrix.util.StringList;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxAttribute;
import org.mxeclipse.model.MxObjectSearchCriteria;
import org.mxeclipse.model.MxTreeAttribute;
import org.mxeclipse.model.MxTreeDomainObject;
import org.mxeclipse.model.MxTreeType;
import org.mxeclipse.utils.MxEclipseUtils;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;

public class SearchFindLikeComposite extends Composite implements ISearchComposite {
	private Combo cmbType = null;
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

	private List<MxTreeDomainObject> treeObjectList = new ArrayList();

	public SearchFindLikeComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
		initializeContent();
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
		gridData2.horizontalAlignment = 4;
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
		this.cmbType = new Combo(this, 2048);
		this.cmbType.setLayoutData(gridData);
		this.cmbType.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent selectionevent) {
			}

			public void widgetSelected(SelectionEvent selectionevent) {
				SearchFindLikeComposite.this.typeChanged();
			}
		});
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
		layAttributes.numColumns = 2;
		this.pnlAttributes = new Composite(this.scPanel, 0);
		GridData grdAttributes = new GridData(4, 4, true, true);
		this.pnlAttributes.setLayoutData(grdAttributes);
		this.pnlAttributes.setLayout(layAttributes);

		this.scPanel.setContent(this.pnlAttributes);
		this.scPanel.setExpandHorizontal(true);
		this.scPanel.setExpandVertical(true);
	}

	public void fillSearchCriteria(boolean appendResults) {
		ArrayList attributes = new ArrayList();
		for (Control ctl : this.pnlAttributes.getChildren()) {
			if ((ctl instanceof Text)) {
				Text txt = (Text)ctl;
				if ((!txt.getText().equals("")) && (!txt.getText().equals("*"))) {
					MxTreeAttribute data = (MxTreeAttribute)txt.getData();
					MxAttribute attribute = new MxAttribute(data.getName(), txt.getText());
					attributes.add(attribute);
				}
			}
		}

		this.searchCriteria = 
				new MxObjectSearchCriteria(this.txtId.getText(), this.cmbType.getText(), this.txtName.getText(), 
						this.txtRevision.getText(), this.txtPolicy.getText(), this.txtState.getText(), this.txtDescription.getText(), appendResults, "findlike", attributes);
	}

	public void typeChanged() {
		if(!cmbType.getText().equals("")) {
			try {
				ArrayList oldAttributes = null;
				if (pnlAttributes.getChildren().length > 0) {
					oldAttributes = new ArrayList();
					Control acontrol[];
					int j = (acontrol = pnlAttributes.getChildren()).length;
					for (int i = 0; i < j; i++) {
						Control ctl = acontrol[i];
						if (ctl instanceof Text) {
							Text txtOldAttribute = (Text)ctl;
							if (!txtOldAttribute.getText().equals("")) {
								MxTreeAttribute oldAttribute = (MxTreeAttribute)txtOldAttribute.getData();
								oldAttributes.add(new MxAttribute(oldAttribute.getName(), txtOldAttribute.getText()));
							}
						}
						ctl.dispose();
					}

				}
				MxTreeType type = new MxTreeType(cmbType.getText());
				ArrayList alAttributes = type.getAttributes(false);
				for (Iterator iterator = alAttributes.iterator(); iterator.hasNext();) {
					MxTreeAttribute attribute = (MxTreeAttribute)iterator.next();
					Label lblAttrName = new Label(pnlAttributes, 0);
					GridData grdLbl = new GridData();
					grdLbl.horizontalAlignment = 4;
					lblAttrName.setText(attribute.getName());
					lblAttrName.setLayoutData(grdLbl);
					Text txtAttValue = new Text(pnlAttributes, 2048);
					txtAttValue.setData(attribute);
					txtAttValue.setVisible(true);
					GridData grdTxt = new GridData();
					grdTxt.horizontalAlignment = 4;
					grdTxt.grabExcessHorizontalSpace = true;
					txtAttValue.setLayoutData(grdTxt);
					if(oldAttributes == null && searchCriteria != null && searchCriteria.getAttributes() != null) {
						oldAttributes = searchCriteria.getAttributes();
					}
					if(oldAttributes != null) {
						for(Iterator iterator1 = oldAttributes.iterator(); iterator1.hasNext();) {
							MxAttribute attCriteria = (MxAttribute)iterator1.next();
							if(attCriteria.getName().equals(attribute.getName())) {
								txtAttValue.setText(attCriteria.getValue());
							}
						}

					}
				}

				pnlAttributes.pack();
				pnlAttributes.layout();
				scPanel.setMinSize(pnlAttributes.computeSize(-1, -1));
				scPanel.layout();
				layout();
			} catch(Exception e) {
				Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
				ErrorDialog.openError(getShell(), "Error when trying to initialize data in the find like form", "Error when trying to initialize data in the find like form", status);
			}
		}
	}

	public void initializeContent() {
		try {
			this.cmbType.setItems(MxTreeType.getAllTypeNames(false));
		} catch (Exception e) {
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getShell(), 
					"Error when trying to initialize data in the find like form", 
					"Error when trying to initialize data in the find like form", 
					status);
		}
	}

	public MxObjectSearchCriteria getSearchCriteria() {
		return this.searchCriteria;
	}

	public void setSearchCriteria(MxObjectSearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
		if (searchCriteria != null) {
			this.txtId.setText(searchCriteria.getId());
			if (searchCriteria.getType() != null) {
				for (int i = 0; i < this.cmbType.getItemCount(); i++) {
					if (this.cmbType.getItem(i).equals(searchCriteria.getType())) {
						this.cmbType.select(i);
						break;
					}
				}
			}
			this.txtName.setText(searchCriteria.getName());
			this.txtRevision.setText(searchCriteria.getRevision());
			this.txtPolicy.setText(searchCriteria.getPolicy());
			this.txtState.setText(searchCriteria.getState());
			this.txtDescription.setText(searchCriteria.getDescription());

			if (this.cmbType.getSelectionIndex() >= 0) {
				typeChanged();
			}
		}
	}

	public static List<MxTreeDomainObject> findObjects(MxObjectSearchCriteria criteria) throws Exception {
		String id = criteria.getId() == null ? "" : criteria.getId();
		String type = (criteria.getType() == null) || (criteria.getType().equals("")) ? "*" : criteria.getType();
		String name = (criteria.getName() == null) || (criteria.getName().equals("")) ? "*" : criteria.getName();
		String rev = (criteria.getRevision() == null) || (criteria.getRevision().equals("")) ? "*" : criteria.getRevision();
		String policy = (criteria.getPolicy() == null) || (criteria.getPolicy().equals("")) ? "*" : criteria.getPolicy();
		String state = (criteria.getState() == null) || (criteria.getState().equals("")) ? "*" : criteria.getState();
		String description = (criteria.getDescription() == null) || (criteria.getDescription().equals("")) ? "*" : criteria.getDescription();
		ArrayList alAttributes = criteria.getAttributes();

		String where = "";
		if (!description.equals("*")) {
			where = "description smatch '" + description + "'";
		}
		if (!policy.equals("*")) {
			if (!where.equals("")) {
				where = where + " and ";
			}
			where = where + " policy smatch '" + policy + "'";
		}
		if (!state.equals("*")) {
			if (!where.equals("")) {
				where = where + " and ";
			}
			where = where + " current smatch '" + state + "'";
		}
		if ((alAttributes != null) && (alAttributes.size() > 0)) {
			for(Iterator iterator = alAttributes.iterator(); iterator.hasNext();) {
				MxAttribute attribute = (MxAttribute)iterator.next();
				if(!where.equals("")) {
					where = (new StringBuilder(String.valueOf(where))).append(" and ").toString();
				}
				where = (new StringBuilder(String.valueOf(where))).append("attribute[").append(attribute.getName()).append("]").toString();
				if(attribute.getValue().contains("*")) {
					where = (new StringBuilder(String.valueOf(where))).append(" smatch '").toString();
				} else {
					where = (new StringBuilder(String.valueOf(where))).append(" == '").toString();
				}
				where = (new StringBuilder(String.valueOf(where))).append(attribute.getValue()).append("'").toString();
			}
		}

		List treeObjectList = new ArrayList();

		String myId = null;

		Context context = MxEclipsePlugin.getDefault().getContext();
		if (context != null) {
			treeObjectList.clear();
			if (!id.equals("")) {
				myId = id;
				treeObjectList.add(new MxTreeDomainObject(myId));
			} else {
				StringList selObjects = new StringList();
				selObjects.addElement("id");
				selObjects.addElement("type");
				selObjects.addElement("name");
				selObjects.addElement("revision");
				selObjects.addElement("policy");
				selObjects.addElement("current");
				int objectLimit = MxEclipsePlugin.getDefault().getPreferenceStore().getInt("ObjectSearchLimit");
				MapList lstObjects = DomainObject.findObjects(context, type, name, rev, "*", "*", where, "", true, selObjects, (short)objectLimit);
				Iterator itObjects = lstObjects.iterator();
				while (itObjects.hasNext()) {
					Map mapObject = (Map)itObjects.next();
					myId = (String)mapObject.get("id");
					treeObjectList.add(
							new MxTreeDomainObject(myId, (String)mapObject.get("type"), 
									(String)mapObject.get("name"), (String)mapObject.get("revision"), (String)mapObject.get("policy"), (String)mapObject.get("current")));
				}
			}
		}

		return treeObjectList;
	}

	public void okPressed(boolean appendResults) {
		fillSearchCriteria(appendResults);
		try {
			this.treeObjectList = findObjects(this.searchCriteria);
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