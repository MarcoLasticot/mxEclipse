package org.mxeclipse.utils;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class MxEclipseLabelProvider extends LabelProvider
  implements ITableLabelProvider
{
  public String getColumnText(Object element, int columnIndex)
  {
    return getText(element);
  }

  public Image getColumnImage(Object element, int columnIndex)
  {
    Image image = null;
    try {
      image = element == null ? MxEclipseUtils.getImageRegistry().get("All") : 
        MxEclipseUtils.getImageRegistry().get(element.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return image;
  }
}