package org.mxeclipse.views;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.configure.table.ConfigureTableDialog;
import org.mxeclipse.configure.table.MxTableColumnList;
import org.mxeclipse.dialogs.ConnectExistingDialog;
import org.mxeclipse.dialogs.ConnectNewDialog;
import org.mxeclipse.dialogs.CreateNewDialog;
import org.mxeclipse.dialogs.FilterObjectsDialog;
import org.mxeclipse.dialogs.SearchMatrixBusinessObjectsDialog;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.model.MxAttribute;
import org.mxeclipse.model.MxFilter;
import org.mxeclipse.model.MxObjectSearchCriteria;
import org.mxeclipse.model.MxTreeDomainObject;
import org.mxeclipse.object.property.MxObjectProperyTable;
import org.mxeclipse.object.tree.MxTreeContentProvider;
import org.mxeclipse.object.tree.MxTreeLabelProvider;
import org.mxeclipse.utils.MxEclipseLogger;
import org.mxeclipse.utils.MxEclipseUtils;
import org.mxeclipse.utils.MxPersistUtils;

public class MxEclipseObjectView extends ViewPart
implements IModifyable, IPropertyChangeListener
{
	public static final String VIEW_ID = MxEclipseObjectView.class.getName();

	private Tree treObjects = null;

	private TreeViewer treeViewer = null;
	private Action actFind;
	private Action actExpand;
	private Action actRevisions;
	private Action actSave;
	private Action actCreateNew;
	private Action actConnectExisting;
	private Action actConnectNew;
	private Action actDemote;
	private Action actPromote;
	private Action actRevise;
	private Action actDisconnect;
	private Action actDelete;
	private Action actFilter;
	private Action actTableColumns;
	private Action actClear;
	private TabFolder tabFolder = null;
	private MxObjectProperyTable basicInfoTable;
	private MxObjectProperyTable attributesInfoTable;
	private MxObjectProperyTable relationshipInfoTable;
	private MxObjectProperyTable historyInfoTable;
	private TabItem itmHistory;
	private Composite topmost = null;
	private SashForm top = null;
	private MxTreeContentProvider treeContentProvider;
	private MxTableColumnList tableColumns;
	private boolean modified;
	private Composite statusBar;
	private Label lblTriggers = null;
	private Label lblServerUser = null;

	private Text txtSearchLimit = null;

	private Label lblSearchLimit = null;

	private Composite pnlSearchLimit = null;

	private Label lblSeparator = null;
	private Label lblSeparator2 = null;
	private boolean appendToList;
	private MxObjectSearchCriteria searchCriteria;
	private ArrayList<MxObjectSearchCriteria> searchCriteriaHistory = new ArrayList();

	public void createPartControl(Composite parent)
	{
		GridData gridData5 = new GridData();
		gridData5.widthHint = 2;
		gridData5.heightHint = 18;
		GridData gridData31 = new GridData();
		gridData31.widthHint = 60;
		gridData31.verticalAlignment = 2;
		gridData31.horizontalAlignment = 1;
		GridData gridData32 = new GridData();
		gridData32.verticalAlignment = 2;
		gridData32.horizontalAlignment = 1;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = 4;
		gridData3.heightHint = 18;
		gridData3.verticalAlignment = 2;
		GridData gridData1 = new GridData();
		gridData1.grabExcessVerticalSpace = true;
		gridData1.horizontalAlignment = 4;
		gridData1.verticalAlignment = 4;
		gridData1.grabExcessHorizontalSpace = true;
		this.topmost = new Composite(parent, 0);
		GridLayout gridLayoutTop = new GridLayout();
		gridLayoutTop.numColumns = 1;
		gridLayoutTop.marginHeight = 1;
		gridLayoutTop.marginWidth = 3;
		gridLayoutTop.verticalSpacing = 2;
		this.topmost.setLayout(gridLayoutTop);

		this.top = new SashForm(this.topmost, 65792);
		GridData gridDataTop = new GridData();
		gridDataTop.grabExcessHorizontalSpace = true;
		gridDataTop.grabExcessVerticalSpace = true;
		gridDataTop.horizontalAlignment = 4;
		gridDataTop.verticalAlignment = 4;

		this.statusBar = new Composite(this.topmost, 0);
		GridLayout gridLayoutStatus = new GridLayout();
		gridLayoutStatus.numColumns = 5;
		gridLayoutStatus.marginWidth = 1;
		gridLayoutStatus.verticalSpacing = 5;
		gridLayoutStatus.horizontalSpacing = 5;
		gridLayoutStatus.marginHeight = 0;
		this.statusBar.setLayout(gridLayoutStatus);
		this.statusBar.setLayoutData(gridData3);

		createPnlSearchLimit();
		this.lblSeparator = new Label(this.statusBar, 2);
		this.lblSeparator.setText("Label");
		this.lblSeparator.setLayoutData(gridData5);
		this.lblTriggers = new Label(this.statusBar, 16779268);
		this.lblTriggers.setText("lblTriggers");
		this.lblTriggers.setLayoutData(gridData31);
		this.lblSeparator2 = new Label(this.statusBar, 2);
		this.lblSeparator2.setText("Label");
		this.lblSeparator2.setLayoutData(gridData5);
		this.lblServerUser = new Label(this.statusBar, 16779268);
		this.lblServerUser.setText("lblServerUser");
		this.lblServerUser.setLayoutData(gridData32);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = false;
		gridData.horizontalAlignment = 4;
		gridData.verticalAlignment = 4;
		gridData.widthHint = 200;
		gridData.grabExcessVerticalSpace = true;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		this.treObjects = new Tree(this.top, 82178);
		this.treObjects.setLayoutData(gridData);

		this.treObjects.setHeaderVisible(true);

		this.treeViewer = new TreeViewer(this.treObjects);
		this.treeContentProvider = new MxTreeContentProvider();

		List lstTableColumns = MxPersistUtils.loadObjects(MxTableColumnList.class);
		if (lstTableColumns.size() > 0)
			this.tableColumns = ((MxTableColumnList)lstTableColumns.get(0));
		else {
			this.tableColumns = new MxTableColumnList();
		}
		this.tableColumns.createColumns(this.treeViewer);
		this.treeViewer.setLabelProvider(new MxTreeLabelProvider(this.tableColumns));

		this.treeViewer.setContentProvider(this.treeContentProvider);

		createTabFolder();
		this.top.setLayoutData(gridData1);
		this.treObjects.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				Point p = new Point(e.x, e.y);
				TreeItem treeItem = MxEclipseObjectView.this.treObjects.getItem(p);
				MxEclipseObjectView.this.expandItem(treeItem);
			}
		});
		this.treeViewer.getTree().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				TreeItem item = (TreeItem)event.item;
				if (item != null) {
					MxEclipseObjectView.this.nodeSelected(item);
					MxEclipseObjectView.this.actExpand.setEnabled(true);
					MxEclipseObjectView.this.actRevisions.setEnabled(true);
					MxEclipseObjectView.this.actConnectExisting.setEnabled(true);
					MxEclipseObjectView.this.actConnectNew.setEnabled(true);
					MxEclipseObjectView.this.actPromote.setEnabled(true);
					MxEclipseObjectView.this.actDemote.setEnabled(true);
					MxEclipseObjectView.this.actRevise.setEnabled(true);
					MxEclipseObjectView.this.actDisconnect.setEnabled(item.getParentItem() != null);
					MxEclipseObjectView.this.actDelete.setEnabled(true);
				} else {
					MxEclipseObjectView.this.actExpand.setEnabled(false);
					MxEclipseObjectView.this.actRevisions.setEnabled(false);
					MxEclipseObjectView.this.actConnectExisting.setEnabled(false);
					MxEclipseObjectView.this.actConnectNew.setEnabled(false);
					MxEclipseObjectView.this.actPromote.setEnabled(false);
					MxEclipseObjectView.this.actDemote.setEnabled(false);
					MxEclipseObjectView.this.actRevise.setEnabled(false);
					MxEclipseObjectView.this.actDisconnect.setEnabled(false);
					MxEclipseObjectView.this.actDelete.setEnabled(false);
					MxEclipseObjectView.this.actSave.setEnabled(false);
				}
				MxEclipseObjectView.this.setModified(false);
			}
		});
		MxEclipsePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		this.lblTriggers.addMouseListener(new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
				IPreferenceStore store = MxEclipsePlugin.getDefault().getPreferenceStore();
				store.setValue("TriggerOff", !store.getBoolean("TriggerOff"));
				try {
					MxEclipseUtils.triggerOnOff();
				} catch (MatrixException ex) {
					Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
					ErrorDialog.openError(MxEclipseObjectView.this.top.getShell(), "Error when creating actions", ex.getMessage(), status);
				}
				MxEclipseObjectView.this.refreshStatusBar();
			}
		});
		this.lblTriggers.setCursor(Display.getCurrent().getSystemCursor(21));

		makeActions();
		hookContextMenu();
		contributeToActionBars();
		refreshStatusBar();
	}

	public void dispose() {
		MxEclipsePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
	}

	private void nodeSelected(TreeItem item) {
		this.basicInfoTable.setObject((MxTreeDomainObject)item.getData());
		this.attributesInfoTable.setObject((MxTreeDomainObject)item.getData());
		this.relationshipInfoTable.setObject((MxTreeDomainObject)item.getData());
		if ((this.tabFolder.getSelection().length > 0) && (this.tabFolder.getSelection()[0].equals(this.itmHistory)))
			this.historyInfoTable.setObject((MxTreeDomainObject)item.getData());
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(this.actFind);
		manager.add(this.actExpand);
		manager.add(this.actRevisions);
		manager.add(this.actFilter);
		manager.add(this.actTableColumns);
		manager.add(new Separator("additions"));
		manager.add(this.actSave);
		manager.add(this.actCreateNew);
		manager.add(this.actConnectNew);
		manager.add(this.actConnectExisting);
		manager.add(new Separator("additions"));
		manager.add(this.actPromote);
		manager.add(this.actDemote);
		manager.add(this.actRevise);
		manager.add(new Separator("additions"));
		manager.add(this.actDisconnect);
		manager.add(this.actDelete);
		manager.add(new Separator("additions"));
		manager.add(this.actClear);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(this.actFind);
		manager.add(this.actExpand);
		manager.add(this.actRevisions);
		manager.add(this.actFilter);
		manager.add(this.actTableColumns);
		manager.add(new Separator());
		manager.add(this.actSave);
		manager.add(this.actCreateNew);
		manager.add(this.actConnectNew);
		manager.add(this.actConnectExisting);
		manager.add(new Separator("additions"));
		manager.add(this.actPromote);
		manager.add(this.actDemote);
		manager.add(this.actRevise);
		manager.add(new Separator("additions"));
		manager.add(this.actDisconnect);
		manager.add(this.actDelete);
		manager.add(new Separator());
		manager.add(this.actClear);
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(this.actFind);
		manager.add(this.actExpand);
		manager.add(this.actRevisions);
		manager.add(this.actFilter);
		manager.add(this.actTableColumns);
		manager.add(new Separator());
		manager.add(this.actSave);
		manager.add(this.actCreateNew);
		manager.add(this.actConnectNew);
		manager.add(this.actConnectExisting);
		manager.add(new Separator("additions"));
		manager.add(this.actPromote);
		manager.add(this.actDemote);
		manager.add(this.actRevise);
		manager.add(new Separator("additions"));
		manager.add(this.actDisconnect);
		manager.add(this.actDelete);
		manager.add(new Separator());
		manager.add(this.actClear);
	}

	public void findObjects(String id, String type, String name, String rev, ArrayList<MxAttribute> alAttributes, boolean appendObjects) {
		try {
			Context ctx = MxEclipsePlugin.getDefault().getContext();
			if ((ctx != null) && (ctx.isConnected())) {
				List resultList = SearchMatrixBusinessObjectsDialog.findObjects(id, type, name, rev, alAttributes);

				MxTreeDomainObject root = (appendObjects) && (this.treeViewer.getInput() != null) ? (MxTreeDomainObject)this.treeViewer.getInput() : new MxTreeDomainObject();
				for (int i = 0; i < resultList.size(); i++) {
					MxTreeDomainObject child = (MxTreeDomainObject)resultList.get(i);
					child.setContentProvider(this.treeContentProvider);
					root.addChild(child);
				}
				if (!appendObjects) {
					clearAll();
				}
				this.treeViewer.setInput(root);

				if (resultList.size() > 0) {
					nodeSelected(this.treObjects.getItem(0));

					this.actExpand.setEnabled(true);
					this.actRevisions.setEnabled(true);
					this.actConnectNew.setEnabled(true);
					this.actConnectExisting.setEnabled(true);
					this.actPromote.setEnabled(true);
					this.actDemote.setEnabled(true);
					this.actRevise.setEnabled(true);
					this.actDisconnect.setEnabled(false);
					this.actDelete.setEnabled(true);

					this.treObjects.setSelection(this.treObjects.getItem(0));
					this.tabFolder.setSelection(1);
				} else {
					this.actExpand.setEnabled(false);
					this.actRevisions.setEnabled(false);
					this.actConnectNew.setEnabled(false);
					this.actConnectExisting.setEnabled(false);
					this.actPromote.setEnabled(false);
					this.actDemote.setEnabled(false);
					this.actRevise.setEnabled(false);
					this.actDisconnect.setEnabled(false);
					this.actDelete.setEnabled(false);
				}
			}
			else {
				MessageDialog.openInformation(this.top.getShell(), 
						"Searech Matrix Business Objects", 
						"No User connected to Matrix");
			}
		}
		catch (Exception e) {
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getViewSite().getShell(), 
					"Search Error", 
					"Error Occurred while trying to perform search", 
					status);
		}
	}

	private void makeActions() {
		try {
			ImageDescriptor imgDesClr = MxEclipsePlugin.getImageDescriptor("eraser.gif");

			this.actFind = new SearchHistoryDropDownAction(this);
			this.actFind.setText("Find Business Objects");
			this.actFind.setToolTipText("Find Business Objects");
			this.actFind.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("find.gif"));
			this.actFind.setActionDefinitionId("org.mxeclipse.FindObjects");

			IHandlerService handlerService = (IHandlerService)getSite().getService(IHandlerService.class);
			IHandler myFind = new AbstractHandler() {
				public Object execute(ExecutionEvent event) throws ExecutionException {
					MxEclipseObjectView.this.actFind.run();
					return null;
				}
			};
			handlerService.activateHandler("org.eclipse.ui.edit.findReplace", myFind);

			this.actExpand = new Action() {
				public void run() {
					TreeItem[] curItems = MxEclipseObjectView.this.treObjects.getSelection();
					if (curItems.length > 0)
						MxEclipseObjectView.this.expandItem(curItems[0]);
				}
			};
			this.actExpand.setText("Expand Relationships");
			this.actExpand.setToolTipText("Expands all the relationships of the currently selected node");
			this.actExpand.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("expand.gif"));
			this.actExpand.setEnabled(false);

			this.actRevisions = new Action() {
				public void run() {
					TreeItem[] curItems = MxEclipseObjectView.this.treObjects.getSelection();
					if (curItems.length > 0)
						MxEclipseObjectView.this.showRevisions(curItems[0]);
				}
			};
			this.actRevisions.setText("Show Revisions");
			this.actRevisions.setToolTipText("Shows all revisions of the currently selected node - the node will be expanded");
			this.actRevisions.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("revisions.gif"));
			this.actRevisions.setEnabled(false);

			this.actFilter = new Action() {
				public void run() {
					Context ctx = MxEclipsePlugin.getDefault().getContext();
					if ((ctx != null) && (ctx.isConnected())) {
						FilterObjectsDialog dlgFilter = new FilterObjectsDialog(MxEclipseObjectView.this.top.getShell(), MxEclipseObjectView.this.treeContentProvider.getFilter());
						dlgFilter.open();
						if (dlgFilter.getReturnCode() == 0) {
							MxFilter resultFilter = dlgFilter.getFilter();

							MxEclipseObjectView.this.treeContentProvider.setFilter(resultFilter);
							MxTreeDomainObject root = (MxTreeDomainObject)MxEclipseObjectView.this.treObjects.getData();
							if (root != null)
								try {
									root.refresh();
								}
							catch (Exception e) {
								MxEclipseLogger.getLogger().severe(e.getMessage());
							}
						}
					}
					else {
						MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Filter Definition", "No User connected to Matrix");
					}
				}
			};
			this.actFilter.setText("Filter Rels/Objects");
			this.actFilter.setToolTipText("Defines filter(s) for relationships/objects to be shown in the structure");
			this.actFilter.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("filter.gif"));

			this.actTableColumns = new Action() {
				public void run() {
					Context ctx = MxEclipsePlugin.getDefault().getContext();
					if ((ctx != null) && (ctx.isConnected())) {
						ConfigureTableDialog dlgFilter = new ConfigureTableDialog(MxEclipseObjectView.this.top.getShell(), MxEclipseObjectView.this.tableColumns);

						dlgFilter.open();
						if (dlgFilter.getReturnCode() == 0) {
							MxEclipseObjectView.this.tableColumns = dlgFilter.getTableColuns();
							MxEclipseObjectView.this.tableColumns.createColumns(MxEclipseObjectView.this.treeViewer);
							MxEclipseObjectView.this.treeViewer.setLabelProvider(new MxTreeLabelProvider(MxEclipseObjectView.this.tableColumns));
						}
					} else {
						MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Filter Definition", "No User connected to Matrix");
					}
				}
			};
			this.actTableColumns.setText("Table Columns");
			this.actTableColumns.setToolTipText("Defines table columns to be shown");
			this.actTableColumns.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("column.gif"));

			this.actSave = new Action() {
				public void run() {
					Context ctx = MxEclipsePlugin.getDefault().getContext();
					if ((ctx != null) && (ctx.isConnected()))
						MxEclipseObjectView.this.saveAll();
					else
						MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), 
								"Search Matrix Business Objects", 
								"No User connected to Matrix");
				}
			};
			this.actSave.setText("Save");
			this.actSave.setToolTipText("Save Business Object/Relationship");
			this.actSave.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("save.gif"));
			this.actSave.setEnabled(false);

			IHandler mySave = new AbstractHandler() {
				public Object execute(ExecutionEvent event) throws ExecutionException {
					MxEclipseObjectView.this.actSave.run();
					return null;
				}
			};
			handlerService.activateHandler("org.eclipse.ui.file.save", mySave);

			this.actCreateNew = new Action() {
				public void run() {
					Context ctx = MxEclipsePlugin.getDefault().getContext();
					if ((ctx != null) && (ctx.isConnected()))
						try {
							CreateNewDialog dlgConnect = new CreateNewDialog(MxEclipseObjectView.this.top.getShell());
							dlgConnect.open();
							if (dlgConnect.getReturnCode() != 0) return; MxTreeDomainObject otherSelectedObject = dlgConnect.getNewObject();
							MxTreeDomainObject root = MxEclipseObjectView.this.treeViewer.getInput() != null ? (MxTreeDomainObject)MxEclipseObjectView.this.treeViewer.getInput() : new MxTreeDomainObject();
							if (otherSelectedObject == null) return; root.addChild(otherSelectedObject);
							otherSelectedObject.setContentProvider(MxEclipseObjectView.this.treeContentProvider);
							MxEclipseObjectView.this.treeViewer.add(root, otherSelectedObject);
							for (int i = 0; i < MxEclipseObjectView.this.treObjects.getItemCount(); i++) {
								if (MxEclipseObjectView.this.treObjects.getItem(i).getData().equals(otherSelectedObject)) {
									MxEclipseObjectView.this.treObjects.setSelection(MxEclipseObjectView.this.treObjects.getItem(i));
									MxEclipseObjectView.this.nodeSelected(MxEclipseObjectView.this.treObjects.getItem(i));
								}
							}
							MxEclipseObjectView.this.tabFolder.setSelection(1);

							MxEclipseObjectView.this.actExpand.setEnabled(true);
							MxEclipseObjectView.this.actRevisions.setEnabled(true);
							MxEclipseObjectView.this.actConnectNew.setEnabled(true);
							MxEclipseObjectView.this.actConnectExisting.setEnabled(true);
							MxEclipseObjectView.this.actPromote.setEnabled(true);
							MxEclipseObjectView.this.actDemote.setEnabled(true);
							MxEclipseObjectView.this.actRevise.setEnabled(true);
							MxEclipseObjectView.this.actDisconnect.setEnabled(false);
							MxEclipseObjectView.this.actDelete.setEnabled(true);
						}
					catch (Exception e)
					{
						MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Connect Existing", "Error when connecting objects " + e.getMessage());
					}
					else
						MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Connect Existing", "No User connected to Matrix");
				}
			};
			this.actCreateNew.setText("Create New");
			this.actCreateNew.setToolTipText("Create New Object");
			this.actCreateNew.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("create.gif"));
			this.actCreateNew.setEnabled(true);

			this.actConnectNew = new Action() {
				public void run() {
					TreeItem[] curItems = MxEclipseObjectView.this.treObjects.getSelection();
					if (curItems.length > 0) {
						Context ctx = MxEclipsePlugin.getDefault().getContext();
						if ((ctx != null) && (ctx.isConnected())) {
							TreeItem treeItem = curItems[0];
							if (treeItem != null) {
								MxTreeDomainObject selectedObject = (MxTreeDomainObject)treeItem.getData();
								try {
									ConnectNewDialog dlgConnect = new ConnectNewDialog(MxEclipseObjectView.this.top.getShell(), selectedObject.getType());
									dlgConnect.open();
									if (dlgConnect.getReturnCode() != 0) return; MxTreeDomainObject otherSelectedObject = dlgConnect.getSelectedObject();
									if (otherSelectedObject != null) {
										if (dlgConnect.isFrom())
											selectedObject.getDomainObject().addToObject(ctx, new RelationshipType(dlgConnect.getRelationshipType()), otherSelectedObject.getId());
										else {
											selectedObject.getDomainObject().addFromObject(ctx, new RelationshipType(dlgConnect.getRelationshipType()), otherSelectedObject.getId());
										}
									}
									MxEclipseObjectView.this.expandItem(treeItem);

									for (int i = 0; i < treeItem.getItemCount(); i++) {
										if (((MxTreeDomainObject)treeItem.getItem(i).getData()).getId().equals(otherSelectedObject.getId())) {
											MxEclipseObjectView.this.treObjects.setSelection(treeItem.getItem(i));
											MxEclipseObjectView.this.nodeSelected(treeItem.getItem(i));
										}
									}
									MxEclipseObjectView.this.tabFolder.setSelection(1);
								}
								catch (Exception e) {
									MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Connect New", "Error when creating/connecting objects " + e.getMessage());
								}
							}
						} else {
							MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Connect New", "No User connected to Matrix");
						}
					}
				}
			};
			this.actConnectNew.setText("Connect New");
			this.actConnectNew.setToolTipText("Connect New Object");
			this.actConnectNew.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("connectnew.gif"));
			this.actConnectNew.setEnabled(false);

			this.actConnectExisting = new Action() {
				public void run() {
					TreeItem[] curItems = MxEclipseObjectView.this.treObjects.getSelection();
					if (curItems.length > 0) {
						Context ctx = MxEclipsePlugin.getDefault().getContext();
						if ((ctx != null) && (ctx.isConnected())) {
							TreeItem treeItem = curItems[0];
							if (treeItem != null) {
								MxTreeDomainObject selectedObject = (MxTreeDomainObject)treeItem.getData();
								try {
									ConnectExistingDialog dlgConnect = new ConnectExistingDialog(MxEclipseObjectView.this.top.getShell(), selectedObject.getType(), MxEclipseObjectView.this.tableColumns);
									dlgConnect.open();
									if (dlgConnect.getReturnCode() != 0) return; MxTreeDomainObject otherSelectedObject = dlgConnect.getSelectedObject();
									if (otherSelectedObject != null) {
										if (dlgConnect.isFrom())
											selectedObject.getDomainObject().addToObject(ctx, new RelationshipType(dlgConnect.getRelationshipType()), otherSelectedObject.getId());
										else {
											selectedObject.getDomainObject().addFromObject(ctx, new RelationshipType(dlgConnect.getRelationshipType()), otherSelectedObject.getId());
										}
									}
									MxEclipseObjectView.this.expandItem(treeItem);
								}
								catch (Exception e) {
									MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Connect Existing", "Error when connecting objects " + e.getMessage());
								}
							}
						} else {
							MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Connect Existing", "No User connected to Matrix");
						}
					}
				}
			};
			this.actConnectExisting.setText("Connect Existing");
			this.actConnectExisting.setToolTipText("Connect Existing Object");
			this.actConnectExisting.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("connect.gif"));
			this.actConnectExisting.setEnabled(false);

			this.actPromote = new Action() {
				public void run() {
					if (MessageDialog.openConfirm(MxEclipseObjectView.this.top.getShell(), "Promote Business Objects", "Are you sure that you want to promote objects?")) {
						TreeItem[] curItems = MxEclipseObjectView.this.treObjects.getSelection();
						for (int i = curItems.length - 1; i >= 0; i--) {
							Context ctx = MxEclipsePlugin.getDefault().getContext();
							if(ctx != null && ctx.isConnected())
                            {
                                TreeItem treeItem = curItems[i];
                                if(treeItem != null)
                                {
                                    MxTreeDomainObject selectedObject = (MxTreeDomainObject)treeItem.getData();
                                    try
                                    {
                                        selectedObject.getDomainObject().promote(ctx);
                                        selectedObject.setCurrent(selectedObject.getDomainObject().getInfo(ctx, "current"));
                                    }
                                    catch(Exception e)
                                    {
                                        MessageDialog.openInformation(top.getShell(), "Promote Business Objects", (new StringBuilder("Error when promoting objects ")).append(e.getMessage()).toString());
                                    }
                                }
                            } else {
								MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Promote Business Objects", "No User connected to Matrix");
							}
						}

						TreeItem[] selectedItems = MxEclipseObjectView.this.treObjects.getSelection();
						TreeItem atreeitem[];
                        int k = (atreeitem = selectedItems).length;
                        for(int j = 0; j < k; j++)
                        {
                            TreeItem selectedItem = atreeitem[j];
                            treeViewer.refresh(selectedItem.getData(), true);
                        }
						if (selectedItems.length > 0)
							MxEclipseObjectView.this.nodeSelected(selectedItems[0]);
					}
				}
			};
			this.actPromote.setText("Promote Object");
			this.actPromote.setToolTipText("Promote business object");
			this.actPromote.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("promote.gif"));
			this.actPromote.setEnabled(false);

			this.actDemote = new Action() {
				public void run() {
					if (MessageDialog.openConfirm(MxEclipseObjectView.this.top.getShell(), "Demote Business Objects", "Are you sure that you want to demote objects?")) {
						TreeItem[] curItems = MxEclipseObjectView.this.treObjects.getSelection();
						for (int i = curItems.length - 1; i >= 0; i--) {
							Context ctx = MxEclipsePlugin.getDefault().getContext();
							if ((ctx != null) && (ctx.isConnected())) {
								TreeItem treeItem = curItems[i];
								if (treeItem != null) {
									MxTreeDomainObject selectedObject = (MxTreeDomainObject)treeItem.getData();
									try
									{
										selectedObject.getDomainObject().demote(ctx);
										selectedObject.setCurrent(selectedObject.getDomainObject().getInfo(ctx, "current"));
									} catch (Exception e) {
										MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Demote Business Objects", "Error when demoting objects " + e.getMessage());
									}
								}
							} else {
								MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Demote Business Objects", "No User connected to Matrix");
							}
						}

						TreeItem[] selectedItems = MxEclipseObjectView.this.treObjects.getSelection();
						TreeItem atreeitem[];
                        int k = (atreeitem = selectedItems).length;
                        for(int j = 0; j < k; j++)
                        {
                            TreeItem selectedItem = atreeitem[j];
                            treeViewer.refresh(selectedItem.getData(), true);
                        }
						if (selectedItems.length > 0)
							MxEclipseObjectView.this.nodeSelected(selectedItems[0]);
					}
				}
			};
			this.actDemote.setText("Demote Object");
			this.actDemote.setToolTipText("Demote business object");
			this.actDemote.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("demote.gif"));
			this.actDemote.setEnabled(false);

			this.actRevise = new Action() {
				public void run() {
					if (MessageDialog.openConfirm(MxEclipseObjectView.this.top.getShell(), "Revise Business Objects", "Are you sure that you want to revise objects?")) {
						TreeItem[] curItems = MxEclipseObjectView.this.treObjects.getSelection();
						for (int i = curItems.length - 1; i >= 0; i--) {
							Context ctx = MxEclipsePlugin.getDefault().getContext();
							if ((ctx != null) && (ctx.isConnected())) {
								TreeItem treeItem = curItems[i];
								if (treeItem != null) {
									MxTreeDomainObject selectedObject = (MxTreeDomainObject)treeItem.getData();
									try
									{
										BusinessObject newRevObj = selectedObject.getDomainObject().reviseObject(ctx, true);
										selectedObject = new MxTreeDomainObject(newRevObj.getObjectId());
										treeItem.setData(selectedObject);
									} catch (Exception e) {
										MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Revise Business Objects", "Error when revising objects " + e.getMessage());
									}
								}
							} else {
								MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Revise Business Objects", "No User connected to Matrix");
							}
						}

						TreeItem[] selectedItems = MxEclipseObjectView.this.treObjects.getSelection();
						TreeItem atreeitem[];
                        int k = (atreeitem = selectedItems).length;
                        for(int j = 0; j < k; j++)
                        {
                            TreeItem selectedItem = atreeitem[j];
                            treeViewer.refresh(selectedItem.getData(), true);
                        }
						if (selectedItems.length > 0)
							MxEclipseObjectView.this.nodeSelected(selectedItems[0]);
					}
				}
			};
			this.actRevise.setText("Revise Object");
			this.actRevise.setToolTipText("Revise business object");
			this.actRevise.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("revise.gif"));
			this.actRevise.setEnabled(false);

			this.actDisconnect = new Action() {
				public void run() {
					if (MessageDialog.openConfirm(MxEclipseObjectView.this.top.getShell(), "Disconnect Business Objects", "Are you sure that you want to disconnect objects?")) {
						TreeItem[] curItems = MxEclipseObjectView.this.treObjects.getSelection();
						for (int i = curItems.length - 1; i >= 0; i--) {
							Context ctx = MxEclipsePlugin.getDefault().getContext();
							if ((ctx != null) && (ctx.isConnected())) {
								TreeItem treeItem = curItems[i];
								if (treeItem != null) {
									MxTreeDomainObject selectedObject = (MxTreeDomainObject)treeItem.getData();

									if (selectedObject.getDomainRelationship() == null) continue;
									try {
										DomainRelationship.disconnect(ctx, selectedObject.getRelId());
									} catch (Exception e) {
										MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Disconnect Business Objects", "Error when disconnecting objects " + e.getMessage());
									}
								}
							}
							else
							{
								MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Disconnect Business Objects", "No User connected to Matrix");
							}
						}

						for (int i = curItems.length - 1; i >= 0; i--) {
							TreeItem treeItem = curItems[i];
							TreeItem parentItem = treeItem.getParentItem();
							MxEclipseObjectView.this.expandItem(parentItem);
						}
					}
				}
			};
			this.actDisconnect.setText("Disconnect Relationship");
			this.actDisconnect.setToolTipText("Disconnect the relationships of the currently selected node to the parent node");
			this.actDisconnect.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("disconnect.gif"));
			this.actDisconnect.setEnabled(false);

			this.actDelete = new Action() {
				public void run() {
					if (MessageDialog.openConfirm(MxEclipseObjectView.this.top.getShell(), "Delete Business Object", "Are you sure that you want to delete object?")) {
						TreeItem[] curItems = MxEclipseObjectView.this.treObjects.getSelection();
						if (curItems.length > 0) {
							Context ctx = MxEclipsePlugin.getDefault().getContext();
							if ((ctx != null) && (ctx.isConnected()))
								for (int i = 0; i < curItems.length; i++) {
									TreeItem treeItem = curItems[i];
									if (treeItem != null) {
										MxTreeDomainObject selectedObject = (MxTreeDomainObject)treeItem.getData();
										try {
											DomainObject.deleteObjects(ctx, new String[] { selectedObject.getId() });

											treeItem.dispose();
										} catch (Exception e) {
											MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Delete Business Object", "Error when deleting objects " + e.getMessage());
										}
									}
								}
							else
								MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), "Delete Business Object", "No User connected to Matrix");
						}
					}
				}
			};
			this.actDelete.setText("Delete Object");
			this.actDelete.setToolTipText("Delete currently selected node");
			this.actDelete.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("delete.gif"));
			this.actDelete.setEnabled(false);

			this.actClear = new Action()
			{
				public void run() {
					MxEclipseObjectView.this.clearAll();
				}
			};
			this.actClear.setText("Clear");
			this.actClear.setToolTipText("Clear");
			this.actClear.setImageDescriptor(imgDesClr);
		}
		catch (Exception e)
		{
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getViewSite().getShell(), 
					"Error when creating actions", 
					e.getMessage(), 
					status);
		}
	}

	private void saveAll()
	{
		this.basicInfoTable.save();
		this.relationshipInfoTable.save();
		this.attributesInfoTable.save();

		this.basicInfoTable.refresh();
		this.attributesInfoTable.refresh();
		this.relationshipInfoTable.refresh();
		this.historyInfoTable.refresh();
		if (getPartName().substring(0, 1).equals("*")) {
			setPartName(getPartName().substring(1));
		}
		this.actSave.setEnabled(false);

		TreeItem[] selectedItems = this.treObjects.getSelection();
		for (TreeItem selectedItem : selectedItems)
			this.treeViewer.refresh(selectedItem.getData(), true);
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager) {
				MxEclipseObjectView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(this.treeViewer.getControl());
		this.treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, this.treeViewer);
	}

	public void setFocus()
	{
	}

	private void createTabFolder()
	{
		this.tabFolder = new TabFolder(this.top, 131072);
		TabItem item1 = new TabItem(this.tabFolder, 0);
		item1.setText("Basics");
		this.basicInfoTable = new MxObjectProperyTable(this.tabFolder, "Basic", this);
		item1.setControl(this.basicInfoTable.getControl());

		TabItem item2 = new TabItem(this.tabFolder, 0);
		item2.setText("Attributes");
		this.attributesInfoTable = new MxObjectProperyTable(this.tabFolder, "Attributes", this);
		item2.setControl(this.attributesInfoTable.getControl());

		TabItem item3 = new TabItem(this.tabFolder, 0);
		item3.setText("Relationship");
		this.relationshipInfoTable = new MxObjectProperyTable(this.tabFolder, "Relationship", this);
		item3.setControl(this.relationshipInfoTable.getControl());

		this.itmHistory = new TabItem(this.tabFolder, 0);
		this.itmHistory.setText("History");
		this.historyInfoTable = new MxObjectProperyTable(this.tabFolder, "History", this);
		this.itmHistory.setControl(this.historyInfoTable.getControl());

		this.tabFolder.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				if (MxEclipseObjectView.this.itmHistory.equals(arg0.item)) {
					TreeItem[] curItems = MxEclipseObjectView.this.treObjects.getSelection();
					if (curItems.length > 0) {
						TreeItem item = curItems[0];
						MxEclipseObjectView.this.historyInfoTable.setObject((MxTreeDomainObject)item.getData());
					}
				}
			}
		});
	}

	public void clearAll() {
		this.treeViewer.getTree().removeAll();
		this.basicInfoTable.clear();
		this.attributesInfoTable.clear();
		this.relationshipInfoTable.clear();
		this.historyInfoTable.clear();
	}

	protected void expandItem(TreeItem treeItem) {
		if (treeItem != null) {
			MxTreeDomainObject selectedObject = (MxTreeDomainObject)treeItem.getData();
			try {
				selectedObject.getChildren(true);
				this.treeViewer.refresh(selectedObject, false);
				this.treeViewer.expandToLevel(selectedObject, 1);
			} catch (MxEclipseException e1) {
				MessageDialog.openError(this.top.getShell(), "Error", e1.getMessage());
			} catch (MatrixException e1) {
				e1.printStackTrace();
			}
		}
	}

	protected void showRevisions(TreeItem treeItem)
	{
		if (treeItem != null) {
			MxTreeDomainObject selectedObject = (MxTreeDomainObject)treeItem.getData();
			try {
				selectedObject.showRevisions();
				this.treeViewer.refresh(selectedObject, false);
				this.treeViewer.expandToLevel(selectedObject, 1);
			} catch (MxEclipseException e1) {
				MessageDialog.openError(this.top.getShell(), "Error", e1.getMessage());
			} catch (MatrixException e1) {
				e1.printStackTrace();
			}
		}
	}

	public boolean isModified()
	{
		return this.modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
		this.actSave.setEnabled(modified);
		if ((modified) && (!getPartName().substring(0, 1).equals("*")))
			setPartName("*" + getPartName());
	}

	public void refreshStatusBar()
	{
		if (Display.getCurrent() == null) {
			return;
		}
		if (this.lblTriggers != null) {
			if (MxEclipseUtils.isTriggersOff()) {
				this.lblTriggers.setForeground(Display.getCurrent().getSystemColor(3));
				this.lblTriggers.setText("Trigger Off");
			} else {
				this.lblTriggers.setForeground(Display.getCurrent().getSystemColor(6));
				this.lblTriggers.setText("Trigger On");
			}
		}
		if ((this.lblServerUser != null) && (MxEclipsePlugin.getDefault() != null)) {
			if (MxEclipsePlugin.getDefault().getHost() != null) {
				String host = MxEclipsePlugin.getDefault().getHost();
				this.lblServerUser.setText(MxEclipsePlugin.getDefault().getUser() + 
						" @ " + host);
				if ((host.startsWith("http://")) || (host.startsWith("rmi:")))
					this.lblServerUser.setForeground(Display.getCurrent().getSystemColor(3));
				else
					this.lblServerUser.setForeground(Display.getCurrent().getSystemColor(6));
			}
			else {
				this.lblServerUser.setText("-- Not Connected --");
				this.lblServerUser.setForeground(Display.getCurrent().getSystemColor(2));
			}
			this.lblServerUser.redraw();
		}
		if (this.txtSearchLimit != null) {
            txtSearchLimit.setText((new StringBuilder()).append(MxEclipsePlugin.getDefault().getPreferenceStore().getInt("ObjectSearchLimit")).toString());
		}
		this.statusBar.pack();
	}

	public static void refreshViewStatusBar(Shell shell) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		try {
			if (page != null) {
				MxEclipseObjectView view = (MxEclipseObjectView)page.showView("org.mxeclipse.views.MxEclipseObjectView");
				if (view != null)
					view.refreshStatusBar();
			}
		}
		catch (PartInitException localPartInitException)
		{
		}
	}

	public void propertyChange(PropertyChangeEvent event)
	{
		if (("TriggerOff".equals(event.getProperty())) || ("ObjectSearchLimit".equals(event.getProperty())) || 
				("MatrixHost".equals(event.getProperty())) || ("MatrixUser".equals(event.getProperty())))
			refreshStatusBar();
	}

	private void createPnlSearchLimit()
	{
		this.pnlSearchLimit = new Composite(this.statusBar, 0);
		GridLayout gridLayoutLimit = new GridLayout();
		gridLayoutLimit.numColumns = 2;
		gridLayoutLimit.marginHeight = 0;
		this.pnlSearchLimit.setLayout(gridLayoutLimit);

		GridData gridData4 = new GridData();
		gridData4.widthHint = 30;
		this.lblSearchLimit = new Label(this.pnlSearchLimit, 0);
		this.lblSearchLimit.setText("Search Limit");
		this.txtSearchLimit = new Text(this.pnlSearchLimit, 2048);
		this.txtSearchLimit.setLayoutData(gridData4);
		this.txtSearchLimit.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				MxEclipseObjectView.this.storeSearchLimit();
			}
		});
		this.txtSearchLimit.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13)
					MxEclipseObjectView.this.storeSearchLimit();
			}
		});
	}

	protected void storeSearchLimit()
	{
		int oldLimit = MxEclipsePlugin.getDefault().getPreferenceStore().getInt("ObjectSearchLimit");
		int newLimit = oldLimit;
		try {
			newLimit = Integer.parseInt(this.txtSearchLimit.getText());
		}
		catch (NumberFormatException localNumberFormatException) {
		}
		MxEclipsePlugin.getDefault().getPreferenceStore().setValue("ObjectSearchLimit", newLimit);

		refreshStatusBar();
	}

	class HistoryFindAction extends Action
	{
		private MxEclipseObjectView mView;
		private MxObjectSearchCriteria searchCriteria;

		public HistoryFindAction(MxEclipseObjectView viewPart, MxObjectSearchCriteria searchCriteria)
		{
			this.mView = viewPart;
			this.searchCriteria = searchCriteria;

			String criteriaName = searchCriteria != null ? searchCriteria.toString() : "";
			setText(criteriaName);
			setImageDescriptor(getImageDescriptor(searchCriteria));

			setDescription(criteriaName);
			setToolTipText(criteriaName);
		}

		private ImageDescriptor getImageDescriptor(MxObjectSearchCriteria element)
		{
			return null;
		}

		public void run()
		{
			Context ctx = MxEclipsePlugin.getDefault().getContext();
			if ((ctx != null) && (ctx.isConnected())) {
				SearchMatrixBusinessObjectsDialog searchBusinessDlg = new SearchMatrixBusinessObjectsDialog(MxEclipseObjectView.this.top.getShell(), this.searchCriteria);
				searchBusinessDlg.open();
				if (searchBusinessDlg.getReturnCode() == 0)
					try {
						MxEclipseObjectView.this.appendToList = searchBusinessDlg.getSearchCriteria().isAppendResults();
						List resultList = searchBusinessDlg.getTreeObjectList();

						MxTreeDomainObject root = null;
						if (MxEclipseObjectView.this.appendToList)
							root = (MxTreeDomainObject)MxEclipseObjectView.this.treeViewer.getInput();
						else {
							root = new MxTreeDomainObject();
						}

						for (int i = 0; i < resultList.size(); i++) {
							MxTreeDomainObject child = (MxTreeDomainObject)resultList.get(i);
							boolean alreadyChild = false;
							for (MxTreeDomainObject rootChildren : root.getChildren(false)) {
								if ((rootChildren.getType().equals(child.getType())) && (rootChildren.getName().equals(child.getName())) && (rootChildren.getRevision().equals(child.getRevision()))) {
									alreadyChild = true;
								}
							}
							if (alreadyChild)
								continue;
							child.setContentProvider(MxEclipseObjectView.this.treeContentProvider);
							root.addChild(child);
						}

						MxEclipseObjectView.this.clearAll();
						MxEclipseObjectView.this.treeViewer.setInput(root);
						MxEclipseObjectView.this.actExpand.setEnabled(false);
						MxEclipseObjectView.this.actRevisions.setEnabled(false);
						MxEclipseObjectView.this.actConnectNew.setEnabled(false);
						MxEclipseObjectView.this.actConnectExisting.setEnabled(false);
						MxEclipseObjectView.this.actPromote.setEnabled(false);
						MxEclipseObjectView.this.actDemote.setEnabled(false);
						MxEclipseObjectView.this.actRevise.setEnabled(false);
						MxEclipseObjectView.this.actDisconnect.setEnabled(false);
						MxEclipseObjectView.this.actDelete.setEnabled(false);

						this.searchCriteria = searchBusinessDlg.getSearchCriteria();
						MxEclipseObjectView.this.searchCriteriaHistory.add(this.searchCriteria);
					} catch (Exception ex) {
						Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
						ErrorDialog.openError(MxEclipseObjectView.this.getViewSite().getShell(), 
								"Error when performing search", 
								ex.getMessage(), 
								status);
					}
			}
			else {
				MessageDialog.openInformation(MxEclipseObjectView.this.top.getShell(), 
						"Searech Matrix Business Objects", 
						"No User connected to Matrix");
			}
		}
	}

	class SearchHistoryDropDownAction extends Action
	implements IMenuCreator
	{
		public static final int RESULTS_IN_DROP_DOWN = 10;
		private MxEclipseObjectView mView;
		private Menu mMenu;

		public SearchHistoryDropDownAction(MxEclipseObjectView view)
		{
			this.mView = view;
			this.mMenu = null;
			setToolTipText("Previous searches");
			setImageDescriptor(MxEclipsePlugin.getImageDescriptor("find.gif"));
			setMenuCreator(this);
		}

		public void dispose() {
			this.mView = null;
			if (this.mMenu != null) {
				this.mMenu.dispose();
				this.mMenu = null;
			}
		}

		public Menu getMenu(Control parent) {
			if (this.mMenu != null) {
				this.mMenu.dispose();
			}
			this.mMenu = new Menu(parent);
			ArrayList criterias = this.mView.searchCriteriaHistory;
			addEntries(this.mMenu, criterias);
			return this.mMenu;
		}

		private boolean addEntries(Menu menu, ArrayList<MxObjectSearchCriteria> elements) {
			boolean checked = false;

			int min = Math.min(elements.size(), 10);
			for (int i = 0; i < min; i++) {
				MxObjectSearchCriteria criteria = (MxObjectSearchCriteria)elements.get(elements.size() - 1 - i);
				HistoryFindAction action = new HistoryFindAction(mView, criteria);
				action.setChecked(criteria.equals(this.mView.searchCriteria));
				checked = (checked) || (action.isChecked());
				addActionToMenu(menu, action);
			}
			return checked;
		}

		public Menu getMenu(Menu menu) {
			return null;
		}

		protected void addActionToMenu(Menu parent, Action action) {
			ActionContributionItem item = new ActionContributionItem(action);
			item.fill(parent, -1);
		}

		public void run() {
			HistoryFindAction hfa = new HistoryFindAction(mView, null);
			hfa.run();
		}
	}
}