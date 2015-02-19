package org.mxeclipse.model;

import com.matrixone.apps.domain.DomainRelationship;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import matrix.db.BusinessType;
import matrix.db.BusinessTypeItr;
import matrix.db.BusinessTypeList;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.db.RelationshipType;
import matrix.db.RelationshipTypeItr;
import matrix.db.RelationshipTypeList;
import matrix.util.MatrixException;
import org.mxeclipse.business.table.type.MxTypeComposite.MxTypeContentProvider;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeRelationship extends MxTreeBusiness
  implements IAttributable, ITriggerable
{
  RelationshipType relationshipType;
  protected String description;
  protected boolean hidden;
  protected boolean preventDuplicates;
  protected ArrayList<MxTreeType> fromTypes;
  protected ArrayList<MxTreeType> toTypes;
  protected DirectionInfo fromInfo;
  protected DirectionInfo toInfo;
  protected static String MQL_INFO = "print relationship \"{0}\" select description hidden preventduplicates dump |;";
  protected static String MQL_DIRECTION_INFO = "print relationship \"{0}\" select {1}cardinality {1}reviseaction {1}cloneaction {1}propagatemodify {1}propagateconnection dump |;";
  protected static String MQL_ADD_ATTRIBUTE = "modify relationship \"{0}\" add attribute \"{1}\";";
  protected static String MQL_REMOVE_ATTRIBUTE = "modify relationship \"{0}\" remove attribute \"{1}\";";
  protected static String MQL_ADD_TYPES = "modify relationship \"{0}\" {1} add type \"{2}\";";
  protected static String MQL_REMOVE_TYPES = "modify relationship \"{0}\" {1} remove type \"{2}\";";
  protected static String MQL_MODIFY_DIRECTION_INFO = "modify relationship \"{0}\" {1} cardinality {2} revision {3} clone {4}  {5}  {6};";
  protected static final int INFO_DESCRIPTION = 0;
  protected static final int INFO_HIDDEN = 1;
  protected static final int INFO_PREVENT_DUPLICATES = 2;
  protected static final int DIRECTION_INFO_CARDINALITY = 0;
  protected static final int DIRECTION_INFO_REVISION = 1;
  protected static final int DIRECTION_INFO_CLONE = 2;
  protected static final int DIRECTION_INFO_PROPAGATE_MODIFY = 3;
  protected static final int DIRECTION_INFO_PROPAGATE_CONNECTION = 4;
  public static String[] CARDINALITIES = { "One", "N" };
  public static String[] REVISION_ACTIONS = { "none", "float", "replicate" };
  public static String[] CLONE_ACTIONS = { "none", "float", "replicate" };
  protected static ArrayList<MxTreeRelationship> allRelationships;

  public MxTreeRelationship(String name)
    throws MxEclipseException, MatrixException
  {
    super("Relationship", name);
    this.relationshipType = new RelationshipType(name);
  }

  public void refresh() throws MxEclipseException, MatrixException
  {
    super.refresh();
    this.relationshipType = new RelationshipType(this.name);
    fillBasics();
    this.attributes = getAttributes(true);
    this.fromTypes = getTypes(true, true);
    this.toTypes = getTypes(true, false);
    fillDirectionInfo(true);
    fillDirectionInfo(false);
  }

  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isHidden() {
    return this.hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public boolean isPreventDuplicates() {
    return this.preventDuplicates;
  }

  public void setPreventDuplicates(boolean preventDuplicates) {
    this.preventDuplicates = preventDuplicates;
  }

  public DirectionInfo getFromInfo() {
    return this.fromInfo;
  }

  public void setFromInfo(DirectionInfo fromInfo) {
    this.fromInfo = fromInfo;
  }

  public DirectionInfo getToInfo() {
    return this.toInfo;
  }

  public void setToInfo(DirectionInfo toInfo) {
    this.toInfo = toInfo;
  }

  public static ArrayList<MxTreeRelationship> getAllRelationships(boolean refresh) throws MatrixException, MxEclipseException {
    if ((refresh) || (allRelationships == null)) {
      Context context = getContext();
      RelationshipTypeList rtl = RelationshipType.getRelationshipTypes(context, true);
      allRelationships = new ArrayList();
      RelationshipTypeItr rti = new RelationshipTypeItr(rtl);
      while (rti.next()) {
        RelationshipType rt = rti.obj();
        MxTreeRelationship rel = new MxTreeRelationship(rt.getName());
        allRelationships.add(rel);
      }
      Collections.sort(allRelationships);
    }
    return allRelationships;
  }

  public static String[] getAllRelationshipNames(boolean refresh) throws MatrixException, MxEclipseException {
    ArrayList allRelationships = getAllRelationships(refresh);

    String[] retVal = new String[allRelationships.size()];
    for (int i = 0; i < retVal.length; i++) {
      retVal[i] = ((MxTreeRelationship)allRelationships.get(i)).getName();
    }
    return retVal;
  }

  public static ArrayList<MxTreeAttribute> getAttributes(MxTreeRelationship relationship) {
    ArrayList retAttributes = new ArrayList();
    try {
      Context context = getContext();
      relationship.relationshipType.open(context);
      try {
        Map mapAttributes = DomainRelationship.getTypeAttributes(context, relationship.getName(), true);
        for (Iterator localIterator = mapAttributes.keySet().iterator(); localIterator.hasNext(); ) { Object oAttribute = localIterator.next();

          MxTreeAttribute attribute = new MxTreeAttribute((String)oAttribute);
          attribute.setParent(relationship);
          attribute.setFrom(true);
          attribute.setRelType("contains");
          retAttributes.add(attribute); }
      }
      finally {
        relationship.relationshipType.close(context);
      }
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
    return retAttributes;
  }

  public ArrayList<MxTreeAttribute> getAttributes(boolean forceRefresh) {
    if ((forceRefresh) || (this.attributes == null)) {
      this.attributes = getAttributes(this);
    }
    return this.attributes;
  }

  public void addType(boolean from)
    throws MxEclipseException, MatrixException
  {
    addType(new MxTreeType(""), from);
  }
  public void addType(MxTreeType newType, boolean from)
  {
      if(from)
          fromTypes.add(newType);
      else
          toTypes.add(newType);
      for(Iterator iterator = changeListeners.iterator(); iterator.hasNext();)
      {
          IMxBusinessViewer contentProvider = (IMxBusinessViewer)iterator.next();
          if((contentProvider instanceof org.mxeclipse.business.table.type.MxTypeComposite.MxTypeContentProvider) && from == ((org.mxeclipse.business.table.type.MxTypeComposite.MxTypeContentProvider)contentProvider).getFrom())
              contentProvider.addProperty(newType);
      }

  }

  public void removeType(MxTreeType type, boolean from)
  {
      if(from)
      {
          if(fromTypes == null)
              getTypes(false, true);
          fromTypes.remove(type);
      } else
      {
          if(toTypes == null)
              getTypes(false, false);
          toTypes.remove(type);
      }
      for(Iterator iterator = changeListeners.iterator(); iterator.hasNext();)
      {
          IMxBusinessViewer contentProvider = (IMxBusinessViewer)iterator.next();
          if((contentProvider instanceof org.mxeclipse.business.table.type.MxTypeComposite.MxTypeContentProvider) && from == ((org.mxeclipse.business.table.type.MxTypeComposite.MxTypeContentProvider)contentProvider).getFrom())
              contentProvider.removeProperty(type);
      }

  }

  public void fillBasics()
  {
    try
    {
      Context context = getContext();
      this.relationshipType.open(context);
      try {
        this.name = this.relationshipType.getName();

        MQLCommand command = new MQLCommand();
        command.executeCommand(context, MessageFormat.format(MQL_INFO, new Object[] { this.name }));

        String[] info = command.getResult().trim().split("\\|");
        this.description = info[0];
        this.hidden = info[1].equalsIgnoreCase("true");
        this.preventDuplicates = info[2].equalsIgnoreCase("true");
      }
      finally {
        this.relationshipType.close(context);
      }
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
  }

  public static DirectionInfo getDirectionInfo(MxTreeRelationship relationship, boolean from) {
	  DirectionInfo directionInfo = relationship. new DirectionInfo();
    try {
      Context context = getContext();
      MQLCommand command = new MQLCommand();
      command.executeCommand(context, MessageFormat.format(MQL_DIRECTION_INFO, new Object[] { relationship.getName(), from ? "from" : "to" }));

      String[] info = command.getResult().trim().split("\\|");

      directionInfo.setCardinality(info[0]);
      directionInfo.setRevision(info[1]);
      directionInfo.setClone(info[2]);
      directionInfo.setPropagateConnection(info[4].equalsIgnoreCase("true"));
      directionInfo.setPropagateModify(info[3].equalsIgnoreCase("true"));
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
    return directionInfo;
  }

  public void fillDirectionInfo(boolean from) {
    DirectionInfo directionInfo = getDirectionInfo(this, from);
    if (from)
      this.fromInfo = directionInfo;
    else
      this.toInfo = directionInfo;
  }

  public static ArrayList<MxTreeType> getTypes(MxTreeRelationship relationship, boolean from)
  {
    ArrayList retTypes = new ArrayList();
    try {
      Context context = getContext();
      relationship.relationshipType.open(context);
      try {
        BusinessTypeList btl = from ? relationship.relationshipType.getFromTypes(context) : relationship.relationshipType.getToTypes(context);
        BusinessTypeItr itBusiness = new BusinessTypeItr(btl);
        MxTreeType child;
        while (itBusiness.next()) {
          BusinessType bt = itBusiness.obj();
          child = new MxTreeType(bt.getName());
          child.setParent(relationship);
          child.setFrom(from);
          child.setRelType(from ? "from" : "to");
          retTypes.add(child);
        }

        for(Iterator iterator = retTypes.iterator(); iterator.hasNext();)
        {
            child = (MxTreeType)iterator.next();
            MxTreeType parentType = child.getParentType(false);
            if(parentType != null)
            {
                for(Iterator iterator1 = retTypes.iterator(); iterator1.hasNext();)
                {
                    MxTreeType retType = (MxTreeType)iterator1.next();
                    if(retType.getName().equals(parentType.getName()))
                    {
                        child.setInherited(true);
                        break;
                    }
                }

            }
        }
      }
      finally
      {
        relationship.relationshipType.close(context);
      }
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
    return retTypes;
  }

  public ArrayList<MxTreeType> getTypes(boolean forceRefresh, boolean from) {
    if (from) {
      if ((forceRefresh) || (this.fromTypes == null)) {
        this.fromTypes = getTypes(this, from);
      }
      return this.fromTypes;
    }
    if ((forceRefresh) || (this.toTypes == null)) {
      this.toTypes = getTypes(this, from);
    }
    return this.toTypes;
  }

  public void saveAttributes(Context context, MQLCommand command)
    throws MatrixException
  {
    ArrayList oldAttributes = getAttributes(this);

    for (int i = 0; i < this.attributes.size(); i++) {
      if (!((MxTreeAttribute)this.attributes.get(i)).getOldName().equals(""))
        continue;
      command.executeCommand(context, MessageFormat.format(MQL_ADD_ATTRIBUTE, new Object[] { getName(), ((MxTreeAttribute)this.attributes.get(i)).getName() }));
    }

    String mql = "";
    if (oldAttributes != null)
      for (int i = 0; i < oldAttributes.size(); i++) {
        boolean bFound = false;
        MxTreeAttribute oldAttribute = (MxTreeAttribute)oldAttributes.get(i);
        for (int j = 0; j < this.attributes.size(); j++) {
          MxTreeAttribute attribute = (MxTreeAttribute)this.attributes.get(j);

          if (oldAttribute.getName().equals(attribute.getName())) {
            bFound = true;
            break;
          }if (oldAttribute.getName().equals(attribute.getOldName())) {
            command.executeCommand(context, MessageFormat.format(MQL_ADD_ATTRIBUTE, new Object[] { getName(), attribute.getName() }));
            command.executeCommand(context, MessageFormat.format(MQL_REMOVE_ATTRIBUTE, new Object[] { getName(), oldAttribute.getOldName() }));
            bFound = true;
            break;
          }
        }
        if (bFound)
          continue;
        command.executeCommand(context, MessageFormat.format(MQL_REMOVE_ATTRIBUTE, new Object[] { getName(), oldAttribute.getOldName() }));
      }
  }

  public void saveTypes(Context context, MQLCommand command, boolean from)
    throws MatrixException, MxEclipseException
  {
    ArrayList oldTypes = getTypes(this, from);
    ArrayList types = from ? this.fromTypes : this.toTypes;

    String sAdded = "";
    String sRemoved = "";

    for (int i = 0; i < types.size(); i++) {
      if (!((MxTreeType)types.get(i)).getOldName().equals(""))
        continue;
      sAdded = sAdded + (!sAdded.equals("") ? "," : "") + ((MxTreeType)types.get(i)).getName();
    }

    if (oldTypes != null) {
      for (int i = 0; i < oldTypes.size(); i++) {
        boolean bFound = false;
        MxTreeType oldType = (MxTreeType)oldTypes.get(i);
        for (int j = 0; j < types.size(); j++) {
          MxTreeType type = (MxTreeType)types.get(j);

          if (oldType.getName().equals(type.getName())) {
            bFound = true;
            break;
          }if (oldType.getName().equals(type.getOldName())) {
            sAdded = sAdded + (!sAdded.equals("") ? "," : "") + type.getName();
            sRemoved = sRemoved + (!sRemoved.equals("") ? "," : "") + oldType.getOldName();
            bFound = true;
            break;
          }
        }
        if (bFound)
          continue;
        sRemoved = sRemoved + (!sRemoved.equals("") ? "," : "") + oldType.getOldName();
      }

    }

    command.executeCommand(context, MessageFormat.format(MQL_ADD_TYPES, new Object[] { getName(), from ? "from" : "to", sAdded }));
    command.executeCommand(context, MessageFormat.format(MQL_REMOVE_TYPES, new Object[] { getName(), from ? "from" : "to", sRemoved }));
  }

  public void saveDirectionInfo(Context context, MQLCommand command, boolean from) throws MatrixException, MxEclipseException {
    DirectionInfo oldDirectionInfo = getDirectionInfo(this, from);
    DirectionInfo directionInfo = from ? this.fromInfo : this.toInfo;
    command.executeCommand(context, MessageFormat.format(MQL_MODIFY_DIRECTION_INFO, new Object[] { getName(), from ? "from" : "to", 
      directionInfo.getCardinality().equals("One") ? "1" : directionInfo.getCardinality(), directionInfo.getRevision(), directionInfo.getClone(), 
      directionInfo.isPropagateModify() != oldDirectionInfo.isPropagateModify() ? (directionInfo.isPropagateModify() ? "" : "!") + "propagatemodify" : "", 
      directionInfo.isPropagateConnection() != oldDirectionInfo.isPropagateConnection() ? (directionInfo.isPropagateConnection() ? "" : "!") + "propagateconnection" : "" }));
  }

  public void save()
  {
    try {
      MQLCommand command = new MQLCommand();
      Context context = getContext();
      this.relationshipType.open(context);
      try
      {
        String modString = "";
        String relationshipName = this.relationshipType.getName();
        boolean changedName = !relationshipName.equals(getName());
        if (changedName) {
          modString = modString + " name \"" + getName() + "\"";
        }

        command.executeCommand(context, MessageFormat.format(MQL_INFO, new Object[] { this.relationshipType.getName() }));
        String[] info = command.getResult().trim().split("\\|");
        if (!info[0].equals(getDescription())) {
          modString = modString + " description \"" + getDescription() + "\"";
        }
        boolean oldIsHidden = info[1].equalsIgnoreCase("true");
        if (oldIsHidden != isHidden()) {
          modString = modString + (isHidden() ? " hidden" : " nothidden");
        }
        boolean oldPreventDuplicates = info[2].equalsIgnoreCase("true");
        if (oldPreventDuplicates != this.preventDuplicates) {
          modString = modString + (this.preventDuplicates ? " preventduplicates" : " !preventduplicates");
        }

        if (!modString.equals("")) {
          command.executeCommand(context, "modify relationship \"" + relationshipName + "\" " + modString + ";");
        }

        if (changedName) {
          this.relationshipType = new RelationshipType(this.name);
        }
        saveAttributes(context, command);
        saveTypes(context, command, true);
        saveDirectionInfo(context, command, true);
        saveTypes(context, command, false);
        saveDirectionInfo(context, command, false);
        saveTriggers(context, command);

        allRelationships = null;
        this.attributes = null;
        this.fromTypes = null;
        this.toTypes = null;
        refresh();
      } finally {
        this.relationshipType.close(context);
      }
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
  }

  public MxTreeBusiness[] getChildren(boolean forceUpdate)
    throws MxEclipseException, MatrixException
  {
    if (forceUpdate) {
      this.children = null;
    }
    if (this.children == null) {
      this.children = new ArrayList();

      this.children.addAll(getAttributes(false));
      this.children.addAll(getTypes(false, true));
      this.children.addAll(getTypes(false, false));
    }
    return (MxTreeBusiness[])this.children.toArray(new MxTreeBusiness[this.children.size()]);
  }

  public static void clearCache() {
    allRelationships = null;
  }

  public class DirectionInfo
  {
    private String cardinality;
    private String revision;
    private String clone;
    private boolean propagateModify;
    private boolean propagateConnection;

    public DirectionInfo()
    {
    }

    public String getCardinality()
    {
      return this.cardinality;
    }
    public void setCardinality(String cardinality) {
      this.cardinality = cardinality;
    }
    public String getClone() {
      return this.clone;
    }
    public void setClone(String clone) {
      this.clone = clone;
    }
    public boolean isPropagateConnection() {
      return this.propagateConnection;
    }
    public void setPropagateConnection(boolean propagateConnection) {
      this.propagateConnection = propagateConnection;
    }
    public boolean isPropagateModify() {
      return this.propagateModify;
    }
    public void setPropagateModify(boolean propagateModify) {
      this.propagateModify = propagateModify;
    }
    public String getRevision() {
      return this.revision;
    }
    public void setRevision(String revision) {
      this.revision = revision;
    }
  }
}