package org.mxeclipse.model;

import java.util.ArrayList;

public class MxObjectSearchCriteria
{
  private String id;
  private String type;
  private String name;
  private String revision;
  private String policy;
  private String state;
  private String description;
  private boolean appendResults;
  private String searchKind;
  private ArrayList<MxAttribute> attributes;
  public static final String KIND_STANDARD = "standard";
  public static final String KIND_FIND_LIKE = "findlike";

  public MxObjectSearchCriteria(String id, String type, String name, String revision, String policy, String state, String description, boolean appendResults, String searchKind, ArrayList<MxAttribute> attributes)
  {
    this.id = id;
    this.type = type;
    this.name = name;
    this.revision = revision;
    this.policy = policy;
    this.state = state;
    this.description = description;
    this.appendResults = appendResults;
    this.attributes = attributes;
    this.searchKind = searchKind;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRevision() {
    return this.revision;
  }

  public void setRevision(String revision) {
    this.revision = revision;
  }

  public String getPolicy() {
    return this.policy;
  }

  public void setPolicy(String policy) {
    this.policy = policy;
  }

  public String getState() {
    return this.state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isAppendResults() {
    return this.appendResults;
  }

  public void setAppendResults(boolean appendResults) {
    this.appendResults = appendResults;
  }

  public ArrayList<MxAttribute> getAttributes() {
    return this.attributes;
  }

  public void setAttributes(ArrayList<MxAttribute> attributes) {
    this.attributes = attributes;
  }

  public String getSearchKind() {
    return this.searchKind;
  }

  public void setSearchKind(String searchKind) {
    this.searchKind = searchKind;
  }

  public String toString()
  {
    String retVal = (this.id != null) && (!this.id.equals("")) ? "id=" + this.id : "";
    if ((this.type != null) && (!this.type.equals(""))) {
      if (!retVal.equals("")) {
        retVal = retVal + ",";
      }
      retVal = retVal + "type=" + this.type;
    }
    if ((this.name != null) && (!this.name.equals(""))) {
      if (!retVal.equals("")) {
        retVal = retVal + ",";
      }
      retVal = retVal + "name=" + this.name;
    }
    if ((this.revision != null) && (!this.revision.equals(""))) {
      if (!retVal.equals("")) {
        retVal = retVal + ",";
      }
      retVal = retVal + "revision=" + this.revision;
    }
    if ((this.policy != null) && (!this.policy.equals(""))) {
      if (!retVal.equals("")) {
        retVal = retVal + ",";
      }
      retVal = retVal + "policy=" + this.policy;
    }
    if ((this.state != null) && (!this.state.equals(""))) {
      if (!retVal.equals("")) {
        retVal = retVal + ",";
      }
      retVal = retVal + "state=" + this.state;
    }
    if ((this.description != null) && (!this.description.equals(""))) {
      if (!retVal.equals("")) {
        retVal = retVal + ",";
      }
      retVal = retVal + "description=" + this.description;
    }
    if (this.attributes != null) {
      for (MxAttribute attribute : this.attributes) {
        if ((attribute.getValue() != null) && (!attribute.getValue().equals(""))) {
          if (!retVal.equals("")) {
            retVal = retVal + ",";
          }
          retVal = retVal + attribute.getName() + "=" + attribute.getValue();
        }
      }

    }

    return retVal;
  }
}