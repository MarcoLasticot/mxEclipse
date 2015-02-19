package org.mxeclipse.business.table.trigger;

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
import org.eclipse.jface.viewers.TextCellEditor;
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
import org.mxeclipse.model.MxTreeTrigger;
import org.mxeclipse.utils.DummyCellEditor;
import org.mxeclipse.utils.MxEclipseLogger;
import org.mxeclipse.views.IModifyable;

public class MxTriggerComposite extends Composite {
	private Table tblObjects = null;
	private ToolBar toolBar = null;
	private TableViewer tableViewer = null;
	private MxTreeBusiness businessObject;
	private IModifyable viewPart;
	public static final String COLUMN_EVENT_TYPE = "EventType";
	public static final String COLUMN_TRIGGER_TYPE = "TriggerType";
	public static final String COLUMN_PROGRAM_NAME = "Program";
	public static final String COLUMN_ARGS = "Args";
	public static final String COLUMN_LINK_TO_OBJECT = "Link";
	public static final String[] columnNames = { 
		"EventType", "TriggerType", "Program", "Args", "Link" };

	public MxTreeBusiness getBusiness() {
		return this.businessObject;
	}

	public MxTriggerComposite(Composite parent, int style, MxTreeBusiness businessObject, IModifyable view) {
		super(parent, style);
		initialize();
		setData(businessObject);
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

		TableColumn tableColumnEventType = new TableColumn(this.tblObjects, 0, 0);
		tableColumnEventType.setWidth(100);
		tableColumnEventType.setText("Event Type");

		TableColumn tableColumnTriggerType = new TableColumn(this.tblObjects, 0, 1);
		tableColumnTriggerType.setWidth(70);
		tableColumnTriggerType.setText("Trigger");

		TableColumn tableColumnProgram = new TableColumn(this.tblObjects, 0, 2);
		tableColumnProgram.setWidth(100);
		tableColumnProgram.setText("Program");

		TableColumn tableColumnArgs = new TableColumn(this.tblObjects, 0, 3);
		tableColumnArgs.setWidth(200);
		tableColumnArgs.setText("Args");

		TableColumn tableColumnsLink = new TableColumn(this.tblObjects, 0, 4);
		tableColumnsLink.setWidth(16);
		tableColumnsLink.setText("Link");

		setSize(new Point(437, 200));
		CellEditor[] editors = new CellEditor[columnNames.length];
		try {
			String[] allEventTypes = MxTreeTrigger.getAvailableEventTypes(this.businessObject);
			editors[0] = new ComboBoxCellEditor(this.tblObjects, allEventTypes, 0);
		} catch (Exception ex) {
			MessageDialog.openError(getShell(), "Trigger retrieval", "Error when retrieving a list of all trigger types for type " + this.businessObject.getType() + "!");
		}

		editors[1] = new ComboBoxCellEditor(this.tblObjects, MxTreeTrigger.TRIGGER_TYPES);

		editors[2] = new TextCellEditor(this.tblObjects, 0);
		editors[3] = new TextCellEditor(this.tblObjects, 0);
		editors[4] = new DummyCellEditor(this.tblObjects, 0);

		this.tableViewer = new TableViewer(this.tblObjects);
		this.tableViewer.setUseHashlookup(true);
		this.tableViewer.setColumnProperties(columnNames);
		this.tableViewer.setContentProvider(new MxTriggerContentProvider());
		this.tableViewer.setLabelProvider(new MxTriggerLabelProvider());
		this.tableViewer.setCellEditors(editors);
		this.tableViewer.setCellModifier(new MxTriggerCellModifier(this));
		this.tableViewer.setSorter(new MxBusinessSorter("EventType"));
	}

	public void setData(MxTreeBusiness businessObject) {
		this.tblObjects.removeAll();

		this.businessObject = businessObject;
		try {
			String[] allEventTypes = MxTreeTrigger.getAvailableEventTypes(businessObject);
			((ComboBoxCellEditor)this.tableViewer.getCellEditors()[0]).setItems(allEventTypes);
		} catch (Exception ex) {
			MessageDialog.openError(getShell(), "Trigger retrieval", "Error when retrieving a list of all trigger types for type " + businessObject.getType() + "!");
		}

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
				Iterator itSelected = ((IStructuredSelection)MxTriggerComposite.this.tableViewer.getSelection()).iterator();
				while (itSelected.hasNext()) {
					MxTreeTrigger trigger = (MxTreeTrigger)itSelected.next();
					if (trigger != null) {
						MxTriggerComposite.this.businessObject.removeTrigger(trigger);
					}
				}

				MxTriggerComposite.this.viewPart.setModified(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		cmdNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					MxTriggerComposite.this.businessObject.addTrigger();

					MxTriggerComposite.this.viewPart.setModified(true);
				} catch (Exception ex) {
					Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
					ErrorDialog.openError(MxTriggerComposite.this.getParent().getShell().getShell(), 
							"Unable to add trigger", 
							"Error Occurred while  adding a trigger to a type", 
							status);
				}
			}
		});
	}

	public void clear() {
		this.tblObjects.clearAll();
	}

	class MxTriggerContentProvider implements IStructuredContentProvider, IMxBusinessViewer {
		MxTriggerContentProvider() {
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
			MxTriggerComposite.this.tableViewer.refresh();
		}

		public void dispose() {
			MxTriggerComposite.this.businessObject.removeChangeListener(this);
		}

		public Object[] getElements(Object parent) {
			try {
				if (MxTriggerComposite.this.businessObject != null) {
					ArrayList alTriggers = MxTriggerComposite.this.businessObject.getTriggers(false);
					return alTriggers.toArray(new MxTreeTrigger[alTriggers.size()]);
				}
				return new Object[0];
			} catch (Exception ex) {
				MxEclipseLogger.getLogger().severe(ex.getMessage());
			}
			return new Object[0];
		}

		public void addProperty(MxTreeBusiness task) {
			if ((task instanceof MxTreeTrigger)) {
				MxTriggerComposite.this.tableViewer.add(task);
				TableItem[] items = MxTriggerComposite.this.tblObjects.getItems();
				for (int i = 0; i < items.length; i++)
					if (items[i].getData().equals(task)) {
						MxTriggerComposite.this.tblObjects.setSelection(i);
						break;
					}
			}
		}

		public void removeProperty(MxTreeBusiness task) {
			if ((task instanceof MxTreeTrigger))
				MxTriggerComposite.this.tableViewer.remove(task);
		}

		public void updateProperty(MxTreeBusiness property) {
			if ((property instanceof MxTreeTrigger)) {
				MxTriggerComposite.this.tableViewer.update(property, null);
				MxTriggerComposite.this.viewPart.setModified(true);
			}
		}
	}

}
