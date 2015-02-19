package org.mxeclipse.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeWebCommand extends MxTreeWebNavigation
{
  protected ArrayList<MxTreeWebCommand> parentMenus;
  private static ArrayList<MxTreeWebCommand> allCommands;

  public MxTreeWebCommand(String name)
    throws MxEclipseException, MatrixException
  {
    super("Command", name);
  }

  public void refresh()
    throws MxEclipseException, MatrixException
  {
    super.refresh();
    fillBasics();
  }

  public void fillBasics()
  {
    super.fillBasics();
  }

  public static ArrayList getAllCommands(boolean refresh)
	        throws MatrixException, MxEclipseException
	    {
	        if(refresh || allCommands == null)
	        {
	            allCommands = new ArrayList();
	            matrix.db.Context context = getContext();
	            MQLCommand command = new MQLCommand();
	            command.executeCommand(context, MessageFormat.format(MQL_LIST_ALL, new Object[] {
	                "Command".toLowerCase()
	            }));
	            String lines[] = command.getResult().split("\n");
	            for(int i = 0; i < lines.length; i++)
	            {
	                String name = lines[i] = lines[i].trim();
	                allCommands.add(new MxTreeWebCommand(name));
	            }

	            Collections.sort(allCommands);
	        }
	        return allCommands;
	    }


  public static String[] getAllCommandNames(boolean refresh) throws MatrixException, MxEclipseException {
    ArrayList allCommands = getAllCommands(refresh);

    String[] retVal = new String[allCommands.size()];
    for (int i = 0; i < retVal.length; i++) {
      retVal[i] = ((MxTreeWebCommand)allCommands.get(i)).getName();
    }
    return retVal;
  }

  public void save()
  {
    try
    {
      MQLCommand command = new MQLCommand();
      Context context = getContext();

      String modString = "";
      MxTreeWebCommand oldCommand = new MxTreeWebCommand(this.oldName);
      oldCommand.fillBasics();

      if ((oldCommand.getName() == null) || (!oldCommand.getName().equals(getName()))) {
        modString = modString + " name \"" + getName() + "\"";
      }
      if (oldCommand.isHidden() != isHidden()) {
        modString = modString + (isHidden() ? " hidden" : "!hidden");
      }
      if ((oldCommand.getDescription() == null) || (!oldCommand.getDescription().equals(getDescription()))) {
        modString = modString + " description \"" + getDescription() + "\"";
      }
      if ((oldCommand.getLabel() == null) || (!oldCommand.getLabel().equals(getLabel()))) {
        modString = modString + " label \"" + getLabel() + "\"";
      }
      if ((oldCommand.getHref() == null) || (!oldCommand.getHref().equals(getHref()))) {
        modString = modString + " href \"" + getHref() + "\"";
      }
      if ((oldCommand.getAlt() == null) || (!oldCommand.getAlt().equals(getAlt()))) {
        modString = modString + " alt \"" + getAlt() + "\"";
      }

      modString = modString + prepareSaveSettings(oldCommand);
      modString = modString + prepareSaveUsers(oldCommand);

      if (!modString.equals("")) {
        command.executeCommand(context, "modify " + this.realType + " \"" + oldCommand.getName() + "\" " + modString + ";");
      }

      this.parentMenus = null;
      clearCache();
      refresh();
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
      this.children.addAll(getParentMenus());
      this.children.addAll(getUsers(false));
    }
    return (MxTreeBusiness[])this.children.toArray(new MxTreeBusiness[this.children.size()]);
  }

  public static void clearCache() {
    allCommands = null;
    MxTreeWebNavigation.clearCache();
  }
}