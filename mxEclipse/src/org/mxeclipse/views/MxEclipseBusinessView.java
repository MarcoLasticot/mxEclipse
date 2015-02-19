package org.mxeclipse.views;

import java.io.PrintStream;
import java.util.List;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.business.basic.MxAssignmentBasicComposite;
import org.mxeclipse.business.basic.MxAssociationBasicComposite;
import org.mxeclipse.business.basic.MxAttributeBasicComposite;
import org.mxeclipse.business.basic.MxBusinessBasicComposite;
import org.mxeclipse.business.basic.MxDefaultBasicComposite;
import org.mxeclipse.business.basic.MxIndexBasicComposite;
import org.mxeclipse.business.basic.MxPersonAddressComposite;
import org.mxeclipse.business.basic.MxPersonBasicComposite;
import org.mxeclipse.business.basic.MxPersonRightsComposite;
import org.mxeclipse.business.basic.MxPolicyBasicComposite;
import org.mxeclipse.business.basic.MxProgramBasicComposite;
import org.mxeclipse.business.basic.MxProgramCodeComposite;
import org.mxeclipse.business.basic.MxRelationshipBasicComposite;
import org.mxeclipse.business.basic.MxStateBasicComposite;
import org.mxeclipse.business.basic.MxStateRightsComposite;
import org.mxeclipse.business.basic.MxTypeBasicComposite;
import org.mxeclipse.business.basic.MxWebBasicComposite;
import org.mxeclipse.business.basic.MxWebColumnBasicComposite;
import org.mxeclipse.business.basic.MxWebColumnExpressionComposite;
import org.mxeclipse.business.basic.MxWebLinkComposite;
import org.mxeclipse.business.basic.MxWebTableBasicComposite;
import org.mxeclipse.business.table.assignment.MxAssignmentComposite;
import org.mxeclipse.business.table.attribute.MxAttributeComposite;
import org.mxeclipse.business.table.person.MxPersonComposite;
import org.mxeclipse.business.table.policy.MxPolicyComposite;
import org.mxeclipse.business.table.range.MxRangeComposite;
import org.mxeclipse.business.table.setting.MxWebSettingComposite;
import org.mxeclipse.business.table.state.MxStateComposite;
import org.mxeclipse.business.table.trigger.MxTriggerComposite;
import org.mxeclipse.business.table.type.MxDirectionsComposite;
import org.mxeclipse.business.table.user.MxUserComposite;
import org.mxeclipse.business.table.web.MxWebComposite;
import org.mxeclipse.business.table.webcolumn.MxWebColumnComposite;
import org.mxeclipse.business.tree.MxBusinessContentProvider;
import org.mxeclipse.business.tree.MxBusinessLabelProvider;
import org.mxeclipse.configure.table.MxTableColumnList;
import org.mxeclipse.dialogs.CreateNewAdminDialog;
import org.mxeclipse.dialogs.SearchMatrixAdminObjectsDialog;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.model.MxTreeAssignment;
import org.mxeclipse.model.MxTreeAssociation;
import org.mxeclipse.model.MxTreeAttribute;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeGroup;
import org.mxeclipse.model.MxTreeIndex;
import org.mxeclipse.model.MxTreePerson;
import org.mxeclipse.model.MxTreePolicy;
import org.mxeclipse.model.MxTreeProgram;
import org.mxeclipse.model.MxTreeRelationship;
import org.mxeclipse.model.MxTreeRole;
import org.mxeclipse.model.MxTreeState;
import org.mxeclipse.model.MxTreeType;
import org.mxeclipse.model.MxTreeWeb;
import org.mxeclipse.model.MxTreeWebColumn;
import org.mxeclipse.model.MxTreeWebCommand;
import org.mxeclipse.model.MxTreeWebMenu;
import org.mxeclipse.model.MxTreeWebNavigation;
import org.mxeclipse.model.MxTreeWebTable;
import org.mxeclipse.object.property.MxObjectProperyTable;

