package org.mxeclipse.business.basic;

import org.eclipse.swt.widgets.Composite;
import org.mxeclipse.model.MxTreeBusiness;

public abstract class MxBusinessBasicComposite extends Composite {
	public MxBusinessBasicComposite(Composite parent, int style) {
		super(parent, style);
	}

	public abstract void initializeContent(MxTreeBusiness paramMxTreeBusiness);

	public abstract void storeData();
}