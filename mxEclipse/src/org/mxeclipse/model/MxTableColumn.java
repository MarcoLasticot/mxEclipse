package org.mxeclipse.model;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.mxeclipse.business.tree.MxBusinessSorter;
import org.mxeclipse.object.tree.MxTreeObjectSorter;
import org.mxeclipse.utils.IXMLPersistable;
import org.mxeclipse.utils.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MxTableColumn
  implements IXMLPersistable
{
  public static String FIELD_NAME = "Name";
  public static String FIELD_TYPE = "Type";
  public static String FIELD_VISIBLE = "Visible";
  public static String FIELD_ON_RELATIONSHIP = "OnRelationship";
  public static String FIELD_WIDTH = "Width";

  public static String TYPE_BASIC = "Basic";
  public static String TYPE_ATTRIBUTE = "Attribute";

  public static String BASIC_TYPE = "Type";
  public static String BASIC_NAME = "Name";
  public static String BASIC_REVISION = "R";
  public static String BASIC_RELATIONSHIP = "Rel";
  public static String BASIC_STATE = "State";
  private String type;
  private boolean visible;
  private boolean onRelationship;
  private String name;
  private int width;
  private boolean inBusiness;

  public MxTableColumn(Node node, boolean inBusiness)
  {
    this.inBusiness = inBusiness;
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      String name = child.getNodeName();
      if (name.equals(FIELD_NAME))
        this.name = child.getFirstChild().getNodeValue();
      else if (name.equals(FIELD_TYPE))
        this.type = child.getFirstChild().getNodeValue();
      else if (name.equals(FIELD_VISIBLE))
        this.visible = new Boolean(child.getFirstChild().getNodeValue()).booleanValue();
      else if (name.equals(FIELD_ON_RELATIONSHIP))
        this.onRelationship = new Boolean(child.getFirstChild().getNodeValue()).booleanValue();
      else if (name.equals(FIELD_WIDTH))
        this.width = Integer.parseInt(child.getFirstChild().getNodeValue());
    }
  }

  public MxTableColumn(String name, String type, boolean visible, boolean onRelationship, int width, boolean inBusiness)
  {
    this.name = name;
    this.type = type;
    this.visible = visible;
    this.onRelationship = onRelationship;
    this.width = width;
    this.inBusiness = inBusiness;
  }

  public String getType() {
    return this.type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public boolean isVisible() {
    return this.visible;
  }
  public void setVisible(boolean visible) {
    this.visible = visible;
  }
  public boolean isOnRelationship() {
    return this.onRelationship;
  }
  public void setOnRelationship(boolean onRelationship) {
    this.onRelationship = onRelationship;
  }
  public String getName() {
    return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public void setWidth(int width) {
    this.width = width;
  }
  public int getWidth() {
    if (this.width == 0) {
      if (this.name.equals(BASIC_NAME))
        return 120;
      if (this.name.equals(BASIC_TYPE))
        return 130;
      if (this.name.equals(BASIC_REVISION))
        return 20;
      if (this.name.equals(BASIC_RELATIONSHIP))
        return 120;
      if (this.name.equals(BASIC_STATE)) {
        return 80;
      }
      return 100;
    }

    return this.width;
  }

  public void createColumn(final TreeViewer treeViewer)
  {
    Tree tree = treeViewer.getTree();
    TreeColumn treeColumn = new TreeColumn(tree, 0);
    treeColumn.setText(getName());
    treeColumn.setWidth(getWidth());
    treeColumn.setMoveable(true);
    treeColumn.addSelectionListener(new SelectionAdapter() {
    	 public void widgetSelected(SelectionEvent e)
         {
             if(!inBusiness)
                 treeViewer.setSorter(new MxTreeObjectSorter(name));
             else
                 treeViewer.setSorter(new MxBusinessSorter(name));
         }
    });
  }

  public void toXML(Document doc, Node node)
  {
    Element xmlColumn = doc.createElement("Column");
    xmlColumn.appendChild(XmlUtils.createElementWithText(doc, FIELD_NAME, getName()));
    xmlColumn.appendChild(XmlUtils.createElementWithText(doc, FIELD_TYPE, getType()));
    xmlColumn.appendChild(XmlUtils.createElementWithText(doc, FIELD_VISIBLE, (new StringBuilder()).append(isVisible()).toString()));
    xmlColumn.appendChild(XmlUtils.createElementWithText(doc, FIELD_ON_RELATIONSHIP, (new StringBuilder()).append(isOnRelationship()).toString()));
    xmlColumn.appendChild(XmlUtils.createElementWithText(doc, FIELD_WIDTH, (new StringBuilder()).append(getWidth()).toString()));
    node.appendChild(xmlColumn);
  }
}