public class MxEclipseBusinessView extends ViewPart
  implements IModifyable
{
  public static final String VIEW_ID = MxEclipseBusinessView.class.getName();

  public Tree treObjects = null;
  private TreeViewer treeViewer = null;

  private SashForm top = null;
  private MxBusinessContentProvider treeContentProvider;
  private MxTableColumnList tableColumns;
  private TabFolder tabFolder = null;
  private MxObjectProperyTable basicInfoTable;
  private MxBusinessBasicComposite basicComposite;
  private MxBusinessBasicComposite additionalComposite;
  private MxRangeComposite rangeTable;
  private MxPolicyComposite policyTable;
  private MxTriggerComposite triggerTable;
  private MxAttributeComposite attributeTable;
  private MxDirectionsComposite directionsTable;
  private MxAssignmentComposite roleTable;
  private MxAssignmentComposite groupTable;
  private MxAssignmentComposite parentAssignmentTable;
  private MxAssignmentComposite childAssignmentTable;
  private MxPersonComposite personTable;
  private MxPersonRightsComposite rightsComposite;
  private MxStateRightsComposite stateRightsComposite;
  private MxStateComposite stateTable;
  private MxWebLinkComposite webLinkComposite;
  private MxWebComposite webItemsTable;
  private MxWebSettingComposite webSettingsTable;
  private MxUserComposite userComposite;
  private MxWebColumnComposite webColumnsTable;
  private MxWebColumnExpressionComposite webColumnExpressionComposite;
  private boolean appendToList;
  private Action actFind;
  private Action actExpand;
  private Action actSave;
  private Action actNew;
  private Action actDelete;
  private Action action3;
  private Action doubleClickAction;
  private TabFolder matrixDetailsTab;
  private Shell shell;
  private boolean modified;
  MxTreeBusiness selectedBusiness;
  MxTreeBusiness oldSelectedBusiness;
  private String[] types;
  private String namePattern;
  private boolean appendResults;

  public void createPartControl(Composite parent)
  {
    this.top = new SashForm(parent, 65792);

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
    this.treeContentProvider = new MxBusinessContentProvider();

    this.tableColumns = new MxTableColumnList(true);

    this.tableColumns.createColumns(this.treeViewer);
    this.treeViewer.setLabelProvider(new MxBusinessLabelProvider(this.tableColumns));

    this.treeViewer.setContentProvider(this.treeContentProvider);

    createTabFolder();

    this.treeViewer.getTree().addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        TreeItem item = (TreeItem)event.item;
        if (item != null) {
          MxEclipseBusinessView.this.actExpand.setEnabled(true);
          MxEclipseBusinessView.this.actDelete.setEnabled(true);
          MxEclipseBusinessView.this.nodeSelected(item);
        } else {
          MxEclipseBusinessView.this.selectedBusiness = null;
          MxEclipseBusinessView.this.actExpand.setEnabled(false);
          MxEclipseBusinessView.this.actSave.setEnabled(false);
          MxEclipseBusinessView.this.actDelete.setEnabled(false);
        }
        MxEclipseBusinessView.this.setModified(false);
      }
    });
    this.treeViewer.getTree().addMouseListener(new MouseAdapter() {
      public void mouseDoubleClick(MouseEvent e) {
        Point p = new Point(e.x, e.y);
        TreeItem treeItem = MxEclipseBusinessView.this.treObjects.getItem(p);
        MxEclipseBusinessView.this.expandItem(treeItem);
      }
    });
    makeActions();
    hookContextMenu();

    contributeToActionBars();
  }

  public void setFocus()
  {
    this.treeViewer.getControl().setFocus();
  }

  private void contributeToActionBars() {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void fillContextMenu(IMenuManager manager) {
    manager.add(this.actFind);
    manager.add(this.actExpand);
    manager.add(new Separator("additions"));
    manager.add(this.actSave);
    manager.add(this.actNew);
    manager.add(this.actDelete);

    manager.add(new Separator("additions"));
    manager.add(this.action3);
  }

  private void fillLocalPullDown(IMenuManager manager) {
    manager.add(this.actFind);
    manager.add(this.actExpand);
    manager.add(new Separator());
    manager.add(this.actSave);
    manager.add(this.actNew);
    manager.add(this.actDelete);
    manager.add(new Separator());
    manager.add(this.action3);
  }

  private void fillLocalToolBar(IToolBarManager manager) {
    manager.add(this.actFind);
    manager.add(this.actExpand);
    manager.add(new Separator());
    manager.add(this.actSave);
    manager.add(this.actNew);
    manager.add(this.actDelete);
    manager.add(new Separator());
    manager.add(this.action3);
  }

  private void hookContextMenu() {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(IMenuManager manager) {
        MxEclipseBusinessView.this.fillContextMenu(manager);
      }
    });
    Menu menu = menuMgr.createContextMenu(this.treeViewer.getControl());
    this.treeViewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, this.treeViewer);
  }

  private void hookDoubleClickAction() {
    this.treeViewer.addDoubleClickListener(new IDoubleClickListener()
    {
      public void doubleClick(DoubleClickEvent event) {
        MxEclipseBusinessView.this.doubleClickAction.run();
      }
    });
  }

  public void findAdminObjects(String type, String namePattern, boolean appendObjects)
    throws Exception
  {
    try
    {
      Context ctx = MxEclipsePlugin.getDefault().getContext();
      if ((ctx != null) && (ctx.isConnected())) {
        List resultList = SearchMatrixAdminObjectsDialog.findAdminObjects(type, namePattern);

        MxTreeBusiness root = (appendObjects) && (this.treeViewer.getInput() != null) ? (MxTreeBusiness)this.treeViewer.getInput() : new MxTreeBusiness();

        int lastFound = -1;
        for (int i = 0; i < resultList.size(); i++) {
          lastFound = -1;
          MxTreeBusiness child = (MxTreeBusiness)resultList.get(i);
          boolean alreadyChild = false;
          int existingIndex = 0;
          for (MxTreeBusiness rootChildren : root.getChildren(false)) {
            if ((rootChildren.getType().equals(child.getType())) && (rootChildren.getName().equals(child.getName()))) {
              alreadyChild = true;
              break;
            }
            existingIndex++;
          }

          if (!alreadyChild) {
            child.setContentProvider(this.treeContentProvider);
            root.addChild(child);
          } else {
            lastFound = existingIndex;
          }
        }
        if (!appendObjects) {
          clearAll();
        }
        this.treeViewer.setInput(root);

        if (resultList.size() > 0) {
          nodeSelected(this.treObjects.getItem(lastFound < 0 ? this.treObjects.getItemCount() - 1 : lastFound));

          this.actExpand.setEnabled(true);
          this.actDelete.setEnabled(true);

          this.treObjects.setSelection(this.treObjects.getItem(lastFound < 0 ? this.treObjects.getItemCount() - 1 : lastFound));
          this.tabFolder.setSelection(0);
        } else {
          this.actExpand.setEnabled(false);
          this.actDelete.setEnabled(false);
        }
      }
      else {
        MessageDialog.openInformation(this.top.getShell(), 
          "Search Matrix Admin Objects", 
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
      this.actFind = new Action() {
        public void run() {
          Context ctx = MxEclipsePlugin.getDefault().getContext();
          if ((ctx != null) && (ctx.isConnected())) {
            SearchMatrixAdminObjectsDialog searchAdminTypeDlg = null;
            if (MxEclipseBusinessView.this.namePattern != null)
              searchAdminTypeDlg = new SearchMatrixAdminObjectsDialog(MxEclipseBusinessView.this.shell, MxEclipseBusinessView.this.types, MxEclipseBusinessView.this.namePattern, MxEclipseBusinessView.this.appendResults);
            else {
              searchAdminTypeDlg = new SearchMatrixAdminObjectsDialog(MxEclipseBusinessView.this.shell);
            }

            searchAdminTypeDlg.open();
            if (searchAdminTypeDlg.getReturnCode() == 0)
              try {
                MxEclipseBusinessView.this.appendToList = searchAdminTypeDlg.isAppendResults();
                List resultList = searchAdminTypeDlg.getResultList();
                MxTreeBusiness root = null;
                if (searchAdminTypeDlg.isAppendResults())
                  root = (MxTreeBusiness)MxEclipseBusinessView.this.treeViewer.getInput();
                else {
                  root = new MxTreeBusiness();
                }
                for (int i = 0; i < resultList.size(); i++) {
                  MxTreeBusiness child = (MxTreeBusiness)resultList.get(i);
                  boolean alreadyChild = false;
                  for (MxTreeBusiness rootChildren : root.getChildren(false)) {
                    if ((rootChildren.getType().equals(child.getType())) && (rootChildren.getName().equals(child.getName()))) {
                      alreadyChild = true;
                    }
                  }
                  if (!alreadyChild) {
                    child.setContentProvider(MxEclipseBusinessView.this.treeContentProvider);
                    root.addChild(child);
                  }
                }
                MxEclipseBusinessView.this.selectedBusiness = null;
                MxEclipseBusinessView.this.clearAll();
                MxEclipseBusinessView.this.treeViewer.setInput(root);
                MxEclipseBusinessView.this.actExpand.setEnabled(false);
                MxEclipseBusinessView.this.actDelete.setEnabled(false);

                MxEclipseBusinessView.this.types = searchAdminTypeDlg.getTypes();
                MxEclipseBusinessView.this.namePattern = searchAdminTypeDlg.getNamePattern();
                MxEclipseBusinessView.this.appendResults = searchAdminTypeDlg.isAppendResults();
              } catch (Exception ex) {
                Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
                ErrorDialog.openError(MxEclipseBusinessView.this.getViewSite().getShell(), 
                  "Error when performing search", 
                  ex.getMessage(), 
                  status);
              }
          }
          else {
            MessageDialog.openInformation(MxEclipseBusinessView.this.shell, 
              "Search Matrix Admin Objects", 
              "No User connected to Matrix");
          }
        }
      };
      this.actFind.setText("Find Admin Objects");
      this.actFind.setToolTipText("Find Admin Objects");
      this.actFind.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("find.gif"));
      IHandlerService handlerService = (IHandlerService)getSite().getService(IHandlerService.class);
      IHandler myFind = new AbstractHandler() {
        public Object execute(ExecutionEvent event) throws ExecutionException {
          MxEclipseBusinessView.this.actFind.run();
          return null;
        }
      };
      handlerService.activateHandler("org.eclipse.ui.edit.findReplace", myFind);

      this.actExpand = new Action() {
        public void run() {
          TreeItem[] curItems = MxEclipseBusinessView.this.treObjects.getSelection();
          if (curItems.length > 0)
            MxEclipseBusinessView.this.expandItem(curItems[0]);
        }
      };
      this.actExpand.setText("Expand Relationships");
      this.actExpand.setToolTipText("Expands all the relationships of the currently selected node");
      this.actExpand.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("expand.gif"));
      this.actExpand.setEnabled(false);

      this.actSave = new Action() {
        public void run() {
          Context ctx = MxEclipsePlugin.getDefault().getContext();
          if ((ctx != null) && (ctx.isConnected()))
            MxEclipseBusinessView.this.saveAll();
          else
            MessageDialog.openInformation(MxEclipseBusinessView.this.top.getShell(), 
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
          MxEclipseBusinessView.this.actSave.run();
          return null;
        }
      };
      handlerService.activateHandler("org.eclipse.ui.file.save", mySave);

      this.actNew = new Action() {
        public void run() {
          Context ctx = MxEclipsePlugin.getDefault().getContext();
          if ((ctx != null) && (ctx.isConnected())) {
            CreateNewAdminDialog createNewAdminDlg = new CreateNewAdminDialog(MxEclipseBusinessView.this.shell);

            createNewAdminDlg.open();
            if (createNewAdminDlg.getReturnCode() == 0)
              try {
                MxTreeBusiness newObject = MxTreeBusiness.create(createNewAdminDlg.getAdminType(), createNewAdminDlg.getAdminName(), createNewAdminDlg.getAttributeType());

                MxTreeBusiness root = (MxTreeBusiness)MxEclipseBusinessView.this.treeViewer.getInput();
                if (root == null) {
                  root = new MxTreeBusiness();
                }
                root.addChild(newObject);
                newObject.setContentProvider(MxEclipseBusinessView.this.treeContentProvider);
                MxEclipseBusinessView.this.treeViewer.add(root, newObject);
                MxEclipseBusinessView.this.treeViewer.setInput(root);

                for (int i = 0; i < MxEclipseBusinessView.this.treObjects.getItemCount(); i++) {
                  if (MxEclipseBusinessView.this.treObjects.getItem(i).getData().equals(newObject)) {
                    MxEclipseBusinessView.this.treObjects.setSelection(MxEclipseBusinessView.this.treObjects.getItem(i));
                    MxEclipseBusinessView.this.nodeSelected(MxEclipseBusinessView.this.treObjects.getItem(i));
                  }
                }
                MxEclipseBusinessView.this.tabFolder.setSelection(0);
                MxEclipseBusinessView.this.actExpand.setEnabled(true);
                MxEclipseBusinessView.this.actDelete.setEnabled(true);
              } catch (Exception ex) {
                Status status = new Status(4, "MxEclipse", 0, ex.getMessage(), ex);
                ErrorDialog.openError(MxEclipseBusinessView.this.getViewSite().getShell(), 
                  "Error when performing search", 
                  ex.getMessage(), 
                  status);
              }
          }
          else {
            MessageDialog.openInformation(MxEclipseBusinessView.this.shell, 
              "Create New Admin Object", 
              "No User connected to Matrix");
          }
        }
      };
      this.actNew.setText("Create New Admin Object");
      this.actNew.setToolTipText("Creates a new admin object");
      this.actNew.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("create.gif"));

      this.actDelete = new Action() {
        public void run() {
          if (MessageDialog.openConfirm(MxEclipseBusinessView.this.top.getShell(), "Delete Admin Object", "Are you sure that you want to delete admin object(s)?")) {
            TreeItem[] curItems = MxEclipseBusinessView.this.treObjects.getSelection();
            if (curItems.length > 0) {
              Context ctx = MxEclipsePlugin.getDefault().getContext();
              if ((ctx != null) && (ctx.isConnected()))
                for (int i = 0; i < curItems.length; i++) {
                  TreeItem treeItem = curItems[i];
                  if (treeItem != null) {
                    MxTreeBusiness selectedObject = (MxTreeBusiness)treeItem.getData();
                    try {
                      selectedObject.delete();

                      treeItem.dispose();
                    } catch (Exception e) {
                      MessageDialog.openInformation(MxEclipseBusinessView.this.top.getShell(), "Delete Admin Object", "Error when deleting objects " + e.getMessage());
                    }
                  }
                }
              else
                MessageDialog.openInformation(MxEclipseBusinessView.this.top.getShell(), "Delete Admin Object", "No User connected to Matrix");
            }
          }
        }
      };
      this.actDelete.setText("Delete Object");
      this.actDelete.setToolTipText("Delete currently selected node");
      this.actDelete.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("delete.gif"));
      this.actDelete.setEnabled(false);

      this.action3 = new Action()
      {
        public void run() {
          MxEclipseBusinessView.this.treeViewer.getTree().removeAll();
          MxEclipseBusinessView.this.actExpand.setEnabled(false);
          MxEclipseBusinessView.this.actDelete.setEnabled(false);
        }
      };
      this.action3.setText("Clear");
      this.action3.setToolTipText("Clear");
      this.action3.setImageDescriptor(MxEclipsePlugin.getImageDescriptor("eraser.gif"));

      this.doubleClickAction = new Action() {
        public void run() {
          System.out.println("in double click");
        }
      };
    }
    catch (Exception e) {
      Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
      ErrorDialog.openError(getViewSite().getShell(), 
        "Unable to load Images", 
        "Error Occurred while loading the images in the toolbar", 
        status);
    }
  }

  protected void clearProperties()
  {
    if (this.tabFolder != null) {
      TabItem[] oldTabItems = this.tabFolder.getItems();
      for (int i = oldTabItems.length - 1; i >= 0; i--) {
        oldTabItems[i].dispose();
      }
      this.tabFolder.redraw();
    }
  }

  public void clearAll() {
    this.treeViewer.getTree().removeAll();
    clearProperties();
  }

  private void createTabFolder()
  {
    if ((this.selectedBusiness != null) && (this.oldSelectedBusiness != null) && (this.selectedBusiness.getType().equals(this.oldSelectedBusiness.getType())))
    {
      return;
    }

    clearProperties();

    if (this.tabFolder == null) {
      this.tabFolder = new TabFolder(this.top, 131072);
    }

    TabItem item1 = new TabItem(this.tabFolder, 0);
    item1.setText("Basics");
    if ((this.selectedBusiness != null) && ((this.selectedBusiness instanceof MxTreeAttribute)))
    {
      MxAttributeBasicComposite composite = new MxAttributeBasicComposite(this.tabFolder, 0, this, this.selectedBusiness);
      this.basicComposite = composite;
      item1.setControl(composite);
    } else if ((this.selectedBusiness instanceof MxTreeType)) {
      MxTypeBasicComposite composite = new MxTypeBasicComposite(this.tabFolder, 0, this, this.selectedBusiness);
      this.basicComposite = composite;
      item1.setControl(composite);
    } else if ((this.selectedBusiness instanceof MxTreePolicy)) {
      MxPolicyBasicComposite composite = new MxPolicyBasicComposite(this.tabFolder, 0, this, this.selectedBusiness);
      this.basicComposite = composite;
      item1.setControl(composite);
    } else if ((this.selectedBusiness instanceof MxTreeState)) {
      MxStateBasicComposite composite = new MxStateBasicComposite(this.tabFolder, 0, this, this.selectedBusiness);
      this.basicComposite = composite;
      item1.setControl(composite);
    } else if ((this.selectedBusiness instanceof MxTreeRelationship)) {
      MxRelationshipBasicComposite composite = new MxRelationshipBasicComposite(this.tabFolder, 0, this, this.selectedBusiness);
      this.basicComposite = composite;
      item1.setControl(composite);
    } else if ((this.selectedBusiness instanceof MxTreePerson)) {
      MxPersonBasicComposite composite = new MxPersonBasicComposite(this.tabFolder, 0, this, this.selectedBusiness);
      this.basicComposite = composite;
      item1.setControl(composite);
    } else if ((this.selectedBusiness instanceof MxTreeAssignment)) {
      MxAssignmentBasicComposite composite = new MxAssignmentBasicComposite(this.tabFolder, 0, this, this.selectedBusiness);
      this.basicComposite = composite;
      item1.setControl(composite);
    } else if ((this.selectedBusiness instanceof MxTreeAssociation)) {
      MxAssociationBasicComposite composite = new MxAssociationBasicComposite(this.tabFolder, 0, this, this.selectedBusiness);
      this.basicComposite = composite;
      item1.setControl(composite);
    } else if ((this.selectedBusiness instanceof MxTreeProgram)) {
      MxProgramBasicComposite composite = new MxProgramBasicComposite(this.tabFolder, 0, this, this.selectedBusiness);
      this.basicComposite = composite;
      item1.setControl(composite);
    } else if ((this.selectedBusiness instanceof MxTreeIndex)) {
      MxIndexBasicComposite composite = new MxIndexBasicComposite(this.tabFolder, 0, this, this.selectedBusiness);
      this.basicComposite = composite;
      item1.setControl(composite);
    } else if ((this.selectedBusiness instanceof MxTreeWebNavigation)) {
      MxWebBasicComposite composite = new MxWebBasicComposite(this.tabFolder, 0, this, this.selectedBusiness);
      this.basicComposite = composite;
      item1.setControl(composite);
    } else if ((this.selectedBusiness instanceof MxTreeWebTable)) {
      MxWebTableBasicComposite composite = new MxWebTableBasicComposite(this.tabFolder, 0, this, this.selectedBusiness);
      this.basicComposite = composite;
      item1.setControl(composite);
    } else if ((this.selectedBusiness instanceof MxTreeWebColumn)) {
      MxWebColumnBasicComposite composite = new MxWebColumnBasicComposite(this.tabFolder, 0, this, this.selectedBusiness);
      this.basicComposite = composite;
      item1.setControl(composite);
    } else if (this.selectedBusiness != null) {
      MxDefaultBasicComposite composite = new MxDefaultBasicComposite(this.tabFolder, 0, this, this.selectedBusiness);
      this.basicComposite = composite;
      item1.setControl(composite);
    } else {
      this.basicInfoTable = new MxObjectProperyTable(this.tabFolder, "Basic", this);
      item1.setControl(this.basicInfoTable.getControl());
    }

    if (this.selectedBusiness != null)
      if ((this.selectedBusiness instanceof MxTreeAttribute)) {
        TabItem itmRanges = new TabItem(this.tabFolder, 0);
        itmRanges.setText("Ranges");
        this.rangeTable = new MxRangeComposite(this.tabFolder, 0, null, this);
        itmRanges.setControl(this.rangeTable);

        TabItem itmTriggers = new TabItem(this.tabFolder, 0);
        itmTriggers.setText("Triggers");
        this.triggerTable = new MxTriggerComposite(this.tabFolder, 0, null, this);
        itmTriggers.setControl(this.triggerTable);
      } else if ((this.selectedBusiness instanceof MxTreeType)) {
        TabItem itmAttributes = new TabItem(this.tabFolder, 0);
        itmAttributes.setText("Attributes");
        this.attributeTable = new MxAttributeComposite(this.tabFolder, 0, null, this);
        itmAttributes.setControl(this.attributeTable);

        TabItem itmPolicies = new TabItem(this.tabFolder, 0);
        itmPolicies.setText("Policies");
        this.policyTable = new MxPolicyComposite(this.tabFolder, 0, null, this);
        itmPolicies.setControl(this.policyTable);

        TabItem itmTriggers = new TabItem(this.tabFolder, 0);
        itmTriggers.setText("Triggers");
        this.triggerTable = new MxTriggerComposite(this.tabFolder, 0, null, this);
        itmTriggers.setControl(this.triggerTable);
      } else if ((this.selectedBusiness instanceof MxTreeRelationship)) {
        TabItem itmAttributes = new TabItem(this.tabFolder, 0);
        itmAttributes.setText("Attributes");
        this.attributeTable = new MxAttributeComposite(this.tabFolder, 0, null, this);
        itmAttributes.setControl(this.attributeTable);

        TabItem itmTypes = new TabItem(this.tabFolder, 0);
        itmTypes.setText("Allowed Types");
        this.directionsTable = new MxDirectionsComposite(this.tabFolder, 0, null, this);
        itmTypes.setControl(this.directionsTable);

        TabItem itmTriggers = new TabItem(this.tabFolder, 0);
        itmTriggers.setText("Triggers");
        this.triggerTable = new MxTriggerComposite(this.tabFolder, 0, null, this);
        itmTriggers.setControl(this.triggerTable);
      } else if ((this.selectedBusiness instanceof MxTreePolicy)) {
        TabItem itmStates = new TabItem(this.tabFolder, 0);
        itmStates.setText("States");
        this.stateTable = new MxStateComposite(this.tabFolder, 0, null, this);
        itmStates.setControl(this.stateTable);
      } else if ((this.selectedBusiness instanceof MxTreeState)) {
        TabItem itmUserRights = new TabItem(this.tabFolder, 0);
        itmUserRights.setText("User Rights");
        this.stateRightsComposite = new MxStateRightsComposite(this.tabFolder, 0, this, this.selectedBusiness);
        itmUserRights.setControl(this.stateRightsComposite);

        TabItem itmTriggers = new TabItem(this.tabFolder, 0);
        itmTriggers.setText("Triggers");
        this.triggerTable = new MxTriggerComposite(this.tabFolder, 0, null, this);
        itmTriggers.setControl(this.triggerTable);
      } else if ((this.selectedBusiness instanceof MxTreePerson)) {
        TabItem itmAddress = new TabItem(this.tabFolder, 0);
        itmAddress.setText("Additional");
        this.additionalComposite = new MxPersonAddressComposite(this.tabFolder, 0, this, this.selectedBusiness);
        itmAddress.setControl(this.additionalComposite);

        TabItem itmRoles = new TabItem(this.tabFolder, 0);
        itmRoles.setText("Roles");
        this.roleTable = new MxAssignmentComposite(this.tabFolder, 0, this, this.selectedBusiness, "Role", true);
        itmRoles.setControl(this.roleTable);

        TabItem itmGroups = new TabItem(this.tabFolder, 0);
        itmGroups.setText("Groups");
        this.groupTable = new MxAssignmentComposite(this.tabFolder, 0, this, this.selectedBusiness, "Group", true);
        itmGroups.setControl(this.groupTable);

        TabItem itmRights = new TabItem(this.tabFolder, 0);
        itmRights.setText("Rights");
        this.rightsComposite = new MxPersonRightsComposite(this.tabFolder, 0, this, this.selectedBusiness);
        itmRights.setControl(this.rightsComposite);
      } else if ((this.selectedBusiness instanceof MxTreeRole)) {
        TabItem itmParentRoles = new TabItem(this.tabFolder, 0);
        itmParentRoles.setText("Parents");
        this.parentAssignmentTable = new MxAssignmentComposite(this.tabFolder, 0, this, this.selectedBusiness, "Role", true);
        itmParentRoles.setControl(this.parentAssignmentTable);

        TabItem itmChildRoles = new TabItem(this.tabFolder, 0);
        itmChildRoles.setText("Children");
        this.childAssignmentTable = new MxAssignmentComposite(this.tabFolder, 0, this, this.selectedBusiness, "Role", false);
        itmChildRoles.setControl(this.childAssignmentTable);

        TabItem itmPersons = new TabItem(this.tabFolder, 0);
        itmPersons.setText("Persons");
        this.personTable = new MxPersonComposite(this.tabFolder, 0, null, this);
        itmPersons.setControl(this.personTable);
      } else if ((this.selectedBusiness instanceof MxTreeGroup)) {
        TabItem itmParentGroups = new TabItem(this.tabFolder, 0);
        itmParentGroups.setText("Parents");
        this.parentAssignmentTable = new MxAssignmentComposite(this.tabFolder, 0, this, this.selectedBusiness, "Group", true);
        itmParentGroups.setControl(this.parentAssignmentTable);

        TabItem itmChildGroups = new TabItem(this.tabFolder, 0);
        itmChildGroups.setText("Children");
        this.childAssignmentTable = new MxAssignmentComposite(this.tabFolder, 0, this, this.selectedBusiness, "Group", false);
        itmChildGroups.setControl(this.childAssignmentTable);

        TabItem itmPersons = new TabItem(this.tabFolder, 0);
        itmPersons.setText("Persons");
        this.personTable = new MxPersonComposite(this.tabFolder, 0, null, this);
        itmPersons.setControl(this.personTable);
      } else if ((this.selectedBusiness instanceof MxTreeProgram)) {
        TabItem itmAddress = new TabItem(this.tabFolder, 0);
        itmAddress.setText("Code");
        this.additionalComposite = new MxProgramCodeComposite(this.tabFolder, 0, this, this.selectedBusiness);
        itmAddress.setControl(this.additionalComposite);
      } else if ((this.selectedBusiness instanceof MxTreeIndex)) {
        TabItem itmAttributes = new TabItem(this.tabFolder, 0);
        itmAttributes.setText("Attributes");
        this.attributeTable = new MxAttributeComposite(this.tabFolder, 0, this.selectedBusiness, this);
        itmAttributes.setControl(this.attributeTable);
      } else if ((this.selectedBusiness instanceof MxTreeWeb)) {
        if ((this.selectedBusiness instanceof MxTreeWebNavigation)) {
          TabItem itmLink = new TabItem(this.tabFolder, 0);
          itmLink.setText("Link");
          this.webLinkComposite = new MxWebLinkComposite(this.tabFolder, 0, this, this.selectedBusiness);
          itmLink.setControl(this.webLinkComposite);

          if ((this.selectedBusiness instanceof MxTreeWebMenu)) {
            TabItem itmWebItems = new TabItem(this.tabFolder, 0);
            itmWebItems.setText("Items");
            this.webItemsTable = new MxWebComposite(this.tabFolder, 0, null, this);
            itmWebItems.setControl(this.webItemsTable);
          }
        } else if ((this.selectedBusiness instanceof MxTreeWebColumn)) {
          TabItem itmLink = new TabItem(this.tabFolder, 0);
          itmLink.setText("Expression");
          this.webColumnExpressionComposite = new MxWebColumnExpressionComposite(this.tabFolder, 0, this, this.selectedBusiness);
          itmLink.setControl(this.webColumnExpressionComposite);
        }

        TabItem itmSettings = new TabItem(this.tabFolder, 0);
        itmSettings.setText("Settings");
        this.webSettingsTable = new MxWebSettingComposite(this.tabFolder, 0, this.selectedBusiness, this);
        itmSettings.setControl(this.webSettingsTable);

        if ((this.selectedBusiness instanceof MxTreeWebCommand)) {
          TabItem itmUserRights = new TabItem(this.tabFolder, 0);
          itmUserRights.setText("User Rights");
          this.userComposite = new MxUserComposite(this.tabFolder, 0, this.selectedBusiness, this);
          itmUserRights.setControl(this.userComposite);
        }
      }
      else if ((this.selectedBusiness instanceof MxTreeWebTable)) {
        TabItem itmColumns = new TabItem(this.tabFolder, 0);
        itmColumns.setText("Columns");
        this.webColumnsTable = new MxWebColumnComposite(this.tabFolder, 0, null, this);
        itmColumns.setControl(this.webColumnsTable);
      }
  }

  public boolean isModified()
  {
    return this.modified;
  }

  public void setModified(boolean modified) {
    this.modified = modified;
    this.actSave.setEnabled(modified);
    if ((modified) && (!getPartName().substring(0, 1).equals("*"))) {
      setPartName("*" + getPartName());
    }
    if ((!modified) && (getPartName().substring(0, 1).equals("*")))
      setPartName(getPartName().substring(1));
  }

  public void expandItem(TreeItem treeItem)
  {
    if (treeItem != null) {
      MxTreeBusiness selectedObject = (MxTreeBusiness)treeItem.getData();
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

  private void saveAll()
  {
    this.basicComposite.storeData();
    if ((this.selectedBusiness instanceof MxTreeRelationship)) {
      this.directionsTable.storeDirectionInfo();
    } else if ((this.selectedBusiness instanceof MxTreeState)) {
      this.stateRightsComposite.storeData();
    } else if ((this.selectedBusiness instanceof MxTreePerson)) {
      this.additionalComposite.storeData();
      this.rightsComposite.storeData();
    } else if ((this.selectedBusiness instanceof MxTreeProgram)) {
      this.additionalComposite.storeData();
    } else if ((this.selectedBusiness instanceof MxTreeWebNavigation)) {
      this.webLinkComposite.storeData();
    } else if ((this.selectedBusiness instanceof MxTreeWebColumn)) {
      this.webColumnExpressionComposite.storeData();
    }
    if ((!(this.selectedBusiness instanceof MxTreeWebTable)) && (!(this.selectedBusiness instanceof MxTreeWebColumn))) {
      this.selectedBusiness.save();
    }
    setModified(false);
    this.treeViewer.refresh(this.selectedBusiness, true);
    for (TreeItem itm : this.treObjects.getSelection())
      nodeSelected(itm);
  }

  public void nodeSelected(TreeItem item)
  {
    try
    {
      this.oldSelectedBusiness = this.selectedBusiness;
      this.selectedBusiness = ((MxTreeBusiness)item.getData());
      this.selectedBusiness.refresh();
      createTabFolder();

      if ((this.selectedBusiness instanceof MxTreeAttribute)) {
        this.rangeTable.setData((MxTreeAttribute)item.getData());
        this.triggerTable.setData((MxTreeAttribute)item.getData());
      } else if ((this.selectedBusiness instanceof MxTreeType)) {
        this.policyTable.setData((MxTreeType)item.getData());
        this.attributeTable.setData((MxTreeType)item.getData());
        this.triggerTable.setData((MxTreeType)item.getData());
      } else if ((this.selectedBusiness instanceof MxTreeRelationship)) {
        this.attributeTable.setData((MxTreeRelationship)item.getData());
        this.directionsTable.setData((MxTreeRelationship)item.getData());
        this.directionsTable.initializeDirectionInfo((MxTreeRelationship)item.getData());
        this.triggerTable.setData((MxTreeRelationship)item.getData());
      } else if ((this.selectedBusiness instanceof MxTreePolicy)) {
        this.stateTable.setData((MxTreePolicy)item.getData());
      } else if ((this.selectedBusiness instanceof MxTreeState)) {
        this.stateRightsComposite.initializeContent((MxTreeState)item.getData());
        this.triggerTable.setData((MxTreeState)item.getData());
      } else if ((this.selectedBusiness instanceof MxTreePerson)) {
        this.additionalComposite.initializeContent(this.selectedBusiness);
        this.groupTable.setData((MxTreePerson)item.getData());
        this.roleTable.setData((MxTreePerson)item.getData());
        this.rightsComposite.initializeContent((MxTreePerson)item.getData());
      } else if ((this.selectedBusiness instanceof MxTreeRole)) {
        this.parentAssignmentTable.setData((MxTreeRole)item.getData());
        this.childAssignmentTable.setData((MxTreeRole)item.getData());
        this.personTable.setData((MxTreeRole)item.getData());
      } else if ((this.selectedBusiness instanceof MxTreeGroup)) {
        this.parentAssignmentTable.setData((MxTreeGroup)item.getData());
        this.childAssignmentTable.setData((MxTreeGroup)item.getData());
        this.personTable.setData((MxTreeGroup)item.getData());
      } else if ((this.selectedBusiness instanceof MxTreeProgram)) {
        this.additionalComposite.initializeContent(this.selectedBusiness);
      } else if ((this.selectedBusiness instanceof MxTreeIndex)) {
        this.attributeTable.setData((MxTreeIndex)item.getData());
      } else if ((this.selectedBusiness instanceof MxTreeWeb)) {
        if ((this.selectedBusiness instanceof MxTreeWebNavigation)) {
          this.webLinkComposite.initializeContent((MxTreeWebNavigation)item.getData());
          if ((this.selectedBusiness instanceof MxTreeWebMenu)) {
            this.webItemsTable.setData((MxTreeWebMenu)item.getData());
          }
        }
        if ((this.selectedBusiness instanceof MxTreeWebCommand)) {
          this.userComposite.setData((MxTreeWebNavigation)item.getData());
        }
        if ((this.selectedBusiness instanceof MxTreeWebColumn)) {
          this.webColumnExpressionComposite.initializeContent((MxTreeWebColumn)item.getData());
        }
        this.webSettingsTable.setData((MxTreeWeb)item.getData());
      } else if ((this.selectedBusiness instanceof MxTreeWebTable)) {
        this.webColumnsTable.setData((MxTreeWebTable)item.getData());
      }

      this.basicComposite.initializeContent(this.selectedBusiness);
      setModified(false);
    }
    catch (Exception e)
    {
      Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
      ErrorDialog.openError(getViewSite().getShell(), 
        "Error when trying to get fresh data from Matrix", 
        "Error when trying to get fresh data from Matrix", 
        status);
    }
  }

  class NameSorter extends ViewerSorter
  {
    NameSorter()
    {
    }
  }
}