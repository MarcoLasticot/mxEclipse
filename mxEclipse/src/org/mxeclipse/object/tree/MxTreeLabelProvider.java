package org.mxeclipse.object.tree;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import matrix.db.BusinessType;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.configure.table.MxTableColumnList;
import org.mxeclipse.model.MxTableColumn;
import org.mxeclipse.model.MxTreeDomainObject;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeLabelProvider
  implements ITableLabelProvider
{
  private Map imageCache = new HashMap();
  private MxTableColumnList columns;
  public static final String LEFT_ARROW_IMAGE = "left_arrow";
  public static final String RIGHT_ARROW_IMAGE = "right_arrow";
  public static final String REVISION_IMAGE = "revision";
  private static ImageRegistry imageRegistry = new ImageRegistry();

  static
  {
    imageRegistry.put("left_arrow", MxEclipsePlugin.getImageDescriptor("left_arrow.gif"));
    imageRegistry.put("right_arrow", MxEclipsePlugin.getImageDescriptor("right_arrow.gif"));
    imageRegistry.put("revision", MxEclipsePlugin.getImageDescriptor("revisions.gif"));
  }

  public MxTreeLabelProvider(MxTableColumnList columns) {
    this.columns = columns;
  }

  public String getText(Object element) {
    if ((element instanceof MxTreeDomainObject)) {
      MxTreeDomainObject treObject = (MxTreeDomainObject)element;
      char arrow = ' ';
      if (treObject.getDomainRelationship() != null) {
        if (!treObject.getRelFromId().equals(treObject.getId()))
        	arrow = '\u2192';
        else {
          arrow = '<';
        }
      }
      return arrow + (treObject.getDomainRelationship() != null ? treObject.getRelType() : "") + " - " + treObject.getName() + " (" + treObject.getRevision() + "), " + treObject.getType();
    }
    return null;
  }

  public Image getDirectionImage(Object element) {
    Image image = null;
    if ((element instanceof MxTreeDomainObject)) {
      MxTreeDomainObject treObject = (MxTreeDomainObject)element;
      if (treObject.getRelId() != null) {
        if (!treObject.getRelFromId().equals(treObject.getId()))
          image = imageRegistry.get("right_arrow");
        else
          image = imageRegistry.get("left_arrow");
      }
      else if ((treObject.getRelType() != null) && (treObject.getRelType().equals("Revision"))) {
        image = imageRegistry.get("revision");
      }
    }
    return image;
  }

  public Image getTypeImage(Object element)
  {
    if ((element instanceof MxTreeDomainObject)) {
      MxTreeDomainObject treObject = (MxTreeDomainObject)element;
      if ((treObject.getRelType() != null) && (treObject.getRelType().equals("Revision"))) {
        return imageRegistry.get("revision");
      }
      DomainObject domObject = treObject.getDomainObject();

      Context context = MxEclipsePlugin.getDefault().getContext();
      if ((context != null) && (context.isConnected())) {
        try
        {
          domObject.open(context);
          byte[] bytes = (byte[])null;
          Image image1;
          if (domObject.hasImage()) {
            bytes = domObject.getIcon(context);
          } else {
            String type = treObject.getType();
            BusinessType oType = new BusinessType(type, context.getVault());
            oType.open(context);
            bytes = oType.getIcon();
            oType.close(context);
          }
          if ((bytes == null) || (bytes.length == 0))
          {
            ImageDescriptor descriptor = MxEclipsePlugin.getImageDescriptor("iconSmallType.gif");
            Image image = (Image)this.imageCache.get(descriptor);
            if (image == null) {
              image = descriptor.createImage();
              this.imageCache.put(descriptor, image);
            }
            image1 = image;
            return image1;
          }
          Image image = new Image(Display.getCurrent(), new ByteArrayInputStream(bytes));
          Image localImage1 = image;
          return localImage1;
        } catch (MatrixException e) {
          e.printStackTrace();
          return null;
        } finally {
          if ((domObject != null) && (domObject.isOpen())) {
            try {
              domObject.close(context);
            } catch (MatrixException e) {
              e.printStackTrace();
            }
          }
        }
      }

      return null;
    }

    return null;
  }

  public void dispose()
  {
    for (Iterator i = this.imageCache.values().iterator(); i.hasNext(); ) {
      ((Image)i.next()).dispose();
    }
    this.imageCache.clear();
  }

  protected MxTableColumn getColumn(int columnIndex)
  {
    MxTableColumn column = null;
    int no = -1;
    for (int i = 0; i < this.columns.getColumns().size(); i++) {
      if (((MxTableColumn)this.columns.getColumns().get(i)).isVisible()) {
        no++;
        if (no == columnIndex) {
          column = (MxTableColumn)this.columns.getColumns().get(i);
          break;
        }
      }
    }
    return column;
  }

  public Image getColumnImage(Object element, int columnIndex) {
    String columnName = getColumn(columnIndex).getName();
    if (columnName.equals(MxTableColumn.BASIC_TYPE))
      return getTypeImage(element);
    if (columnName.equals(MxTableColumn.BASIC_RELATIONSHIP)) {
      return getDirectionImage(element);
    }
    return null;
  }

  public String getColumnText(Object element, int columnIndex)
  {
    if ((element instanceof MxTreeDomainObject)) {
      MxTreeDomainObject treObject = (MxTreeDomainObject)element;
      char arrow = ' ';

      MxTableColumn column = getColumn(columnIndex);
      String columnName = column.getName();

      if (columnName.equals(MxTableColumn.BASIC_TYPE))
        return treObject.getType();
      if (columnName.equals(MxTableColumn.BASIC_NAME))
        return treObject.getName();
      if (columnName.equals(MxTableColumn.BASIC_REVISION))
        return treObject.getRevision();
      if (columnName.equals(MxTableColumn.BASIC_STATE))
        return treObject.getCurrent();
      if (columnName.equals(MxTableColumn.BASIC_RELATIONSHIP)) {
        if (treObject.getRelId() != null)
        {
          return arrow + treObject.getRelType();
        }
        return null;
      }
      try
      {
        Context context = MxEclipsePlugin.getDefault().getContext();

        if (column.isOnRelationship()) {
          DomainRelationship rel = treObject.getDomainRelationship();
          String value = "";
          if (rel != null)
            try
            {
              rel.open(context);

              value = treObject.getDomainRelationship().getAttributeValue(context, columnName);
            } catch (MatrixException e) {
              MxEclipseLogger.getLogger().warning("Error in label provider, column " + columnName + ". Cause: " + e.getMessage());
              try
              {
                rel.close(context); } catch (MatrixException localMatrixException1) {
              } } finally { try { rel.close(context);
              } catch (MatrixException localMatrixException2)
              {
              } }
          return value;
        }
        return treObject.getDomainObject().getInfo(context, "attribute[" + columnName + "]");
      }
      catch (FrameworkException e)
      {
        MxEclipseLogger.getLogger().warning("Error in label provider, column " + columnName + ". Cause: " + e.getMessage());
      }

    }

    return null;
  }

  public void addListener(ILabelProviderListener listener)
  {
  }

  public boolean isLabelProperty(Object element, String property)
  {
    return false;
  }

  public void removeListener(ILabelProviderListener listener)
  {
  }
}