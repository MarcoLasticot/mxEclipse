package org.mxeclipse.business.table.type;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.mxeclipse.business.tree.MxBusinessSorter;
import org.mxeclipse.model.IMxBusinessViewer;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeRelationship;
import org.mxeclipse.model.MxTreeRelationship.DirectionInfo;
import org.mxeclipse.model.MxTreeType;
import org.mxeclipse.views.IModifyable;

public class MxTypeComposite extends Composite
{
  private Table tblObjects = null;
  private ToolBar toolBar = null;
  private TableViewer tableViewer = null;
  private MxTreeBusiness business;
  private IModifyable viewPart;
  private String relType;
  public static String BASIC_NAME = "Name";

  private String[] columnNames = { 
    BASIC_NAME };

  private Label lblCardinality = null;
  private Combo cmbCardinality = null;
  private Label lblRevision = null;
  private Combo cmbRevision = null;
  private Label lblClone = null;
  private Combo cmbClone = null;
  private Button chkPropagateModify = null;
  private Button chkPropagateConnection = null;
  private Label lblTitle = null;

  public MxTreeBusiness getBusiness()
  {
    return this.business;
  }

  public MxTypeComposite(Composite parent, int style, MxTreeBusiness business, IModifyable view, String relType)
  {
    super(parent, style);
    this.viewPart = view;
    this.relType = relType;
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
    GridData gridData21 = new GridData();
    gridData21.horizontalSpan = 2;
    GridData gridData11 = new GridData();
    gridData11.horizontalSpan = 2;
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

    this.lblCardinality = new Label(this, 0);
    this.lblCardinality.setText("Cardinality");
    TableColumn tableColumn = new TableColumn(this.tblObjects, 0, 0);
    tableColumn.setWidth(300);
    tableColumn.setText("Name");

    createCmbCardinality();
    setSize(new Point(331, 200));
    Label filler3 = new Label(this, 0);
    Label filler7 = new Label(this, 0);
    this.lblRevision = new Label(this, 0);
    this.lblRevision.setText("Revision");
    createCmbRevision();
    this.lblClone = new Label(this, 0);
    this.lblClone.setText("Clone");
    createCmbClone();
    this.chkPropagateModify = new Button(this, 32);
    this.chkPropagateModify.setText("Propagate Modify");
    this.chkPropagateModify.setLayoutData(gridData11);
    this.chkPropagateConnection = new Button(this, 32);
    this.chkPropagateConnection.setText("Propagate Connection");
    this.chkPropagateConnection.setLayoutData(gridData21);
    CellEditor[] editors = new CellEditor[this.columnNames.length];
    try
    {
      ArrayList allTypes = MxTreeType.getAllTypes(false);
      String[] typeNames = new String[allTypes.size()];
      for (int i = 0; i < typeNames.length; i++) {
        typeNames[i] = ((MxTreeType)allTypes.get(i)).getName();
      }
      editors[0] = new ComboBoxCellEditor(this.tblObjects, typeNames, 8);
    } catch (Exception ex) {
      MessageDialog.openError(getShell(), "Type Retrieval", "Error when retrieving a list of all types in the system!");
    }

    this.tableViewer = new TableViewer(this.tblObjects);
    this.tableViewer.setUseHashlookup(true);
    this.tableViewer.setColumnProperties(this.columnNames);
    MxTypeContentProvider contentProvider = new MxTypeContentProvider();
    this.tableViewer.setContentProvider(contentProvider);
    this.tableViewer.setLabelProvider(new MxTypeLabelProvider());
    this.tableViewer.setCellEditors(editors);
    this.tableViewer.setCellModifier(new MxTypeCellModifier(this));
    this.tableViewer.setSorter(new MxBusinessSorter(BASIC_NAME));
  }

  public void setData(MxTreeBusiness business)
  {
    this.tblObjects.removeAll();

    this.business = business;
    this.tableViewer.setInput(business);

    this.tblObjects.redraw();

    if (this.relType.equals("from"))
      this.lblTitle.setText("From Types and Options");
    else
      this.lblTitle.setText("To Types and Options");
  }

  public void initializeDirectionInfo(MxTreeRelationship relationship)
  {
    this.business = relationship;
    MxTreeRelationship.DirectionInfo directionInfo = this.relType.equals("from") ? relationship.getFromInfo() : relationship.getToInfo();

    this.cmbCardinality.setItems(MxTreeRelationship.CARDINALITIES);
    for (int i = 0; i < this.cmbCardinality.getItemCount(); i++) {
      if (this.cmbCardinality.getItem(i).equals(directionInfo.getCardinality())) {
        this.cmbCardinality.select(i);
      }
    }

    this.cmbRevision.setItems(MxTreeRelationship.REVISION_ACTIONS);
    for (int i = 0; i < this.cmbRevision.getItemCount(); i++) {
      if (this.cmbRevision.getItem(i).equals(directionInfo.getRevision())) {
        this.cmbRevision.select(i);
      }
    }

    this.cmbClone.setItems(MxTreeRelationship.CLONE_ACTIONS);
    for (int i = 0; i < this.cmbClone.getItemCount(); i++) {
      if (this.cmbClone.getItem(i).equals(directionInfo.getClone())) {
        this.cmbClone.select(i);
      }
    }

    this.chkPropagateConnection.setSelection(directionInfo.isPropagateConnection());

    this.chkPropagateModify.setSelection(directionInfo.isPropagateModify());
  }

