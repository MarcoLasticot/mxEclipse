package org.mxeclipse.dialogs;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class Sample extends Composite
{
  private Tree tree = null;

  public Sample(Composite parent, int style) {
    super(parent, style);
    initialize();
  }

  private void initialize() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    GridData gridData = new GridData();
    gridData.verticalSpan = 2;
    gridData.horizontalSpan = 2;
    this.tree = new Tree(this, 2);
    this.tree.setHeaderVisible(true);
    this.tree.setLayoutData(gridData);
    this.tree.setLinesVisible(true);
    TreeColumn treeColumn = new TreeColumn(this.tree, 0);
    treeColumn.setWidth(60);
    treeColumn.setText("jjj");
    TreeColumn treeColumn1 = new TreeColumn(this.tree, 0);
    treeColumn1.setWidth(60);
    setLayout(gridLayout);
    setSize(new Point(300, 200));
  }
}