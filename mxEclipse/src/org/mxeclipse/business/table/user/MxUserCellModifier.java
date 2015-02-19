package org.mxeclipse.business.table.user;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeStateUserAccess;
import org.mxeclipse.model.MxTreeUser;
import org.mxeclipse.views.MxEclipseBusinessView;

public class MxUserCellModifier
implements ICellModifier
{
	MxUserComposite composite;

	public MxUserCellModifier(MxUserComposite composite)
	{
		this.composite = composite;
	}

	public boolean canModify(Object element, String property)
	{
		if ((element instanceof MxTreeStateUserAccess)) {
			MxTreeStateUserAccess userAccess = (MxTreeStateUserAccess)element;
			return userAccess.getUserBasicType().equals("user");
		}
		return (element instanceof MxTreeUser);
	}

	public Object getValue(Object element, String property)
	{
		Object result = "";
		String userType = null;
		String userName = null;
		if(element == null)
			return Integer.valueOf(-1);
		if(element instanceof MxTreeStateUserAccess)
		{
			MxTreeStateUserAccess userAccess = (MxTreeStateUserAccess)element;
			if(userAccess.getUser() == null)
				return Integer.valueOf(-1);
			userType = userAccess.getUserType();
			userName = userAccess.getName();
		} else
			if(element instanceof MxTreeUser)
			{
				MxTreeUser user = (MxTreeUser)element;
				userType = user.getType();
				userName = user.getName();
			} else
			{
				return Integer.valueOf(-1);
			}
		try
		{
			if(property.equals("Type"))
			{
				result = Integer.valueOf(-1);
				for(int i = 0; i < MxTreeUser.ALL_USER_TYPES.length; i++)
				{
					if(!MxTreeUser.ALL_USER_TYPES[i].equals(userType))
						continue;
					result = Integer.valueOf(i);
					break;
				}

			} else
				if(property.equals("Name"))
				{
					result = Integer.valueOf(-1);
					ComboBoxCellEditor ed = (ComboBoxCellEditor)composite.editors[1];
					ed.setItems(MxTreeUser.getAllUserNames(false, userType));
					for(int i = 0; i < MxTreeUser.getAllUserNames(false, userType).length; i++)
					{
						if(!MxTreeUser.getAllUserNames(false, userType)[i].equals(userName))
							continue;
						result = Integer.valueOf(i);
						break;
					}

				} else
					if(element instanceof MxTreeUser)
					{
						if(userName.length() > 0)
						{
							IWorkbench workbench = PlatformUI.getWorkbench();
							IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
							IWorkbenchPage page = window.getActivePage();
							try
							{
								MxEclipseBusinessView view = (MxEclipseBusinessView)page.showView("org.mxeclipse.views.MxEclipseBusinessView");
								TreeItem selected[] = view.treObjects.getSelection();
								if(selected.length > 0)
								{
									view.expandItem(selected[0]);
									TreeItem atreeitem[];
									int k = (atreeitem = selected[0].getItems()).length;
									for(int j = 0; j < k; j++)
									{
										TreeItem subItem = atreeitem[j];
										if(!(subItem.getData() instanceof MxTreeUser))
											continue;
										MxTreeUser subPerson = (MxTreeUser)subItem.getData();
										if(!subPerson.getName().equals(userName))
											continue;
										view.nodeSelected(subItem);
										view.treObjects.setSelection(subItem);
										break;
									}

								}
							}
							catch(PartInitException e)
							{
								MessageDialog.openError(composite.getShell(), "User selection", (new StringBuilder("Error when trying to select a user! ")).append(e.getMessage()).toString());
							}
						}
					} else
					{
						String triggerObjectNames = userName;
						result = "";
					}
		}
		catch(Exception ex)
		{
			MessageDialog.openError(composite.getShell(), "Trigger retrieval", "Error when retrieving a trigger properties for editing!");
		}
		return result;
	}

	public void modify(Object element, String property, Object value)
	{
		TableItem item = (TableItem)element;
		boolean bChanged = false;
		if(item.getData() instanceof MxTreeStateUserAccess)
		{
			MxTreeStateUserAccess userAccess = (MxTreeStateUserAccess)item.getData();
			try
			{
				if(property.equals("Type"))
				{
					int nValue = ((Integer)value).intValue();
					if(nValue >= 0)
					{
						userAccess.setUserType(MxTreeUser.ALL_USER_TYPES[nValue]);
						bChanged = true;
					}
				} else
					if(property.equals("Name"))
					{
						int nValue = ((Integer)value).intValue();
						if(nValue >= 0)
						{
							userAccess.setName(MxTreeUser.getAllUserNames(false, userAccess.getUserType())[nValue]);
							if(userAccess.getUserType().equals(""))
								userAccess.setUserBasicType(userAccess.getName());
							bChanged = true;
						}
					}
			}
			catch(Exception ex)
			{
				MessageDialog.openInformation(composite.getShell(), "State User Access Configuration", "Error when editing value. Please give a correct value!");
			}
		} else
			if(item.getData() instanceof MxTreeUser)
			{
				MxTreeUser user = (MxTreeUser)item.getData();
				try
				{
					if(property.equals("Type"))
					{
						int nValue = ((Integer)value).intValue();
						if(nValue >= 0)
						{
							user.setType(MxTreeUser.ALL_USER_TYPES[nValue]);
							bChanged = true;
						}
					} else
						if(property.equals("Name"))
						{
							int nValue = ((Integer)value).intValue();
							if(nValue >= 0)
							{
								user.setName(MxTreeUser.getAllUserNames(false, user.getType())[nValue]);
								bChanged = true;
							}
						} else
						{
							property.equals("Select");
						}
				}
				catch(Exception ex)
				{
					MessageDialog.openInformation(composite.getShell(), "User Access Configuration", "Error when editing value. Please give a correct value!");
				}
			}
		if(bChanged)
			composite.getBusiness().propertyChanged((MxTreeBusiness)item.getData());
	}

}