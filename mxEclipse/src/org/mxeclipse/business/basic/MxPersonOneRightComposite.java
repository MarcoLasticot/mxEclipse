package org.mxeclipse.business.basic;

import java.util.Set;
import java.util.TreeSet;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.mxeclipse.model.MxTreePerson;
import org.mxeclipse.views.IModifyable;

public class MxPersonOneRightComposite extends Composite {
	public String[] allRights;
	public Set<String> selectedRights = new TreeSet();
	protected boolean adminRights;
	private ScrolledComposite scPanel = null;
	private Composite pnlInner = null;
	private Button checkAll = null;
	private IModifyable view;

	public MxPersonOneRightComposite(Composite parent, int style, IModifyable view, boolean adminRights) {
		super(parent, style);
		this.adminRights = adminRights;
		this.view = view;
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = 4;
		gridData.verticalAlignment = 4;
		setLayout(gridLayout);
		setLayoutData(gridData);

		this.checkAll = new Button(this, 32);
		this.checkAll.setText("Check All");
		this.checkAll.setSelection(false);
		this.checkAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button checkBox = (Button)e.getSource();
				for (Control ctrl : MxPersonOneRightComposite.this.pnlInner.getChildren()) {
					if ((ctrl instanceof Button)) {
						((Button)ctrl).setSelection(checkBox.getSelection());
						MxPersonOneRightComposite.this.selectedRights.clear();
						if (checkBox.getSelection()) {
							for (int i = 0; i < MxPersonOneRightComposite.this.allRights.length; i++) {
								MxPersonOneRightComposite.this.selectedRights.add(MxPersonOneRightComposite.this.allRights[i]);
							}
						}
						MxPersonOneRightComposite.this.view.setModified(true);
					}
				}
			}
		});
		this.scPanel = new ScrolledComposite(this, 2816);
		this.scPanel.setLayoutData(new GridData(4, 4, true, true, 1, 1));
		this.pnlInner = new Composite(this.scPanel, 0);
		this.pnlInner.setLayout(new GridLayout(1, true));
		this.pnlInner.setBackground(Display.getCurrent().getSystemColor(1));

		this.scPanel.setContent(this.pnlInner);
		this.scPanel.setExpandHorizontal(true);
		this.scPanel.setExpandVertical(true);
	}

	public void initCheckboxes() {
		this.allRights = (this.adminRights ? MxTreePerson.ADMIN_OPTIONS : MxTreePerson.ACCESS_OPTIONS);
		for (String oneAdminObject : this.allRights) {
			Button checkBox = new Button(this.pnlInner, 32);
			checkBox.setText(oneAdminObject);
			checkBox.setBackground(Display.getCurrent().getSystemColor(1));
			checkBox.setData(oneAdminObject);
			checkBox.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Button checkBox = (Button)e.getSource();
					if (checkBox.getSelection()) {
						MxPersonOneRightComposite.this.selectedRights.add((String)checkBox.getData());
					} else {
						MxPersonOneRightComposite.this.selectedRights.remove((String)checkBox.getData());
					}
					MxPersonOneRightComposite.this.view.setModified(true);
				}

			});
		}

		this.scPanel.setMinSize(this.pnlInner.computeSize(-1, -1));
		this.scPanel.layout();
	}

	public void selectAll() {
		this.checkAll.setSelection(true);
		this.selectedRights.clear();
		for (int i = 0; i < this.allRights.length; i++) {
			this.selectedRights.add(this.allRights[i]);
		}
		for (Control ctrl : this.pnlInner.getChildren())
			if ((ctrl instanceof Button)) {
				((Button)ctrl).setSelection(true);
			}
	}

	public void setRights(Set<String> accessRights) {
		this.selectedRights = accessRights;
		for (Control ctrl : this.pnlInner.getChildren())
			if ((ctrl instanceof Button)) {
				Button cmdCheck = (Button)ctrl;
				cmdCheck.setSelection(false);
				for (String toSet : this.selectedRights) {
					if (((String)cmdCheck.getData()).equals(toSet)) {
						cmdCheck.setSelection(true);
						break;
					}
				}
			}
	}

	public Set<String> getRights() {
		return this.selectedRights;
	}
}