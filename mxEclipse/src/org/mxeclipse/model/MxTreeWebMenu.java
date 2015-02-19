package org.mxeclipse.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeWebMenu extends MxTreeWebNavigation
{
	protected static String MQL_INFO_MENUS = "print menu \"{0}\" select menu dump |;";
	protected static String MQL_INFO_COMMANDS = "print menu \"{0}\" select command dump |;";
	protected ArrayList<MxTreeWebMenu> parentMenus;
	protected ArrayList<MxTreeWebMenu> childMenus;
	protected ArrayList<MxTreeWebCommand> childCommands;
	protected ArrayList<MxTreeWebNavigation> childrenItems;
	private static ArrayList<MxTreeWebMenu> allMenus;
	protected static final String CHILDREN = "children";

	public MxTreeWebMenu(String name)
			throws MxEclipseException, MatrixException
	{
		super("Menu", name);
	}

	public void refresh()
			throws MxEclipseException, MatrixException
	{
		super.refresh();
		fillBasics();

		this.childMenus = null;
	}

	public void fillBasics() {
		super.fillBasics();
	}

	public static ArrayList getAllMenus(boolean refresh)
			throws MatrixException, MxEclipseException
	{
		if(refresh || allMenus == null)
		{
			allMenus = new ArrayList();
			matrix.db.Context context = getContext();
			MQLCommand command = new MQLCommand();
			command.executeCommand(context, MessageFormat.format(MQL_LIST_ALL, new Object[] {
					"Menu".toLowerCase()
			}));
			String lines[] = command.getResult().split("\n");
			for(int i = 0; i < lines.length; i++)
			{
				String name = lines[i] = lines[i].trim();
				allMenus.add(new MxTreeWebMenu(name));
			}

			Collections.sort(allMenus);
		}
		return allMenus;
	}

	public static String[] getAllMenuNames(boolean refresh) throws MatrixException, MxEclipseException {
		ArrayList allMenus = getAllMenus(refresh);

		String[] retVal = new String[allMenus.size()];
		for (int i = 0; i < retVal.length; i++) {
			retVal[i] = ((MxTreeWebMenu)allMenus.get(i)).getName();
		}
		return retVal;
	}

	public static ArrayList<MxTreeWebNavigation> getChildrenItems(MxTreeWebMenu menu)
	{
		ArrayList retMenus = new ArrayList();
		try {
			Context context = getContext();

			MQLCommand command = new MQLCommand();
			command.executeCommand(context, MessageFormat.format(MxTreeState.MQL_SIMPLE_PRINT, new Object[] { "Menu", menu.getName() }));

			String[] lines = command.getResult().split("\n");
			String myLineBeginning = "children";
			boolean bProcessing = false;
			for (int i = 0; i < lines.length; i++) {
				lines[i] = lines[i].trim();
				if (lines[i].startsWith(myLineBeginning))
					bProcessing = true;
				else if ((lines[i].startsWith("Menu".toLowerCase())) || (lines[i].startsWith("Command".toLowerCase())))
				{
					if (bProcessing) {
						int indexOfBlank = lines[i].indexOf(" ");
						String t = lines[i].substring(0, indexOfBlank);
						String tname = lines[i].substring(indexOfBlank).trim();

						MxTreeWebNavigation child = null;
						if (t.equalsIgnoreCase("Menu"))
							child = new MxTreeWebMenu(tname);
						else {
							child = new MxTreeWebCommand(tname);
						}
						child.setFrom(true);
						child.setRelType("contains");
						child.setParent(menu);
						retMenus.add(child);
					}
				}
				else bProcessing = false;

			}

			return retMenus;
		} catch (Exception ex) {
			MxEclipseLogger.getLogger().severe(ex.getMessage());
		}return null;
	}

	public ArrayList<MxTreeWebNavigation> getChildrenItems(boolean forceRefresh)
	{
		if ((forceRefresh) || (this.childrenItems == null)) {
			this.childrenItems = getChildrenItems(this);
		}
		return this.childrenItems;
	}

	public void addChildItem(boolean bCommand) throws MxEclipseException, MatrixException
	{
		if (bCommand)
			addChildItem(new MxTreeWebCommand(""));
		else
			addChildItem(new MxTreeWebMenu(""));
	}

	public void insertChildItem(int index, boolean bCommand) throws MxEclipseException, MatrixException {
		if (bCommand)
			insertChildItem(new MxTreeWebCommand(""), index);
		else
			insertChildItem(new MxTreeWebMenu(""), index);
	}

	public void addChildItem(MxTreeWebNavigation newItem) {
		this.childrenItems.add(newItem);
		Iterator iterator = this.changeListeners.iterator();
		while (iterator.hasNext())
			((IMxStateViewer)iterator.next()).addProperty(newItem); 
	}

	public void insertChildItem(MxTreeWebNavigation newItem, int index) {
		this.childrenItems.add(index, newItem);
		Iterator iterator = this.changeListeners.iterator();
		while (iterator.hasNext())
			((IMxStateViewer)iterator.next()).insertProperty(newItem, index); 
	}

	public void removeChildItem(MxTreeWebNavigation item) {
		if (this.childrenItems == null) {
			getChildrenItems(false);
		}
		this.childrenItems.remove(item);
		Iterator iterator = this.changeListeners.iterator();
		while (iterator.hasNext())
			((IMxStateViewer)iterator.next()).removeProperty(item);
	}

	public static ArrayList<MxTreeWebMenu> getChildrenMenus(MxTreeWebMenu menu)
	{
		ArrayList retMenus = new ArrayList();
		try {
			Context context = getContext();
			MQLCommand command = new MQLCommand();
			command.executeCommand(context, MessageFormat.format(MQL_INFO_MENUS, new Object[] { menu.getName() }));
			String[] t = command.getResult().split("\\|");
			for (int i = 0; i < t.length; i++) {
				String tname = t[i].trim();
				if (!tname.equals("")) {
					MxTreeWebMenu a = new MxTreeWebMenu(tname);
					a.setFrom(true);
					a.setRelType("contains");
					a.setParent(menu);
					retMenus.add(a);
				}
			}
			Collections.sort(retMenus);
			return retMenus;
		} catch (Exception ex) {
			MxEclipseLogger.getLogger().severe(ex.getMessage());
		}return null;
	}

	public ArrayList<MxTreeWebMenu> getChildrenMenus(boolean forceRefresh)
	{
		if ((forceRefresh) || (this.childMenus == null)) {
			this.childMenus = getChildrenMenus(this);
		}
		return this.childMenus;
	}

	public String prepareSaveMenus(MxTreeWebMenu oldMenu)
			throws MatrixException, MxEclipseException
	{
		ArrayList subItems = getChildrenItems(false);
		ArrayList oldSubItems = oldMenu.getChildrenItems(false);
		String sAdded = "";
		String sRemoved = "";
		for(Iterator iterator = oldSubItems.iterator(); iterator.hasNext();)
		{
			MxTreeWebNavigation oldItem = (MxTreeWebNavigation)iterator.next();
			sRemoved = (new StringBuilder(String.valueOf(sRemoved))).append(" remove ").append(oldItem.getType().toLowerCase()).append(" \"").append(oldItem.getName()).append("\"").toString();
		}

		for(Iterator iterator1 = subItems.iterator(); iterator1.hasNext();)
		{
			MxTreeWebNavigation item = (MxTreeWebNavigation)iterator1.next();
			sAdded = (new StringBuilder(String.valueOf(sAdded))).append(" add ").append(item.getType().toLowerCase()).append(" \"").append(item.getName()).append("\"").toString();
		}

		return (new StringBuilder(String.valueOf(sRemoved))).append(sAdded).toString();
	}

	public void save()
	{
		try {
			MQLCommand command = new MQLCommand();
			Context context = getContext();

			String modString = "";
			MxTreeWebMenu oldMenu = new MxTreeWebMenu(this.oldName);
			oldMenu.fillBasics();

			if ((oldMenu.getName() == null) || (!oldMenu.getName().equals(getName()))) {
				modString = modString + " name \"" + getName() + "\"";
			}
			if (oldMenu.isHidden() != isHidden()) {
				modString = modString + (isHidden() ? " hidden" : "!hidden");
			}
			if ((oldMenu.getDescription() == null) || (!oldMenu.getDescription().equals(getDescription()))) {
				modString = modString + " description \"" + getDescription() + "\"";
			}
			if ((oldMenu.getLabel() == null) || (!oldMenu.getLabel().equals(getLabel()))) {
				modString = modString + " label \"" + getLabel() + "\"";
			}
			if ((oldMenu.getHref() == null) || (!oldMenu.getHref().equals(getHref()))) {
				modString = modString + " href \"" + getHref() + "\"";
			}
			if ((oldMenu.getAlt() == null) || (!oldMenu.getAlt().equals(getAlt()))) {
				modString = modString + " alt \"" + getAlt() + "\"";
			}

			String sChildMenus = prepareSaveMenus(oldMenu);
			String saveSetting = prepareSaveSettings(oldMenu);
			String saveUsers = prepareSaveUsers(oldMenu);

			modString = modString + sChildMenus + saveSetting + saveUsers;

			if (!modString.equals("")) {
				command.executeCommand(context, "modify " + this.realType + " \"" + oldMenu.getName() + "\" " + modString + ";");
			}

			this.childMenus = null;
			this.parentMenus = null;
			this.childCommands = null;
			this.childrenItems = null;
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
			this.children.addAll(getChildrenItems(false));
			this.children.addAll(getParentMenus());
		}
		return (MxTreeBusiness[])this.children.toArray(new MxTreeBusiness[this.children.size()]);
	}

	public static void clearCache() {
		allMenus = null;
		MxTreeWebNavigation.clearCache();
	}
}