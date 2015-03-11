package org.mxeclipse.model;

import java.util.ArrayList;
import matrix.util.MatrixException;
import org.mxeclipse.exception.MxEclipseException;

public class MxTreeBasic extends MxTreeAttribute {
	
	public static String[] ALL_BASICS = { "type",
		"name", 
		"revision", 
		"policy", 
		"current", 
		"owner", 
		"locker", 
		"modified", 
	"originated" };
	
	private static ArrayList<MxTreeBasic> basics;

	public MxTreeBasic(String name) throws MxEclipseException, MatrixException {
		super(name);
		this.type = "Basic";
	}

	public static ArrayList<MxTreeBasic> getAllBasics() throws MxEclipseException, MatrixException {
		if (basics == null) {
			basics = new ArrayList();
			for (int i = 0; i < ALL_BASICS.length; i++) {
				MxTreeBasic b = new MxTreeBasic(ALL_BASICS[i]);
				basics.add(b);
			}
		}
		return basics;
	}
}