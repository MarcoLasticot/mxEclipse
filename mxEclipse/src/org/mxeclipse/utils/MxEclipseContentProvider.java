package org.mxeclipse.utils;

import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MxEclipseContentProvider
  implements IStructuredContentProvider
{
  public Object[] getElements(Object inputElement)
  {
    Object[] adminTypes = ((List)inputElement).toArray();
    Arrays.sort(adminTypes);
    return adminTypes;
  }

  public void dispose()
  {
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
  }
}