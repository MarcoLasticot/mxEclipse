package org.mxeclipse.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import matrix.util.StringList;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeType extends MxTreeBusiness
  implements IAttributable, ITriggerable
{
  BusinessType businessType;
  ArrayList<MxTreeAttribute> attributes;
  protected String description;
  protected boolean hidden;
  protected boolean abstractType;
  protected MxTreeType parentType;
  protected MxTreeType oldParentType;
  protected ArrayList<MxTreeType> childTypes;
  protected ArrayList<MxTreeRelationship> fromRelationships;
  protected ArrayList<MxTreeRelationship> toRelationships;
  protected ArrayList<MxTreePolicy> policies;
  protected static String MQL_INFO = "print type \"{0}\" select description hidden abstract dump |;";
  protected static String MQL_INFO_POLICY = "print type \"{0}\" select policy dump |;";
  protected static String MQL_INFO_ATTRIBUTE = "print type \"{0}\" select attribute dump |;";
  protected static String MQL_ADD_ATTRIBUTE = "modify type \"{0}\" add attribute \"{1}\";";
  protected static String MQL_REMOVE_ATTRIBUTE = "modify type \"{0}\" remove attribute \"{1}\";";
  protected static final int INFO_DESCRIPTION = 0;
  protected static final int INFO_HIDDEN = 1;
  protected static final int INFO_ABSTRACT = 2;
  protected static ArrayList<MxTreeType> allTypes;

  public MxTreeType(String name)
    throws MxEclipseException, MatrixException
  {
    super("Type", name);
    this.businessType = new BusinessType(name, getContext().getVault());
  }

  public void refresh() throws MxEclipseException, MatrixException
  {
    super.refresh();
    this.businessType = new BusinessType(this.name, getContext().getVault());
    fillBasics();
    getPolicies(true);
    this.attributes = getAttributes(true);
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

  public boolean isAbstractType() {
    return this.abstractType;
  }

  public void setAbstractType(boolean abstractType) {
    this.abstractType = abstractType;
  }

  public static ArrayList<MxTreeType> getAllTypes(boolean refresh) throws MatrixException, MxEclipseException {
    if ((refresh) || (allTypes == null)) {
      Context context = getContext();
      BusinessTypeList btl = BusinessType.getBusinessTypes(context, true);
      allTypes = new ArrayList();
      BusinessTypeItr bti = new BusinessTypeItr(btl);
      while (bti.next()) {
        BusinessType bt = bti.obj();
        MxTreeType type = new MxTreeType(bt.getName());
        allTypes.add(type);
      }
      Collections.sort(allTypes);
    }
    return allTypes;
  }

  public static String[] getAllTypeNames(boolean refresh) throws MatrixException, MxEclipseException {
    ArrayList allTypes = getAllTypes(refresh);

    String[] retVal = new String[allTypes.size()];
    for (int i = 0; i < retVal.length; i++) {
      retVal[i] = ((MxTreeType)allTypes.get(i)).getName();
    }
    return retVal;
  }

  public static ArrayList<MxTreeAttribute> getAttributes(MxTreeType type) {
    ArrayList retAttributes = new ArrayList();
    try {
      Context context = getContext();
      type.businessType.open(context);
      MxTreeType parentType = type.getParentType(false);
      try {
        MQLCommand command = new MQLCommand();
        command.executeCommand(context, MessageFormat.format(MQL_INFO_ATTRIBUTE, new Object[] { type.name }));

        String[] attributes = command.getResult().trim().split("\\|");
        String as[];
        int j = (as = attributes).length;
        for(int i = 0; i < j; i++)
        {
            String at = as[i];
            MxTreeAttribute attribute = new MxTreeAttribute(at);
            attribute.setParent(type);
            attribute.setFrom(true);
            attribute.setRelType("contains");
            if(parentType != null)
            {
                ArrayList parentAttributes = parentType.getAttributes(false);
                for(Iterator iterator = parentAttributes.iterator(); iterator.hasNext();)
                {
                    MxTreeAttribute parentAttribute = (MxTreeAttribute)iterator.next();
                    if(parentAttribute.getName().equals(attribute.getName()))
                    {
                        attribute.setInherited(true);
                        break;
                    }
                }

            }
            retAttributes.add(attribute);
        }
      } finally {
        type.businessType.close(context);
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

  public static ArrayList<MxTreePolicy> getPolicies(MxTreeType type) {
    ArrayList retPolicies = new ArrayList();
    try {
      Context context = getContext();
      MxTreeType parentType = type.getParentType(false);
      MQLCommand command = new MQLCommand();

      command.executeCommand(context, MessageFormat.format(MQL_INFO_POLICY, new Object[] { type.getName() }));
      String[] p = command.getResult().split("\\|");
      for(int i = 0; i < p.length; i++)
      {
          String pname = p[i].trim();
          if(!pname.equals(""))
          {
              MxTreePolicy policy = new MxTreePolicy(pname);
              policy.setParent(type);
              policy.setFrom(true);
              policy.setRelType("policy");
              if(parentType != null)
              {
                  ArrayList parentPolicies = parentType.getPolicies(false);
                  for(Iterator iterator = parentPolicies.iterator(); iterator.hasNext();)
                  {
                      MxTreePolicy parentPolicy = (MxTreePolicy)iterator.next();
                      if(parentPolicy.getName().equals(policy.getName()))
                      {
                          policy.setInherited(true);
                          break;
                      }
                  }

              }
              retPolicies.add(policy);
          }
      }
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
    return retPolicies;
  }

  public ArrayList<MxTreePolicy> getPolicies(boolean forceRefresh) {
    if ((forceRefresh) || (this.policies == null)) {
      this.policies = getPolicies(this);
    }
    return this.policies;
  }

  public String[] getPolicyNames(boolean forceRefresh) {
    ArrayList policies = getPolicies(forceRefresh);
    String[] retVal = new String[policies.size()];
    for (int i = 0; i < retVal.length; i++) {
      retVal[i] = ((MxTreePolicy)policies.get(i)).getName();
    }
    return retVal;
  }

  public static ArrayList<MxTreeType> getChildTypes(MxTreeType type) {
    ArrayList retTypes = new ArrayList();
    try {
      Context context = getContext();
      type.businessType.open(context);
      try {
        BusinessTypeList btl = type.businessType.getChildren(context);
        BusinessTypeItr itBusiness = new BusinessTypeItr(btl);
        while (itBusiness.next()) {
          BusinessType bt = itBusiness.obj();
          MxTreeType child = new MxTreeType(bt.getName());
          child.setParent(type);
          child.setFrom(true);
          child.setRelType("inherits");
          retTypes.add(child);
        }
      } finally {
        type.businessType.close(context);
      }
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
    return retTypes;
  }

  public ArrayList<MxTreeType> getChildTypes(boolean forceRefresh) {
    if ((forceRefresh) || (this.childTypes == null)) {
      this.childTypes = getChildTypes(this);
    }
    return this.childTypes;
  }

  public static ArrayList<MxTreeRelationship> getRelationships(MxTreeType type, boolean from) {
    ArrayList retRels = new ArrayList();
    try {
      Context context = getContext();
      type.businessType.open(context);
      try
      {
        RelationshipTypeList rtl = type.businessType.getRelationshipTypes(context, !from, from, false);
        RelationshipTypeItr itBusiness = new RelationshipTypeItr(rtl);
        while (itBusiness.next()) {
          RelationshipType rt = itBusiness.obj();
          MxTreeRelationship child = new MxTreeRelationship(rt.getName());
          child.setParent(type);
          child.setFrom(from);
          child.setRelType(from ? "from" : "to");
          retRels.add(child);
        }
      } finally {
        type.businessType.close(context);
      }
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
    return retRels;
  }

  public ArrayList<MxTreeRelationship> getRelationships(boolean forceRefresh, boolean from) {
    if (from) {
      if ((forceRefresh) || (this.fromRelationships == null)) {
        this.fromRelationships = getRelationships(this, from);
      }
    }
    else if ((forceRefresh) || (this.toRelationships == null)) {
      this.toRelationships = getRelationships(this, from);
    }

    return from ? this.fromRelationships : this.toRelationships;
  }

  public void fillBasics() {
    try {
      Context context = getContext();
      this.businessType.open(context);
      try {
        this.name = this.businessType.getName();
        MQLCommand command = new MQLCommand();
        command.executeCommand(context, MessageFormat.format(MQL_INFO, new Object[] { this.name }));

        String[] info = command.getResult().trim().split("\\|");
        this.description = info[0];
        this.hidden = info[1].equalsIgnoreCase("true");
        this.abstractType = info[2].equalsIgnoreCase("true");

        getParentType(true);
      } finally {
        this.businessType.close(context);
      }
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
  }

  public MxTreeType getParentType(boolean forceRefresh) throws MatrixException, MxEclipseException {
    if ((forceRefresh) || (this.parentType == null)) {
      Context context = getContext();
      StringList parents = this.businessType.getParents(context);
      if (parents.size() > 0) {
        this.parentType = new MxTreeType((String)parents.get(0));
        this.parentType.setFrom(false);
        this.parentType.setRelType("inherits");
      } else {
        this.parentType = null;
      }
    }
    return this.parentType;
  }

  public void setParentType(String parentName) throws MxEclipseException, MatrixException {
    this.oldParentType = this.parentType;
    if (parentName != null) {
      this.parentType = new MxTreeType(parentName);
      this.parentType.setFrom(false);
      this.parentType.setRelType("inherits");
    } else {
      this.parentType = null;
    }
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

  public void addPolicy() throws MxEclipseException, MatrixException {
    addPolicy(new MxTreePolicy(""));
  }

  public void addPolicy(MxTreePolicy newPolicy) {
    this.policies.add(newPolicy);
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext())
      ((IMxBusinessViewer)iterator.next()).addProperty(newPolicy); 
  }

  public void removePolicy(MxTreePolicy policy) {
    if (this.policies == null) {
      getPolicies(true);
    }
    this.policies.remove(policy);
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext())
      ((IMxBusinessViewer)iterator.next()).removeProperty(policy);
  }

  public void savePolicies(Context context, MQLCommand command)
  {
    ArrayList oldPolicies = getPolicies(this);

    for (int i = 0; i < this.policies.size(); i++) {
      if (!((MxTreePolicy)this.policies.get(i)).getOldName().equals(""))
        continue;
      ((MxTreePolicy)this.policies.get(i)).saveAddType(this);
    }

    if (oldPolicies != null)
      for (int i = 0; i < oldPolicies.size(); i++) {
        boolean bFound = false;
        MxTreePolicy oldPolicy = (MxTreePolicy)oldPolicies.get(i);
        for (int j = 0; j < this.policies.size(); j++) {
          MxTreePolicy policy = (MxTreePolicy)this.policies.get(j);

          if (oldPolicy.getName().equals(policy.getName())) {
            bFound = true;
            break;
          }if (oldPolicy.getName().equals(policy.getOldName())) {
            policy.saveAddType(this);
            oldPolicy.saveRemoveType(this);
            bFound = true;
            break;
          }
        }
        if (bFound)
          continue;
        oldPolicy.saveRemoveType(this);
      }
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

  public void save()
  {
    try
    {
      MQLCommand command = new MQLCommand();
      Context context = getContext();
      this.businessType.open(context);
      try
      {
        String modString = "";
        String typeName = this.businessType.getName();
        boolean changedName = !typeName.equals(getName());
        if (changedName) {
          modString = modString + " name \"" + getName() + "\"";
        }

        command.executeCommand(context, MessageFormat.format(MQL_INFO, new Object[] { this.businessType.getName() }));
        String[] info = command.getResult().trim().split("\\|");
        if (!info[0].equals(getDescription())) {
          modString = modString + " description \"" + getDescription() + "\"";
        }
        boolean oldIsHidden = info[1].equalsIgnoreCase("true");
        if (oldIsHidden != isHidden()) {
          modString = modString + (isHidden() ? " hidden" : " nothidden");
        }
        if (((this.oldParentType != null) && (!this.oldParentType.equals(this.parentType))) || ((this.parentType != null) && (!this.parentType.equals(this.oldParentType)))) {
          if (this.parentType == null)
            modString = modString + " remove derived ";
          else {
            modString = modString + " derived \"" + this.parentType.getName() + "\"";
          }
        }
        boolean oldIsAbstract = info[2].equalsIgnoreCase("true");
        if (oldIsAbstract != this.abstractType) {
          modString = modString + (this.abstractType ? " abstract true" : " abstract false");
        }

        if (!modString.equals("")) {
          command.executeCommand(context, "modify type \"" + typeName + "\" " + modString + ";");
        }

        if (changedName) {
          this.businessType = new BusinessType(this.name, getContext().getVault());
        }
        saveAttributes(context, command);
        savePolicies(context, command);
        saveTriggers(context, command);

        allTypes = null;
        refresh();
      } finally {
        this.businessType.close(context);
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
      this.children.addAll(getChildTypes(false));
      this.children.addAll(getAttributes(false));
      this.children.addAll(getPolicies(false));
      this.children.addAll(getRelationships(false, true));
      this.children.addAll(getRelationships(false, false));

      if (getParentType(false) != null) {
        this.children.add(getParentType(false));
      }
    }
    return (MxTreeBusiness[])this.children.toArray(new MxTreeBusiness[this.children.size()]);
  }

  public static void clearCache() {
    allTypes = null;
  }
}