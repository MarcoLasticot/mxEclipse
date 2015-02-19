package org.mxeclipse.business.table.type;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeType;

public class MxTypeLabelProvider extends LabelProvider
  implements ITableLabelProvider
{
  public static final String TYPE_IMAGE = "type";
  public static final String TYPE_IMAGE_GRAY = "typegray";
  public static final String CHECKED_IMAGE = "checked";
  public static final String UNCHECKED_IMAGE = "unchecked";
  private static ImageRegistry imageRegistry = new ImageRegistry();

  static
  {
    imageRegistry.put("type", MxEclipsePlugin.getImageDescriptor("type.gif"));
    imageRegistry.put("typegray", MxEclipsePlugin.getImageDescriptor("type_gray.gif"));
    imageRegistry.put("checked", MxEclipsePlugin.getImageDescriptor("checked.gif"));
    imageRegistry.put("unchecked", MxEclipsePlugin.getImageDescriptor("unchecked.gif"));
  }

  private Image getTypeImage(MxTreeBusiness obj)
  {
    if (obj.isInherited()) {
      return imageRegistry.get("typegray");
    }
    return imageRegistry.get("type");
  }

  public String getColumnText(Object element, int columnIndex)
  {
    String result = "";
    MxTreeType type = (MxTreeType)element;
    switch (columnIndex) {
    case 0:
      result = type.getName();
    }

    return result;
  }

  public Image getColumnImage(Object element, int columnIndex)
  {
    Image result = null;
    MxTreeType type = (MxTreeType)element;
    switch (columnIndex) {
    case 0:
      result = getTypeImage(type);
    }

    return result;
  }
}