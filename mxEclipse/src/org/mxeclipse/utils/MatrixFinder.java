package org.mxeclipse.utils;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectItr;
import matrix.db.BusinessObjectList;
import matrix.db.Context;
import matrix.db.Query;
import matrix.util.MatrixException;
import matrix.util.SelectList;
import matrix.util.StringList;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.exception.MxEclipseException;

public class MatrixFinder
{
  public static BusinessObject findBusinessObject(String typeName, String objectName, String revision)
    throws MatrixException, MxEclipseException
  {
    Query query = null;
    Date date = new Date();
    Context context = getContext();
    try {
      query = new Query((new StringBuilder()).append(date).toString());
      query.open(context);
      query.setBusinessObjectType(typeName);
      query.setBusinessObjectName(objectName);
      if (revision != null) {
        query.setBusinessObjectRevision(revision);
      }

      BusinessObjectList bol = query.evaluate(context);
      BusinessObjectItr busItr = new BusinessObjectItr(bol);
      BusinessObject bo = null;

      while (busItr.next()) {
        BusinessObject tmpBo = busItr.obj();
        if ((bo == null) || (bo.getRevision().compareTo(tmpBo.getRevision()) < 0)) {
          bo = tmpBo;
        }
      }
      BusinessObject localBusinessObject1 = bo;
      return localBusinessObject1;
    } finally {
      try {
        if (query != null) query.close(context); 
      } catch (MatrixException localMatrixException1) {
    	  throw localMatrixException1;
      }
    }
  }

  public static BusinessObject findBusinessObject(String typeName, String objectName) throws MatrixException, MxEclipseException
  {
    return findBusinessObject(typeName, objectName, null);
  }

  public static DomainObject[] findDomainObjects(String typeName, String objectName)
    throws MatrixException, MxEclipseException
  {
    Query query = null;
    Context context = getContext();
    try {
      StringList selObj = new StringList();
      selObj.addElement("id");
      if ((typeName == null) || (typeName.equals(""))) {
        typeName = "*";
      }
      if ((objectName == null) || (objectName.equals(""))) {
        objectName = "*";
      }
      if ((typeName.equals("*")) && (objectName.equals("*"))) {
        throw new MxEclipseException("At least one of the two (type/name) has to be specified!");
      }
      MapList lstObjects = DomainObject.findObjects(context, typeName, objectName, "*", "*", "*", "", true, selObj);

      Iterator itObjects = lstObjects.iterator();
      ArrayList alObjectIds = new ArrayList();
      ArrayList alObjects = new ArrayList();

      while (itObjects.hasNext()) {
        Map mapObject = (Map)itObjects.next();
        String id = (String)mapObject.get("id");
        DomainObject obj = DomainObject.newInstance(context, id);
        BusinessObject boLastRevision = obj.getLastRevision(context);
        String lastRevisionId = boLastRevision.getObjectId();
        if (!alObjectIds.contains(lastRevisionId)) {
          alObjectIds.add(lastRevisionId);
          alObjects.add(new DomainObject(boLastRevision));
        }
      }
      DomainObject[] arrayOfDomainObject = (DomainObject[])alObjects.toArray(new DomainObject[alObjects.size()]);
      return arrayOfDomainObject;
    } finally {
      try {
        if (query != null) query.close(context); 
      } catch (MatrixException localMatrixException1) {
    	  throw localMatrixException1;
      }
    }
  }

