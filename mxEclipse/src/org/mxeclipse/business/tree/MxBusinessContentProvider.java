package org.mxeclipse.business.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mxeclipse.model.MxFilter;
import org.mxeclipse.model.MxTreeBusiness;

public class MxBusinessContentProvider implements ITreeContentProvider {
	private MxFilter filter;

	public Object[] getChildren(Object parentElement) {
		if ((parentElement != null) && ((parentElement instanceof MxTreeBusiness))) {
			MxTreeBusiness parentBusiness = (MxTreeBusiness)parentElement;
			try {
				return parentBusiness.getChildren(false);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public Object getParent(Object element) {
		MxTreeBusiness treeBusiness = (MxTreeBusiness)element;
		return treeBusiness.getParent();
	}

	public boolean hasChildren(Object element) {
		MxTreeBusiness treeBusinessObject = (MxTreeBusiness)element;
		return treeBusinessObject.hasChildren();
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public void setFilter(MxFilter filter) {
		this.filter = filter;
	}

	public MxFilter getFilter() {
		return this.filter;
	}
}