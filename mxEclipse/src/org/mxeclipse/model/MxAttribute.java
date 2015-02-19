package org.mxeclipse.model;

public class MxAttribute
{
  private String name;
  private String value;

  public MxAttribute(String name, String value)
  {
    this.name = name;
    this.value = value;
  }

  public MxAttribute() {
  }

  public String getName() {
    return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getValue() {
    return this.value;
  }
  public void setValue(String value) {
    this.value = value;
  }

  public boolean equals(Object obj) {
    if ((obj instanceof MxAttribute)) {
      MxAttribute attribute = (MxAttribute)obj;
      return (this.name.equals(attribute.name)) && (this.value.equals(attribute.value));
    }
    return false;
  }
}