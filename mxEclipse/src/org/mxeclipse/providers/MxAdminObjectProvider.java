package org.mxeclipse.providers;

import java.io.PrintStream;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.mxeclipse.MxEclipsePlugin;

public class MxAdminObjectProvider extends LabelProvider
  implements IStructuredContentProvider, ITableLabelProvider
{
  public Object[] getElements(Object inputElement)
  {
    String[] admObjs = (String[])null;
    if ((inputElement instanceof String))
      admObjs = new String[] { inputElement.toString() };
    else {
      admObjs = new String[0];
    }
    System.out.println("MxAdminObjectProvider.getElements() == " + inputElement);
    return admObjs;
  }

  public void dispose()
  {
    super.dispose();
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    if ((newInput != null) && 
      ((viewer instanceof TableViewer))) {
      TableViewer tableViewer = (TableViewer)viewer;
      if ((newInput instanceof String)) {
        String selItem = newInput.toString();
        int spacePos = selItem.indexOf(" ");
        if (spacePos != -1) {
          String admType = selItem.substring(0, spacePos);
          String admName = selItem.substring(spacePos + 1);
          Context ctx = MxEclipsePlugin.getDefault().getContext();
          MQLCommand command = new MQLCommand();
          try {
            String query = "print " + admType + " selectable";
            boolean executed = command.executeCommand(ctx, query);
            if (executed) {
              String str1 = command.getResult();
            }
          }
          catch (MatrixException e) {
            e.printStackTrace();
          }
        }

        String[] admObjs = { selItem };
        System.out.println("MxAdminObjectProvider.inputChanged() == oldInput == " + oldInput + 
          " == newInput == " + newInput);
        if (admObjs != null)
          tableViewer.add(admObjs);
      }
    }
  }

  public Image getColumnImage(Object element, int columnIndex)
  {
    return null;
  }

  public String getColumnText(Object element, int columnIndex)
  {
    System.out.println("MxAdminObjectProvider.getColumnText() == element == " + element);
    return element != null ? element.toString() : "ElementIsNull";
  }
}