package org.mxeclipse.model;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.object.property.IMxObjectPropertyViewer;
import org.mxeclipse.object.property.MxObjectProperty;
import org.mxeclipse.object.tree.MxTreeContentProvider;
import org.mxeclipse.utils.MatrixFinder;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeDomainObject
  implements Comparable
{
  private DomainObject domainObject;
  private String id;
  private String type;
  private String name;
  private String revision;
  private String policy;
  private String current;
  private DomainRelationship domainRelationship;
  private String relId;
  private String relType;
  private String relName;
  private String relFromId;
  private String relFromName;
  private String relToName;
  private MxTreeDomainObject parent;
  private ArrayList<MxTreeDomainObject> children;
  private MxTreeContentProvider contentProvider;
  private Set changeListeners = new HashSet();

  public MxTreeDomainObject()
  {
    this.children = new ArrayList();
  }

  public void addChild(MxTreeDomainObject child) {
    this.children.add(child);
    child.setParent(this);
  }

  public MxTreeDomainObject(DomainObject domainObject) throws MxEclipseException, MatrixException {
    this(domainObject.getObjectId());
  }

  public MxTreeDomainObject(String objectId, String type, String name, String revision, String policy, String current) {
    this.id = objectId;
    this.type = type;
    this.name = name;
    this.revision = revision;
    this.policy = policy;
    this.current = current;
  }

  public MxTreeDomainObject(String objectId, String type, String name, String revision, String policy, String current, String relId, String relType, String relFromId, String relFromName, String relToName) {
    this(objectId, type, name, revision, policy, current);
    this.relId = relId;
    this.relName = relId;
    this.relType = relType;
    this.relFromId = relFromId;
    this.relFromName = relFromName;
    this.relToName = relToName;
  }

  public MxTreeDomainObject(String objectId) throws MxEclipseException, MatrixException {
    if (objectId == null) return;
    Map mapInfo = MatrixFinder.getInfo(objectId, new String[] { "id", "type", "name", "revision", "policy", "current" });
    this.id = ((String)mapInfo.get("id"));
    this.type = ((String)mapInfo.get("type"));
    this.name = ((String)mapInfo.get("name"));
    this.revision = ((String)mapInfo.get("revision"));
    this.policy = ((String)mapInfo.get("policy"));
    this.current = ((String)mapInfo.get("current"));
  }

  public MxTreeDomainObject(String objectId, String relId) throws MxEclipseException, MatrixException {
    this(objectId);
    Context context = getContext();
    this.domainRelationship = DomainRelationship.newInstance(context, relId);
    this.relId = relId;
    this.domainRelationship.open(context);
    try {
      this.relType = this.domainRelationship.getTypeName();
      this.relName = this.domainRelationship.getName();
      DomainObject from = DomainObject.newInstance(context, this.domainRelationship.getFrom());
      this.relFromName = from.getInfo(context, "name");
      DomainObject to = DomainObject.newInstance(context, this.domainRelationship.getTo());
      this.relToName = to.getInfo(context, "name");
    } finally {
      this.domainRelationship.close(context);
    }
  }

  protected String getAttributeClause(ArrayList<MxAttribute> attributes) {
    String attFilter = "";
    for (int i = 0; i < attributes.size(); i++) {
      MxAttribute mxAttribute = (MxAttribute)attributes.get(i);
      if (i > 0) attFilter = attFilter + " and ";
      attFilter = attFilter + "attribute[" + mxAttribute.getName() + "]" + " smatch '" + mxAttribute.getValue() + "'";
    }
    return attFilter;
  }

  public MxTreeDomainObject[] getChildren(boolean forceUpdate)
    throws MxEclipseException, MatrixException
  {
    if (forceUpdate) {
      this.children = null;
    }
    if (this.children == null) {
      String types = "*";
      String relTypes = "*";
      String objFilter = "";
      String relFilter = "";
      boolean filterFrom = true;
      boolean filterTo = true;
      if (this.contentProvider != null) {
        MxFilter filter = this.contentProvider.getFilter();
        if (filter != null) {
          types = filter.getTypes();
          if (types.equals("")) {
            types = "*";
          }
          relTypes = filter.getRelTypes();
          if (relTypes.equals("")) {
            relTypes = "*";
          }

          objFilter = getAttributeClause(filter.getAttributes());
          relFilter = getAttributeClause(filter.getRelAttributes());

          filterFrom = filter.isRelFrom();
          filterTo = filter.isRelTo();
        }
      }

      this.children = new ArrayList();

      boolean bFrom = false;
      for (int count = 0; count < 2; count++) {
        if (((!bFrom) && (filterFrom)) || ((bFrom) && (filterTo))) {
          List lstOutputObjects = MatrixFinder.getRelatedObjectsInfo(this.id, relTypes, types, new String[] { "id", "type", "name", "revision", "policy", "current" }, new String[] { "id[connection]", "type[connection]", "from.id", "from.name", "to.name" }, bFrom, 1, objFilter, relFilter);

          Iterator itOutputObjects = lstOutputObjects.iterator();
          while (itOutputObjects.hasNext()) {
            Map map = (Map)itOutputObjects.next();
            String id = (String)map.get("id");
            String relId = (String)map.get("id[connection]");
            MxTreeDomainObject child = new MxTreeDomainObject(id, (String)map.get("type"), (String)map.get("name"), (String)map.get("revision"), (String)map.get("policy"), (String)map.get("current"), 
              relId, (String)map.get("type[connection]"), (String)map.get("from.id"), (String)map.get("from.name"), (String)map.get("to.name"));
            child.setParent(this);
            child.setContentProvider(this.contentProvider);
            this.children.add(child);
          }
          bFrom = !bFrom;
        }
      }
      Collections.sort(this.children);
    }
    return (MxTreeDomainObject[])this.children.toArray(new MxTreeDomainObject[this.children.size()]);
  }

  public MxTreeDomainObject[] showRevisions()
	        throws MxEclipseException, MatrixException
	    {
	        if(children == null)
	            children = new ArrayList();
	        ArrayList toBeRemoved = new ArrayList();
	        for(int i = 0; i < children.size() && ((MxTreeDomainObject)children.get(0)).getName().equals(name); i++)
	            toBeRemoved.add((MxTreeDomainObject)children.get(i));

	        children.removeAll(toBeRemoved);
	        List lstRevisions = MatrixFinder.findObjectsInfo(getType(), getName(), null, null, new String[] {
	            "id", "type", "name", "revision", "policy", "current"
	        }, false);
	        MxTreeDomainObject child;
	        for(Iterator iterator = lstRevisions.iterator(); iterator.hasNext(); children.add(0, child))
	        {
	            Map mapRevision = (Map)iterator.next();
	            String id = (String)mapRevision.get("id");
	            child = new MxTreeDomainObject(id, (String)mapRevision.get("type"), (String)mapRevision.get("name"), (String)mapRevision.get("revision"), (String)mapRevision.get("policy"), (String)mapRevision.get("current"), null, "Revision", this.id, name, (String)mapRevision.get("name"));
	            child.setParent(this);
	            child.setContentProvider(contentProvider);
	        }

	        return (MxTreeDomainObject[])children.toArray(new MxTreeDomainObject[children.size()]);
	    }

  public DomainObject getDomainObject()
  {
    if (this.domainObject == null) {
      try {
        this.domainObject = DomainObject.newInstance(getContext(), this.id);
      } catch (Exception e) {
        MxEclipseLogger.getLogger().severe(e.getMessage());
      }
    }
    return this.domainObject;
  }

  public DomainRelationship getDomainRelationship() {
    if ((this.domainRelationship == null) && (this.relId != null)) {
      try {
        this.domainRelationship = DomainRelationship.newInstance(getContext(), this.relId);
      } catch (Exception e) {
        MxEclipseLogger.getLogger().severe(e.getMessage());
      }
    }
    return this.domainRelationship;
  }

  public MxTreeDomainObject getParent()
  {
    return this.parent;
  }

  public void setParent(MxTreeDomainObject parent) {
    this.parent = parent;
  }

  public static Context getContext() throws MxEclipseException {
    Context context = MxEclipsePlugin.getDefault().getContext();
    if ((context != null) && (context.isConnected())) {
      return context;
    }
    throw new MxEclipseException("No user connected to Matrix");
  }

  public String getId()
  {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRevision() {
    return this.revision;
  }

  public void setRevision(String revision) {
    this.revision = revision;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getRelId() {
    return this.relId;
  }

  public String getRelName() {
    return this.relName;
  }

  public String getRelType() {
    return this.relType;
  }

  public String getRelFromId() {
    return this.relFromId;
  }

  public String getRelFromName() {
    return this.relFromName;
  }

  public String getRelToName() {
    return this.relToName;
  }

  public String getPolicy() {
    return this.policy;
  }

  public void setPolicy(String policy) {
    this.policy = policy;
  }

  public String getCurrent() {
    return this.current;
  }

  public void setCurrent(String current) {
    this.current = current;
  }

  public boolean hasChildren()
  {
    return (this.children != null) && (this.children.size() > 0);
  }

  public int compareTo(Object o) {
    if ((o instanceof MxTreeDomainObject)) {
      MxTreeDomainObject otherObject = (MxTreeDomainObject)o;
      if (getRelId() == null) {
        return getName().compareTo(otherObject.getName());
      }
      int retVal = getRelType().compareTo(otherObject.getRelType());
      if (retVal == 0) {
        return getName().compareTo(otherObject.getName());
      }
      return retVal;
    }

    return -1;
  }

  public static MxTreeDomainObject createNewObject(String type, String name, String revision, String policy, String vault) throws MxEclipseException, MatrixException
  {
    Context context = getContext();
    DomainObject domObj = DomainObject.newInstance(context);
    domObj.createObject(context, type, name, revision, policy, vault);
    return new MxTreeDomainObject(domObj);
  }

  public void setContentProvider(MxTreeContentProvider provider) {
    this.contentProvider = provider;
  }

  public void refresh() throws MxEclipseException, MatrixException {
    if (this.children != null) {
      for (int i = 0; i < this.children.size(); i++) {
        ((MxTreeDomainObject)this.children.get(i)).refresh();
      }
      if (this.parent != null)
        getChildren(true);
    }
  }

  public void propertyChanged(MxObjectProperty task)
  {
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext())
      ((IMxObjectPropertyViewer)iterator.next()).updateProperty(task);
  }

  public void removeChangeListener(IMxObjectPropertyViewer viewer)
  {
    this.changeListeners.remove(viewer);
  }

  public void addChangeListener(IMxObjectPropertyViewer viewer)
  {
    this.changeListeners.add(viewer);
  }
}