package org.mxeclipse.business.table.web;

import java.util.ArrayList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeWebCommand;
import org.mxeclipse.model.MxTreeWebMenu;
import org.mxeclipse.model.MxTreeWebNavigation;
import org.mxeclipse.views.MxEclipseBusinessView;

public class MxWebCellModifier
  implements ICellModifier
{
  MxWebComposite composite;

  public MxWebCellModifier(MxWebComposite composite)
  {
    this.composite = composite;
  }

  public boolean canModify(Object element, String property)
  {
    return true;
  }

  public Object getValue(Object element, String property)
  {
    Object result = "";
    MxTreeWebNavigation item = (MxTreeWebNavigation)element;
    try
    {
      if (property.equals("Type")) {
        result = Integer.valueOf(-1);
        for (int i = 0; i < MxTreeWebNavigation.ALL_WEB_TYPES.length; i++)
          if (MxTreeWebNavigation.ALL_WEB_TYPES[i].equals(item.getType())) {
            result = Integer.valueOf(i);
            break;
          }
      }
      else if (property.equals("Name")) {
        result = Integer.valueOf(-1);
        ComboBoxCellEditor ed = (ComboBoxCellEditor)this.composite.editors[1];
        ArrayList allItems = item.getType().equalsIgnoreCase("Command") ? MxTreeWebCommand.getAllCommands(false) : MxTreeWebMenu.getAllMenus(false);
        String[] allNames = new String[allItems.size()];

        for (int i = 0; i < allItems.size(); i++) {
          MxTreeWebNavigation current = (MxTreeWebNavigation)allItems.get(i);
          allNames[i] = current.getName();
          if ((current.getName().equals(item.getName())) && (current.getType().equals(item.getType()))) {
            result = Integer.valueOf(i);
          }
        }
        ed.setItems(allNames);
      } else {
        String itemName = item.getName();
        if (itemName.length() > 0) {
          IWorkbench workbench = PlatformUI.getWorkbench();
          IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
          IWorkbenchPage page = window.getActivePage();
          try {
            MxEclipseBusinessView view = (MxEclipseBusinessView)page.showView("org.mxeclipse.views.MxEclipseBusinessView");
            TreeItem[] selected = view.treObjects.getSelection();
            if (selected.length > 0) {
              view.expandItem(selected[0]);
              for (TreeItem subItemNode : selected[0].getItems())
                if ((subItemNode.getData() instanceof MxTreeWebNavigation)) {
                  MxTreeWebNavigation subItem = (MxTreeWebNavigation)subItemNode.getData();
                  if ((subItem.getName().equals(itemName)) && (subItem.getType().equals(item.getType()))) {
                    view.nodeSelected(subItemNode);
                    view.treObjects.setSelection(subItemNode);
                    break;
                  }
                }
            }
          }
          catch (PartInitException e) {
            MessageDialog.openError(this.composite.getShell(), "State selection", "Error when trying to select state! " + e.getMessage());
          }
        }

        result = "";
      }
    } catch (Exception ex) {
      MessageDialog.openError(this.composite.getShell(), "Menu/command retrieval", "Error when retrieving a menu/command properties for editing!");
    }

    return result;
  }

  public void modify(Object element, String property, Object value)
  {
    TableItem tableItem = (TableItem)element;
    MxTreeWebNavigation item = (MxTreeWebNavigation)tableItem.getData();
    boolean bChanged = false;
    try
    {
      if (property.equals("Type")) {
        int nValue = ((Integer)value).intValue();
        if (nValue >= 0) {
          item.setType(MxTreeWebNavigation.ALL_WEB_TYPES[nValue]);
          bChanged = true;
        }
      } else if (property.equals("Name")) {
        int nValue = ((Integer)value).intValue();
        if (nValue >= 0) {
          item.setName(MxTreeWebMenu.getAllMenuNames(false)[nValue]);
          bChanged = true;
        }
      }
    } catch (Exception ex) {
      MessageDialog.openInformation(this.composite.getShell(), "Web item Configuration", "Error when editing value. Please give a correct value!");
    }
    if (!property.equals("Link"))
      this.composite.getBusiness().propertyChanged(item);
  }
}