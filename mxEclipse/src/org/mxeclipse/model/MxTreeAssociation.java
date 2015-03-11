package org.mxeclipse.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeAssociation extends MxTreeUser {
	private static ArrayList<MxTreeAssociation> allAssociations;
	protected String description;
	protected String definition;
	protected boolean hidden;
	protected static final String DESCRIPTION = "description";
	protected static final String DEFINITION = "definition";
	protected static final String HIDDEN = "hidden";
	protected static final String NOTHIDDEN = "nothidden";

	public MxTreeAssociation(String name) throws MxEclipseException, MatrixException {
		super("Association", name);
	}

	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDefinition() {
		return this.definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
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
			command.executeCommand(context, MessageFormat.format(MxTreeState.MQL_SIMPLE_PRINT, new Object[] { "Association", getName() }));

			String[] lines = command.getResult().split("\n");
			for (int i = 0; i < lines.length; i++) {
				lines[i] = lines[i].trim();

				if (lines[i].startsWith("description")) {
					this.description = lines[i].substring("description".length()).trim();
				} else if (lines[i].startsWith("definition")) {
					this.definition = lines[i].substring("definition".length()).trim();
				} else if (lines[i].equals("hidden")) {
					this.hidden = true;
				} else if (lines[i].equals("nothidden")) {
					this.hidden = false;
				}
			}
		} catch (Exception ex) {
			MxEclipseLogger.getLogger().severe(ex.getMessage());
		}
	}

	public static ArrayList<MxTreeAssociation> getAllAssociations(boolean refresh) throws MatrixException, MxEclipseException {
		if ((refresh) || (allAssociations == null)) {
			Context context = getContext();
			try {
				MQLCommand command = new MQLCommand();
				command.executeCommand(context, MessageFormat.format(MxTreeState.MQL_SIMPLE_LIST, new Object[] { "Association" }));

				String[] lines = command.getResult().split("\n");

				allAssociations = new ArrayList();
				for (int i = 0; i < lines.length; i++) {
					lines[i] = lines[i].trim();
					MxTreeAssociation role = new MxTreeAssociation(lines[i]);
					allAssociations.add(role);
				}
				Collections.sort(allAssociations);
			} catch (Exception ex) {
				MxEclipseLogger.getLogger().severe(ex.getMessage());
			}
		}
		return allAssociations;
	}

	public void save() {
		try {
			MQLCommand command = new MQLCommand();
			Context context = getContext();

			String modString = "";
			command.executeCommand(context, MessageFormat.format(MxTreeState.MQL_SIMPLE_PRINT, new Object[] { "Association", getName() }));

			String[] lines = command.getResult().split("\n");
			String oldDescription = null;
			String oldDefinition = null;
			boolean oldHidden = false;
			for (int i = 0; i < lines.length; i++) {
				lines[i] = lines[i].trim();

				if (lines[i].startsWith("description")) {
					oldDescription = lines[i].substring("description".length()).trim();
				} else if (lines[i].startsWith("definition")) {
					oldDefinition = lines[i].substring("definition".length()).trim();
				} else if (lines[i].equals("hidden")) {
					oldHidden = true;
				} else if (lines[i].equals("nothidden")) {
					oldHidden = false;
				}
			}

			boolean changedName = !this.oldName.equals(getName());
			if (changedName) {
				modString = modString + " name \"" + getName() + "\"";
			}

			if (!oldDescription.equals(getDescription())) {
				modString = modString + " description \"" + getDescription() + "\"";
			}

			if (!oldDefinition.equals(getDefinition())) {
				modString = modString + " definition \"" + getDefinition() + "\"";
			}

			if (oldHidden != isHidden()) {
				modString = modString + (isHidden() ? " hidden" : " nothidden");
			}

			if (!modString.equals("")) {
				command.executeCommand(context, "modify " + this.type.toLowerCase() + " \"" + this.oldName + "\" " + modString + ";");
			}

			refresh();
		} catch (Exception ex) {
			MxEclipseLogger.getLogger().severe(ex.getMessage());
		}
	}

	public void refresh() throws MxEclipseException, MatrixException {
		super.refresh();
		fillBasics();
	}

	public static void clearCache() {
		allAssociations = null;
	}
}