package org.mxeclipse.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeIndex extends MxTreeBusiness
{
  private static ArrayList<MxTreeIndex> allIndices;
  protected String description;
  protected boolean unique;
  protected boolean enabled;
  protected static String MQL_INFO = "print index \"{0}\" select name description unique enabled dump |;";
  protected static String MQL_INFO_FIELD = "print index \"{0}\" select field dump |;";
  protected static String MQL_ADD_FIELD = "modify index \"{0}\" add field \"{1}\";";
  protected static String MQL_REMOVE_FIELD = "modify index \"{0}\" remove field \"{1}\";";
  protected static String MQL_LIST_ALL = "list index;";
  protected static final int INFO_NAME = 0;
  protected static final int INFO_DESCRIPTION = 1;
  protected static final int INFO_UNIQUE = 2;
  protected static final int INFO_ENABLED = 3;

  public MxTreeIndex(String name)
    throws MxEclipseException, MatrixException
  {
    super("Index", name);
  }

  public void refresh() throws MxEclipseException, MatrixException
  {
    super.refresh();
    fillBasics();
    this.attributes = null;
  }

  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isUnique() {
    return this.unique;
  }

  public void setUnique(boolean unique) {
    this.unique = unique;
  }

  public void fillBasics() {
    try {
      Context context = getContext();
      MQLCommand command = new MQLCommand();
      command.executeCommand(context, MessageFormat.format(MQL_INFO, new Object[] { this.name }));

      String[] info = command.getResult().trim().split("\\|");
      this.name = info[0];
      this.description = info[1];
      this.unique = info[2].equalsIgnoreCase("true");
      this.enabled = info[3].equalsIgnoreCase("true");
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
  }

  public static ArrayList getAllIndices(boolean refresh)
	        throws MatrixException, MxEclipseException
	    {
	        if(refresh || allIndices == null)
	        {
	            allIndices = new ArrayList();
	            Context context = getContext();
	            MQLCommand command = new MQLCommand();
	            command.executeCommand(context, MQL_LIST_ALL);
	            String lines[] = command.getResult().split("\n");
	            for(int i = 0; i < lines.length; i++)
	            {
	                String name = lines[i] = lines[i].trim();
	                allIndices.add(new MxTreeIndex(name));
	            }

	        }
	        return allIndices;
	    }

  public static String[] getAllIndicesNames(boolean refresh) throws MatrixException, MxEclipseException {
    ArrayList allIndices = getAllIndices(refresh);

    String[] retVal = new String[allIndices.size()];
    for (int i = 0; i < retVal.length; i++) {
      retVal[i] = ((MxTreeIndex)allIndices.get(i)).getName();
    }
    return retVal;
  }

  public static ArrayList<MxTreeAttribute> getAttributes(MxTreeIndex index) {
    ArrayList retFields = new ArrayList();
    try {
      Context context = getContext();
      MQLCommand command = new MQLCommand();

      command.executeCommand(context, MessageFormat.format(MQL_INFO_FIELD, new Object[] { index.getName() }));
      String[] t = command.getResult().split("\\|");
      for (int i = 0; i < t.length; i++) {
        String tname = t[i].trim();
        if (!tname.equals(""))
          if (tname.startsWith("attribute[")) {
            String attributeName = tname.substring("attribute[".length(), tname.length() - 1);
            retFields.add(new MxTreeAttribute(attributeName));
          } else {
            retFields.add(new MxTreeBasic(tname));
          }
      }
    }
    catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
    return retFields;
  }

  public ArrayList<MxTreeAttribute> getAttributes(boolean forceRefresh) {
    if ((forceRefresh) || (this.attributes == null)) {
      this.attributes = getAttributes(this);
    }
    return this.attributes;
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

  public void saveAttributes(Context context, MQLCommand command) throws MatrixException {
    ArrayList oldAttributes = getAttributes(this);

    for (int i = 0; i < this.attributes.size(); i++) {
      MxTreeAttribute attr = (MxTreeAttribute)this.attributes.get(i);
      if (attr.getOldName().equals("")) {
        String attrName = "attribute[" + attr.getName() + "]";
        command.executeCommand(context, MessageFormat.format(MQL_ADD_FIELD, new Object[] { getName(), attrName }));
      }
    }

    String mql = "";
    if (oldAttributes != null)
      for (int i = 0; i < oldAttributes.size(); i++) {
        boolean bFound = false;
        MxTreeAttribute oldAttribute = (MxTreeAttribute)oldAttributes.get(i);
        String oldAttrName = "attribute[" + oldAttribute.getName() + "]";
        for (int j = 0; j < this.attributes.size(); j++) {
          MxTreeAttribute attribute = (MxTreeAttribute)this.attributes.get(j);
          String attrName = "attribute[" + attribute.getName() + "]";

          if (oldAttribute.getName().equals(attribute.getName())) {
            bFound = true;
            break;
          }if (oldAttribute.getName().equals(attribute.getOldName())) {
            command.executeCommand(context, MessageFormat.format(MQL_ADD_FIELD, new Object[] { getName(), attrName }));
            command.executeCommand(context, MessageFormat.format(MQL_REMOVE_FIELD, new Object[] { getName(), oldAttrName }));
            bFound = true;
            break;
          }
        }
        if (bFound)
          continue;
        command.executeCommand(context, MessageFormat.format(MQL_REMOVE_FIELD, new Object[] { getName(), oldAttrName }));
      }
  }

  public void save()
  {
    try
    {
      MQLCommand command = new MQLCommand();
      Context context = getContext();
      MxTreeIndex oldIndex = new MxTreeIndex(this.oldName);
      oldIndex.fillBasics();

      String modString = "";
      if ((oldIndex.getName() == null) || (!oldIndex.getName().equals(getName()))) {
        modString = modString + " name \"" + getName() + "\"";
      }
      if ((oldIndex.getDescription() == null) || (!oldIndex.getDescription().equals(getDescription()))) {
        modString = modString + " description \"" + getDescription() + "\"";
      }
      if (oldIndex.isUnique() != isUnique()) {
        modString = modString + " " + (isUnique() ? "" : "!") + "unique";
      }

      if (!modString.equals("")) {
        command.executeCommand(context, "modify index \"" + this.oldName + "\" " + modString + ";");
      }

      saveAttributes(context, command);
      refresh();
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
  }

  public void setEnabled(boolean enabled) throws Exception {
    MQLCommand command = new MQLCommand();
    Context context = getContext();
    String sEnable = enabled ? "enable" : "disable";
    if (this.oldName == null) {
      throw new MxEclipseException("Please save the index first!");
    }
    command.executeCommand(context, sEnable + " index \"" + this.oldName + "\";");
    refresh();
    this.enabled = enabled;
  }

  public void validate() throws Exception {
    MQLCommand command = new MQLCommand();
    Context context = getContext();
    if (this.oldName == null) {
      throw new MxEclipseException("Please save the index first!");
    }
    command.executeCommand(context, "validate level 4 index \"" + this.oldName + "\";");
    refresh();
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public static void clearCache() {
    allIndices = null;
  }
}