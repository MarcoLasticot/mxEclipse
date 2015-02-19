package org.mxeclipse.business.table.policy;

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
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreePolicy;
import org.mxeclipse.model.MxTreeType;
import org.mxeclipse.views.IModifyable;

public class MxPolicyComposite extends Composite {
	private Table tblObjects = null;
	private ToolBar toolBar = null;
	private TableViewer tableViewer = null;
	private MxTreeType type;
	private IModifyable viewPart;
	public static String BASIC_NAME = "Name";

	private String[] columnNames = { 
			BASIC_NAME };

	public MxTreeType getType() {
		return this.type;
	}

	public MxPolicyComposite(Composite parent, int style, MxTreeType type, IModifyable view) {
		super(parent, style);
		initialize();
		setData(type);
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
			ArrayList allPolicies = MxTreePolicy.getAllPolicies(true);
			String[] policyNames = new String[allPolicies.size()];
			for (int i = 0; i < policyNames.length; i++) {
				policyNames[i] = ((MxTreePolicy)allPolicies.get(i)).getName();
			}
			editors[0] = new ComboBoxCellEditor(this.tblObjects, policyNames, 0);
		} catch (Exception ex) {
			MessageDialog.openError(getShell(), "Policy retrieval", "Error when retrieving a list of all policies in the system!");
		}

		this.tableViewer = new TableViewer(this.tblObjects);
		this.tableViewer.setUseHashlookup(true);
		this.tableViewer.setColumnProperties(this.columnNames);
		this.tableViewer.setContentProvider(new MxPolicyContentProvider());
		this.tableViewer.setLabelProvider(new MxPolicyLabelProvider());
		this.tableViewer.setCellEditors(editors);
		this.tableViewer.setCellModifier(new MxPolicyCellModifier(this));
		this.tableViewer.setSorter(new MxBusinessSorter(BASIC_NAME));
	}

	public void setData(MxTreeType type) {
		this.tblObjects.removeAll();

		this.type = type;
		this.tableViewer.setInput(type);

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
				Iterator itSelected = ((IStructuredSelection)MxPolicyComposite.this.tableViewer.getSelection()).iterator();
				while (itSelected.hasNext()) {
					MxTreePolicy policy = (MxTreePolicy)itSelected.next();
					if (policy != null) {
						MxPolicyComposite.this.type.removePolicy(policy);
					}
				}

				MxPolicyComposite.this.viewPart.setModified(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		cmdNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					MxPolicyComposite.this.type.addPolicy();

					MxPolicyComposite.this.viewPart.setModified(true);
				} catch (Exception ex) {
					Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
					ErrorDialog.openError(MxPolicyComposite.this.getParent().getShell().getShell(), 
							"Unable to add policy", 
							"Error Occurred while  adding a policy to a type", 
							status);
				}
			}
		});
	}

	public void clear() {
		this.tblObjects.clearAll();
	}

	class MxPolicyContentProvider implements IStructuredContentProvider, IMxBusinessViewer {
		MxPolicyContentProvider() {
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			MxTreeType newType = (MxTreeType)newInput;
			MxTreeType oldType = (MxTreeType)oldInput;
			if (oldInput != null) {
				oldType.removeChangeListener(this);
			}
			if (newInput != null) {
				newType.addChangeListener(this);
			}
			MxPolicyComposite.this.tableViewer.refresh();
		}

		public void dispose() {
			MxPolicyComposite.this.type.removeChangeListener(this);
		}

		public Object[] getElements(Object parent) {
			if (MxPolicyComposite.this.type != null) {
				return MxPolicyComposite.this.type.getPolicies(false).toArray(new MxTreePolicy[MxPolicyComposite.this.type.getPolicies(false).size()]);
			}
			return new Object[0];
		}

		public void addProperty(MxTreeBusiness task) {
			if ((task instanceof MxTreePolicy)) {
				MxPolicyComposite.this.tableViewer.add(task);
				TableItem[] items = MxPolicyComposite.this.tblObjects.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].getData().equals(task)) {
						MxPolicyComposite.this.tblObjects.setSelection(i);
						break;
					}
				}
			}
		}

		public void removeProperty(MxTreeBusiness task) {
			if ((task instanceof MxTreePolicy)) {
				MxPolicyComposite.this.tableViewer.remove(task);
			}
		}

		public void updateProperty(MxTreeBusiness property) {
			if ((property instanceof MxTreePolicy)) {
				MxPolicyComposite.this.tableViewer.update(property, null);
				MxPolicyComposite.this.viewPart.setModified(true);
			}
		}
	}
}