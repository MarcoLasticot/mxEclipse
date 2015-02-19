package org.mxeclipse.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.Logger;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeSite extends MxTreeBusiness
{
  private static ArrayList<MxTreeSite> allSites;
  protected String description;
  protected boolean hidden;
  protected static String MQL_INFO = "print site \"{0}\" select name description hidden dump |;";
  protected static String MQL_LIST_ALL = "list site;";
  protected static final int INFO_NAME = 0;
  protected static final int INFO_DESCRIPTION = 1;
  protected static final int INFO_HIDDEN = 2;

  public MxTreeSite(String name)
    throws MxEclipseException, MatrixException
  {
    super("Site", name);
  }

  public void refresh() throws MxEclipseException, MatrixException
  {
    super.refresh();
    fillBasics();
  }

  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void fillBasics() {
    try {
      Context context = getContext();
      MQLCommand command = new MQLCommand();
      command.executeCommand(context, MessageFormat.format(MQL_INFO, new Object[] { this.name }));

      String[] info = command.getResult().trim().split("\\|");
      this.name = info[0];
      this.description = info[1];
      this.hidden = info[2].equalsIgnoreCase("true");
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
  }

  public static ArrayList getAllSites(boolean refresh)
	        throws MatrixException, MxEclipseException
	    {
	        if(refresh || allSites == null)
	        {
	            allSites = new ArrayList();
	            matrix.db.Context context = getContext();
	            MQLCommand command = new MQLCommand();
	            command.executeCommand(context, MQL_LIST_ALL);
	            String lines[] = command.getResult().split("\n");
	            for(int i = 0; i < lines.length; i++)
	            {
	                String name = lines[i] = lines[i].trim();
	                allSites.add(new MxTreeSite(name));
	            }

	        }
	        return allSites;
	    }

  public static String[] getAllVaultNames(boolean refresh) throws MatrixException, MxEclipseException {
    ArrayList allSites = getAllSites(refresh);

    String[] retVal = new String[allSites.size()];
    for (int i = 0; i < retVal.length; i++) {
      retVal[i] = ((MxTreeSite)allSites.get(i)).getName();
    }
    return retVal;
  }

  public void save()
  {
  }

  public static void clearCache() {
    allSites = null;
  }
}