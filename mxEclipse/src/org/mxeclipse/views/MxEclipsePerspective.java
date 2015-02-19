package org.mxeclipse.views;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class MxEclipsePerspective
  implements IPerspectiveFactory
{
  public static final String ID = MxEclipsePerspective.class.getName();

  public void createInitialLayout(IPageLayout layout)
  {
    layout.addNewWizardShortcut("org.polarion.team.svn.ui.wizard.NewRepositoryLocationWizard");

    layout.addShowViewShortcut(MxEclipseBusinessView.VIEW_ID);
    layout.addShowViewShortcut(MxEclipseObjectView.VIEW_ID);
    layout.addShowViewShortcut(MxEclipseMqlView.VIEW_ID);

    layout.addShowViewShortcut("org.eclipse.jdt.ui.PackageExplorer");

    layout.addPerspectiveShortcut(ID);
    layout.addPerspectiveShortcut("org.eclipse.jdt.ui.JavaPerspective");
    layout.addPerspectiveShortcut("org.eclipse.ui.resourcePerspective");

    String editorArea = layout.getEditorArea();
    layout.setEditorAreaVisible(true);

    IFolderLayout left = layout.createFolder("left", 1, 0.17F, editorArea);
    left.addView("org.eclipse.jdt.ui.PackageExplorer");

    IFolderLayout top = layout.createFolder("top", 3, 0.3F, editorArea);
    top.addView(MxEclipseObjectView.VIEW_ID);

    IFolderLayout bottom = layout.createFolder("bottom", 4, 0.65F, editorArea);
    bottom.addView(MxEclipseBusinessView.VIEW_ID);

    IFolderLayout right = layout.createFolder("right", 2, 0.7F, editorArea);
    right.addView(MxEclipseMqlView.VIEW_ID);
  }
}