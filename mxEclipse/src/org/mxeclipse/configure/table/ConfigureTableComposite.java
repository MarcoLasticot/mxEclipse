package org.mxeclipse.configure.table;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import matrix.db.AttributeType;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.mxeclipse.model.MxTableColumn;
import org.mxeclipse.model.MxTreeDomainObject;
import org.mxeclipse.utils.MxEclipseUtils;

public class ConfigureTableComposite extends Composite
{
  private Table tblObjects = null;
  private ToolBar toolBar = null;
  private TableViewer tableViewer = null;
  private MxTableColumnList columns;
  TreeMap<String, AttributeType> allAttributes;
  MxTableColumnList initialColumns;
  private String[] columnNames = { 
    MxTableColumn.FIELD_NAME, 
    MxTableColumn.FIELD_TYPE, 
    MxTableColumn.FIELD_VISIBLE, 
    MxTableColumn.FIELD_ON_RELATIONSHIP, 
    MxTableColumn.FIELD_WIDTH };

  private Button cmdUp = null;
  private Button cmdDown = null;

  public ConfigureTableComposite(Composite parent, int style, MxTableColumnList initialColumns)
  {
    super(parent, style);
    this.initialColumns = initialColumns;
    initialize();
  }

  private void initialize()
  {
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
    this.cmdUp = new Button(this, 0);
    this.cmdUp.setText("Move Up");
    this.cmdUp.setLayoutData(gridData3);
    this.cmdUp.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        if (ConfigureTableComposite.this.tblObjects.getSelectionIndex() >= 0) {
          int selectedIndex = ConfigureTableComposite.this.tblObjects.getSelectionIndex();
          ConfigureTableComposite.this.columns.moveUp(selectedIndex);
          ConfigureTableComposite.this.tableViewer.refresh();
          ConfigureTableComposite.this.tblObjects.setSelection(selectedIndex - 1);
        }
      }
    });
    this.cmdDown = new Button(this, 0);
    this.cmdDown.setText("Move Down");
    this.cmdDown.setLayoutData(gridData1);
    this.cmdDown.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        if (ConfigureTableComposite.this.tblObjects.getSelectionIndex() < ConfigureTableComposite.this.tblObjects.getItemCount() - 1) {
          int selectedIndex = ConfigureTableComposite.this.tblObjects.getSelectionIndex();
          ConfigureTableComposite.this.columns.moveDown(selectedIndex);
          ConfigureTableComposite.this.tableViewer.refresh();
          ConfigureTableComposite.this.tblObjects.setSelection(selectedIndex + 1);
          ConfigureTableComposite.this.tblObjects.redraw();
        }
      }
    });
    TableColumn tableColumn = new TableColumn(this.tblObjects, 0, 0);
    tableColumn.setWidth(120);
    tableColumn.setText("Name");
    TableColumn tableColumn1 = new TableColumn(this.tblObjects, 0, 1);
    tableColumn1.setWidth(120);
    tableColumn1.setText("Column Type");
    TableColumn tableColumn2 = new TableColumn(this.tblObjects, 16777216, 2);
    tableColumn2.setWidth(20);
    tableColumn2.setText("Visible");
    TableColumn tableColumn3 = new TableColumn(this.tblObjects, 16777216, 3);
    tableColumn3.setWidth(20);
    tableColumn3.setText("On Relationship");
    TableColumn tableColumn4 = new TableColumn(this.tblObjects, 0, 4);
    tableColumn4.setWidth(50);
    tableColumn4.setText("Width");

    setSize(new Point(437, 200));
    CellEditor[] editors = new CellEditor[this.columnNames.length];

    Set allAttributes = MxEclipseUtils.getAllAttributes().keySet();
    editors[0] = new ComboBoxCellEditor(this.tblObjects, (String[])allAttributes.toArray(new String[allAttributes.size()]), 0);

    TextCellEditor textEditor = new TextCellEditor(this.tblObjects);
    ((Text)textEditor.getControl()).setTextLimit(500);
    editors[1] = textEditor;

    editors[2] = new CheckboxCellEditor(this.tblObjects);

    editors[3] = new CheckboxCellEditor(this.tblObjects);

    TextCellEditor textEditorWidth = new TextCellEditor(this.tblObjects);
    ((Text)textEditorWidth.getControl()).setTextLimit(3);
    editors[4] = textEditorWidth;

    this.tableViewer = new TableViewer(this.tblObjects);
    this.tableViewer.setUseHashlookup(true);
    this.tableViewer.setColumnProperties(this.columnNames);
    this.tableViewer.setContentProvider(new MxColumnContentProvider());
    this.tableViewer.setLabelProvider(new MxTableColumnLabelProvider());
    this.tableViewer.setCellEditors(editors);
    this.tableViewer.setCellModifier(new MxTableColumnCellModifier(this));
    this.columns = new MxTableColumnList(this.initialColumns);
    this.tableViewer.setInput(this.columns);
  }

  public void setData(List<MxTreeDomainObject> objects)
  {
    this.tblObjects.removeAll();

    this.tblObjects.redraw();
  }

  public MxTableColumnList getTableColumns() {
    return this.columns;
  }

  public void setTableColumns(MxTableColumnList columns) {
    this.columns = columns;
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
        Iterator itSelected = ((IStructuredSelection)ConfigureTableComposite.this.tableViewer.getSelection()).iterator();
        while (itSelected.hasNext()) {
          MxTableColumn column = (MxTableColumn)itSelected.next();
          if ((column != null) && (!column.getType().equals(MxTableColumn.TYPE_BASIC)))
            ConfigureTableComposite.this.columns.removeTask(column);
        }
      }

      public void widgetDefaultSelected(SelectionEvent e)
      {
      }
    });
    cmdNew.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
        ConfigureTableComposite.this.columns.addTask();
      }
    });
  }

  public MxTableColumnList getColumns()
  {
    return this.columns;
  }

  class MxColumnContentProvider implements IStructuredContentProvider, IMxTableColumnViewer
  {
    MxColumnContentProvider()
    {
    }

    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
      if (newInput != null)
        ConfigureTableComposite.this.columns.addChangeListener(this);
      if (oldInput != null)
        ConfigureTableComposite.this.columns.removeChangeListener(this);
      ConfigureTableComposite.this.tableViewer.refresh();
    }

    public void dispose() {
      ConfigureTableComposite.this.columns.removeChangeListener(this);
    }

    public Object[] getElements(Object parent)
    {
      if (ConfigureTableComposite.this.columns != null) {
        return ConfigureTableComposite.this.columns.getColumns().toArray();
      }
      return new Object[0];
    }

    public void addTask(MxTableColumn task)
    {
      ConfigureTableComposite.this.tableViewer.add(task);
    }

    public void removeTask(MxTableColumn task)
    {
      ConfigureTableComposite.this.tableViewer.remove(task);
    }

    public void updateProperty(MxTableColumn property) {
      ConfigureTableComposite.this.tableViewer.update(property, null);
    }
  }
}