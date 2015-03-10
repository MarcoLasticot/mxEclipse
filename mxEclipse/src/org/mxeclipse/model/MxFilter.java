package org.mxeclipse.model;

import java.util.ArrayList;

public class MxFilter {
	private String types;
	private String relTypes;
	private boolean relFrom;
	private boolean relTo;
	private ArrayList<MxAttribute> lstAttributes = new ArrayList();
	private ArrayList<MxAttribute> lstRelAttributes = new ArrayList();

	public String getRelTypes() {
		return this.relTypes;
	}

	public void setRelTypes(String relTypes) {
		this.relTypes = relTypes;
	}

	public String getTypes() {
		return this.types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public void addAttribute(MxAttribute newAttribute) {
		if (!this.lstAttributes.contains(newAttribute)) {
			this.lstAttributes.add(newAttribute);
		}
	}

	public void addAttribute(String name, String value) {
		MxAttribute newAttribute = new MxAttribute(name, value);
		addAttribute(newAttribute);
	}

	public ArrayList<MxAttribute> getAttributes() {
		return this.lstAttributes;
	}

	public void addRelAttribute(MxAttribute newAttribute) {
		if (!this.lstRelAttributes.contains(newAttribute)) {
			this.lstRelAttributes.add(newAttribute);
		}
	}

	public void addRelAttribute(String name, String value) {
		MxAttribute newAttribute = new MxAttribute(name, value);
		addRelAttribute(newAttribute);
	}

	public ArrayList<MxAttribute> getRelAttributes() {
		return this.lstRelAttributes;
	}

	public boolean isRelFrom() {
		return this.relFrom;
	}

	public void setRelFrom(boolean relFrom) {
		this.relFrom = relFrom;
	}

	public boolean isRelTo() {
		return this.relTo;
	}

	public void setRelTo(boolean relTo) {
		this.relTo = relTo;
	}
}