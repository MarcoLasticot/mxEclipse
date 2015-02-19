package org.mxeclipse.business.table.trigger;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeTrigger;
import org.mxeclipse.views.MxEclipseObjectView;

public class MxTriggerCellModifier
  implements ICellModifier
{
  MxTriggerComposite composite;

  public MxTriggerCellModifier(MxTriggerComposite composite)
  {
    this.composite = composite;
  }

  public boolean canModify(Object element, String property)
  {
    return true;
  }

  public Object getValue(Object element, String property)
  {
    Object result = "";
    MxTreeTrigger trigger = (MxTreeTrigger)element;
    try
    {
      if (property.equals("EventType")) {
        result = Integer.valueOf(-1);
        for (int i = 0; i < trigger.getAvailableEventTypes().length; i++)
          if (trigger.getAvailableEventTypes()[i].equals(trigger.getEventType())) {
            result = Integer.valueOf(i);
            break;
          }
      }
      else if (property.equals("TriggerType")) {
        result = Integer.valueOf(-1);
        for (int i = 0; i < MxTreeTrigger.TRIGGER_TYPES.length; i++)
          if (MxTreeTrigger.TRIGGER_TYPES[i].equals(trigger.getTriggerType())) {
            result = Integer.valueOf(i);
            break;
          }
      }
      else if (property.equals("Program")) {
        result = trigger.getMainProgramName();
      } else if (property.equals("Args")) {
        result = trigger.getArgs();
      } else {
        String[] triggerObjectNames = trigger.getTriggerObjectNames();
        if (triggerObjectNames.length > 0) {
          IWorkbench workbench = PlatformUI.getWorkbench();
          IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
          IWorkbenchPage page = window.getActivePage();
          try {
            MxEclipseObjectView view = (MxEclipseObjectView)page.showView("org.mxeclipse.views.MxEclipseObjectView");
            for (int i = 0; i < triggerObjectNames.length; i++)
              view.findObjects(null, "eService Trigger Program Parameters", triggerObjectNames[i], null, null, i > 0);
          }
          catch (PartInitException e) {
            MessageDialog.openError(this.composite.getShell(), "Switch View", "Error when trying to switch to object view!" + e.getMessage());
          }
        }

        result = "";
      }
    } catch (Exception ex) {
      MessageDialog.openError(this.composite.getShell(), "Trigger retrieval", "Error when retrieving a trigger properties for editing!");
    }

    return result;
  }

  public void modify(Object element, String property, Object value)
  {
    TableItem item = (TableItem)element;
    MxTreeTrigger trigger = (MxTreeTrigger)item.getData();
    try
    {
      if (property.equals("EventType")) {
        int nValue = ((Integer)value).intValue();
        if (nValue >= 0)
          trigger.setEventType(trigger.getAvailableEventTypes()[nValue]);
      }
      else if (property.equals("TriggerType")) {
        int nValue = ((Integer)value).intValue();
        if (nValue >= 0)
          trigger.setTriggerType(MxTreeTrigger.TRIGGER_TYPES[nValue]);
      }
      else if (property.equals("Program")) {
        trigger.setMainProgramName((String)value);
      } else if (property.equals("Args")) {
        trigger.setArgs((String)value);
      }
    } catch (Exception ex) {
      MessageDialog.openInformation(this.composite.getShell(), "Trigger Configuration", "Error when editing value. Please give a correct value!");
    }
    if (!property.equals("Link"))
      this.composite.getBusiness().propertyChanged(trigger);
  }
}