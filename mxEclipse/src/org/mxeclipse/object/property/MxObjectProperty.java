package org.mxeclipse.object.property;

public class MxObjectProperty
  implements Comparable
{
  public static String TYPE_BASIC = "Basic";
  public static String TYPE_ATTRIBUTE = "Attribute";
  public static String TYPE_HISTORY = "History";

  public static String BASIC_ID = "Id";
  public static String BASIC_NAME = "Name";
  public static String BASIC_REVISION = "Revision";
  public static String BASIC_TYPE = "Type";
  public static String BASIC_POLICY = "Policy";
  public static String BASIC_STATE = "State";
  private String name;
  private String value;
  private boolean modified;
  private String type;

  public MxObjectProperty(String name, String value, String type)
  {
    this.name = name;
    this.value = value;
    this.type = type;
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getValue()
  {
    return this.value;
  }

  public void setValue(String value)
  {
    if ((this.type.equals(TYPE_ATTRIBUTE)) || ((this.type.equals(TYPE_BASIC)) && (!this.name.equals(BASIC_ID)))) {
      if ((value == null) || (!value.equals(this.value))) {
        this.modified = true;
      }
      this.value = value;
    }
  }

  public boolean isModified() {
    return this.modified;
  }

  public void setModified(boolean modified) {
    this.modified = modified;
  }

  public String getType() {
    return this.type;
  }
  public int compareTo(Object o) {
    MxObjectProperty other = (MxObjectProperty)o;
    return getName().compareToIgnoreCase(other.getName());
  }
}