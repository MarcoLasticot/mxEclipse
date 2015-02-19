package org.mxeclipse.dialogs;

import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.PropertyUtil;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import matrix.db.BusinessType;
import matrix.db.BusinessTypeItr;
import matrix.db.BusinessTypeList;
import matrix.db.Context;
import matrix.db.RelationshipType;
import matrix.db.RelationshipTypeItr;
import matrix.db.RelationshipTypeList;
import matrix.util.MatrixException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreeDomainObject;

public class ConnectNewDialog extends Dialog
{
  Label lblRelationshipType;
  Combo cmbRelationshipType;
  Button cmdDirection;
  String relationshipType;
  boolean from = true;
  String objectType;
  CreateNewComposite inner;
  public static final String LEFT_ARROW_IMAGE = "left_arrow";
  public static final String RIGHT_ARROW_IMAGE = "right_arrow";
  private static ImageRegistry imageRegistry = new ImageRegistry();
  protected MxTreeDomainObject selectedObject;

  static
  {
    imageRegistry.put("left_arrow", MxEclipsePlugin.getImageDescriptor("left_arrow.gif"));
    imageRegistry.put("right_arrow", MxEclipsePlugin.getImageDescriptor("right_arrow.gif"));
  }

  public ConnectNewDialog(Shell parent, String objectType)
  {
    super(parent);
    setShellStyle(getShellStyle() | 0x10);
    this.objectType = objectType;
  }

  public static void main(String[] args)
  {
    Display display = Display.getDefault();
    ConnectNewDialog thisClass = new ConnectNewDialog(null, "custSubstation");
    thisClass.createDialogArea(null);
    thisClass.open();

    while (!thisClass.getShell().isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }

  protected void cancelPressed()
  {
    super.cancelPressed();
  }

  protected void okPressed()
  {
    this.inner.okPressed();
    this.selectedObject = this.inner.getNewObject();
    if (this.selectedObject == null) {
      MessageDialog.openInformation(getShell(), "Connect New", "Somehow the new object is null");
      return;
    }
    if (this.cmbRelationshipType.getSelectionIndex() >= 0) {
      this.relationshipType = this.cmbRelationshipType.getText();
      super.okPressed();
    } else {
      MessageDialog.openInformation(getShell(), "Connect New", "Relationship type cannot be empty");
    }
  }

  protected void directionPressed()
  {
    if (this.cmdDirection.getSelection()) {
      this.from = false;
      this.cmdDirection.setImage(imageRegistry.get("left_arrow"));
    } else {
      this.from = true;
      this.cmdDirection.setImage(imageRegistry.get("right_arrow"));
    }
    fillRelTypeCombo();
  }

  public MxTreeDomainObject getSelectedObject() {
    return this.selectedObject;
  }

  public String getRelationshipType() {
    return this.relationshipType;
  }

  public boolean isFrom() {
    return this.from;
  }

  public MxTreeDomainObject getNewObject() {
    return this.inner.getNewObject();
  }

  protected void fillRelTypeCombo() {
    this.cmbRelationshipType.removeAll();
    Context context = MxEclipsePlugin.getDefault().getContext();
    try {
      RelationshipTypeList rtl = RelationshipType.getRelationshipTypes(context);
      RelationshipTypeItr itType = new RelationshipTypeItr(rtl);
      TreeMap alType = new TreeMap();
      while (itType.next()) {
        RelationshipType rt = itType.obj();
        boolean bAvailable = false;
        if (this.objectType != null) {
          BusinessTypeList btl = null;
          if (this.from)
            btl = rt.getFromTypes(context, true);
          else {
            btl = rt.getToTypes(context, true);
          }
          BusinessTypeItr itBt = new BusinessTypeItr(btl);
          while (itBt.next()) {
            BusinessType bt = itBt.obj();
            if (bt.getName().equals(this.objectType)) {
              bAvailable = true;
              break;
            }
          }
        } else {
          bAvailable = true;
        }

        if (bAvailable) {
          String typeName = rt.getName();
          alType.put(typeName, rt);
        }
      }

      Iterator it = alType.keySet().iterator();
      while (it.hasNext()) {
        String typeName = (String)it.next();
        this.cmbRelationshipType.add(typeName);
      }
    } catch (MatrixException ex) {
      MessageDialog.openInformation(getShell(), "Connect Existing", "Error when trying to get relationship types " + ex.getMessage());
    }
  }

  protected Control createDialogArea(Composite parent)
  {
    Composite comp = (Composite)super.createDialogArea(parent);
    GridLayout layout = (GridLayout)comp.getLayout();
    layout.numColumns = 1;

    Composite comRel = new Composite(comp, 0);
    GridLayout layRel = new GridLayout();
    layRel.numColumns = 3;
    comRel.setLayout(layRel);

    this.cmdDirection = new Button(comRel, 2);
    this.cmdDirection.setImage(imageRegistry.get("right_arrow"));
    this.from = true;
    this.cmdDirection.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        ConnectNewDialog.this.directionPressed();
      }
    });
    this.lblRelationshipType = new Label(comRel, 0);
    this.lblRelationshipType.setText("Relationship");

    this.cmbRelationshipType = new Combo(comRel, 0);
    fillRelTypeCombo();
    this.cmbRelationshipType.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        ConnectNewDialog.this.relationshipSelected();
      }
    });
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = 4;
    this.inner = new CreateNewComposite(comp, 0);
    this.inner.setLayoutData(gridData);
    this.inner.setTypes(new String[0]);

    return comp;
  }

  protected void configureShell(Shell newShell)
  {
    super.configureShell(newShell);
    newShell.setText("Search Business Objects");
    newShell.setMinimumSize(400, 300);
  }

  private void relationshipSelected() {
    try {
      if (this.cmbRelationshipType.getSelectionIndex() >= 0) {
        Context context = MxEclipsePlugin.getDefault().getContext();
        Map mapTypes = DomainRelationship.getAllowedTypes(context, this.cmbRelationshipType.getItem(this.cmbRelationshipType.getSelectionIndex()), this.from);

        String[] types = ((String)mapTypes.get("include")).split(",");
        for (int i = 0; i < types.length; i++) {
          types[i] = PropertyUtil.getSchemaProperty(context, types[i]);
        }

        this.inner.setTypes(types);
      } else {
        this.inner.setTypes(new String[0]);
      }
    } catch (MatrixException ex) {
      MessageDialog.openInformation(getShell(), "Connect Existing", "Error when trying to get available type for selected relationship: " + ex.getMessage());
    }
  }
}