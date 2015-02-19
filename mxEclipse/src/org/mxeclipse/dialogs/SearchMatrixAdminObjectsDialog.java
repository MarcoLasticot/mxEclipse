package org.mxeclipse.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import matrix.db.Context;
import matrix.db.MQLCommand;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.utils.MxEclipseContentProvider;
import org.mxeclipse.utils.MxEclipseLabelProvider;
import org.mxeclipse.utils.MxEclipseUtils;

public class SearchMatrixAdminObjectsDialog extends Dialog
{
  private Label objectTypeLabel;
  private Label objNameLabel;
  private Text objNameText;
  private Button radioReplace;
  private Button radioAppend;
  private TableViewer tableViewer;
  private List<MxTreeBusiness> resultList;
  private String[] types;
  private String namePattern;
  private boolean appendResults;

  protected SearchMatrixAdminObjectsDialog(IShellProvider parentShell)
  {
    super(parentShell);
  }

  public SearchMatrixAdminObjectsDialog(Shell parentShell)
  {
    super(parentShell);
  }

  public SearchMatrixAdminObjectsDialog(Shell parentShell, String[] types, String namePattern, boolean appendResults)
  {
    super(parentShell);
    this.types = types;
    this.namePattern = namePattern;
    this.appendResults = appendResults;
  }

  protected Control createDialogArea(Composite parent)
  {
    Composite comp = (Composite)super.createDialogArea(parent);
    GridLayout layout = (GridLayout)comp.getLayout();
    layout.numColumns = 2;
    layout.marginLeft = 5;
    layout.marginRight = 5;
    layout.marginTop = 5;
    layout.marginBottom = 5;

    GridData labelData = new GridData(4, 4, true, true);
    labelData.horizontalSpan = 2;
    GridData fieldData = new GridData(4, 4, true, true);
    fieldData.horizontalSpan = 2;
    GridData chkBoxData = new GridData(4, 4, true, true);
    GridData gridData = new GridData(4, 4, true, true);
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalSpan = 2;

    this.objectTypeLabel = new Label(comp, 16384);
    this.objectTypeLabel.setText(MxEclipseUtils.getString("label.Object"));
    this.objectTypeLabel.setLayoutData(labelData);

    int style = 68354;
    Table table = new Table(comp, style);
    table.setLayoutData(gridData);

    this.tableViewer = new TableViewer(table);
    this.tableViewer.setContentProvider(new MxEclipseContentProvider());
    this.tableViewer.setLabelProvider(new MxEclipseLabelProvider());

    this.tableViewer.setInput(MxEclipseUtils.getAdminTypes());
    if ((this.types == null) || (this.types.length == 0)) {
      this.tableViewer.getTable().select(0);
    } else {
      int[] nTypes = new int[this.types.length];
      TableItem[] items = this.tableViewer.getTable().getItems();
      for (int i = 0; i < this.types.length; i++) {
        for (int j = 0; j < items.length; j++) {
          if (this.types[i].equals(items[j].getText())) {
            nTypes[i] = j;
          }
        }
      }
      this.tableViewer.getTable().select(nTypes);
    }

    this.objNameLabel = new Label(comp, 16384);
    this.objNameLabel.setText(MxEclipseUtils.getString("label.Name"));
    this.objNameLabel.setLayoutData(labelData);

    this.objNameText = new Text(comp, 2048);
    this.objNameText.setLayoutData(fieldData);
    this.objNameText.setText("*");
    if (this.namePattern != null) {
      this.objNameText.setText(this.namePattern);
    }

    this.radioReplace = new Button(comp, 16);
    this.radioReplace.setText(MxEclipseUtils.getString("radio.ReplaceObjects"));
    this.radioReplace.setLayoutData(chkBoxData);
    this.radioReplace.setSelection(!this.appendResults);

    this.radioAppend = new Button(comp, 16);
    this.radioAppend.setText(MxEclipseUtils.getString("radio.AppendObjects"));
    this.radioAppend.setLayoutData(chkBoxData);
    this.radioAppend.setSelection(this.appendResults);

    return comp;
  }

