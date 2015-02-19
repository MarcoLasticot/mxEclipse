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

public class MxTreeState extends MxTreeBusiness
  implements ITriggerable
{
  private MxTreePolicy policy;
  protected boolean versionable;
  protected boolean revisionable;
  protected boolean promote;
  protected boolean checkoutHistory;
  protected ArrayList<MxTreeStateUserAccess> userAccess = new ArrayList();
  public static final String STATE = "state";
  public static final String PROPERTY = "property";
  public static final String NOTHIDDEN = "nothidden";
  public static final String HIDDEN = "hidden";
  public static final String VERSIONABLE = "versionable";
  public static final String REVISIONABLE = "revisionable";
  public static final String PROMOTE = "promote";
  public static final String CHECKOUT_HISTORY = "checkout history";
  public static final String FILTER = "filter";

  public MxTreeState(String name, MxTreePolicy policy)
    throws MxEclipseException, MatrixException
  {
    super("State", name);
    this.policy = policy;
  }

  public void refresh()
    throws MxEclipseException, MatrixException
  {
    super.refresh();
    fillBasics();
  }

  public boolean isVersionable()
  {
    return this.versionable;
  }

  public void setVersionable(boolean versionable) {
    this.versionable = versionable;
  }

  public boolean isRevisionable() {
    return this.revisionable;
  }

  public void setRevisionable(boolean revisionable) {
    this.revisionable = revisionable;
  }

  public boolean isPromote() {
    return this.promote;
  }

  public void setPromote(boolean promote) {
    this.promote = promote;
  }

  public boolean isCheckoutHistory() {
    return this.checkoutHistory;
  }

  public void setCheckoutHistory(boolean checkoutHistory) {
    this.checkoutHistory = checkoutHistory;
  }

  public void splitUserAccess(String lines)
  {
  }

  public MxTreePolicy getPolicy() {
    return this.policy;
  }

  public void fillBasics() {
    try {
      this.userAccess.clear();
      Context context = getContext();

      MQLCommand command = new MQLCommand();
      command.executeCommand(context, MessageFormat.format(MQL_SIMPLE_PRINT, new Object[] { "Policy", this.policy.getName() }));

      String[] lines = command.getResult().split("\n");
      String myLineBeginning = "state " + this.name;
      boolean bProcessing = false;
      for (int i = 0; i < lines.length; i++) {
        lines[i] = lines[i].trim();
        if (lines[i].startsWith(myLineBeginning)) {
          bProcessing = true;
        } else if ((lines[i].startsWith("state")) || (lines[i].startsWith("property")) || (lines[i].startsWith("nothidden")) || (lines[i].startsWith("hidden")))
        {
          if (bProcessing)
            break;
        } else {
          if (!bProcessing)
            continue;
          if (lines[i].startsWith("versionable")) {
            this.versionable = lines[i].endsWith("true");
          } else if (lines[i].startsWith("revisionable")) {
            this.revisionable = lines[i].endsWith("true");
          } else if (lines[i].startsWith("promote")) {
            this.promote = lines[i].endsWith("true");
          } else if (lines[i].startsWith("checkout history")) {
            this.checkoutHistory = lines[i].endsWith("true"); } else {
            if ((!lines[i].startsWith("owner")) && (!lines[i].startsWith("public")) && (!lines[i].startsWith("user")))
              continue;
            int indexOfBlank = lines[i].indexOf(" ");
            if (indexOfBlank > 0) {
              String userBasicType = lines[i].substring(0, indexOfBlank).trim();
              String userName = "";
              String withoutCommand = lines[i].substring(indexOfBlank).trim();
              if (userBasicType.equals("user")) {
                indexOfBlank = withoutCommand.lastIndexOf(" ");
                if (indexOfBlank > 0) {
                  userName = withoutCommand.substring(0, indexOfBlank);
                  withoutCommand = withoutCommand.substring(indexOfBlank).trim();
                }
              } else {
                userName = userBasicType;
              }
              MxTreeStateUserAccess sua = new MxTreeStateUserAccess(userBasicType, userName);
              sua.setAccessRights(withoutCommand);
              if ((i < lines.length) && (lines[(i + 1)].trim().startsWith("filter")))
              {
                withoutCommand = lines[(i + 1)].trim().substring("filter".length()).trim();
                sua.setFilter(withoutCommand);
              }
              this.userAccess.add(sua);
            }
          }
        }
      }
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
  }

  public ArrayList<MxTreeStateUserAccess> getUserAccess() {
    return this.userAccess;
  }

  public void setUserAccess(ArrayList<MxTreeStateUserAccess> userAccess) {
    this.userAccess = userAccess;
  }

  public void addUserAccess() throws MxEclipseException, MatrixException {
    addUserAccess(new MxTreeStateUserAccess("user", ""));
  }
  public void addUserAccess(MxTreeStateUserAccess newUserAccess) {
    this.userAccess.add(newUserAccess);
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext())
      ((IMxBusinessViewer)iterator.next()).addProperty(newUserAccess); 
  }

  public void removeUserAccess(MxTreeStateUserAccess oldUserAccess) {
    if (this.userAccess == null) {
      this.userAccess = new ArrayList();
    }
    this.userAccess.remove(oldUserAccess);
    Iterator iterator = this.changeListeners.iterator();
    while (iterator.hasNext())
      ((IMxBusinessViewer)iterator.next()).removeProperty(oldUserAccess);
  }

  public String prepareSaveUserAccess(MxTreeState oldState) throws MatrixException, MxEclipseException {
    ArrayList oldUserAccesses = oldState.getUserAccess();
    ArrayList userAccesses = getUserAccess();

    String sAdded = "";
    String sRemoved = "";

    for (int i = 0; i < userAccesses.size(); i++) {
      if (!((MxTreeStateUserAccess)userAccesses.get(i)).getOldName().equals(""))
        continue;
      MxTreeStateUserAccess newUserAccess = (MxTreeStateUserAccess)userAccesses.get(i);
      String ar = newUserAccess.getAccessRightCommaSeparated();
      sAdded = sAdded + " " + newUserAccess.getUserBasicType() + (newUserAccess.getUserBasicType().equals("user") ? " \"" + newUserAccess.getName() + "\" " : " ") + ar + "\n";
      if ((newUserAccess.getFilter() != null) && (!newUserAccess.getFilter().equals(""))) {
        sAdded = sAdded + " filter \"" + newUserAccess.getFilter() + "\"\n";
      }
    }

    if (oldUserAccesses != null) {
      for (int i = 0; i < oldUserAccesses.size(); i++) {
        boolean bFound = false;
        MxTreeStateUserAccess oldUserAccess = (MxTreeStateUserAccess)oldUserAccesses.get(i);
        for (int j = 0; j < userAccesses.size(); j++) {
          MxTreeStateUserAccess userAccess = (MxTreeStateUserAccess)userAccesses.get(j);
          if (oldUserAccess.getUserBasicType().equals(userAccess.getUserBasicType())) {
            if (oldUserAccess.getName().equals(userAccess.getName())) {
              bFound = true;
              String diff = userAccess.getDifferenceCommaSeparated(oldUserAccess);
              if (diff.equals("")) break;
              String ar = userAccess.getAccessRightCommaSeparated();
              sAdded = sAdded + " " + userAccess.getUserBasicType() + (userAccess.getUserBasicType().equals("user") ? " \"" + userAccess.getName() + "\" " : " ") + ar + "\n";
              if ((userAccess.getFilter() == null) || (userAccess.getFilter().equals(""))) break;
              sAdded = sAdded + " filter \"" + userAccess.getFilter() + "\"\n";

              break;
            }if (oldUserAccess.getName().equals(userAccess.getOldName())) {
              String ar = userAccess.getAccessRightCommaSeparated();
              if (ar.equals("")) break;
              sAdded = sAdded + " " + userAccess.getUserBasicType() + (userAccess.getUserBasicType().equals("user") ? " \"" + userAccess.getName() + "\" " : " ") + ar + "\n";
              if ((userAccess.getFilter() == null) || (userAccess.getFilter().equals(""))) break;
              sAdded = sAdded + " filter \"" + userAccess.getFilter() + "\"\n";

              break;
            }
          }
        }
        if (bFound)
          continue;
        sRemoved = sRemoved + " remove " + oldUserAccess.getUserBasicType() + (oldUserAccess.getUserBasicType().equals("user") ? " \"" + oldUserAccess.getName() + "\" all" : " all") + "\n";
      }

    }

    return sAdded + sRemoved;
  }

  public void save()
  {
    try {
      MQLCommand command = new MQLCommand();
      Context context = getContext();
      MxTreeState oldState = new MxTreeState(this.oldName, this.policy);
      oldState.fillBasics();

      String modString = "";
      if ((oldState.getName() == null) || (!oldState.getName().equals(getName()))) {
        modString = modString + " name \"" + getName() + "\"";
      }
      if (oldState.isVersionable() != isVersionable()) {
        modString = modString + " version " + (isVersionable() ? "true" : "false");
      }
      if (oldState.isRevisionable() != isRevisionable()) {
        modString = modString + " revision " + (isRevisionable() ? "true" : "false");
      }
      if (oldState.isPromote() != isPromote()) {
        modString = modString + " promote " + (isPromote() ? "true" : "false");
      }
      if (oldState.isCheckoutHistory() != isCheckoutHistory()) {
        modString = modString + " checkouthistory " + (isCheckoutHistory() ? "true" : "false");
      }

      modString = modString + prepareSaveUserAccess(oldState);

      if (!modString.equals("")) {
        command.executeCommand(context, "modify policy \"" + this.policy.getName() + "\" state \"" + this.oldName + "\" " + modString + ";");
      }

      saveTriggers(context, command);
      this.userAccess.clear();
      refresh();
    } catch (Exception ex) {
      MxEclipseLogger.getLogger().severe(ex.getMessage());
    }
  }
}