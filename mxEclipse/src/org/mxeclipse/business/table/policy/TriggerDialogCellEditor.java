package org.mxeclipse.business.table.policy;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class TriggerDialogCellEditor extends DialogCellEditor {
	public TriggerDialogCellEditor() {
	}

	public TriggerDialogCellEditor(Composite parent) {
		super(parent);
	}

	public TriggerDialogCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	protected Object openDialogBox(Control cellEditorWindow) {
		System.out.println("aa");
		return null;
	}
}