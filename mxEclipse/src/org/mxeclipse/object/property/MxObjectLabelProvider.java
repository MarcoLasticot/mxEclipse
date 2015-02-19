package org.mxeclipse.object.property;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mxeclipse.MxEclipsePlugin;

public class MxObjectLabelProvider extends LabelProvider
  implements ITableLabelProvider
{
  public static final String CHECKED_IMAGE = "checked";
  public static final String UNCHECKED_IMAGE = "unchecked";
  public static final String PENDING_IMAGE = "pending";
  public static final String ATTRIBUTE_IMAGE = "attribute";
  public static final String HISTORY_IMAGE = "history";
  private static ImageRegistry imageRegistry = new ImageRegistry();

  static
  {
    imageRegistry.put("checked", MxEclipsePlugin.getImageDescriptor("checked.gif"));
    imageRegistry.put("unchecked", MxEclipsePlugin.getImageDescriptor("unchecked.gif"));
    imageRegistry.put("attribute", MxEclipsePlugin.getImageDescriptor("iconSmallAttribute.gif"));
    imageRegistry.put("pending", MxEclipsePlugin.getImageDescriptor("pending.gif"));
    imageRegistry.put("history", MxEclipsePlugin.getImageDescriptor("history.gif"));
  }

  private Image getImageChanged(boolean isChanged)
  {
    String key = isChanged ? "pending" : null;
    return imageRegistry.get(key);
  }

  private Image getTypeImage(String type) {
    if (type.equals(MxObjectProperty.TYPE_ATTRIBUTE))
      return imageRegistry.get("attribute");
    if (type.equals(MxObjectProperty.TYPE_HISTORY)) {
      return imageRegistry.get("history");
    }
    return null;
  }

  public String getColumnText(Object element, int columnIndex)
  {
    String result = "";
    MxObjectProperty task = (MxObjectProperty)element;
    switch (columnIndex) {
    case 0:
      result = task.getName();
      break;
    case 1:
      result = task.getValue();
      break;
    }

    return result;
  }

  public Image getColumnImage(Object element, int columnIndex)
  {
    Image result = null;
    MxObjectProperty task = (MxObjectProperty)element;
    switch (columnIndex) {
    case 0:
      result = getTypeImage(task.getType());
      break;
    case 1:
      result = getImageChanged(((MxObjectProperty)element).isModified());
      break;
    }

    return result;
  }
}