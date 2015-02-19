package org.mxeclipse.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.business.tree.MxBusinessContentProvider;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeBusiness
  implements Comparable, Cloneable
{
  public static final String REL_TYPE_CONTAINS = "contains";
  public static final String REL_TYPE_INHERITS = "inherits";
  public static final String REL_TYPE_POLICY = "policy";
  public static final String REL_TYPE_FROM_TYPE = "from";
  public static final String REL_TYPE_TO_TYPE = "to";
  public static final String REL_TYPE_ACCESS = "access";
  protected String type;
  protected String name;
  protected String oldName;
  protected boolean inherited;
  protected boolean from;
  protected String relType;
  protected MxTreeBusiness parent;
  protected ArrayList<MxTreeBusiness> children;
  protected ArrayList<MxTreeAttribute> attributes;
  protected ArrayList<MxTreeTrigger> triggers;
  private MxBusinessContentProvider contentProvider;
  protected Set changeListeners = new HashSet();

  protected static String MQL_ADD_TRIGGER = "modify {0} \"{1}\" add trigger {2} {3} \"{4}\" input \"{5}\";";
  protected static String MQL_ADD_STATE_TRIGGER = "modify policy \"{0}\" state \"{1}\" add trigger {2} {3} \"{4}\" input \"{5}\";";
  protected static String MQL_REMOVE_TRIGGER = "modify {0} \"{1}\" remove trigger {2} {3};";
  protected static String MQL_REMOVE_STATE_TRIGGER = "modify policy \"{0}\" state \"{1}\" remove trigger {2} {3};";

  protected static String MQL_SIMPLE_LIST = "list {0};";
  protected static String MQL_SIMPLE_PRINT = "print {0} \"{1}\";";
  protected static String MQL_LIST_ALL = "list {0};";
  protected static String MQL_CREATE_NEW = "add {0} \"{1}\";";
  protected static String MQL_CREATE_NEW_WEBFORM = "add {0} \"{1}\" web;";
  protected static String MQL_CREATE_NEW_TABLE = "add {0} \"{1}\" system;";
  protected static String MQL_CREATE_NEW_ATTRIBUTE = "add {0} \"{1}\" type {2};";
  protected static String MQL_DELETE = "delete {0} \"{1}\";";
  protected static String MQL_DELETE_TABLE = "delete {0} \"{1}\" system;";

  public MxTreeBusiness() {
    this.children = new ArrayList();
  }

  public String getOldName() {
    return this.oldName;
  }

  public void addChild(MxTreeBusiness child) {
    this.children.add(child);
    child.setParent(this);
  }

  protected MxTreeBusiness(String type, String name) throws MxEclipseException, MatrixException
  {
    this();
    this.type = type;
    this.oldName = name;
    this.name = name;
  }

  public static MxTreeBusiness createBusiness(String type, String name) throws MatrixException, MxEclipseException {
    MxTreeBusiness newObject = null;
    if (type.equals("Attribute"))
      newObject = new MxTreeAttribute(name);
    else if (type.equals("Type"))
      newObject = new MxTreeType(name);
    else if (type.equals("Relationship"))
      newObject = new MxTreeRelationship(name);
    else if (type.equals("Policy"))
      newObject = new MxTreePolicy(name);
    else if (type.equals("Person"))
      newObject = new MxTreePerson(name);
    else if (type.equals("Association"))
      newObject = new MxTreeAssociation(name);
    else if (type.equals("Role"))
      newObject = new MxTreeRole(name);
    else if (type.equals("Group"))
      newObject = new MxTreeGroup(name);
    else if (type.equals("Program"))
      newObject = new MxTreeProgram(name);
    else if (type.equals("Index"))
      newObject = new MxTreeIndex(name);
    else if (type.equals("Menu"))
      newObject = new MxTreeWebMenu(name);
    else if (type.equals("Command"))
      newObject = new MxTreeWebCommand(name);
    else if (type.equals("Table"))
      newObject = new MxTreeWebTable(name);
    else {
      newObject = new MxTreeBusiness(type, name);
    }
    return newObject;
  }

  public MxTreeBusiness[] getChildren(boolean forceUpdate)
    throws MxEclipseException, MatrixException
  {
    if (forceUpdate) {
      this.children = null;
    }
    if (this.children == null) {
      this.children = new ArrayList();
    }

    return (MxTreeBusiness[])this.children.toArray(new MxTreeBusiness[this.children.size()]);
  }

  public MxTreeBusiness getParent()
  {
    return this.parent;
  }

  public void setParent(MxTreeBusiness parent) {
    this.parent = parent;
  }

  public static Context getContext() throws MxEclipseException {
    Context context = MxEclipsePlugin.getDefault().getContext();
    if ((context != null) && (context.isConnected())) {
      return context;
    }
    throw new MxEclipseException("No user connected to Matrix");
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getType() {
    return this.type;
  }

  public boolean isInherited() {
    return this.inherited;
  }

  public void setInherited(boolean inherited) {
    this.inherited = inherited;
  }

  public boolean isFrom() {
    return this.from;
  }

  public void setFrom(boolean from) {
    this.from = from;
  }

  public String getRelType()
  {
    return this.relType;
  }

  public void setRelType(String relType) {
    this.relType = relType;
  }

  public static String[] getNames(ArrayList<MxTreeBusiness> al) {
    String[] retVal = new String[al.size()];
    for (int i = 0; i < retVal.length; i++) {
      retVal[i] = ((MxTreeBusiness)al.get(i)).getName();
    }
    return retVal;
  }

  public ArrayList<MxTreeAttribute> getAttributes(boolean forceRefresh)
  {
    return null;
  }

  public void addAttribute() throws MxEclipseException, MatrixException {
    addAttribute(new MxTreeAttribute(""));
  }
  public void addAttribute(MxTreeAttribute newAttribute) {
    this.attributes.add(newAttribute);
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext())
      ((IMxBusinessViewer)iterator.next()).addProperty(newAttribute); 
  }

  public void removeAttribute(MxTreeAttribute attribute) {
    if (this.attributes == null) {
      getAttributes(false);
    }
    this.attributes.remove(attribute);
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext())
      ((IMxBusinessViewer)iterator.next()).removeProperty(attribute);
  }

  public ArrayList<MxTreeTrigger> getTriggers(boolean forceRefresh)
  {
    if ((forceRefresh) || (this.triggers == null)) {
      this.triggers = MxTreeTrigger.getTriggersForObject(this);
    }
    return this.triggers;
  }

  public void addTrigger() throws MxEclipseException, MatrixException {
    addTrigger(new MxTreeTrigger(this));
  }
  public void addTrigger(MxTreeTrigger newTrigger) {
    this.triggers.add(newTrigger);
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext())
      ((IMxBusinessViewer)iterator.next()).addProperty(newTrigger); 
  }

  public void removeTrigger(MxTreeTrigger trigger) {
    if (this.triggers == null) {
      getTriggers(false);
    }
    this.triggers.remove(trigger);
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext())
      ((IMxBusinessViewer)iterator.next()).removeProperty(trigger);
  }

  public void saveTriggers(Context context, MQLCommand command)
    throws MatrixException
  {
    ArrayList oldTriggers = MxTreeTrigger.getTriggersForObject(this);

    for (int i = 0; i < this.triggers.size(); i++) {
      if (!((MxTreeTrigger)this.triggers.get(i)).getOldName().equals(""))
        continue;
      MxTreeTrigger t = (MxTreeTrigger)this.triggers.get(i);
      if (!this.type.equals("State"))
        command.executeCommand(context, MessageFormat.format(MQL_ADD_TRIGGER, new Object[] { getType(), getName(), t.getEventType().toLowerCase(), t.getTriggerType(), t.getMainProgramName(), t.getArgs() }));
      else {
        command.executeCommand(context, MessageFormat.format(MQL_ADD_STATE_TRIGGER, new Object[] { ((MxTreeState)this).getPolicy().getName(), getName(), t.getEventType().toLowerCase(), t.getTriggerType(), t.getMainProgramName(), t.getArgs() }));
      }

    }

    String mql = "";
    if (oldTriggers != null)
      for (int i = 0; i < oldTriggers.size(); i++) {
        boolean bFound = false;
        MxTreeTrigger oldTrigger = (MxTreeTrigger)oldTriggers.get(i);
        for (int j = 0; j < this.triggers.size(); j++) {
          MxTreeTrigger trigger = (MxTreeTrigger)this.triggers.get(j);

          if (oldTrigger.getName().equals(trigger.getName())) {
            bFound = true;
            break;
          }if (oldTrigger.getName().equals(trigger.getOldName())) {
            if (!this.type.equals("State")) {
              command.executeCommand(context, MessageFormat.format(MQL_REMOVE_TRIGGER, new Object[] { getType(), getName(), oldTrigger.getEventType(), oldTrigger.getTriggerType() }));
              command.executeCommand(context, MessageFormat.format(MQL_ADD_TRIGGER, new Object[] { getType(), getName(), trigger.getEventType(), trigger.getTriggerType(), trigger.getMainProgramName(), trigger.getArgs() }));
            } else {
              command.executeCommand(context, MessageFormat.format(MQL_REMOVE_STATE_TRIGGER, new Object[] { ((MxTreeState)this).getPolicy().getName(), getName(), oldTrigger.getEventType(), oldTrigger.getTriggerType() }));
              command.executeCommand(context, MessageFormat.format(MQL_ADD_STATE_TRIGGER, new Object[] { ((MxTreeState)this).getPolicy().getName(), getName(), trigger.getEventType(), trigger.getTriggerType(), trigger.getMainProgramName(), trigger.getArgs() }));
            }
            bFound = true;
            break;
          }
        }
        if (bFound)
          continue;
        if (!this.type.equals("State"))
          command.executeCommand(context, MessageFormat.format(MQL_REMOVE_TRIGGER, new Object[] { getType(), getName(), oldTrigger.getEventType(), oldTrigger.getTriggerType() }));
        else
          command.executeCommand(context, MessageFormat.format(MQL_REMOVE_STATE_TRIGGER, new Object[] { ((MxTreeState)this).getPolicy().getName(), getName(), oldTrigger.getEventType(), oldTrigger.getTriggerType() }));
      }
  }

  public static String[] findAdminObjects(String typeName, String namePattern)
  {
    String pattern = namePattern;
    StringBuffer query = new StringBuffer("list ");

    if ("WebForm".equalsIgnoreCase(typeName))
      query.append("Form");
    else {
      query.append(typeName);
    }
    if (!typeName.equals("Association"))
    {
      query.append(" '").append(pattern);
      query.append("' select name ");
      if (("WebForm".equalsIgnoreCase(typeName)) || 
        ("Form".equalsIgnoreCase(typeName))) {
        query.append("web ");
      }
      query.append("dump |");
    }
    try
    {
      Context context = getContext();
      MQLCommand command = new MQLCommand();
      boolean executed = command.executeCommand(context, query.toString());
      if (executed) {
        String result = command.getResult();
        String[] ret = result.split("\n");
        return ret;
      }
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
    return new String[0];
  }

  public void save()
  {
  }

  public static MxTreeBusiness create(String type, String name, String attributeType)
    throws MxEclipseException, MatrixException
  {
    Context context = getContext();
    MQLCommand command = new MQLCommand();
    if (type.equals("Program")) {
      MxTreeProgram.allPrograms = null;
    }
    if (type.equals("WebForm"))
      command.executeCommand(context, MessageFormat.format(MQL_CREATE_NEW_WEBFORM, new Object[] { "Form".toLowerCase(), name }));
    else if (type.equals("Table"))
      command.executeCommand(context, MessageFormat.format(MQL_CREATE_NEW_TABLE, new Object[] { "Table".toLowerCase(), name }));
    else if (type.equals("Attribute"))
      command.executeCommand(context, MessageFormat.format(MQL_CREATE_NEW_ATTRIBUTE, new Object[] { type.toLowerCase(), name, attributeType }));
    else {
      command.executeCommand(context, MessageFormat.format(MQL_CREATE_NEW, new Object[] { type.toLowerCase(), name }));
    }
    if (!command.getError().equals("")) {
      throw new MxEclipseException(command.getError());
    }

    return createBusiness(type, name);
  }

  public void delete()
    throws MxEclipseException, MatrixException
  {
    Context context = getContext();
    MQLCommand command = new MQLCommand();
    if (this.type.equals("WebForm"))
      command.executeCommand(context, MessageFormat.format(MQL_DELETE, new Object[] { "Form".toLowerCase(), this.name }));
    else if (this.type.equals("Table"))
      command.executeCommand(context, MessageFormat.format(MQL_DELETE_TABLE, new Object[] { "Table".toLowerCase(), this.name }));
    else {
      command.executeCommand(context, MessageFormat.format(MQL_DELETE, new Object[] { this.type.toLowerCase(), this.name }));
    }
    if (!command.getError().equals(""))
      throw new MxEclipseException(command.getError());
  }

  public boolean hasChildren()
  {
    return (this.children != null) && (this.children.size() > 0);
  }

  public int compareTo(Object o) {
    if ((o instanceof MxTreeBusiness)) {
      MxTreeBusiness otherObject = (MxTreeBusiness)o;

      int retVal = getType().compareToIgnoreCase(otherObject.getType());
      if (retVal == 0) {
        return getName().compareToIgnoreCase(otherObject.getName());
      }
      return retVal;
    }

    return -1;
  }

  public void setContentProvider(MxBusinessContentProvider provider)
  {
    this.contentProvider = provider;
  }

  public void refresh() throws MxEclipseException, MatrixException {
    if ((this instanceof ITriggerable))
      getTriggers(true);
  }

  public void removeChangeListener(IMxBusinessViewer viewer)
  {
    this.changeListeners.remove(viewer);
  }

  public void addChangeListener(IMxBusinessViewer viewer)
  {
    this.changeListeners.add(viewer);
  }

  public void propertyChanged(MxTreeBusiness task)
  {
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext())
      ((IMxBusinessViewer)iterator.next()).updateProperty(task);
  }
}