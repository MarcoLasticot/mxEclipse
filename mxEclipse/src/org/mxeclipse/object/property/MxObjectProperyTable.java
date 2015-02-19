package org.mxeclipse.object.property;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import matrix.db.Context;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.model.MxTreeAttribute;
import org.mxeclipse.model.MxTreeDomainObject;
import org.mxeclipse.model.MxTreePolicy;
import org.mxeclipse.model.MxTreeRange;
import org.mxeclipse.model.MxTreeType;
import org.mxeclipse.utils.MxEclipseLogger;
import org.mxeclipse.views.IModifyable;

public class MxObjectProperyTable
{
  private String infoType;
  private IModifyable viewPart;
  private Composite parentComposite;
  Context context = null;
  private Table table;
  private TableViewer tableViewer;
  private TableEditor editor;
  private MxObjectPropertyList properties = new MxObjectPropertyList(this.infoType);

  private final String NAME_COLUMN = "Name";
  private final String VALUE_COLUMN = "value";

  private String[] columnNames = { 
    "Name", 
    "value" };

  public MxObjectProperyTable(Composite parent, String infoType, IModifyable view)
  {
    this.parentComposite = parent;
    this.infoType = infoType;
    addChildControls(parent);
    this.viewPart = view;
  }

  private void run(Shell shell)
  {
    Display display = shell.getDisplay();
    while (!shell.isDisposed())
      if (!display.readAndDispatch())
        display.sleep();
  }

  public void dispose()
  {
    this.tableViewer.getLabelProvider().dispose();
  }

  private void addChildControls(Composite composite)
  {
    Composite innerComposite = new Composite(composite, 0);
    innerComposite.setLayout(new FillLayout());

    GridLayout layout = new GridLayout(3, false);
    layout.marginWidth = 4;
    innerComposite.setLayout(layout);

    createTable(innerComposite);

    createTableViewer();
    this.tableViewer.setContentProvider(new MxObjectContentProvider());
    this.tableViewer.setLabelProvider(new MxObjectLabelProvider());

    this.tableViewer.setInput(new MxTreeDomainObject());
  }

