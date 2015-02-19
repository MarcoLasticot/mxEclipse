package org.mxeclipse.business.table.assignment;

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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.mxeclipse.business.tree.MxBusinessSorter;
import org.mxeclipse.model.IMxBusinessViewer;
import org.mxeclipse.model.MxTreeAssignment;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreePerson;
import org.mxeclipse.views.IModifyable;

public class MxAssignmentComposite extends Composite {
	private Table tblObjects = null;
	private ToolBar toolBar = null;
	private TableViewer tableViewer = null;
	private MxTreeBusiness business;
	private IModifyable viewPart;
	private boolean parentAssignment;
	private String assignmentType;
	public static String BASIC_NAME = "Name";

	private String[] columnNames = { 
			BASIC_NAME };

	private Label lblTitle = null;

	public MxTreeBusiness getBusiness() {
		return this.business;
	}

	public MxAssignmentComposite(Composite parent, int style, IModifyable view, MxTreeBusiness business, String assignmentType, boolean parentAssignment) {
		super(parent, style);
		this.viewPart = view;
		this.assignmentType = assignmentType;
		this.parentAssignment = parentAssignment;
		initialize();
		setData(business);
	}

	private void initialize() {
		GridData gridData12 = new GridData();
		gridData12.horizontalSpan = 4;
		gridData12.horizontalAlignment = 4;
		gridData12.verticalAlignment = 2;
		gridData12.grabExcessHorizontalSpace = true;
		this.lblTitle = new Label(this, 0);
		this.lblTitle.setText("From side");
		this.lblTitle.setFont(new Font(Display.getDefault(), "Tahoma", 8, 1));
		this.lblTitle.setLayoutData(gridData12);
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = 4;
		gridData3.verticalAlignment = 2;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = 4;
		gridData1.verticalAlignment = 1;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 4;
		gridData.horizontalAlignment = 4;
		gridData.verticalSpan = 2;
		gridData.horizontalSpan = 4;
		gridData.grabExcessVerticalSpace = true;
		createToolBar();
		setLayout(gridLayout);
		Label filler = new Label(this, 0);
		Label filler6 = new Label(this, 0);
		Label filler10 = new Label(this, 0);
		this.tblObjects = new Table(this, 68352);

		this.tblObjects.setHeaderVisible(true);
		this.tblObjects.setLayoutData(gridData);
		this.tblObjects.setLinesVisible(true);

		TableColumn tableColumn = new TableColumn(this.tblObjects, 0, 0);
		tableColumn.setWidth(300);
		tableColumn.setText("Name");

		setSize(new Point(331, 200));
		CellEditor[] editors = new CellEditor[this.columnNames.length];
		try {
			ArrayList allAssignments = MxTreeAssignment.getAllAssignments(false, this.assignmentType);
			String[] assignmentNames = new String[allAssignments.size()];
			for (int i = 0; i < assignmentNames.length; i++) {
				assignmentNames[i] = ((MxTreeAssignment)allAssignments.get(i)).getName();
			}
			editors[0] = new ComboBoxCellEditor(this.tblObjects, assignmentNames, 8);
		} catch (Exception ex) {
			MessageDialog.openError(getShell(), "Type Retrieval", "Error when retrieving a list of all types in the system!");
		}

		this.tableViewer = new TableViewer(this.tblObjects);
		this.tableViewer.setUseHashlookup(true);
		this.tableViewer.setColumnProperties(this.columnNames);
		MxAssignmentContentProvider contentProvider = new MxAssignmentContentProvider();
		this.tableViewer.setContentProvider(contentProvider);
		this.tableViewer.setLabelProvider(new MxAssignmentLabelProvider());
		this.tableViewer.setCellEditors(editors);
		this.tableViewer.setCellModifier(new MxAssignmentCellModifier(this));
		this.tableViewer.setSorter(new MxBusinessSorter(BASIC_NAME));
	}

	public void setData(MxTreeBusiness business) {
		this.tblObjects.removeAll();

		this.business = business;
		this.tableViewer.setInput(business);

		this.tblObjects.redraw();

		if (this.parentAssignment) {
			this.lblTitle.setText("Parent " + this.assignmentType + "s");
		} else {
			this.lblTitle.setText("Child " + this.assignmentType + "s");
		}
	}

