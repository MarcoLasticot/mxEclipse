package org.mxeclipse.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public abstract class MxTreeWebNavigation extends MxTreeWeb
{
  protected static String MQL_INFO = "print {0} \"{1}\" select name description hidden label href alt dump |;";
  protected static String MQL_INFO_SETTING = "modify {0} \"{1}\" select setting dump |;";
  protected String description;
  protected String label;
  protected String href;
  protected String alt;
  protected boolean hidden;
  protected String realType;
  protected static ArrayList<MxTreeWebNavigation> allItems;
  protected static final int INFO_NAME = 0;
  protected static final int INFO_DESCRIPTION = 1;
  protected static final int INFO_HIDDEN = 2;
  protected static final int INFO_LABEL = 3;
  protected static final int INFO_HREF = 4;
  protected static final int INFO_ALT = 5;
  public static final String[] ALL_WEB_TYPES = { "Menu", "Command" };

  public MxTreeWebNavigation(String type, String name)
    throws MxEclipseException, MatrixException
  {
    super(type, name);
    this.realType = type.toLowerCase();
  }

  public void setType(String type) {
    this.type = type;
  }
  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getHref() {
    return this.href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public String getAlt() {
    return this.alt;
  }

  public void setAlt(String alt) {
    this.alt = alt;
  }

  public boolean isHidden() {
    return this.hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public void fillBasics() {
    try {
      Context context = getContext();
      MQLCommand command = new MQLCommand();
      command.executeCommand(context, MessageFormat.format(MQL_INFO, new Object[] { this.realType, this.name }));

      String[] info = command.getResult().trim().split("\\|");
      this.name = info[0];
      if (info.length > 1)
        this.description = info[1];
      else {
        this.description = "";
      }
      if (info.length > 2)
        this.hidden = info[2].equalsIgnoreCase("true");
      else {
        this.hidden = false;
      }
      if (info.length > 3)
        this.label = info[3];
      else {
        this.label = "";
      }
      if (info.length > 4)
        this.href = info[4];
      else {
        this.href = "";
      }
      if (info.length > 5)
        this.alt = info[5];
      else
        this.alt = "";
    }
    catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
  }

  public static ArrayList<MxTreeWebNavigation> getAllItems(boolean forceRefresh)
  {
    if ((forceRefresh) || (allItems == null)) {
      try {
        allItems = new ArrayList();
        List menus = MxTreeWebMenu.getAllMenus(forceRefresh);
        List commands = MxTreeWebCommand.getAllCommands(forceRefresh);
        for (Iterator iterator = menus.iterator(); iterator.hasNext(); ) { Object item = iterator.next();
          allItems.add((MxTreeWebNavigation)item);
        }
        for (Iterator iterator1 = commands.iterator(); iterator1.hasNext(); ) { Object item = iterator1.next();
          allItems.add((MxTreeWebNavigation)item);
        }
        Collections.sort(allItems);
      } catch (Exception ex) {
        MxEclipseLogger.getLogger().severe(ex.getMessage());
        return null;
      }
    }
    return allItems;
  }

  public static String[] getAllItemNames(boolean refresh)
    throws MatrixException, MxEclipseException
  {
    ArrayList allItems = getAllItems(refresh);

    String[] retVal = new String[allItems.size()];
    for (int i = 0; i < retVal.length; i++) {
      retVal[i] = ((MxTreeWebNavigation)allItems.get(i)).getName();
    }
    return retVal;
  }

  public void refresh()
    throws MxEclipseException, MatrixException
  {
    super.refresh();
    fillBasics();
  }

  protected ArrayList getParentMenus()
	        throws MxEclipseException, MatrixException
	    {
	        ArrayList retMenus = new ArrayList();
	        ArrayList allMenus = MxTreeWebMenu.getAllMenus(false);
	        for(int i = 0; i < allMenus.size(); i++)
	        {
	            MxTreeWebMenu storedMenu = (MxTreeWebMenu)allMenus.get(i);
	            ArrayList childItems = storedMenu.getChildrenItems(false);
	            for(Iterator iterator = childItems.iterator(); iterator.hasNext();)
	            {
	                MxTreeWebNavigation childItem = (MxTreeWebNavigation)iterator.next();
	                if(childItem.getClass() == getClass() && name.equals(childItem.getName()))
	                {
	                    MxTreeWebMenu oneMenu = new MxTreeWebMenu(storedMenu.getName());
	                    oneMenu.setFrom(false);
	                    oneMenu.setRelType("contains");
	                    oneMenu.setParent(this);
	                    retMenus.add(oneMenu);
	                }
	            }

	        }

	        return retMenus;
	    }

  public static void clearCache() {
    allItems = null;
    MxTreeWeb.clearCache();
  }
}