  private void createTable(Composite parent)
  {
    int style = 101124;

    this.table = new Table(parent, style);

    GridData gridData = new GridData(1808);
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalSpan = 3;
    this.table.setLayoutData(gridData);

    this.table.setLinesVisible(true);
    this.table.setHeaderVisible(true);

    TableColumn column = new TableColumn(this.table, 16777216, 0);
    column.setText(this.columnNames[0]);
    column.setWidth(200);

    column = new TableColumn(this.table, 16384, 1);
    column.setText(this.columnNames[1]);
    column.setWidth(200);

    column.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
      }
    });
    this.editor = new TableEditor(this.table);

    this.editor.horizontalAlignment = 16384;
    this.editor.grabHorizontal = true;
    this.editor.minimumWidth = 50;

    int COLUMN_NAME = 0;
    int COLUMN_VALUE = 1;
    this.table.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e) {
        Control oldEditor = MxObjectProperyTable.this.editor.getEditor();
        if (oldEditor != null) oldEditor.dispose();

        final TableItem item = (TableItem)e.item;
        if (item == null) return;

        Control newEditor = null;
        MxObjectProperty mxProperty = (MxObjectProperty)item.getData();
        MxTreeAttribute attributeType = null;
        if (mxProperty.getType().equals(MxObjectProperty.TYPE_ATTRIBUTE))
          try {
            attributeType = new MxTreeAttribute(item.getText(0));
          } catch (Exception ex) {
            MxEclipseLogger.getLogger().severe("Unable to get attribute " + item.getText(0));
          }
        else if ((!mxProperty.getType().equals(MxObjectProperty.TYPE_BASIC)) || (mxProperty.getName().equals(MxObjectProperty.BASIC_ID))) {
          return;
        }
        if (attributeType != null) {
          if ((attributeType.getRanges(false) != null) && (attributeType.getRanges(false).size() > 0)) {
            List ranges = attributeType.getRanges(false);
            CCombo newComboEditor = new CCombo(MxObjectProperyTable.this.table, 0);
            newEditor = newComboEditor;
            String[] sRanges = new String[ranges.size()];
            String selValue = item.getText(1);
            int selIndex = -1;
            for (int i = 0; i < sRanges.length; i++) {
              sRanges[i] = ((MxTreeRange)ranges.get(i)).getName();
              if (sRanges[i].equals(selValue)) {
                selIndex = i;
              }
            }
            newComboEditor.setItems(sRanges);
            newComboEditor.select(selIndex);
            newComboEditor.setSize(10, 10);

            newComboEditor.addSelectionListener(new SelectionListener() {
            	public void widgetSelected(SelectionEvent e)
                {
                    CCombo combo = (CCombo)e.getSource();
                    editor.getItem().setText(1, combo.getItem(combo.getSelectionIndex()));
                    MxObjectProperty mxProperty = (MxObjectProperty)item.getData();
                    mxProperty.setValue(combo.getItem(combo.getSelectionIndex()));
                    if(mxProperty.isModified())
                        getProperties().propertyChanged(mxProperty);
                }

              public void widgetDefaultSelected(SelectionEvent e)
              {
              }
            });
            newEditor.setFocus();
            MxObjectProperyTable.this.editor.setEditor(newEditor, item, 1);
            return;
          }
          attributeType.fillBasics();
          if (attributeType.getAttributeType().equals("timestamp")) {
            final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            final Calendar cal = GregorianCalendar.getInstance();
            final Text newTextEditor = new Text(MxObjectProperyTable.this.table, 0);
            newTextEditor.setText(item.getText(1));
            newTextEditor.setSize(10, 10);

            newTextEditor.addMouseListener(new MouseAdapter() {
              public void mouseUp(MouseEvent e) {
                Text txtSource = (Text)e.getSource();
                final Shell dialog = new Shell(MxObjectProperyTable.this.parentComposite.getShell(), 2144);
                dialog.setLayout(new GridLayout(3, false));

                final DateTime calendar = new DateTime(dialog, 3072);
                final DateTime time = new DateTime(dialog, 32896);
                if (!txtSource.getText().equals("")) {
                	try
                    {
                        java.util.Date datIn = formatter.parse(txtSource.getText());
                        cal.setTime(datIn);
                        calendar.setData(cal);
                        calendar.setDay(cal.get(5));
                        calendar.setMonth(cal.get(2));
                        calendar.setYear(cal.get(1));
                        time.setHours(cal.get(10));
                        time.setMinutes(cal.get(12));
                        time.setSeconds(cal.get(13));
                        if(cal.get(9) == 1)
                            time.setHours(time.getHours() + 12);
                    }
                    catch(ParseException e1)
                    {
                        MxEclipseLogger.getLogger().severe((new StringBuilder("Couldn't parse date ")).append(txtSource.getText()).toString());
                    }
                }

                new Label(dialog, 0);
                new Label(dialog, 0);
                Button ok = new Button(dialog, 8);
                ok.setText("OK");
                ok.setLayoutData(new GridData(4, 16777216, false, false));
                ok.addSelectionListener(new SelectionAdapter() {
                	 public void widgetSelected(SelectionEvent e)
                     {
                         cal.set(1, calendar.getYear());
                         cal.set(2, calendar.getMonth());
                         cal.set(5, calendar.getDay());
                         cal.set(10, time.getHours());
                         cal.set(12, time.getMinutes());
                         cal.set(13, time.getSeconds());
                         String sDate = formatter.format(cal.getTime());
                         newTextEditor.setText(sDate);
                         editor.getItem().setText(1, sDate);
                         MxObjectProperty mxProperty = (MxObjectProperty)item.getData();
                         mxProperty.setValue(formatter.format(cal.getTime()));
                         if(mxProperty.isModified())
                             getProperties().propertyChanged(mxProperty);
                         dialog.close();
                     }

                });
                dialog.setDefaultButton(ok);
                dialog.pack();
                dialog.setLocation(Display.getCurrent().getCursorLocation());
                dialog.open();
              }
            });
            newTextEditor.setFocus();
            MxObjectProperyTable.this.editor.setEditor(newTextEditor, item, 1);
            return;
          }if (attributeType.getAttributeType().equals("boolean")) {
            CCombo newComboEditor = new CCombo(MxObjectProperyTable.this.table, 0);
            newEditor = newComboEditor;
            String[] sRanges = { "TRUE", "FALSE" };
            String selValue = item.getText(1);
            int selIndex = -1;
            for (int i = 0; i < sRanges.length; i++) {
              if (sRanges[i].equalsIgnoreCase(selValue)) {
                selIndex = i;
              }
            }
            newComboEditor.setItems(sRanges);
            newComboEditor.select(selIndex);
            newComboEditor.setSize(10, 10);

            newComboEditor.addSelectionListener(new SelectionListener() {
              public void widgetSelected(SelectionEvent e) {
                CCombo combo = (CCombo)e.getSource();
                MxObjectProperyTable.this.editor.getItem().setText(1, combo.getItem(combo.getSelectionIndex()));
                MxObjectProperty mxProperty = (MxObjectProperty)item.getData();
                mxProperty.setValue(combo.getItem(combo.getSelectionIndex()));
                if (mxProperty.isModified())
                  MxObjectProperyTable.this.getProperties().propertyChanged(mxProperty);
              }

              public void widgetDefaultSelected(SelectionEvent e)
              {
              }
            });
            newEditor.setFocus();
            MxObjectProperyTable.this.editor.setEditor(newEditor, item, 1);
            return;
          }

        }

        if ((mxProperty.getType().equals(MxObjectProperty.TYPE_BASIC)) && (
          (mxProperty.getName().equals(MxObjectProperty.BASIC_TYPE)) || (mxProperty.getName().equals(MxObjectProperty.BASIC_POLICY)) || 
          (mxProperty.getName().equals(MxObjectProperty.BASIC_STATE)))) {
          String selValue = item.getText(1);
          int selIndex = -1;
          String[] sValues = (String[])null;
          try {
            if (mxProperty.getName().equals(MxObjectProperty.BASIC_TYPE)) {
              List types = MxTreeType.getAllTypes(false);
              sValues = new String[types.size()];
              for (int i = 0; i < sValues.length; i++) {
                sValues[i] = ((MxTreeType)types.get(i)).getName();
                if (sValues[i].equals(selValue))
                  selIndex = i;
              }
            }
            else if (mxProperty.getName().equals(MxObjectProperty.BASIC_POLICY)) {
              Vector props = MxObjectProperyTable.this.properties.getTasks();
              String selectedTypeName = null;
              for(Iterator iterator = props.iterator(); iterator.hasNext();)
              {
                  MxObjectProperty p = (MxObjectProperty)iterator.next();
                  if(p.getName().equals(MxObjectProperty.BASIC_TYPE))
                  {
                      selectedTypeName = p.getValue();
                      break;
                  }
              }
              if (selectedTypeName != null) {
                MxTreeType selectedType = new MxTreeType(selectedTypeName);
                sValues = selectedType.getPolicyNames(false);
              }
              for (int i = 0; i < sValues.length; i++) {
                if (sValues[i].equals(selValue))
                  selIndex = i;
              }
            }
            else if (mxProperty.getName().equals(MxObjectProperty.BASIC_STATE)) {
              Vector props = MxObjectProperyTable.this.properties.getTasks();
              String selectedPolicyName = null;
              for(Iterator iterator1 = props.iterator(); iterator1.hasNext();)
              {
                  MxObjectProperty p = (MxObjectProperty)iterator1.next();
                  if(p.getName().equals(MxObjectProperty.BASIC_POLICY))
                  {
                      selectedPolicyName = p.getValue();
                      break;
                  }
              }
              if (selectedPolicyName != null) {
                MxTreePolicy selectedPolicy = new MxTreePolicy(selectedPolicyName);
                sValues = selectedPolicy.getStateNames(false);
              }
              for (int i = 0; i < sValues.length; i++)
                if (sValues[i].equals(selValue))
                  selIndex = i;
            }
          }
          catch (Exception ex)
          {
            MxEclipseLogger.getLogger().severe("Unable to get attribute " + item.getText(0));
            return;
          }

          CCombo newComboEditor = new CCombo(MxObjectProperyTable.this.table, 0);
          newEditor = newComboEditor;

          newComboEditor.setItems(sValues);
          newComboEditor.select(selIndex);
          newComboEditor.setSize(10, 10);

          newComboEditor.addSelectionListener(new SelectionListener() {
        	  public void widgetSelected(SelectionEvent e)
              {
                  CCombo combo = (CCombo)e.getSource();
                  editor.getItem().setText(1, combo.getItem(combo.getSelectionIndex()));
                  MxObjectProperty mxProperty = (MxObjectProperty)item.getData();
                  mxProperty.setValue(combo.getItem(combo.getSelectionIndex()));
                  if(mxProperty.isModified())
                      getProperties().propertyChanged(mxProperty);
              }

            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
          });
          newEditor.setFocus();
          MxObjectProperyTable.this.editor.setEditor(newEditor, item, 1);
          return;
        }

        Text newTextEditor = new Text(MxObjectProperyTable.this.table, 0);
        newEditor = newTextEditor;
        newTextEditor.setText(item.getText(1));
        newTextEditor.addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent me)
            {
                Text text = (Text)editor.getEditor();
                editor.getItem().setText(1, text.getText());
                MxObjectProperty mxProperty = (MxObjectProperty)item.getData();
                mxProperty.setValue(text.getText());
                if(mxProperty.isModified())
                    getProperties().propertyChanged(mxProperty);
            }
        });
        newTextEditor.selectAll();
        newEditor.setFocus();
        MxObjectProperyTable.this.editor.setEditor(newEditor, item, 1);
      } } );
  }

  public void save() {
    this.properties.save();
  }

  private void createTableViewer()
  {
    this.tableViewer = new TableViewer(this.table);
    this.tableViewer.setUseHashlookup(true);

    this.tableViewer.setColumnProperties(this.columnNames);

    CellEditor[] editors = new CellEditor[this.columnNames.length];

    TextCellEditor textEditor = new TextCellEditor(this.table);
    ((Text)textEditor.getControl()).setTextLimit(500);

    editors[1] = textEditor;

    this.tableViewer.setCellEditors(editors);

    this.tableViewer.setCellModifier(new MxObjectPropertyCellModifier(this));
  }

  public void close()
  {
    Shell shell = this.table.getShell();

    if ((shell != null) && (!shell.isDisposed()))
      shell.dispose();
  }

  private void createButtons(Composite parent)
  {
  }

  public List getColumnNames()
  {
    return Arrays.asList(this.columnNames);
  }

  public ISelection getSelection()
  {
    return this.tableViewer.getSelection();
  }

  public MxObjectPropertyList getProperties()
  {
    return this.properties;
  }

  public Control getControl()
  {
    return this.table.getParent();
  }

  public void setObject(MxTreeDomainObject o)
  {
    this.properties.setObject(o, this.infoType);
    if (this.editor.getEditor() != null) {
      this.editor.getEditor().dispose();
    }

    this.table.redraw();
    this.tableViewer.refresh();
  }

  public void clear() {
    this.table.removeAll();
    if (this.editor.getEditor() != null)
      this.editor.getEditor().dispose();
  }

  public void refresh()
  {
    this.tableViewer.refresh();
  }

  class MxObjectContentProvider
    implements IStructuredContentProvider, IMxObjectPropertyViewer
  {
    MxObjectContentProvider()
    {
    }

    public void inputChanged(Viewer v, Object oldInput, Object newInput)
    {
      if (newInput != null) {
        MxObjectProperyTable.this.properties = new MxObjectPropertyList((MxTreeDomainObject)newInput, MxObjectProperyTable.this.infoType);
      }

      if (newInput != null)
        MxObjectProperyTable.this.properties.addChangeListener(this);
      if (oldInput != null)
        MxObjectProperyTable.this.properties.removeChangeListener(this);
    }

    public void dispose() {
      MxObjectProperyTable.this.properties.removeChangeListener(this);
    }

    public Object[] getElements(Object parent)
    {
      if (MxObjectProperyTable.this.properties != null) {
        return MxObjectProperyTable.this.properties.getTasks().toArray();
      }
      return new Object[0];
    }

    public void updateProperty(MxObjectProperty property)
    {
      MxObjectProperyTable.this.tableViewer.update(property, null);
      MxObjectProperyTable.this.viewPart.setModified(true);
    }
  }
}