  protected static ArrayList<MxTreeBusiness> queryOneType(String typeName, String namePattern)
  {
    ArrayList retList = new ArrayList();
    if (typeName.equalsIgnoreCase("All")) return retList;
    StringBuffer query = new StringBuffer("list ");

    if ("WebForm".equalsIgnoreCase(typeName))
      query.append("Form".toLowerCase());
    else {
      query.append(typeName.toLowerCase());
    }
    if (!typeName.equals("Association"))
    {
      if ("Table".equalsIgnoreCase(typeName)) {
        query.append(" system ");
      }
      query.append(" '").append(namePattern);
      query.append("' ");
      query.append("select name ");
      if (("WebForm".equalsIgnoreCase(typeName)) || 
        ("Form".equalsIgnoreCase(typeName))) {
        query.append("web ");
      }
      query.append("dump |");
    }

    Context context = MxEclipsePlugin.getDefault().getContext();
    if (context != null) {
      try {
        MQLCommand command = new MQLCommand();
        boolean executed = command.executeCommand(context, query.toString());
        if (executed) {
          String result = command.getResult();
          StringTokenizer tkzr = new StringTokenizer(result, "\n");

          if (tkzr.hasMoreTokens()) {
            do {
              String nextToken = tkzr.nextToken();
              if ((nextToken.startsWith("Warning")) || 
                (nextToken.startsWith("Error"))) continue;
              int pipePos = nextToken.indexOf("|");
              if (pipePos != -1) {
                if (typeName.indexOf("Form") > -1) {
                  String name = nextToken.substring(0, pipePos);
                  String webForm = nextToken.substring(pipePos + 1);
                  if ((typeName.equalsIgnoreCase("WebForm")) && 
                    ("TRUE".equalsIgnoreCase(webForm))) {
                    retList.add(MxTreeBusiness.createBusiness(typeName, name)); } else {
                    if ((!typeName.equalsIgnoreCase("Form")) || 
                      (!"FALSE".equalsIgnoreCase(webForm))) continue;
                    retList.add(MxTreeBusiness.createBusiness(typeName, name));
                  }
                }

              }
              else
              {
                if ((typeName.equals("Association")) && (!nextToken.matches(namePattern.replace("*", ".*"))))
                  continue;
                retList.add(MxTreeBusiness.createBusiness(typeName, nextToken));
              }
            }
            while (
              tkzr.hasMoreTokens());
          }
          else if ((result != null) && (result.trim().length() > 0) && (
            (!typeName.equals("Association")) || (result.matches(namePattern.replace("*", ".*")))))
          {
            retList.add(MxTreeBusiness.createBusiness(typeName, result));
          }

        }
        else
        {
          String strError = command.getError();
          Status status = new Status(4, "MxEclipse", 0, strError, new Exception(strError));
          ErrorDialog.openError(null, 
            MxEclipseUtils.getString("SearchMatrixAdminObjectsDialog.error.header.SearchAdminObjectsFailed"), 
            MxEclipseUtils.getString("SearchMatrixAdminObjectsDialog.error.message.SearchAdminObjectsFailed"), 
            status);
        }
      } catch (Exception e) {
        Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
        ErrorDialog.openError(null, 
          MxEclipseUtils.getString("SearchMatrixAdminObjectsDialog.error.header.SearchAdminObjectsFailed"), 
          MxEclipseUtils.getString("SearchMatrixAdminObjectsDialog.error.message.SearchAdminObjectsFailed"), 
          status);
      }
    }
    return retList;
  }

  public static List<MxTreeBusiness> findAdminObjects(String type, String name)
    throws Exception
  {
    return queryOneType(type, name);
  }

  protected void okPressed()
  {
    TableItem[] items = this.tableViewer.getTable().getSelection();
    String[] sItems = new String[items.length];

    for (int i = 0; i < items.length; i++) {
      TableItem item = items[i];
      sItems[i] = item.getText();
      if (item.getText().equals("All")) {
        items = this.tableViewer.getTable().getItems();
        sItems = new String[] { "All" };
        break;
      }
    }

    if (items.length == 0) {
      items = this.tableViewer.getTable().getItems();
    }

    this.resultList = new ArrayList();
    for (int i = 0; i < items.length; i++) {
      this.resultList.addAll(queryOneType(items[i].getText(), this.objNameText.getText()));
    }

    this.types = sItems;
    this.namePattern = this.objNameText.getText();
    this.appendResults = this.radioAppend.getSelection();

    super.okPressed();
  }

  protected void cancelPressed()
  {
    super.cancelPressed();
  }

  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, 0, MxEclipseUtils.getString("button.Search"), true);
    createButton(parent, 1, IDialogConstants.CANCEL_LABEL, false);
  }

  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText(MxEclipseUtils.getString("SearchMatrixAdminObjectsDialog.header.SearchAdminObjects"));
  }

  public List<MxTreeBusiness> getResultList() {
    return this.resultList;
  }

  public boolean isAppendResults() {
    return this.appendResults;
  }

  public void setAppendResults(boolean appendResults) {
    this.appendResults = appendResults;
  }

  public String getNamePattern() {
    return this.namePattern;
  }

  public void setNamePattern(String namePattern) {
    this.namePattern = namePattern;
  }

  public String[] getTypes() {
    return this.types;
  }

  public void setTypes(String[] types) {
    this.types = types;
  }
}