package org.mxeclipse.configure.table;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeColumn;
import org.mxeclipse.model.MxTableColumn;
import org.mxeclipse.utils.IXMLPersistable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MxTableColumnList implements IXMLPersistable {
	private boolean inBusiness;
	private Vector<MxTableColumn> columns = new Vector();
	private Set changeListeners = new HashSet();

	public MxTableColumnList() {
		initData(null);
	}

	public MxTableColumnList(boolean inBusiness) {
		this.inBusiness = inBusiness;
		initData(null);
	}

	public MxTableColumnList(Node node, Boolean inBusiness) {
		this.inBusiness = inBusiness.booleanValue();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String name = child.getNodeName();
			if (name.equals("Column")) {
				MxTableColumn column = new MxTableColumn(child, inBusiness.booleanValue());
				addTask(column);
			}
		}
	}

	public MxTableColumnList(MxTableColumnList initialColumns) {
		initData(initialColumns);
	}

	private void initData(MxTableColumnList initialColumns) {
		if (initialColumns == null) {
			addTask(MxTableColumn.BASIC_TYPE, MxTableColumn.TYPE_BASIC, true, false, 120);
			addTask(MxTableColumn.BASIC_NAME, MxTableColumn.TYPE_BASIC, true, false, 130);
			if (!this.inBusiness) {
				addTask(MxTableColumn.BASIC_REVISION, MxTableColumn.TYPE_BASIC, true, false, 20);
			}
			addTask(MxTableColumn.BASIC_RELATIONSHIP, MxTableColumn.TYPE_BASIC, true, true, 120);
			if (!this.inBusiness) {
				addTask(MxTableColumn.BASIC_STATE, MxTableColumn.TYPE_BASIC, true, false, 80);
			}
		}
		else {
			for (int i = 0; i < initialColumns.getColumns().size(); i++) {
				MxTableColumn column = (MxTableColumn)initialColumns.getColumns().get(i);
				addTask(column.getName(), column.getType(), column.isVisible(), column.isOnRelationship(), column.getWidth());
			}
		}
	}

	public void createColumns(TreeViewer viewer) {
		TreeColumn[] oldColumns = viewer.getTree().getColumns();
		for (int i = 0; i < oldColumns.length; i++) {
			oldColumns[i].dispose();
		}
		for (int i = 0; i < this.columns.size(); i++) {
			MxTableColumn column = (MxTableColumn)this.columns.get(i);
			if (column.isVisible()) {
				column.createColumn(viewer);
			}
		}
	}

	public Vector getColumns() {
		return this.columns;
	}

	public void addTask(String name, String type, boolean visible, boolean onRelationship, int width) {
		MxTableColumn task = new MxTableColumn(name, type, visible, onRelationship, width, this.inBusiness);
		addTask(task);
	}

	public void addTask(MxTableColumn task) {
		this.columns.add(this.columns.size(), task);
		Iterator iterator = this.changeListeners.iterator();
		while (iterator.hasNext()) {
			((IMxTableColumnViewer)iterator.next()).addTask(task);
		}
	}

	public void addTask() {
		addTask("", MxTableColumn.TYPE_ATTRIBUTE, true, false, 100);
	}

	public void removeTask(MxTableColumn column) {
		this.columns.remove(column);
		Iterator iterator = this.changeListeners.iterator();
		while (iterator.hasNext())
			((IMxTableColumnViewer)iterator.next()).removeTask(column);
	}

	public void save() {
	}

	public void propertyChanged(MxTableColumn task) {
		Iterator iterator = this.changeListeners.iterator();
		while (iterator.hasNext()) {
			((IMxTableColumnViewer)iterator.next()).updateProperty(task);
		}
	}

	public void removeChangeListener(IMxTableColumnViewer viewer) {
		this.changeListeners.remove(viewer);
	}

	public void addChangeListener(IMxTableColumnViewer viewer) {
		this.changeListeners.add(viewer);
	}

	public void toXML(Document doc, Node node) {
		Element xmlColumns = doc.createElement("Columns");
		Iterator itColumn = getColumns().iterator();
		while (itColumn.hasNext()) {
			MxTableColumn column = (MxTableColumn)itColumn.next();
			column.toXML(doc, xmlColumns);
		}

		node.appendChild(xmlColumns);
	}

	public void moveUp(int index) {
		if (index == 0) {
			return;
		}
		MxTableColumn upperColumn = (MxTableColumn)getColumns().get(index - 1);

		getColumns().remove(index - 1);
		getColumns().add(index, upperColumn);
	}

	public void moveDown(int index) {
		if (index == getColumns().size() - 1) {
			return;
		}
		MxTableColumn lowerColumn = (MxTableColumn)getColumns().get(index + 1);

		getColumns().remove(index + 1);
		getColumns().add(index, lowerColumn);
	}
}