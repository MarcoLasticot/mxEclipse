package org.mxeclipse.dialogs;

import java.util.List;

import matrix.util.MatrixException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.mxeclipse.configure.table.MxTableColumnList;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.model.MxTreeDomainObject;
import org.mxeclipse.object.tree.MxTreeContentProvider;
import org.mxeclipse.object.tree.MxTreeLabelProvider;

public class ObjectListComposite extends Composite {
	private Tree treObjects = null;
	private TreeViewer treeViewer;
	private MxTableColumnList initialColumns;
	private MxTreeContentProvider treeContentProvider;

	public ObjectListComposite(Composite parent, int style, MxTableColumnList initialColumns) {
		super(parent, style);
		this.initialColumns = initialColumns;
		initialize();
	}

	private void initialize() {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 4;
		gridData.horizontalAlignment = 4;
		gridData.grabExcessVerticalSpace = true;
		this.treObjects = new Tree(this, 82178);
		this.treObjects.setHeaderVisible(true);
		this.treObjects.setLayoutData(gridData);

		this.treeViewer = new TreeViewer(this.treObjects);
		this.treeContentProvider = new MxTreeContentProvider();

		this.initialColumns.createColumns(this.treeViewer);
		this.treeViewer.setLabelProvider(new MxTreeLabelProvider(this.initialColumns));
		this.treeViewer.setContentProvider(this.treeContentProvider);

		setSize(new Point(318, 200));
		setLayout(new GridLayout());
		this.treObjects.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				Point p = new Point(e.x, e.y);
				TreeItem treeItem = ObjectListComposite.this.treObjects.getItem(p);
				ObjectListComposite.this.expandItem(treeItem);
			}
		});
	}

	protected void expandItem(TreeItem treeItem) {
		if (treeItem != null) {
			MxTreeDomainObject selectedObject = (MxTreeDomainObject)treeItem.getData();
			try {
				selectedObject.getChildren(true);
				this.treeViewer.refresh(selectedObject, false);
				this.treeViewer.expandToLevel(selectedObject, 1);
			} catch (MxEclipseException e1) {
				MessageDialog.openError(getShell(), "Error", e1.getMessage());
			} catch (MatrixException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void setData(List<MxTreeDomainObject> objects) {
		MxTreeDomainObject root = new MxTreeDomainObject();
		for (int i = 0; i < objects.size(); i++) {
			MxTreeDomainObject child = (MxTreeDomainObject)objects.get(i);
			root.addChild(child);
		}
		this.treeViewer.getTree().removeAll();
		this.treeViewer.setInput(root);

		this.treObjects.redraw();
	}

	public MxTreeDomainObject getSelectedObject() {
		TreeItem[] selections = this.treObjects.getSelection();
		if (selections.length > 0) {
			return (MxTreeDomainObject)selections[0].getData();
		}
		return null;
	}
}