package org.mxeclipse.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import matrix.db.AttributeType;
import matrix.db.AttributeTypeItr;
import matrix.db.AttributeTypeList;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeAttribute extends MxTreeBusiness
  implements ITriggerable
{
  AttributeType attribute;
  ArrayList<MxTreeRange> ranges;
  private static ArrayList<MxTreeAttribute> allAttributes;
  protected String description;
  protected String defaultValue;
  protected String attributeType;
  protected boolean hidden;
  protected boolean multiline;
  public static final String ATTRIBUTE_TYPE_STRING = "string";
  public static final String ATTRIBUTE_TYPE_BOOLEAN = "boolean";
  public static final String ATTRIBUTE_TYPE_REAL = "real";
  public static final String ATTRIBUTE_TYPE_INTEGER = "integer";
  public static final String ATTRIBUTE_TYPE_TIMESTAMP = "timestamp";
  public static final String[] ATTRIBUTE_TYPES = { "string", "boolean", 
    "real", "integer", "timestamp" };

  public MxTreeAttribute(String name)
    throws MxEclipseException, MatrixException
  {
    super("Attribute", name);
    this.attribute = new AttributeType(name);
  }

  public void refresh() throws MxEclipseException, MatrixException
  {
    super.refresh();
    this.attribute = new AttributeType(getName());
    fillBasics();
    this.ranges = getRanges(true);
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getAttributeType() {
    return this.attributeType;
  }

  public void setAttributeType(String attributeType) {
    this.attributeType = attributeType;
  }

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getDescription() {
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

  public boolean isMultiline() {
    return this.multiline;
  }

  public void setMultiline(boolean multiline) {
    this.multiline = multiline;
  }

  public static ArrayList<MxTreeAttribute> getAllAttributes(boolean refresh) throws MatrixException, MxEclipseException {
    if ((refresh) || (allAttributes == null)) {
      AttributeTypeList atl = AttributeType.getAttributeTypes(getContext(), true);
      allAttributes = new ArrayList();
      AttributeTypeItr ati = new AttributeTypeItr(atl);
      while (ati.next()) {
        AttributeType at = ati.obj();
        MxTreeAttribute attribute = new MxTreeAttribute(at.getName());
        allAttributes.add(attribute);
      }
      Collections.sort(allAttributes);
    }
    return allAttributes;
  }

  public void fillBasics() {
    try {
      Context context = getContext();
      this.attribute.open(context);
      try {
        this.name = this.attribute.getName();
        this.description = this.attribute.getDescription();
        this.defaultValue = this.attribute.getDefaultValue();
        this.attributeType = this.attribute.getDataType();
        this.hidden = this.attribute.isHidden();
      } finally {
        this.attribute.close(context);
      }
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
  }

  public ArrayList<MxTreeRange> getRanges(boolean forceRefresh) {
    if ((forceRefresh) || (this.ranges == null)) {
      try {
        Context context = getContext();
        this.attribute.open(context);
        try {
          StringList choices = this.attribute.getChoices(context);
          this.ranges = new ArrayList();
          if (choices != null)
            for (int i = 0; i < choices.size(); i++)
              this.ranges.add(new MxTreeRange((String)choices.get(i)));
        }
        finally
        {
          this.attribute.close(context);
        }
      } catch (Exception ex) {
        MxEclipseLogger.getLogger().severe(ex.getMessage());
        return new ArrayList();
      }
    }
    return this.ranges;
  }

  public void addRange() throws MxEclipseException, MatrixException {
    addRange(new MxTreeRange(""));
  }

  public void addRange(MxTreeRange newRange) {
    this.ranges.add(newRange);
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext())
      ((IMxBusinessViewer)iterator.next()).addProperty(newRange); 
  }

  public void removeRange(MxTreeRange range) {
    if (range == null) {
      getRanges(false);
    }
    this.ranges.remove(range);
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext())
      ((IMxBusinessViewer)iterator.next()).removeProperty(range);
  }

  public void save()
  {
    try {
      MQLCommand command = new MQLCommand();
      Context context = getContext();
      this.attribute.open(context);
      try
      {
        String modString = "";
        String attributeName = this.attribute.getName();
        if (!attributeName.equals(getName())) {
          modString = modString + " name " + getName();
        }
        if (!this.attribute.getDescription().equals(getDescription())) {
          modString = modString + " description " + getDescription();
        }
        if (!this.attribute.getDefaultValue().equals(getDefaultValue())) {
          modString = modString + " default " + getDefaultValue();
        }
        if (this.attribute.isHidden() != isHidden()) {
          modString = modString + (isHidden() ? " hidden" : " nothidden");
        }
        if (!modString.equals("")) {
          command.executeCommand(context, "modify attribute " + attributeName + " " + modString + ";");
        }

        StringList choices = this.attribute.getChoices(context);
        Set newRanges = new HashSet();
        Set removedRanges = new HashSet();
        Set changedRanges = new HashSet();

        for (int i = 0; i < getRanges(false).size(); i++) {
          if (!((MxTreeRange)this.ranges.get(i)).getOldName().equals(""))
            continue;
          newRanges.add((MxTreeRange)this.ranges.get(i));
        }

        if (choices != null) {
          for (int i = 0; i < choices.size(); i++) {
            boolean bFound = false;
            String choice = (String)choices.get(i);
            for (int j = 0; j < this.ranges.size(); j++) {
              MxTreeRange range = (MxTreeRange)this.ranges.get(j);
              if (choice.equals(range.getName())) {
                bFound = true;
                break;
              }if (choice.equals(range.getOldName())) {
                changedRanges.add(range);
                this.ranges.remove(j);
                bFound = true;
                break;
              }
            }
            if (bFound)
              continue;
            removedRanges.add(new MxTreeRange(choice));
          }

        }

        String addRange = "";
        String removeRange = "";
        if (newRanges.size() > 0) {
          Iterator itNew = newRanges.iterator();
          while (itNew.hasNext()) {
            MxTreeRange newAttribute = (MxTreeRange)itNew.next();
            if (!newAttribute.getName().equals("")) {
              addRange = addRange + " add range = " + newAttribute.getName();
            }
          }

        }

        if (removedRanges.size() > 0) {
          Iterator itRemoved = removedRanges.iterator();
          while (itRemoved.hasNext()) {
            MxTreeRange removedAttribute = (MxTreeRange)itRemoved.next();
            removeRange = removeRange + " remove range = " + removedAttribute.getOldName();
          }
        }

        if (changedRanges.size() > 0) {
          Iterator itChanged = changedRanges.iterator();
          while (itChanged.hasNext()) {
            MxTreeRange changedAttribute = (MxTreeRange)itChanged.next();
            addRange = addRange + " add range = " + changedAttribute.getName();
            removeRange = removeRange + " remove range = " + changedAttribute.getOldName();
          }
        }

        if (!addRange.equals("")) {
          command.executeCommand(context, "modify attribute " + getName() + " " + addRange + ";");
        }

        if (!removeRange.equals("")) {
          command.executeCommand(context, "modify attribute " + getName() + " " + removeRange + ";");
        }

        saveTriggers(context, command);

        allAttributes = null;
        refresh();
      } finally {
        this.attribute.close(context);
      }
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
  }

  protected ArrayList<MxTreeType> getTypes() throws MxEclipseException, MatrixException {
    ArrayList retTypes = new ArrayList();
    ArrayList allTypes = MxTreeType.getAllTypes(false);
    for(int i = 0; i < allTypes.size(); i++)
    {
        MxTreeType storedType = (MxTreeType)allTypes.get(i);
        ArrayList typeAttributes = storedType.getAttributes(false);
        for(Iterator iterator = typeAttributes.iterator(); iterator.hasNext();)
        {
            MxTreeAttribute typeAttribute = (MxTreeAttribute)iterator.next();
            if(name.equals(typeAttribute.getName()))
            {
                MxTreeType oneType = new MxTreeType(storedType.getName());
                oneType.setFrom(false);
                oneType.setRelType("contains");
                oneType.setParent(this);
                MxTreeType parentType = oneType.getParentType(false);
                if(parentType != null)
                {
                    ArrayList parentAttributes = parentType.getAttributes(false);
                    for(Iterator iterator1 = parentAttributes.iterator(); iterator1.hasNext();)
                    {
                        MxTreeAttribute parentAttribute = (MxTreeAttribute)iterator1.next();
                        if(parentAttribute.getName().equals(name))
                        {
                            oneType.setInherited(true);
                            break;
                        }
                    }

                }
                retTypes.add(oneType);
                break;
            }
        }

    }
    return retTypes;
  }

  protected ArrayList<MxTreeRelationship> getRelationships() throws MxEclipseException, MatrixException {
    ArrayList retRelationships = new ArrayList();
    ArrayList allRelationships = MxTreeRelationship.getAllRelationships(false);
    for(int i = 0; i < allRelationships.size(); i++)
    {
        MxTreeRelationship storedRelationship = (MxTreeRelationship)allRelationships.get(i);
        ArrayList relAttributes = storedRelationship.getAttributes(false);
        for(Iterator iterator = relAttributes.iterator(); iterator.hasNext();)
        {
            MxTreeAttribute relAttribute = (MxTreeAttribute)iterator.next();
            if(name.equals(relAttribute.getName()))
            {
                MxTreeRelationship oneType = new MxTreeRelationship(storedRelationship.getName());
                oneType.setFrom(false);
                oneType.setRelType("contains");
                oneType.setParent(this);
                retRelationships.add(oneType);
            }
        }

    }
    return retRelationships;
  }

  public MxTreeBusiness[] getChildren(boolean forceUpdate) throws MxEclipseException, MatrixException {
    if (forceUpdate) {
      this.children = null;
    }
    if (this.children == null) {
      this.children = new ArrayList();
      this.children.addAll(getTypes());
      this.children.addAll(getRelationships());
    }
    return (MxTreeBusiness[])this.children.toArray(new MxTreeBusiness[this.children.size()]);
  }

  public void propertyChanged(MxTreeBusiness task)
  {
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext())
      ((IMxBusinessViewer)iterator.next()).updateProperty(task);
  }

  public void removeChangeListener(IMxBusinessViewer viewer)
  {
    this.changeListeners.remove(viewer);
  }

  public void addChangeListener(IMxBusinessViewer viewer)
  {
    this.changeListeners.add(viewer);
  }

  public static void clearCache() {
    allAttributes = null;
  }
}