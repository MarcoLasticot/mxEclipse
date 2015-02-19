package org.mxeclipse.business.table.attribute;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.mxeclipse.business.tree.MxBusinessSorter;
import org.mxeclipse.model.IMxBusinessViewer;
import org.mxeclipse.model.MxTreeAttribute;
import org.mxeclipse.model.MxTreeBasic;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeIndex;
import org.mxeclipse.views.IModifyable;

public class MxAttributeComposite extends Composite {
	private Table tblObjects = null;
	private ToolBar toolBar = null;
	private TableViewer tableViewer = null;
	private MxTreeBusiness business;
	private IModifyable viewPart;
	public static String BASIC_NAME = "Name";

	private String[] columnNames = { 
			BASIC_NAME };

	public MxTreeBusiness getBusiness() {
		return this.business;
	}

	public MxAttributeComposite(Composite parent, int style, MxTreeBusiness business, IModifyable view) {
		super(parent, style);
		this.business = business;
		initialize();
		setData(business);
		this.viewPart = view;
	}

	private void initialize() {
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = 4;
		gridData3.verticalAlignment = 2;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = 4;
		gridData1.verticalAlignment = 1;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 4;
		gridData.horizontalAlignment = 4;
		gridData.verticalSpan = 2;
		gridData.grabExcessVerticalSpace = true;
		createToolBar();
		setLayout(gridLayout);
		Label filler = new Label(this, 0);
		this.tblObjects = new Table(this, 68352);

		this.tblObjects.setHeaderVisible(true);
		this.tblObjects.setLayoutData(gridData);
		this.tblObjects.setLinesVisible(true);

		TableColumn tableColumn = new TableColumn(this.tblObjects, 0, 0);
		tableColumn.setWidth(300);
		tableColumn.setText("Name");

		setSize(new Point(437, 200));
		CellEditor[] editors = new CellEditor[this.columnNames.length];
		try {
			ArrayList allAttributes = MxTreeAttribute.getAllAttributes(false);
			int basicsCount = (this.business instanceof MxTreeIndex) ? MxTreeBasic.ALL_BASICS.length : 0;
			String[] attributeNames = new String[allAttributes.size() + basicsCount];
			if (basicsCount > 0) {
				for (int i = 0; i < basicsCount; i++) {
					attributeNames[i] = MxTreeBasic.ALL_BASICS[i];
				}
			}
			for (int i = basicsCount; i < attributeNames.length; i++) {
				attributeNames[i] = ((MxTreeAttribute)allAttributes.get(i - basicsCount)).getName();
			}

			editors[0] = new ComboBoxCellEditor(this.tblObjects, attributeNames, 8);
		} catch (Exception ex) {
			MessageDialog.openError(getShell(), "Attribute Retrieval", "Error when retrieving a list of all attributes in the system!");
		}

		this.tableViewer = new TableViewer(this.tblObjects);
		this.tableViewer.setUseHashlookup(true);
		this.tableViewer.setColumnProperties(this.columnNames);
		this.tableViewer.setContentProvider(new MxAttributeContentProvider());
		this.tableViewer.setLabelProvider(new MxAttributeLabelProvider());
		this.tableViewer.setCellEditors(editors);
		this.tableViewer.setCellModifier(new MxAttributeCellModifier(this));
		this.tableViewer.setSorter(new MxBusinessSorter(BASIC_NAME));
	}

	public void setData(MxTreeBusiness business) {
		this.tblObjects.removeAll();

		this.business = business;
		this.tableViewer.setInput(business);

		this.tblObjects.redraw();
	}

	private void createToolBar() {
		this.toolBar = new ToolBar(this, 0);
		ToolItem cmdNew = new ToolItem(this.toolBar, 8);
		cmdNew.setText("New");
		ToolItem cmdDelete = new ToolItem(this.toolBar, 8);
		cmdDelete.setText("Delete");
		cmdDelete.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Iterator itSelected = ((IStructuredSelection)MxAttributeComposite.this.tableViewer.getSelection()).iterator();
				while (itSelected.hasNext()) {
					MxTreeAttribute attribute = (MxTreeAttribute)itSelected.next();
					if (attribute != null) {
						MxAttributeComposite.this.business.removeAttribute(attribute);
					}
				}

				MxAttributeComposite.this.viewPart.setModified(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		cmdNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					MxAttributeComposite.this.business.addAttribute();

					MxAttributeComposite.this.viewPart.setModified(true);
				} catch (Exception ex) {
					Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
					ErrorDialog.openError(MxAttributeComposite.this.getParent().getShell().getShell(), 
							"Unable to add attribute", 
							"Error Occurred while  adding an attriute to an admin object ", 
							status);
				}
			}
		});
	}

	public void clear() {
		this.tblObjects.clearAll();
	}

	class MxAttributeContentProvider implements IStructuredContentProvider, IMxBusinessViewer {
		MxAttributeContentProvider() {
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			MxTreeBusiness newBusiness = (MxTreeBusiness)newInput;
			MxTreeBusiness oldBusiness = (MxTreeBusiness)oldInput;
			if (oldInput != null)
				oldBusiness.removeChangeListener(this);
			if (newInput != null)
				newBusiness.addChangeListener(this);
			MxAttributeComposite.this.tableViewer.refresh();
		}

		public void dispose() {
			MxAttributeComposite.this.business.removeChangeListener(this);
		}

		public Object[] getElements(Object parent) {
			if (MxAttributeComposite.this.business != null) {
				return MxAttributeComposite.this.business.getAttributes(false).toArray(new MxTreeAttribute[MxAttributeComposite.this.business.getAttributes(false).size()]);
			}
			return new Object[0];
		}

		public void addProperty(MxTreeBusiness task) {
			if ((task instanceof MxTreeAttribute)) {
				MxAttributeComposite.this.tableViewer.add(task);
				TableItem[] items = MxAttributeComposite.this.tblObjects.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].getData().equals(task)) {
						MxAttributeComposite.this.tblObjects.setSelection(i);
						break;
					}
				}
			}
		}

		public void removeProperty(MxTreeBusiness task) {
			if ((task instanceof MxTreeAttribute)) {
				MxAttributeComposite.this.tableViewer.remove(task);
			}
		}

		public void updateProperty(MxTreeBusiness property) {
			if ((property instanceof MxTreeAttribute)) {
				MxAttributeComposite.this.tableViewer.update(property, null);
				MxAttributeComposite.this.viewPart.setModified(true);
			}
		}
	}
}