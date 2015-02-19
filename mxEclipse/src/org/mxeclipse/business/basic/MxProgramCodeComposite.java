package org.mxeclipse.business.basic;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreeBusiness;
import org.mxeclipse.model.MxTreeProgram;
import org.mxeclipse.utils.JavaLineStyler;
import org.mxeclipse.views.IModifyable;

public class MxProgramCodeComposite extends MxBusinessBasicComposite implements IPropertyChangeListener {
	MxTreeProgram businessType;
	IModifyable view;
	private StyledText txtCode = null;
	private JavaLineStyler lineStylerJava = new JavaLineStyler();
	private Button chkJavaFriendly = null;

	public MxProgramCodeComposite(Composite parent, int style, IModifyable view, MxTreeBusiness businessType) {
		super(parent, style);
		this.view = view;
		initialize();
		initializeContent(businessType);
	}

	private void initialize() {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = 4;
		gridData.verticalAlignment = 4;
		gridData.grabExcessVerticalSpace = true;
		this.chkJavaFriendly = new Button(this, 32);
		this.chkJavaFriendly.setText("Java Friendly");
		this.chkJavaFriendly.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IPreferenceStore store = MxEclipsePlugin.getDefault().getPreferenceStore();
				if (store.getBoolean("JpoDialogJavaCodeStyle") == MxProgramCodeComposite.this.chkJavaFriendly.getSelection()) {
					return;
				}
				store.setValue("JpoDialogJavaCodeStyle", MxProgramCodeComposite.this.chkJavaFriendly.getSelection());

				MxProgramCodeComposite.this.toggleCodeStyle();
			}
		});
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = 4;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = 4;
		this.txtCode = new StyledText(this, 2816);
		this.txtCode.setLayoutData(gridData);
		this.txtCode.setLayoutData(gridData1);
		this.txtCode.addKeyListener(new ModifySetter(this.view));

		setSize(new Point(346, 169));
		GridLayout gridLayout = new GridLayout();
		setLayout(gridLayout);
		setSize(new Point(300, 200));
	}

	public void initializeContent(MxTreeBusiness selectedBusiness) {
		try {
			MxEclipsePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
			this.businessType = ((MxTreeProgram)selectedBusiness);
			String code = this.businessType.getCode();

			this.chkJavaFriendly.setVisible(this.businessType.getProgramType().equals("java"));

			if ((this.businessType.getProgramType().equals("java")) && (this.chkJavaFriendly.getSelection())) {
				code = MxTreeProgram.convertToJavaForm(this.businessType, code);
			}
			this.txtCode.setText(code);

			this.txtCode.addLineStyleListener(this.lineStylerJava);

			this.chkJavaFriendly.setSelection(MxEclipsePlugin.getDefault().getPreferenceStore().getBoolean("JpoDialogJavaCodeStyle"));
		} catch (Exception e) {
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getShell(), 
					"Error when trying to initialize data in the basic form", 
					"Error when trying to initialize data in the basic form", 
					status);
		}
	}

	public void storeData() {
		try {
			String code = this.txtCode.getText();
			if (this.chkJavaFriendly.getSelection()) {
				code = MxTreeProgram.convertToMacroForm(this.businessType, code);
			}
			this.businessType.setCode(code);
		} catch (Exception e) {
			Status status = new Status(4, "MxEclipse", 0, e.getMessage(), e);
			ErrorDialog.openError(getShell(), 
					"Error when trying to store the data to matrix", 
					"Error when trying to store the data to matrix", 
					status);
		}
	}

	public void dispose() {
		MxEclipsePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if ("JpoDialogJavaCodeStyle".equals(event.getProperty())) {
			IPreferenceStore store = MxEclipsePlugin.getDefault().getPreferenceStore();
			if (store.getBoolean("JpoDialogJavaCodeStyle") == this.chkJavaFriendly.getSelection())
			{
				return;
			}
			this.chkJavaFriendly.setSelection(store.getBoolean("JpoDialogJavaCodeStyle"));
			toggleCodeStyle();
		}
	}

	private void toggleCodeStyle() {
		try {
			if (this.chkJavaFriendly.getSelection()) {
				this.txtCode.setText(MxTreeProgram.convertToJavaForm(this.businessType, this.txtCode.getText()));
			} else {
				this.txtCode.setText(MxTreeProgram.convertToMacroForm(this.businessType, this.txtCode.getText()));
			}
		}
		catch (Exception e1) {
			Status status = new Status(4, "MxEclipse", 0, e1.getMessage(), e1);
			ErrorDialog.openError(getShell(), 
					"Error when trying change code format", 
					"Error when trying change code format", 
					status);
		}
	}

	class ModifySelectionSetter extends SelectionAdapter {
		private IModifyable view;

		public ModifySelectionSetter(IModifyable view) {
			this.view = view;
		}

		public void widgetSelected(SelectionEvent e) {
			this.view.setModified(true);
		}
	}

	class ModifySetter implements KeyListener {
		private IModifyable view;

		public ModifySetter(IModifyable view) {
			this.view = view;
		}
		public void keyReleased(KeyEvent e) {
		}
		public void keyPressed(KeyEvent e) {
			this.view.setModified(true);
		}
	}
}