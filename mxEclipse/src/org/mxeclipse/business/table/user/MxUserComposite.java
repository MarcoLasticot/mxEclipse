package org.mxeclipse.business.table.user;

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
import org.mxeclipse.model.MxTreeState;
import org.mxeclipse.model.MxTreeStateUserAccess;
import org.mxeclipse.model.MxTreeUser;
import org.mxeclipse.model.MxTreeWeb;
import org.mxeclipse.utils.DummyCellEditor;
import org.mxeclipse.utils.MxEclipseLogger;
import org.mxeclipse.views.IModifyable;

public class MxUserComposite extends Composite {
	public Table tblObjects = null;
	private ToolBar toolBar = null;
	private TableViewer tableViewer = null;
	private MxTreeBusiness businessObject;
	private IModifyable viewPart;
	CellEditor[] editors;
	public static final String COLUMN_TYPE = "Type";
	public static final String COLUMN_NAME = "Name";
	public static final String COLUMN_SELECT = "Select";
	public static final String[] columnNames = { 
		"Type", 
		"Name", 
		"Select" 
	};
	public static final int COL_INDEX_TYPE = 0;
	public static final int COL_INDEX_NAME = 1;
	public static final int COL_INDEX_SELECT = 2;

	public MxTreeBusiness getBusiness() {
		return this.businessObject;
	}

	public MxUserComposite(Composite parent, int style, MxTreeBusiness businessObject, IModifyable view) {
		super(parent, style);
		this.viewPart = view;
		initialize();
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

		TableColumn tableColumnType = new TableColumn(this.tblObjects, 0, 0);
		tableColumnType.setWidth(100);
		tableColumnType.setText("Type");

		TableColumn tableColumnName = new TableColumn(this.tblObjects, 0, 1);
		tableColumnName.setWidth(100);
		tableColumnName.setText("Name");

		TableColumn tableColumnsLink = new TableColumn(this.tblObjects, 0, 2);
		tableColumnsLink.setWidth(16);
		tableColumnsLink.setText("Link");

		setSize(new Point(437, 200));
		this.editors = new CellEditor[columnNames.length];
		try {
			this.editors[0] = new ComboBoxCellEditor(this.tblObjects, MxTreeUser.ALL_USER_TYPES, 0);
			this.editors[1] = new ComboBoxCellEditor(this.tblObjects, MxTreeUser.getAllUserNames(false), 0);
		} catch (Exception ex) {
			MessageDialog.openError(getShell(), "User info retrieval", "Error when retrieving a list of all users/roles/groups/assignments!");
		}
		this.editors[2] = new DummyCellEditor(this.tblObjects, 0);

		this.tableViewer = new TableViewer(this.tblObjects);
		this.tableViewer.setUseHashlookup(true);
		this.tableViewer.setColumnProperties(columnNames);
		this.tableViewer.setContentProvider(new MxUserAccessContentProvider());
		this.tableViewer.setLabelProvider(new MxUserLabelProvider());
		this.tableViewer.setCellEditors(this.editors);
		this.tableViewer.setCellModifier(new MxUserCellModifier(this));
		this.tableViewer.setSorter(new MxBusinessSorter("Name"));
	}

	public void setData(MxTreeBusiness businessObject) {
		this.tblObjects.removeAll();

		this.businessObject = businessObject;

		this.tableViewer.setInput(businessObject);

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
				Iterator itSelected = ((IStructuredSelection)MxUserComposite.this.tableViewer.getSelection()).iterator();
				while (itSelected.hasNext()) {
					if ((MxUserComposite.this.businessObject instanceof MxTreeState)) {
						MxTreeStateUserAccess userAccess = (MxTreeStateUserAccess)itSelected.next();
						if ((userAccess != null) && (userAccess.getUserBasicType().equals("user")))
							((MxTreeState)MxUserComposite.this.businessObject).removeUserAccess(userAccess);
					} else if ((MxUserComposite.this.businessObject instanceof MxTreeWeb)) {
						MxTreeUser user = (MxTreeUser)itSelected.next();
						if (user != null) {
							((MxTreeWeb)MxUserComposite.this.businessObject).removeUser(user);
						}
					}
				}

				MxUserComposite.this.viewPart.setModified(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		cmdNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					if ((MxUserComposite.this.businessObject instanceof MxTreeState)) {
						((MxTreeState)MxUserComposite.this.businessObject).addUserAccess();
					}
					else if ((MxUserComposite.this.businessObject instanceof MxTreeWeb)) {
						((MxTreeWeb)MxUserComposite.this.businessObject).addUser();
					}

					MxUserComposite.this.viewPart.setModified(true);
				} catch (Exception ex) {
					Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
					ErrorDialog.openError(MxUserComposite.this.getParent().getShell().getShell(), 
							"Unable to add trigger", 
							"Error Occurred while  adding a user", 
							status);
				}
			}
		});
	}

	public void clear() {
		this.tblObjects.clearAll();
	}

	class MxUserAccessContentProvider implements IStructuredContentProvider, IMxBusinessViewer {
		MxUserAccessContentProvider() {
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			MxTreeBusiness newType = (MxTreeBusiness)newInput;
			MxTreeBusiness oldType = (MxTreeBusiness)oldInput;
			if (oldInput != null) {
				oldType.removeChangeListener(this);
			}
			if (newInput != null) {
				newType.addChangeListener(this);
			}
			MxUserComposite.this.tableViewer.refresh();
		}

		public void dispose() {
			MxUserComposite.this.businessObject.removeChangeListener(this);
		}

		public Object[] getElements(Object parent) {
			try {
				if (MxUserComposite.this.businessObject != null) {
					if ((MxUserComposite.this.businessObject instanceof MxTreeState)) {
						MxTreeState stateObject = (MxTreeState)MxUserComposite.this.businessObject;
						ArrayList alUserAccess = stateObject.getUserAccess();
						return alUserAccess.toArray(new MxTreeStateUserAccess[alUserAccess.size()]);
					}
					if ((MxUserComposite.this.businessObject instanceof MxTreeWeb)) {
						MxTreeWeb webObject = (MxTreeWeb)MxUserComposite.this.businessObject;
						ArrayList alUsers = webObject.getUsers(false);
						return alUsers.toArray(new MxTreeUser[alUsers.size()]);
					}
					return new Object[0];
				}
				return new Object[0];
			} catch (Exception ex) {
				MxEclipseLogger.getLogger().severe(ex.getMessage());
			}
			return new Object[0];
		}

		public void addProperty(MxTreeBusiness task) {
			if (((task instanceof MxTreeStateUserAccess)) || ((task instanceof MxTreeUser))) {
				MxUserComposite.this.tableViewer.add(task);
				TableItem[] items = MxUserComposite.this.tblObjects.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].getData().equals(task)) {
						MxUserComposite.this.tblObjects.setSelection(i);
						break;
					}
				}
			}
		}

		public void removeProperty(MxTreeBusiness task) {
			if (((task instanceof MxTreeStateUserAccess)) || ((task instanceof MxTreeUser))) {
				MxUserComposite.this.tableViewer.remove(task);
			}
		}

		public void updateProperty(MxTreeBusiness property) {
			if (((property instanceof MxTreeStateUserAccess)) || ((property instanceof MxTreeUser))) {
				MxUserComposite.this.tableViewer.update(property, null);
				MxUserComposite.this.viewPart.setModified(true);
			}
		}
	}
}