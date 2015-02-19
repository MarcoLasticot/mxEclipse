package org.mxeclipse.business.table.type;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeRelationship;
import org.mxeclipse.views.IModifyable;

public class MxDirectionsComposite extends Composite {
	private MxTreeBusiness business;
	private IModifyable viewPart;
	private MxTypeComposite cmpFrom = null;
	private MxTypeComposite cmpTo = null;

	public MxTreeBusiness getBusiness() {
		return this.business;
	}

	public MxDirectionsComposite(Composite parent, int style, MxTreeBusiness business, IModifyable view) {
		super(parent, style);
		this.viewPart = view;
		initialize();
		setData(business);
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		setLayout(gridLayout);
		createCmpFrom();
		createCmpTo();
		setSize(new Point(331, 200));
	}

	public void setData(MxTreeBusiness business) {
		this.business = business;
		this.cmpFrom.setData(business);
		this.cmpTo.setData(business);
	}

	public void initializeDirectionInfo(MxTreeRelationship relationship) {
		this.business = relationship;
		this.cmpFrom.initializeDirectionInfo(relationship);
		this.cmpTo.initializeDirectionInfo(relationship);
	}

	public void storeDirectionInfo() {
		this.cmpFrom.storeDirectionInfo();
		this.cmpTo.storeDirectionInfo();
	}

	public void clear() {
		this.cmpFrom.clear();
		this.cmpTo.clear();
	}

	private void createCmpFrom() {
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = 4;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.horizontalAlignment = 4;
		this.cmpFrom = new MxTypeComposite(this, 0, this.business, this.viewPart, "from");
		this.cmpFrom.setLayoutData(gridData2);
	}

	private void createCmpTo() {
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = 4;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.horizontalAlignment = 4;
		this.cmpTo = new MxTypeComposite(this, 0, this.business, this.viewPart, "to");
		this.cmpTo.setLayoutData(gridData2);
	}
}
