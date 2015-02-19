package org.mxeclipse.model;

import matrix.util.MatrixException;
import org.mxeclipse.exception.MxEclipseException;

public class MxTreeRange extends MxTreeBusiness
{
  public static final String CONDITION_EQUAL = "=";
  public static final String CONDITION_NOT_EQUAL = "!=";
  public static final String CONDITION_LESS_THAN = "<";
  public static final String CONDITION_LESS_OR_EQUAL = "<=";
  public static final String CONDITION_GREATER_THAN = ">";
  public static final String CONDITION_GREATER_OR_EQUAL = ">=";
  public static final String CONDITION_MATCH = "match";
  public static final String CONDITION_NOT_MATCH = "!match";
  public static final String CONDITION_STRING_MATCH = "smatch";
  public static final String CONDITION_NOT_STRING_MATCH = "!smatch";
  private boolean newAttribute;
  private String condition;

  public MxTreeRange(String name)
    throws MxEclipseException, MatrixException
  {
    super("Range", name);
  }

  public boolean isNewAttribute() {
    return this.newAttribute;
  }

  public String getCondition() {
    return this.condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public boolean equals(Object obj)
  {
    if ((obj instanceof MxTreeRange)) {
      MxTreeRange otherRange = (MxTreeRange)obj;
      return this.name.equals(otherRange.name);
    }
    return false;
  }
}