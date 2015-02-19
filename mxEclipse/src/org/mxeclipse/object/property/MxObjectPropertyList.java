package org.mxeclipse.object.property;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;
import matrix.db.Attribute;
import matrix.db.AttributeItr;
import matrix.db.AttributeList;
import matrix.db.BusinessObjectAttributes;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreeDomainObject;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxObjectPropertyList
{
	private Vector<MxObjectProperty> tasks = new Vector();
	private Set changeListeners = new HashSet();
	private String infoType;
	MxTreeDomainObject treeDomainObject;

	public MxObjectPropertyList(String infoType)
	{
		this.infoType = infoType;
	}

	public MxObjectPropertyList(MxTreeDomainObject treeDomainObject, String infoType)
	{
		this.infoType = infoType;
		this.treeDomainObject = treeDomainObject;
		initData();
	}

	public void setObject(MxTreeDomainObject treeDomainObject, String infoType) {
		this.infoType = infoType;
		this.treeDomainObject = treeDomainObject;
		initData();
	}

	private void initData()
	{
		try {
			this.tasks.clear();
			if (this.treeDomainObject != null) {
				String id = this.treeDomainObject.getId();
				if (id != null) {
					Context context = MxEclipsePlugin.getDefault().getContext();
					if (context != null) {
						if (id.indexOf("|") >= 0) {
							id = id.substring(0, id.indexOf("|"));
						}
						DomainObject domObj = DomainObject.newInstance(context, id);
						if (this.infoType.equals("Basic")) {
							this.tasks.add(new MxObjectProperty("Id", id, MxObjectProperty.TYPE_BASIC));
							this.tasks.add(new MxObjectProperty("Type", domObj.getInfo(context, "type"), MxObjectProperty.TYPE_BASIC));
							this.tasks.add(new MxObjectProperty("Name", domObj.getInfo(context, "name"), MxObjectProperty.TYPE_BASIC));
							this.tasks.add(new MxObjectProperty("Revision", domObj.getInfo(context, "revision"), MxObjectProperty.TYPE_BASIC));
							this.tasks.add(new MxObjectProperty("Description", domObj.getInfo(context, "description"), MxObjectProperty.TYPE_BASIC));
							this.tasks.add(new MxObjectProperty("Policy", domObj.getInfo(context, "policy"), MxObjectProperty.TYPE_BASIC));
							this.tasks.add(new MxObjectProperty("State", domObj.getInfo(context, "current"), MxObjectProperty.TYPE_BASIC));
						} else if (this.infoType.equals("Attributes")) {
							BusinessObjectAttributes boa = domObj.getAttributes(context, true);
							AttributeList al = boa.getAttributes();
							AttributeItr itAttribute = new AttributeItr(al);
							while (itAttribute.next()) {
								Attribute attribute = itAttribute.obj();
								String key = attribute.getName();
								String val = attribute.getValue();
								this.tasks.add(new MxObjectProperty(key, val, MxObjectProperty.TYPE_ATTRIBUTE));
							}
							Collections.sort(this.tasks);
						} else if (this.infoType.equals("Relationship")) {
							if (this.treeDomainObject.getDomainRelationship() != null) {
								DomainRelationship domRel = this.treeDomainObject.getDomainRelationship();
								Map attributes = domRel.getAttributeMap(context, true);
								Iterator itAttributes = attributes.keySet().iterator();
								while (itAttributes.hasNext()) {
									String key = (String)itAttributes.next();
									String val = (String)attributes.get(key);
									this.tasks.add(new MxObjectProperty(key, val, MxObjectProperty.TYPE_ATTRIBUTE));
								}
								Collections.sort(this.tasks);
								this.tasks.insertElementAt(new MxObjectProperty("Id", this.treeDomainObject.getRelId(), MxObjectProperty.TYPE_BASIC), 0);
								this.tasks.insertElementAt(new MxObjectProperty("Type", this.treeDomainObject.getRelType(), MxObjectProperty.TYPE_BASIC), 1);
								this.tasks.insertElementAt(new MxObjectProperty("Name", this.treeDomainObject.getRelName(), MxObjectProperty.TYPE_BASIC), 2);
								this.tasks.insertElementAt(new MxObjectProperty("From", this.treeDomainObject.getRelFromName(), MxObjectProperty.TYPE_BASIC), 3);
								this.tasks.insertElementAt(new MxObjectProperty("To", this.treeDomainObject.getRelToName(), MxObjectProperty.TYPE_BASIC), 4);
							}
						} else if (this.infoType.equals("History")) {
							int historyLimit = MxEclipsePlugin.getDefault().getPreferenceStore().getInt("HistoryLimit");
							StringList lstHistory = domObj.getHistory(context);
							int start = lstHistory.size() > historyLimit ? lstHistory.size() - historyLimit + 1 : 1;
							for(int i = start; i <= lstHistory.size(); i++)
							{
								String history = (String)lstHistory.get(i - 1);
								tasks.add(0, new MxObjectProperty((new StringBuilder()).append(i).toString(), history, MxObjectProperty.TYPE_HISTORY));
							}
						}
					}
				}
			}
		}
		catch (MatrixException ex) {
			ex.printStackTrace();
		}
	}

	public Vector getTasks()
	{
		return this.tasks;
	}

	public void save()
	{
		Context context = MxEclipsePlugin.getDefault().getContext();
		try {
			if (context != null) {
				DomainObject domObject = this.treeDomainObject.getDomainObject();
				boolean basicChanged = false;
				Map outMap = new HashMap();
				for (int i = 0; i < this.tasks.size(); i++) {
					MxObjectProperty property = (MxObjectProperty)this.tasks.get(i);
					if ((property.isModified()) && (property.getType().equals(MxObjectProperty.TYPE_ATTRIBUTE)))
						outMap.put(property.getName(), property.getValue());
					else if ((property.isModified()) && (property.getType().equals(MxObjectProperty.TYPE_BASIC))) {
						if (property.getName().equals(MxObjectProperty.BASIC_TYPE)) {
							this.treeDomainObject.setType(property.getValue());
							basicChanged = true;
						} else if (property.getName().equals(MxObjectProperty.BASIC_NAME)) {
							this.treeDomainObject.setName(property.getValue());
							basicChanged = true;
						} else if (property.getName().equals(MxObjectProperty.BASIC_REVISION)) {
							this.treeDomainObject.setRevision(property.getValue());
							basicChanged = true;
						} else if (property.getName().equals(MxObjectProperty.BASIC_POLICY)) {
							this.treeDomainObject.setPolicy(property.getValue());
							basicChanged = true;
						} else if (property.getName().equals(MxObjectProperty.BASIC_STATE)) {
							this.treeDomainObject.setCurrent(property.getValue());
							basicChanged = true;
						}
					}
				}
				if (basicChanged)
					try
				{
						domObject.open(context);
						domObject.change(context, this.treeDomainObject.getType(), this.treeDomainObject.getName(), this.treeDomainObject.getRevision(), domObject.getVault(), this.treeDomainObject.getPolicy());
						if (!domObject.getInfo(context, "current").equals(this.treeDomainObject.getCurrent()))
							domObject.setState(context, this.treeDomainObject.getCurrent());
				}
				catch (MatrixException e) {
					MxEclipseLogger.getLogger().severe("Couldn't change matrix object: " + e);

					if (domObject.isOpen())
						try {
							domObject.close(context);
						}
					catch (MatrixException localMatrixException1)
					{
					}
				}
				finally
				{
					if (domObject.isOpen())
						try {
							domObject.close(context);
						}
					catch (MatrixException localMatrixException2)
					{
					}
				}
				if (outMap.keySet().size() > 0) {
					if (this.infoType.equals("Attributes"))
						this.treeDomainObject.getDomainObject().setAttributeValues(context, outMap);
					else if (this.infoType.equals("Relationship")) {
						this.treeDomainObject.getDomainRelationship().setAttributeValues(context, outMap);
					}
				}
			}
			for (int i = 0; i < this.tasks.size(); i++)
				((MxObjectProperty)this.tasks.get(i)).setModified(false);
		}
		catch (FrameworkException e) {
			MessageDialog.openInformation(null, "Save Business Object/Relationship", "Can't save information to the database: " + e.getMessage());
		}
	}

	public void propertyChanged(MxObjectProperty task)
	{
		Iterator iterator = this.changeListeners.iterator();
		while (iterator.hasNext())
			((IMxObjectPropertyViewer)iterator.next()).updateProperty(task);
	}

	public void removeChangeListener(IMxObjectPropertyViewer viewer)
	{
		this.changeListeners.remove(viewer);
	}

	public void addChangeListener(IMxObjectPropertyViewer viewer)
	{
		this.changeListeners.add(viewer);
	}
}