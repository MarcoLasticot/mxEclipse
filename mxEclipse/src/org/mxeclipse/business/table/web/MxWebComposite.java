package org.mxeclipse.business.table.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.mxeclipse.model.IMxStateViewer;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeWebMenu;
import org.mxeclipse.model.MxTreeWebNavigation;
import org.mxeclipse.utils.DummyCellEditor;
import org.mxeclipse.utils.MxEclipseLogger;
import org.mxeclipse.views.IModifyable;

public class MxWebComposite extends Composite
{
  private Table tblObjects = null;
  private ToolBar toolBar = null;
  private TableViewer tableViewer = null;
  private MxTreeBusiness businessObject;
  protected IModifyable viewPart;
  CellEditor[] editors;
  public static final String COLUMN_TYPE = "Type";
  public static final String COLUMN_NAME = "Name";
  public static final String COLUMN_LINK_TO_OBJECT = "Link";
  public static final String[] columnNames = { 
    "Type", "Name", "Link" };
  public static final int COL_INDEX_TYPE = 0;
  public static final int COL_INDEX_NAME = 1;
  public static final int COL_INDEX_SELECT = 2;

  public MxTreeBusiness getBusiness()
  {
    return this.businessObject;
  }

  public MxWebComposite(Composite parent, int style, MxTreeBusiness businessObject, IModifyable view)
  {
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
    tableColumnType.setWidth(100);
    tableColumnType.setText("Type");

    TableColumn tableColumnName = new TableColumn(this.tblObjects, 0, 1);
    tableColumnName.setWidth(200);
    tableColumnName.setText("Name");

    TableColumn tableColumnsLink = new TableColumn(this.tblObjects, 0, 2);
    tableColumnsLink.setWidth(16);
    tableColumnsLink.setText("Link");

    setSize(new Point(437, 200));
    this.editors = new CellEditor[columnNames.length];
    try
    {
      this.editors[0] = new ComboBoxCellEditor(this.tblObjects, MxTreeWebNavigation.ALL_WEB_TYPES, 0);
      this.editors[1] = new ComboBoxCellEditor(this.tblObjects, MxTreeWebNavigation.getAllItemNames(false), 0);
    } catch (Exception ex) {
      MessageDialog.openError(getShell(), "Item info retrieval", "Error when retrieving a list of all menus/commands!");
    }
    this.editors[2] = new DummyCellEditor(this.tblObjects, 0);

    this.tableViewer = new TableViewer(this.tblObjects);
    this.tableViewer.setUseHashlookup(true);
    this.tableViewer.setColumnProperties(columnNames);
    this.tableViewer.setContentProvider(new MxWebContentProvider());
    this.tableViewer.setLabelProvider(new MxWebLabelProvider());
    this.tableViewer.setCellEditors(this.editors);
    this.tableViewer.setCellModifier(new MxWebCellModifier(this));
  }

  public void setData(MxTreeBusiness businessObject)
  {
    this.tblObjects.removeAll();

    this.businessObject = businessObject;

    this.tableViewer.setInput(businessObject);

    this.tblObjects.redraw();
  }

  private void createToolBar()
  {
    this.toolBar = new ToolBar(this, 0);
    ToolItem cmdNew = new ToolItem(this.toolBar, 8);
    cmdNew.setText("Add");
    ToolItem cmdInsert = new ToolItem(this.toolBar, 8);
    cmdInsert.setText("Insert");
    ToolItem cmdDelete = new ToolItem(this.toolBar, 8);
    cmdDelete.setText("Delete");
    cmdDelete.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent e) {
        Iterator itSelected = ((IStructuredSelection)MxWebComposite.this.tableViewer.getSelection()).iterator();
        while (itSelected.hasNext()) {
          MxTreeWebNavigation item = (MxTreeWebNavigation)itSelected.next();
          if (item != null) {
            ((MxTreeWebMenu)MxWebComposite.this.businessObject).removeChildItem(item);
          }
        }

        MxWebComposite.this.viewPart.setModified(true);
      }

      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
    });
    cmdNew.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        try {
          ((MxTreeWebMenu)MxWebComposite.this.businessObject).addChildItem(true);

          MxWebComposite.this.viewPart.setModified(true);
        } catch (Exception ex) {
          Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
          ErrorDialog.openError(MxWebComposite.this.getParent().getShell().getShell(), 
            "Unable to add state", 
            "Error Occurred while  adding a state to a policy", 
            status);
        }
      }
    });
    cmdInsert.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        try {
          int selIndex = MxWebComposite.this.tblObjects.getSelectionIndex();
          if (selIndex < 0) {
            MessageDialog.openInformation(MxWebComposite.this.getParent().getShell().getShell(), "MxWebMenu", "Nothing selected for insertion!");
            return;
          }
          ((MxTreeWebMenu)MxWebComposite.this.businessObject).insertChildItem(selIndex, true);

          MxWebComposite.this.viewPart.setModified(true);
        } catch (Exception ex) {
          Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
          ErrorDialog.openError(MxWebComposite.this.getParent().getShell().getShell(), 
            "Unable to add state", 
            "Error Occurred while  adding an item to menu", 
            status);
        }
      }
    });
  }

  public void clear()
  {
    this.tblObjects.clearAll();
  }

  class MxWebContentProvider implements IStructuredContentProvider, IMxStateViewer
  {
    MxWebContentProvider()
    {
    }

    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
      MxTreeBusiness newType = (MxTreeBusiness)newInput;
      MxTreeBusiness oldType = (MxTreeBusiness)oldInput;
      if (oldInput != null)
        oldType.removeChangeListener(this);
      if (newInput != null)
        newType.addChangeListener(this);
      MxWebComposite.this.tableViewer.refresh();
    }

    public void dispose() {
      MxWebComposite.this.businessObject.removeChangeListener(this);
    }

    public Object[] getElements(Object parent)
    {
      try {
        if (MxWebComposite.this.businessObject != null) {
          ArrayList alItems = ((MxTreeWebMenu)MxWebComposite.this.businessObject).getChildrenItems(false);
          return alItems.toArray(new MxTreeWebNavigation[alItems.size()]);
        }
        return new Object[0];
      }
      catch (Exception ex) {
        MxEclipseLogger.getLogger().severe(ex.getMessage());
      }return new Object[0];
    }

    public void addProperty(MxTreeBusiness task)
    {
      if ((task instanceof MxTreeWebNavigation)) {
        MxWebComposite.this.tableViewer.add(task);
        TableItem[] items = MxWebComposite.this.tblObjects.getItems();
        for (int i = 0; i < items.length; i++)
          if (items[i].getData().equals(task)) {
            MxWebComposite.this.tblObjects.setSelection(i);
            break;
          }
      }
    }

    public void insertProperty(MxTreeBusiness task, int index)
    {
      if ((task instanceof MxTreeWebNavigation)) {
        MxWebComposite.this.tableViewer.insert(task, index);
        TableItem[] items = MxWebComposite.this.tblObjects.getItems();
        for (int i = 0; i < items.length; i++)
          if (items[i].getData().equals(task)) {
            MxWebComposite.this.tblObjects.setSelection(i);
            break;
          }
      }
    }

    public void removeProperty(MxTreeBusiness task)
    {
      if ((task instanceof MxTreeWebNavigation))
        MxWebComposite.this.tableViewer.remove(task);
    }

    public void updateProperty(MxTreeBusiness property)
    {
      if ((property instanceof MxTreeWebNavigation)) {
        MxWebComposite.this.tableViewer.update(property, null);
        MxWebComposite.this.viewPart.setModified(true);
      }
    }
  }
}