package org.mxeclipse.business.table.web;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeWebMenu;
import org.mxeclipse.model.MxTreeWebNavigation;

public class MxWebLabelProvider extends LabelProvider
  implements ITableLabelProvider
{
  public static final String MENU_IMAGE = "menu";
  public static final String COMMAND_IMAGE = "command";
  public static final String LINK_TO_OBJECT_IMAGE = "link";
  public static final String CHECKED_IMAGE = "checked";
  public static final String UNCHECKED_IMAGE = "unchecked";
  private static ImageRegistry imageRegistry = new ImageRegistry();

  static
  {
    imageRegistry.put("menu", MxEclipsePlugin.getImageDescriptor("menu.gif"));
    imageRegistry.put("command", MxEclipsePlugin.getImageDescriptor("command.gif"));
    imageRegistry.put("link", MxEclipsePlugin.getImageDescriptor("link_to_object.gif"));
    imageRegistry.put("checked", MxEclipsePlugin.getImageDescriptor("checked.gif"));
    imageRegistry.put("unchecked", MxEclipsePlugin.getImageDescriptor("unchecked.gif"));
  }

  private Image getTypeImage(MxTreeBusiness obj)
  {
    if ((obj instanceof MxTreeWebMenu)) {
      return imageRegistry.get("menu");
    }
    return imageRegistry.get("command");
  }

  private Image getLinkImage()
  {
    return imageRegistry.get("link");
  }

  public String getColumnText(Object element, int columnIndex)
  {
    String result = "";
    MxTreeWebNavigation state = (MxTreeWebNavigation)element;
    String columnName = MxWebComposite.columnNames[columnIndex];
    if (columnName.equals("Type"))
      result = state.getType();
    else if (columnName.equals("Name")) {
      result = state.getName();
    }

    return result;
  }

  public Image getColumnImage(Object element, int columnIndex)
  {
    Image result = null;
    MxTreeWebNavigation state = (MxTreeWebNavigation)element;
    String columnName = MxWebComposite.columnNames[columnIndex];
    if (columnName.equals("Type"))
      result = getTypeImage(state);
    else if (columnName.equals("Link")) {
      result = getLinkImage();
    }
    return result;
  }
}