  public static List<Map> findObjectsInfo(String typeName, String objectName, String revision, String[] whereAttributeNames, String[] whereAttributeValues, String[] selectAttributes, boolean latestRevision)
    throws MatrixException, MxEclipseException
  {
    Context context = getContext();
    if ((typeName == null) || (typeName.equals(""))) {
      typeName = "*";
    }
    if ((objectName == null) || (objectName.equals(""))) {
      objectName = "*";
    }
    if ((revision == null) || (revision.equals(""))) {
      revision = "*";
    }
    StringList strList = new StringList();
    for (String selectAttribute : selectAttributes) {
      strList.addElement(selectAttribute);
    }

    String where = (latestRevision) && (revision.equals("*")) ? "revision == last" : "";
    if (whereAttributeNames != null) {
      for (int i = 0; i < whereAttributeNames.length; i++) {
        if ((whereAttributeValues[i] != null) && (!whereAttributeValues[i].equals("")) && (!whereAttributeValues[i].equals("*")) && (!whereAttributeValues[i].equals("**"))) {
          if (!where.equals("")) {
            where = where + " and ";
          }
          where = where + whereAttributeNames[i];
          if (whereAttributeValues[i].contains("*"))
            where = where + " smatch '";
          else {
            where = where + " == '";
          }
          where = where + whereAttributeValues[i] + "'";
        }
      }
    }

    Object lstObjects = DomainObject.findObjects(context, typeName, objectName, revision, "*", "*", where, true, strList);
    return (List<Map>)lstObjects;
  }

  public static List<Map> findObjectsInfo(String typeName, String objectName, String[] whereAttributeNames, String[] whereAttributeValues, String[] selectAttributes, boolean latestRevision)
    throws MatrixException, MxEclipseException
  {
    return findObjectsInfo(typeName, objectName, null, whereAttributeNames, whereAttributeValues, selectAttributes, latestRevision);
  }

  public static List<Map> getRelatedObjectsInfo(String id, String relationships, String types, String[] selectObjAttributes, String[] selectRelAttributes, boolean from, int level, String objFilter, String relFilter)
    throws MatrixException, MxEclipseException
  {
    Context context = getContext();
    if ((relationships == null) || (relationships.equals(""))) {
      relationships = "*";
    }
    if ((types == null) || (types.equals(""))) {
      types = "*";
    }
    if (selectObjAttributes == null) {
      selectObjAttributes = new String[0];
    }
    if (selectRelAttributes == null) {
      selectRelAttributes = new String[0];
    }
    if (objFilter == null) {
      objFilter = "";
    }
    if (relFilter == null) {
      relFilter = "";
    }

    StringList lstObjAttrs = new StringList();
    for (String selectObjAttribute : selectObjAttributes) {
      lstObjAttrs.addElement(selectObjAttribute);
    }
    StringList lstRelAttrs = new StringList();
    for (String selectRelAttribute : selectRelAttributes) {
      lstRelAttrs.addElement(selectRelAttribute);
    }

    DomainObject domObj = DomainObject.newInstance(context, id);
    Object lstObjects = domObj.getRelatedObjects(context, relationships, types, lstObjAttrs, lstRelAttrs, !from, from, (short)level, objFilter, relFilter);
    return (List<Map>)lstObjects;
  }

  public static Map<String, String> getInfo(String id, String[] selectObjAttributes)
    throws MatrixException, MxEclipseException
  {
    Context context = getContext();
    StringList lstObjAttrs = new StringList();
    for (String selectObjAttribute : selectObjAttributes) {
      lstObjAttrs.addElement(selectObjAttribute);
    }

    DomainObject domObj = DomainObject.newInstance(context, id);
    Map mapObject = domObj.getInfo(context, lstObjAttrs);
    return mapObject;
  }

  public static DomainObject[] findDomainObjects(String typeName) throws MatrixException, MxEclipseException {
    return findDomainObjects(typeName, null);
  }

  public static MapList latestRevisionInfo(String type, String nameFilter, String[] selects)
    throws FrameworkException, MxEclipseException
  {
    SelectList selectList = new SelectList();
    for (String select : selects) {
      selectList.add(select);
    }
    return latestRevisionInfo(type, nameFilter, selectList);
  }

  public static MapList latestRevisionInfo(String type, String nameFilter, StringList selectList)
    throws FrameworkException, MxEclipseException
  {
    Context context = getContext();
    if ((nameFilter == null) || (nameFilter.equals(""))) {
      nameFilter = "*";
    }
    MapList objects = DomainObject.findObjects(context, type, nameFilter, "(revision == last)", selectList);
    return objects;
  }

  public static Context getContext() throws MxEclipseException
  {
    Context context = MxEclipsePlugin.getDefault().getContext();
    if ((context != null) && (context.isConnected())) {
      return context;
    }
    throw new MxEclipseException("No user connected to Matrix");
  }
}