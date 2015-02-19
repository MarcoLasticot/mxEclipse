package org.mxeclipse.business.table.setting;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.mxeclipse.model.IMxBusinessViewer;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeWeb;
import org.mxeclipse.model.MxTreeWebSetting;
import org.mxeclipse.utils.MxEclipseLogger;
import org.mxeclipse.views.IModifyable;

public class MxWebSettingComposite extends Composite {
	private Table tblObjects = null;
	private ToolBar toolBar = null;
	private TableViewer tableViewer = null;
	private TableEditor editorName;
	private TableEditor editorValue;
	private MxTreeBusiness businessObject;
	protected IModifyable viewPart;
	CellEditor[] editors;
	public static final String COLUMN_NAME = "Name";
	public static final String COLUMN_VALUE = "Value";
	public static final String COLUMN_LINK_TO_OBJECT = "Link";
	public static final String[] columnNames = { 
		"Name", "Value", "Link" };
	public static final int COL_INDEX_NAME = 0;
	public static final int COL_INDEX_VALUE = 1;
	public static final int COL_INDEX_SELECT = 2;

	public MxTreeBusiness getBusiness() {
		return this.businessObject;
	}

	public MxWebSettingComposite(Composite parent, int style, MxTreeBusiness businessObject, IModifyable view) {
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

		TableColumn tableColumnType = new TableColumn(this.tblObjects, 0, 0);
		tableColumnType.setWidth(200);
		tableColumnType.setText("Name");

		TableColumn tableColumnName = new TableColumn(this.tblObjects, 0, 1);
		tableColumnName.setWidth(200);
		tableColumnName.setText("Value");

		TableColumn tableColumnsLink = new TableColumn(this.tblObjects, 0, 2);
		tableColumnsLink.setWidth(16);
		tableColumnsLink.setText("Link");

		setSize(new Point(437, 200));
		this.editorName = new TableEditor(this.tblObjects);

		this.editorName.horizontalAlignment = 4;
		this.editorName.grabHorizontal = true;
		this.editorName.minimumWidth = 50;

		this.editorValue = new TableEditor(this.tblObjects);

		this.editorValue.horizontalAlignment = 4;
		this.editorValue.grabHorizontal = true;
		this.editorValue.minimumWidth = 50;

		this.tblObjects.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Control oldEditorName = MxWebSettingComposite.this.editorName.getEditor();
				if (oldEditorName != null) oldEditorName.dispose();
				Control oldEditorValue = MxWebSettingComposite.this.editorValue.getEditor();
				if (oldEditorValue != null) {
					oldEditorValue.dispose();
				}

				final TableItem item = (TableItem)e.item;
				if (item == null) return;

				CCombo nameEditor = new CCombo(MxWebSettingComposite.this.tblObjects, 0);
				nameEditor.setText(item.getText(0));
				String[] allSettingNames = MxTreeWebSetting.getAllSettingNames((MxTreeWeb)MxWebSettingComposite.this.businessObject);
				nameEditor.setItems(allSettingNames);
				int selIndex = -1;
				for (int i = 0; i < allSettingNames.length; i++) {
					if (allSettingNames[i].equals(item.getText(0))) {
						selIndex = i;
					}
				}
				if (selIndex >= 0) {
					nameEditor.select(selIndex);
				}
				nameEditor.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent me) {
						CCombo cmbName = (CCombo)editorName.getEditor();
						editorName.getItem().setText(0, cmbName.getText());
						MxTreeWebSetting mxProperty = (MxTreeWebSetting)item.getData();
						mxProperty.setName(cmbName.getText());
						getBusiness().propertyChanged(mxProperty);
						initializeValueEditor(item, mxProperty);
					}
				});
				MxWebSettingComposite.this.editorName.setEditor(nameEditor, item, 0);

				MxWebSettingComposite.this.initializeValueEditor(item, (MxTreeWebSetting)item.getData());
			}
		});
		this.tableViewer = new TableViewer(this.tblObjects);
		this.tableViewer.setUseHashlookup(true);
		this.tableViewer.setColumnProperties(columnNames);
		this.tableViewer.setContentProvider(new MxWebContentProvider());
		this.tableViewer.setLabelProvider(new MxWebSettingLabelProvider());
		this.tableViewer.setCellEditors(this.editors);
	}

	private void initializeValueEditor(final TableItem item, MxTreeWebSetting setting) {
		if (this.editorValue.getEditor() != null) {
			this.editorValue.getEditor().dispose();
		}
		if (setting.getRange() != null) {
			CCombo cmbValue = new CCombo(this.tblObjects, 0);

			cmbValue.setItems(setting.getRange());
			int selIndex = -1;
			for (int i = 0; i < setting.getRange().length; i++) {
				if (setting.getRange()[i].equals(item.getText(1))) {
					selIndex = i;
				}
			}
			cmbValue.select(selIndex);
			cmbValue.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent me) {
					CCombo combo = (CCombo)editorValue.getEditor();
					editorValue.getItem().setText(1, combo.getText());
					MxTreeWebSetting mxProperty = (MxTreeWebSetting)item.getData();
					mxProperty.setValue(combo.getText());
					getBusiness().propertyChanged(mxProperty);
				}
			});
			cmbValue.setFocus();
			this.editorValue.setEditor(cmbValue, item, 1);
		} else {
			Text txtValue = new Text(this.tblObjects, 0);
			txtValue.setText(item.getText(1));
			txtValue.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent me) {
					Text text = (Text)editorValue.getEditor();
					editorValue.getItem().setText(1, text.getText());
					MxTreeWebSetting mxProperty = (MxTreeWebSetting)item.getData();
					mxProperty.setValue(text.getText());
					getBusiness().propertyChanged(mxProperty);
				}
			});
			txtValue.selectAll();
			txtValue.setFocus();

			this.editorValue.setEditor(txtValue, item, 1);
		}
	}

	public void setData(MxTreeBusiness businessObject) {
		this.tblObjects.removeAll();
		if (this.editorName.getEditor() != null) {
			this.editorName.getEditor().dispose();
		}
		if (this.editorValue.getEditor() != null) {
			this.editorValue.getEditor().dispose();
		}

		this.businessObject = businessObject;

		this.tableViewer.setInput(businessObject);

		this.tblObjects.redraw();
	}

	private void createToolBar() {
		this.toolBar = new ToolBar(this, 0);
		ToolItem cmdNew = new ToolItem(this.toolBar, 8);
		cmdNew.setText("Add");
		ToolItem cmdDelete = new ToolItem(this.toolBar, 8);
		cmdDelete.setText("Delete");
		cmdDelete.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Iterator itSelected = ((IStructuredSelection)MxWebSettingComposite.this.tableViewer.getSelection()).iterator();
				while (itSelected.hasNext()) {
					MxTreeWebSetting item = (MxTreeWebSetting)itSelected.next();
					if (item != null) {
						((MxTreeWeb)MxWebSettingComposite.this.businessObject).removeSetting(item);
					}
				}

				MxWebSettingComposite.this.viewPart.setModified(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		cmdNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					((MxTreeWeb)MxWebSettingComposite.this.businessObject).addSetting();

					MxWebSettingComposite.this.viewPart.setModified(true);
				} catch (Exception ex) {
					Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
					ErrorDialog.openError(MxWebSettingComposite.this.getParent().getShell().getShell(), 
							"Unable to add state", 
							"Error Occurred while  adding a state to a policy", 
							status);
				}
			}
		});
	}

	public void clear() {
		this.tblObjects.clearAll();
	}

	class MxWebContentProvider implements IStructuredContentProvider, IMxBusinessViewer {
		MxWebContentProvider() {
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			MxTreeBusiness newType = (MxTreeBusiness)newInput;
			MxTreeBusiness oldType = (MxTreeBusiness)oldInput;
			if (oldInput != null){
				oldType.removeChangeListener(this);
			}
			if (newInput != null){
				newType.addChangeListener(this);
			}
			MxWebSettingComposite.this.tableViewer.refresh();
		}

		public void dispose() {
			MxWebSettingComposite.this.businessObject.removeChangeListener(this);
		}

		public Object[] getElements(Object parent) {
			try {
				if (MxWebSettingComposite.this.businessObject != null) {
					ArrayList alItems = ((MxTreeWeb)MxWebSettingComposite.this.businessObject).getSettings(false);
					return alItems.toArray(new MxTreeWebSetting[alItems.size()]);
				}
				return new Object[0];
			}
			catch (Exception ex) {
				MxEclipseLogger.getLogger().severe(ex.getMessage());
			}
			return new Object[0];
		}

		public void addProperty(MxTreeBusiness task) {
			if ((task instanceof MxTreeWebSetting)) {
				MxWebSettingComposite.this.tableViewer.add(task);
				TableItem[] items = MxWebSettingComposite.this.tblObjects.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].getData().equals(task)) {
						MxWebSettingComposite.this.tblObjects.setSelection(i);
						break;
					}
				}
			}
		}

		public void removeProperty(MxTreeBusiness task) {
			if ((task instanceof MxTreeWebSetting)) {
				MxWebSettingComposite.this.tableViewer.remove(task);
				if (MxWebSettingComposite.this.editorName.getEditor() != null) {
					MxWebSettingComposite.this.editorName.getEditor().dispose();
				}
				if (MxWebSettingComposite.this.editorValue.getEditor() != null) {
					MxWebSettingComposite.this.editorValue.getEditor().dispose();
				}
			}
		}

		public void updateProperty(MxTreeBusiness property) {
			if ((property instanceof MxTreeWebSetting)) {
				MxWebSettingComposite.this.tableViewer.update(property, null);
				MxWebSettingComposite.this.viewPart.setModified(true);
			}
		}
	}

}
