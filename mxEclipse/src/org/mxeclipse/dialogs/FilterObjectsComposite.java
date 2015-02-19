package org.mxeclipse.dialogs;

import java.util.ArrayList;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mxeclipse.model.MxAttribute;
import org.mxeclipse.model.MxFilter;

public class FilterObjectsComposite extends Composite
{
  private Text txtType = null;
  private MxFilter resultFilter;
  private SashForm sashForm = null;
  private Composite frmObject = null;
  private ScrolledComposite scrObject = null;
  private ScrolledComposite scrRelationship = null;
  private Label lblObject = null;
  private Composite frmRelationship = null;
  private Label lblRelType = null;
  private Text txtRelType = null;
  private Label lblRelationship = null;
  private Button chkFrom = null;
  private Label lblFrom = null;
  private Button chkTo = null;
  private Label lblTo = null;
  private ArrayList<AttributeRow> lstObjects = new ArrayList();
  private ArrayList<AttributeRow> lstRelationships = new ArrayList();

  public FilterObjectsComposite(Composite parent, int style, MxFilter initialFilter) { super(parent, style);
    this.resultFilter = initialFilter;
    initialize(); }

  private void initialize()
  {
    createSashForm();
    GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 1;
    setLayout(gridLayout1);
    setSize(new Point(380, 367));
  }

  void okPressed()
  {
    this.resultFilter = new MxFilter();
    this.resultFilter.setTypes(this.txtType.getText());
    this.resultFilter.setRelTypes(this.txtRelType.getText());
    this.resultFilter.setRelFrom(this.chkFrom.getSelection());
    this.resultFilter.setRelTo(this.chkTo.getSelection());

    for (int i = 0; i < this.lstObjects.size(); i++) {
      MxAttribute attribute = ((AttributeRow)this.lstObjects.get(i)).getCondition();
      if (attribute != null) {
        this.resultFilter.addAttribute(attribute);
      }
    }

    for (int i = 0; i < this.lstRelationships.size(); i++) {
      MxAttribute attribute = ((AttributeRow)this.lstRelationships.get(i)).getCondition();
      if (attribute != null)
        this.resultFilter.addRelAttribute(attribute);
    }
  }

  public MxFilter getFilter()
  {
    return this.resultFilter;
  }

  private void createSashForm()
  {
    GridData gridData1 = new GridData();
    gridData1.grabExcessHorizontalSpace = true;
    gridData1.horizontalAlignment = 4;
    gridData1.verticalAlignment = 4;
    gridData1.grabExcessVerticalSpace = true;
    this.sashForm = new SashForm(this, 67840);
    this.sashForm.setOrientation(512);
    this.sashForm.setLayoutData(gridData1);
    createFrmObject();

    createFrmRelationship();
  }

  private void createFrmObject()
  {
    this.scrObject = new ScrolledComposite(this.sashForm, 2816);
    this.scrObject.setExpandHorizontal(true);
    this.scrObject.setExpandVertical(true);
    this.scrObject.setLayout(new FillLayout(512));

    GridData gridData11 = new GridData();
    gridData11.horizontalSpan = 4;
    GridLayout gridLayout2 = new GridLayout();
    gridLayout2.numColumns = 4;
    this.frmObject = new Composite(this.scrObject, 2048);
    this.frmObject.setLayout(gridLayout2);

    this.scrObject.setContent(this.frmObject);

    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = 2;
    gridData.horizontalSpan = 3;
    gridData.horizontalAlignment = 4;
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 5;
    gridLayout.makeColumnsEqualWidth = false;

    this.lblObject = new Label(this.frmObject, 0);
    this.lblObject.setText("Object Filter:");
    this.lblObject.setFont(new Font(Display.getDefault(), "Tahoma", 8, 1));
    this.lblObject.setLayoutData(gridData11);

    Label lblType = new Label(this.frmObject, 0);
    lblType.setText("Type");
    this.txtType = new Text(this.frmObject, 2048);
    this.txtType.setLayoutData(gridData);
    if (this.resultFilter != null) {
      this.txtType.setText(this.resultFilter.getTypes());
    }
    if ((this.resultFilter != null) && (this.resultFilter.getAttributes().size() > 0))
      for (int i = 0; i < this.resultFilter.getAttributes().size(); i++) {
        AttributeRow newRow = new AttributeRow(this.frmObject, this.lstObjects);
        newRow.setCondition((MxAttribute)this.resultFilter.getAttributes().get(i));
      }
    else
      new AttributeRow(this.frmObject, this.lstObjects);
  }

  private void createFrmRelationship()
  {
    this.scrRelationship = new ScrolledComposite(this.sashForm, 2816);
    this.scrRelationship.setExpandHorizontal(true);
    this.scrRelationship.setExpandVertical(true);
    this.scrRelationship.setLayout(new FillLayout(512));

    GridData gridData4 = new GridData();
    gridData4.horizontalSpan = 3;
    GridData gridData2 = new GridData();
    gridData2.horizontalSpan = 3;
    GridData gridData5 = new GridData();
    gridData5.horizontalSpan = 4;
    GridData gridData3 = new GridData();
    gridData3.grabExcessHorizontalSpace = true;
    gridData3.verticalAlignment = 2;
    gridData3.horizontalSpan = 3;
    gridData3.horizontalAlignment = 4;
    GridLayout gridLayout3 = new GridLayout();
    gridLayout3.numColumns = 4;
    this.frmRelationship = new Composite(this.scrRelationship, 2048);
    this.frmRelationship.setLayout(gridLayout3);

    this.scrRelationship.setContent(this.frmRelationship);

    this.lblRelationship = new Label(this.frmRelationship, 0);
    this.lblRelationship.setText("Relationship Filter:");
    this.lblRelationship.setFont(new Font(Display.getDefault(), "Tahoma", 8, 1));
    this.lblRelationship.setLayoutData(gridData5);
    this.lblFrom = new Label(this.frmRelationship, 0);
    this.lblFrom.setText("From");
    this.chkFrom = new Button(this.frmRelationship, 32);
    this.chkFrom.setSelection(true);
    this.chkFrom.setLayoutData(gridData2);
    this.lblTo = new Label(this.frmRelationship, 0);
    this.lblTo.setText("To");
    this.chkTo = new Button(this.frmRelationship, 32);
    this.chkTo.setSelection(true);
    this.chkTo.setLayoutData(gridData4);
    this.lblRelType = new Label(this.frmRelationship, 0);
    this.lblRelType.setText("Type");
    this.txtRelType = new Text(this.frmRelationship, 2048);
    this.txtRelType.setLayoutData(gridData3);
    if (this.resultFilter != null) {
      this.txtRelType.setText(this.resultFilter.getRelTypes());
      this.chkFrom.setSelection(this.resultFilter.isRelFrom());
      this.chkTo.setSelection(this.resultFilter.isRelTo());
    }
    if ((this.resultFilter != null) && (this.resultFilter.getRelAttributes().size() > 0))
      for (int i = 0; i < this.resultFilter.getRelAttributes().size(); i++) {
        AttributeRow newRow = new AttributeRow(this.frmRelationship, this.lstRelationships);
        newRow.setCondition((MxAttribute)this.resultFilter.getRelAttributes().get(i));
      }
    else
      new AttributeRow(this.frmRelationship, this.lstRelationships);
  }
}