	private void createToolBar() {
		this.toolBar = new ToolBar(this, 0);
		ToolItem cmdNew = new ToolItem(this.toolBar, 8);
		cmdNew.setText("New");
		ToolItem cmdDelete = new ToolItem(this.toolBar, 8);
		cmdDelete.setText("Delete");
		cmdDelete.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Iterator itSelected = ((IStructuredSelection)MxAssignmentComposite.this.tableViewer.getSelection()).iterator();
				while (itSelected.hasNext()) {
					MxTreeAssignment assignment = (MxTreeAssignment)itSelected.next();
					if (assignment != null) {
						if ((MxAssignmentComposite.this.business instanceof MxTreePerson)) {
							((MxTreePerson)MxAssignmentComposite.this.business).removeAssignment(assignment);
						} else if ((MxAssignmentComposite.this.business instanceof MxTreeAssignment)) {
							if (MxAssignmentComposite.this.parentAssignment) {
								((MxTreeAssignment)MxAssignmentComposite.this.business).removeParentAssignment(assignment);
							} else {
								((MxTreeAssignment)MxAssignmentComposite.this.business).removeChildAssignment(assignment);
							}
						}
					}
				}

				MxAssignmentComposite.this.viewPart.setModified(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		cmdNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					if ((MxAssignmentComposite.this.business instanceof MxTreePerson)) {
						((MxTreePerson)MxAssignmentComposite.this.business).addAssignment(MxAssignmentComposite.this.assignmentType);
					} else if ((MxAssignmentComposite.this.business instanceof MxTreeAssignment)) {
						if (MxAssignmentComposite.this.parentAssignment) {
							((MxTreeAssignment)MxAssignmentComposite.this.business).addParentAssignment(MxAssignmentComposite.this.assignmentType);
						} else {
							((MxTreeAssignment)MxAssignmentComposite.this.business).addChildAssignment(MxAssignmentComposite.this.assignmentType);
						}

					}

					MxAssignmentComposite.this.viewPart.setModified(true);
				} catch (Exception ex) {
					Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
					ErrorDialog.openError(MxAssignmentComposite.this.getParent().getShell().getShell(), 
							"Unable to add type", 
							"Error Occurred while  adding an assignment to a person", 
							status);
				}
			}
		});
	}

	public void clear() {
		this.tblObjects.clearAll();
	}

	public class MxAssignmentContentProvider implements IStructuredContentProvider, IMxBusinessViewer {
		private String assignmentType;
		private boolean parentAssignment;

		public MxAssignmentContentProvider() {
		}

		public void setAssignmentType(String assignmentType) {
			this.assignmentType = assignmentType;
		}
		public String getAssignmentType() {
			return this.assignmentType;
		}
		public boolean isParentAssignment() {
			return this.parentAssignment;
		}
		public void setParentAssignment(boolean parentAssignment) {
			this.parentAssignment = parentAssignment;
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			MxTreeBusiness newBusiness = (MxTreeBusiness)newInput;
			MxTreeBusiness oldBusiness = (MxTreeBusiness)oldInput;

			if (oldInput != null) {
				oldBusiness.removeChangeListener(this);
			}
			if (newInput != null) {
				if ((newInput instanceof MxTreePerson)) {
					setAssignmentType(MxAssignmentComposite.this.assignmentType);
				} else if ((newInput instanceof MxTreeAssignment)) {
					setParentAssignment(MxAssignmentComposite.this.parentAssignment);
				}
				newBusiness.addChangeListener(this);
			}
			MxAssignmentComposite.this.tableViewer.refresh();
		}

		public void dispose() {
			MxAssignmentComposite.this.business.removeChangeListener(this);
		}

		public Object[] getElements(Object parent) {
			if (MxAssignmentComposite.this.business != null) {
				ArrayList alRet = null;
				if ((MxAssignmentComposite.this.business instanceof MxTreePerson)) {
					if (this.assignmentType.equals("Role")) {
						alRet = ((MxTreePerson)MxAssignmentComposite.this.business).getRoles(false);
					} else {
						alRet = ((MxTreePerson)MxAssignmentComposite.this.business).getGroups(false);
					}
				} else if ((MxAssignmentComposite.this.business instanceof MxTreeAssignment)) {
					if (this.parentAssignment) {
						alRet = ((MxTreeAssignment)MxAssignmentComposite.this.business).getParentAssignments(false);
					} else {
						alRet = ((MxTreeAssignment)MxAssignmentComposite.this.business).getChildrenAssignments(false);
					}
				}
				return alRet.toArray(new MxTreeAssignment[alRet.size()]);
			}
			return new Object[0];
		}

		public void addProperty(MxTreeBusiness task) {
			if ((task instanceof MxTreeAssignment)) {
				MxAssignmentComposite.this.tableViewer.add(task);
				TableItem[] items = MxAssignmentComposite.this.tblObjects.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].getData().equals(task)) {
						MxAssignmentComposite.this.tblObjects.setSelection(i);
						break;
					}
				}
			}
		}

		public void removeProperty(MxTreeBusiness task) {
			if ((task instanceof MxTreeAssignment)) {
				MxAssignmentComposite.this.tableViewer.remove(task);
			}
		}

		public void updateProperty(MxTreeBusiness property) {
			if ((property instanceof MxTreeAssignment)) {
				MxAssignmentComposite.this.tableViewer.update(property, null);
				MxAssignmentComposite.this.viewPart.setModified(true);
			}
		}
	}
}