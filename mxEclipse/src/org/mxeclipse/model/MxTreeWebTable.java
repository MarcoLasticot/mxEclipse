package org.mxeclipse.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.db.Table;
import matrix.util.MatrixException;
import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeWebTable extends MxTreeBusiness
{
	Table table;
	ArrayList<MxTreeWebColumn> columns;
	private static ArrayList<MxTreeWebTable> allTables;
	protected static String MQL_INFO = "print table \"{0}\" system select name description hidden dump |;";
	protected static String MQL_INFO_COLUMN = "print table \"{0}\" system select column dump |;";
	protected static String MQL_INSERT_COLUMN = "modify table \"{0}\" system add column \"{1}\" order {2};";
	protected static String MQL_REMOVE_COLUMN = "modify table \"{0}\" system remove column \"{1}\";";
	protected static final int INFO_NAME = 0;
	protected static final int INFO_DESCRIPTION = 1;
	protected static final int INFO_HIDDEN = 2;
	protected String description;
	protected boolean hidden;

	public MxTreeWebTable(String name)
			throws MxEclipseException, MatrixException
	{
		super("Table", name);
	}

	public void refresh() throws MxEclipseException, MatrixException
	{
		super.refresh();
		fillBasics();
		this.columns = null;
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

	public void addColumn() throws MxEclipseException, MatrixException
	{
		addColumn(new MxTreeWebColumn("", this, getColumns(false).size()));
	}
	public void insertColumn(int index) throws MxEclipseException, MatrixException {
		for (int i = index; i < getColumns(false).size(); i++) {
			((MxTreeWebColumn)getColumns(false).get(i)).setOrder(i + 1);
		}
		insertColumn(new MxTreeWebColumn("", this, index), index);
	}
	public void addColumn(MxTreeWebColumn newColumn) {
		this.columns.add(newColumn);
		Iterator iterator = this.changeListeners.iterator();
		while (iterator.hasNext())
			((IMxStateViewer)iterator.next()).addProperty(newColumn); 
	}

	public void insertColumn(MxTreeWebColumn newColumn, int index) {
		this.columns.add(index, newColumn);
		Iterator iterator = this.changeListeners.iterator();
		while (iterator.hasNext())
			((IMxStateViewer)iterator.next()).insertProperty(newColumn, index); 
	}

	public void removeColumn(MxTreeWebColumn column) {
		if (this.columns == null) {
			getColumns(false);
		}
		this.columns.remove(column);
		Iterator iterator = this.changeListeners.iterator();
		while (iterator.hasNext())
			((IMxStateViewer)iterator.next()).removeProperty(column);
	}

	public String[] getColumnNames(boolean forceRefresh) {
		ArrayList columns = getColumns(forceRefresh);
		String[] retVal = new String[columns.size()];
		for (int i = 0; i < retVal.length; i++) {
			retVal[i] = ((MxTreeWebColumn)columns.get(i)).getName();
		}
		return retVal;
	}

	public static ArrayList<MxTreeWebColumn> getColumns(MxTreeWebTable table) {
		ArrayList retColumns = new ArrayList();
		try {
			Context context = getContext();
			MQLCommand command = new MQLCommand();

			command.executeCommand(context, MessageFormat.format(MQL_INFO_COLUMN, new Object[] { table.getName() }));
			String[] t = command.getResult().split("\\|");
			for (int i = 0; i < t.length; i++) {
				String tname = t[i].trim();
				if (!tname.equals("")) {
					MxTreeWebColumn column = new MxTreeWebColumn(tname, table, i);
					retColumns.add(column);
				}
			}
		} catch (Exception ex) {
			MxEclipseLogger.getLogger().severe(ex.getMessage());
		}
		return retColumns;
	}

	public ArrayList<MxTreeWebColumn> getColumns(boolean forceRefresh) {
		if ((forceRefresh) || (this.columns == null)) {
			this.columns = getColumns(this);
		}
		return this.columns;
	}

	public String prepareSaveColumns(Context context, MQLCommand command) throws MatrixException, MxEclipseException {
		String retVal = "";
		for (int i = 0; i < this.columns.size(); i++) {
			((MxTreeWebColumn)this.columns.get(i)).setOrder(i);
			retVal = retVal + ((MxTreeWebColumn)this.columns.get(i)).prepareSave();
		}
		return retVal;
	}

	public static ArrayList getAllTables(boolean refresh)
			throws MatrixException, MxEclipseException
	{
		if(refresh || allTables == null)
		{
			allTables = new ArrayList();
			Context context = getContext();
			MQLCommand command = new MQLCommand();
			command.executeCommand(context, MQL_LIST_ALL);
			String lines[] = command.getResult().split("\n");
			for(int i = 0; i < lines.length; i++)
			{
				String name = lines[i] = lines[i].trim();
				allTables.add(new MxTreeWebTable(name));
			}

		}
		return allTables;
	}

	public void save()
	{
		try {
			MQLCommand command = new MQLCommand();
			Context context = getContext();

			MxTreeWebTable oldTable = new MxTreeWebTable(this.oldName);
			oldTable.fillBasics();

			String modString = "";
			if ((oldTable.getName() == null) || (!oldTable.getName().equals(getName()))) {
				modString = modString + " name \"" + getName() + "\"";
			}
			if ((oldTable.getDescription() == null) || (!oldTable.getDescription().equals(getDescription()))) {
				modString = modString + " description \"" + getDescription() + "\"";
			}
			if (oldTable.isHidden() != isHidden()) {
				modString = modString + " " + (isHidden() ? "" : "!") + "hidden";
			}

			modString = modString + prepareSaveColumns(context, command);

			if (!modString.equals("")) {
				command.executeCommand(context, "delete table \"" + this.oldName + "\" system;");
				command.executeCommand(context, "add table \"" + this.oldName + "\" system " + modString + ";");
			}

			refresh();
		}
		catch (Exception ex) {
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
			this.children.addAll(getColumns(false));
		}
		return (MxTreeBusiness[])this.children.toArray(new MxTreeBusiness[this.children.size()]);
	}

	public static void clearCache() {
		allTables = null;
	}
}