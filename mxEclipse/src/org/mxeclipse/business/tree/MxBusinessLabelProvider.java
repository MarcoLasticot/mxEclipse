package org.mxeclipse.business.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.configure.table.MxTableColumnList;
import org.mxeclipse.model.MxTableColumn;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.utils.MxEclipseUtils;

public class MxBusinessLabelProvider implements ITableLabelProvider {
	private Map imageCache = new HashMap();
	private MxTableColumnList columns;
	public static final String LEFT_ARROW_IMAGE = "left_arrow";
	public static final String RIGHT_ARROW_IMAGE = "right_arrow";
	private static ImageRegistry imageRegistry = new ImageRegistry();

	static {
		imageRegistry.put("left_arrow", MxEclipsePlugin.getImageDescriptor("left_arrow.gif"));
		imageRegistry.put("right_arrow", MxEclipsePlugin.getImageDescriptor("right_arrow.gif"));
	}

	public MxBusinessLabelProvider(MxTableColumnList columns) {
		this.columns = columns;
	}

	public Image getTypeImage(Object element) {
		Image image = null;
		if ((element instanceof MxTreeBusiness)) {
			MxTreeBusiness business = (MxTreeBusiness)element;
			String type = business.getType() + (business.isInherited() ? "_Gray" : "");
			try {
				image = type == null ? MxEclipseUtils.getImageRegistry().get("All") : 
					MxEclipseUtils.getImageRegistry().get(type);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return image;
	}

	public Image getDirectionImage(Object element) {
		Image image = null;
		if ((element instanceof MxTreeBusiness)) {
			MxTreeBusiness treObject = (MxTreeBusiness)element;
			if (treObject.getRelType() != null) {
				if (treObject.isFrom()) {
					image = imageRegistry.get("right_arrow");
				} else {
					image = imageRegistry.get("left_arrow");
				}
			}
		}
		return image;
	}

	public void dispose() {
		for (Iterator i = this.imageCache.values().iterator(); i.hasNext(); ) {
			((Image)i.next()).dispose();
		}
		this.imageCache.clear();
	}

	protected MxTableColumn getColumn(int columnIndex) {
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
		if (columnName.equals(MxTableColumn.BASIC_TYPE)) {
			return getTypeImage(element);
		}
		if (columnName.equals(MxTableColumn.BASIC_RELATIONSHIP)) {
			return getDirectionImage(element);
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if ((element instanceof MxTreeBusiness)) {
			MxTreeBusiness treObject = (MxTreeBusiness)element;
			char arrow = ' ';

			MxTableColumn column = getColumn(columnIndex);
			String columnName = column.getName();

			if (columnName.equals(MxTableColumn.BASIC_TYPE)) {
				return treObject.getType();
			}
			if (columnName.equals(MxTableColumn.BASIC_NAME)) {
				return treObject.getName();
			}
			if ((columnName.equals(MxTableColumn.BASIC_RELATIONSHIP)) && (treObject.getRelType() != null)) {
				return arrow + treObject.getRelType();
			}

		}

		return null;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}
}