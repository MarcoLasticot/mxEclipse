package org.mxeclipse.business.table.range;

import java.util.Iterator;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.mxeclipse.business.tree.MxBusinessSorter;
import org.mxeclipse.model.IMxBusinessViewer;
import org.mxeclipse.model.MxTreeAttribute;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeRange;
import org.mxeclipse.views.IModifyable;

public class MxRangeComposite extends Composite {
	private Table tblObjects = null;
	private ToolBar toolBar = null;
	private TableViewer tableViewer = null;
	private MxTreeAttribute attribute;
	private IModifyable viewPart;
	public static String BASIC_NAME = "Name";

	private String[] columnNames = { 
			BASIC_NAME };

	public MxTreeAttribute getAttribute() {
		return this.attribute;
	}

	public MxRangeComposite(Composite parent, int style, MxTreeAttribute attribute, IModifyable view) {
		super(parent, style);
		initialize();
		setData(attribute);
		this.viewPart = view;
	}

	private void initialize() {
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = 4;
		gridData3.verticalAlignment = 2;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = 4;
		gridData1.verticalAlignment = 1;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = 4;
		gridData.horizontalAlignment = 4;
		gridData.verticalSpan = 2;
		gridData.grabExcessVerticalSpace = true;
		createToolBar();
		setLayout(gridLayout);
		Label filler = new Label(this, 0);
		this.tblObjects = new Table(this, 68352);

		this.tblObjects.setHeaderVisible(true);
		this.tblObjects.setLayoutData(gridData);
		this.tblObjects.setLinesVisible(true);

		TableColumn tableColumn = new TableColumn(this.tblObjects, 0, 0);
		tableColumn.setWidth(120);
		tableColumn.setText("Name");

		setSize(new Point(437, 200));
		CellEditor[] editors = new CellEditor[this.columnNames.length];

		TextCellEditor textEditor = new TextCellEditor(this.tblObjects);
		((Text)textEditor.getControl()).setTextLimit(500);
		editors[0] = textEditor;

		this.tableViewer = new TableViewer(this.tblObjects);
		this.tableViewer.setUseHashlookup(true);
		this.tableViewer.setColumnProperties(this.columnNames);
		this.tableViewer.setContentProvider(new MxRangeContentProvider());
		this.tableViewer.setLabelProvider(new MxRangeLabelProvider());
		this.tableViewer.setCellEditors(editors);
		this.tableViewer.setCellModifier(new MxRangeCellModifier(this));
		this.tableViewer.setSorter(new MxBusinessSorter(BASIC_NAME));
	}

	public void setData(MxTreeAttribute attribute) {
		this.tblObjects.removeAll();

		this.attribute = attribute;
		this.tableViewer.setInput(attribute);

		this.tblObjects.redraw();
	}

	private void createToolBar() {
		this.toolBar = new ToolBar(this, 0);
		ToolItem cmdNew = new ToolItem(this.toolBar, 8);
		cmdNew.setText("New");
		ToolItem cmdDelete = new ToolItem(this.toolBar, 8);
		cmdDelete.setText("Delete");
		cmdDelete.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				Iterator itSelected = ((IStructuredSelection)MxRangeComposite.this.tableViewer.getSelection()).iterator();
				while (itSelected.hasNext()) {
					MxTreeRange range = (MxTreeRange)itSelected.next();
					if (range != null) {
						MxRangeComposite.this.attribute.removeRange(range);
					}
				}

				MxRangeComposite.this.viewPart.setModified(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		cmdNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					MxRangeComposite.this.attribute.addRange();

					MxRangeComposite.this.viewPart.setModified(true);
				} catch (Exception ex) {
					Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
					ErrorDialog.openError(MxRangeComposite.this.getParent().getShell().getShell(), 
							"Unable to add policy", 
							"Error Occurred while adding a range to an attribute", 
							status);
				}
			}
		});
	}

	public void clear() {
		this.tblObjects.clearAll();
	}

	class MxRangeContentProvider implements IStructuredContentProvider, IMxBusinessViewer {
		MxRangeContentProvider() {
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			MxTreeAttribute newAttribute = (MxTreeAttribute)newInput;
			MxTreeAttribute oldAttribute = (MxTreeAttribute)oldInput;
			if (oldInput != null) {
				oldAttribute.removeChangeListener(this);
			}
			if (newInput != null) {
				newAttribute.addChangeListener(this);
			}
			MxRangeComposite.this.tableViewer.refresh();
		}

		public void dispose() {
			MxRangeComposite.this.attribute.removeChangeListener(this);
		}

		public Object[] getElements(Object parent) {
			if (MxRangeComposite.this.attribute != null) {
				return MxRangeComposite.this.attribute.getRanges(false).toArray(new MxTreeRange[MxRangeComposite.this.attribute.getRanges(false).size()]);
			}
			return new Object[0];
		}

		public void addProperty(MxTreeBusiness task) {
			if ((task instanceof MxTreeRange)) {
				MxRangeComposite.this.tableViewer.add(task);
				TableItem[] items = MxRangeComposite.this.tblObjects.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].getData().equals(task)) {
						MxRangeComposite.this.tblObjects.setSelection(i);
					}
				}
			}
		}

		public void removeProperty(MxTreeBusiness task) {
			if ((task instanceof MxTreeRange)) {
				MxRangeComposite.this.tableViewer.remove(task);
			}
		}

		public void updateProperty(MxTreeBusiness property) {
			if ((property instanceof MxTreeRange)) {
				int count = 0;
				for (int i = 0; i < MxRangeComposite.this.attribute.getRanges(false).size(); i++) {
					if (property.equals(MxRangeComposite.this.attribute.getRanges(false).get(i))) {
						count++;
						if (count > 1) {
							MessageDialog.openInformation(MxRangeComposite.this.getShell(), "Range Error", "This range already exists in the system!");
							property.setName(property.getOldName() != null ? property.getOldName() : "");
							return;
						}
					}
				}
				MxRangeComposite.this.tableViewer.update(property, null);
				MxRangeComposite.this.viewPart.setModified(true);
			}
		}
	}
}