  public void storeDirectionInfo() {
    MxTreeRelationship relationship = (MxTreeRelationship)this.business;
    MxTreeRelationship.DirectionInfo directionInfo = this.relType.equals("from") ? relationship.getFromInfo() : relationship.getToInfo();

    directionInfo.setCardinality(this.cmbCardinality.getItem(this.cmbCardinality.getSelectionIndex()));
    directionInfo.setRevision(this.cmbRevision.getItem(this.cmbRevision.getSelectionIndex()));
    directionInfo.setClone(this.cmbClone.getItem(this.cmbClone.getSelectionIndex()));
    directionInfo.setPropagateModify(this.chkPropagateModify.getSelection());
    directionInfo.setPropagateConnection(this.chkPropagateConnection.getSelection());
  }

  private void createToolBar()
  {
    this.toolBar = new ToolBar(this, 0);
    ToolItem cmdNew = new ToolItem(this.toolBar, 8);
    cmdNew.setText("New");
    ToolItem cmdDelete = new ToolItem(this.toolBar, 8);
    cmdDelete.setText("Delete");
    cmdDelete.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent e) {
        Iterator itSelected = ((IStructuredSelection)MxTypeComposite.this.tableViewer.getSelection()).iterator();
        while (itSelected.hasNext()) {
          MxTreeType type = (MxTreeType)itSelected.next();
          if (type != null) {
            if (MxTypeComposite.this.relType.equals("from"))
              ((MxTreeRelationship)MxTypeComposite.this.business).removeType(type, true);
            else if (MxTypeComposite.this.relType.equals("to")) {
              ((MxTreeRelationship)MxTypeComposite.this.business).removeType(type, false);
            }
          }
        }

        MxTypeComposite.this.viewPart.setModified(true);
      }

      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
    });
    cmdNew.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        try {
          if (MxTypeComposite.this.relType.equals("from"))
            ((MxTreeRelationship)MxTypeComposite.this.business).addType(true);
          else if (MxTypeComposite.this.relType.equals("to")) {
            ((MxTreeRelationship)MxTypeComposite.this.business).addType(false);
          }

          MxTypeComposite.this.viewPart.setModified(true);
        } catch (Exception ex) {
          Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
          ErrorDialog.openError(MxTypeComposite.this.getParent().getShell().getShell(), 
            "Unable to add type", 
            "Error Occurred while  adding a type to an admin object ", 
            status);
        }
      } } );
  }

  public void clear() {
    this.tblObjects.clearAll();
  }

  private void createCmbCardinality()
  {
    GridData gridData5 = new GridData();
    gridData5.grabExcessHorizontalSpace = true;
    gridData5.verticalAlignment = 2;
    gridData5.horizontalAlignment = 4;
    this.cmbCardinality = new Combo(this, 0);
    this.cmbCardinality.setLayoutData(gridData5);
  }

  private void createCmbRevision()
  {
    GridData gridData4 = new GridData();
    gridData4.grabExcessHorizontalSpace = true;
    gridData4.verticalAlignment = 2;
    gridData4.horizontalAlignment = 4;
    this.cmbRevision = new Combo(this, 0);
    this.cmbRevision.setLayoutData(gridData4);
  }

  private void createCmbClone()
  {
    GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.verticalAlignment = 2;
    gridData2.horizontalAlignment = 4;
    this.cmbClone = new Combo(this, 0);
    this.cmbClone.setLayoutData(gridData2);
  }

  public class MxTypeContentProvider
    implements IStructuredContentProvider, IMxBusinessViewer
  {
    private boolean from;

    public MxTypeContentProvider()
    {
    }

    public void setFrom(boolean from)
    {
      this.from = from;
    }
    public boolean getFrom() {
      return this.from;
    }

    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
      MxTreeBusiness newBusiness = (MxTreeBusiness)newInput;
      MxTreeBusiness oldBusiness = (MxTreeBusiness)oldInput;

      if (oldInput != null) {
        oldBusiness.removeChangeListener(this);
      }
      if (newInput != null) {
        if ((newInput instanceof MxTreeRelationship))
        {
          String relType = MxTypeComposite.this.relType;
          if (relType != null) {
            setFrom(relType.equals("from"));
          }
        }
        newBusiness.addChangeListener(this);
      }
      MxTypeComposite.this.tableViewer.refresh();
    }

    public void dispose() {
      MxTypeComposite.this.business.removeChangeListener(this);
    }

    public Object[] getElements(Object parent)
    {
      if (MxTypeComposite.this.business != null) {
        ArrayList alRet = null;
        if (MxTypeComposite.this.relType.equals("from"))
          alRet = ((MxTreeRelationship)MxTypeComposite.this.business).getTypes(false, true);
        else if (MxTypeComposite.this.relType.equals("to")) {
          alRet = ((MxTreeRelationship)MxTypeComposite.this.business).getTypes(false, false);
        }
        return alRet.toArray(new MxTreeType[alRet.size()]);
      }
      return new Object[0];
    }

    public void addProperty(MxTreeBusiness task)
    {
      if ((task instanceof MxTreeType)) {
        MxTypeComposite.this.tableViewer.add(task);
        TableItem[] items = MxTypeComposite.this.tblObjects.getItems();
        for (int i = 0; i < items.length; i++)
          if (items[i].getData().equals(task)) {
            MxTypeComposite.this.tblObjects.setSelection(i);
            break;
          }
      }
    }

    public void removeProperty(MxTreeBusiness task)
    {
      if ((task instanceof MxTreeType))
        MxTypeComposite.this.tableViewer.remove(task);
    }

    public void updateProperty(MxTreeBusiness property)
    {
      if ((property instanceof MxTreeType)) {
        MxTypeComposite.this.tableViewer.update(property, null);
        MxTypeComposite.this.viewPart.setModified(true);
      }
    }
  }
}