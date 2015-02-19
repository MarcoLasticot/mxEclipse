package org.mxeclipse.dialogs;

import java.util.List;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.mxeclipse.model.MxObjectSearchCriteria;
import org.mxeclipse.model.MxTreeDomainObject;
import org.mxeclipse.utils.MxEclipseUtils;

public class SearchMatrixBusinessObjectsComposite extends Composite
{
  private Button radStandard = null;
  private Button radFindLike = null;
  private ISearchComposite searchComposite = null;
  SearchStandardComposite standardComposite = null;
  SearchFindLikeComposite findLikeComposite = null;
  Composite pnlMiddle = null;
  GridData gridSearch = null;
  GridData gridSearch2 = null;
  private Button radReplace;
  private Button radAppend;

  public SearchMatrixBusinessObjectsComposite(Composite parent, int style)
  {
    super(parent, style + 2048);
    initialize();
  }

  private void initialize() {
    GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 2;

    setLayout(gridLayout1);
    setLayoutData(new GridData(4, 4, true, true));

    GridData gridData1 = new GridData();
    gridData1.grabExcessHorizontalSpace = true;
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    this.radStandard = new Button(this, 16);
    this.radStandard.setText("Standard Search");
    this.radStandard.setLayoutData(gridData);
    this.radStandard.setSelection(true);
    this.radStandard.addSelectionListener(new SearchChooser());
    this.radFindLike = new Button(this, 16);
    this.radFindLike.setText("FindLikeSearch");
    this.radFindLike.setLayoutData(gridData1);
    this.radFindLike.addSelectionListener(new SearchChooser());

    GridData gridMiddle = new GridData(4, 4, true, true);
    gridMiddle.horizontalSpan = 2;
    GridLayout layMiddle = new GridLayout();
    layMiddle.numColumns = 1;
    this.pnlMiddle = new Composite(this, 2048);
    this.pnlMiddle.setLayout(layMiddle);
    this.pnlMiddle.setLayoutData(gridMiddle);

    this.gridSearch = new GridData(4, 4, true, true);
    this.standardComposite = new SearchStandardComposite(this.pnlMiddle, 0);
    this.standardComposite.setLayoutData(this.gridSearch);

    this.gridSearch2 = new GridData(4, 4, true, true);
    this.findLikeComposite = new SearchFindLikeComposite(this.pnlMiddle, 0);
    this.findLikeComposite.setLayoutData(this.gridSearch2);
    this.findLikeComposite.setVisible(false);
    this.gridSearch2.exclude = true;

    this.searchComposite = this.standardComposite;

    GridData chkBoxData = new GridData();
    chkBoxData.grabExcessHorizontalSpace = true;
    chkBoxData.horizontalAlignment = 4;

    this.radReplace = new Button(this, 16);
    this.radReplace.setText(MxEclipseUtils.getString("radio.ReplaceObjects"));
    this.radReplace.setLayoutData(chkBoxData);

    this.radAppend = new Button(this, 16);
    this.radAppend.setText(MxEclipseUtils.getString("radio.AppendObjects"));
    this.radAppend.setLayoutData(chkBoxData);
    this.radAppend.setSelection(false);
    this.radReplace.setSelection(true);
  }

  public void okPressed()
  {
    this.searchComposite.okPressed(this.radAppend.getSelection());
  }

  public List<MxTreeDomainObject> getTreeObjectList() {
    return this.searchComposite.getTreeObjectList();
  }

  public void setSearchCriteria(MxObjectSearchCriteria searchCriteria) {
    if (searchCriteria != null) {
      this.radAppend.setSelection(searchCriteria.isAppendResults());
      this.radReplace.setSelection(!searchCriteria.isAppendResults());
      if (searchCriteria.getSearchKind() != null) {
        this.searchComposite = ("findlike".equals(searchCriteria.getSearchKind()) ? this.findLikeComposite : this.standardComposite);
        if ("findlike".equals(searchCriteria.getSearchKind())) {
          this.searchComposite = this.findLikeComposite;
          this.radFindLike.setSelection(true);
          this.radStandard.setSelection(false);
        } else {
          this.searchComposite = this.standardComposite;
          this.radFindLike.setSelection(false);
          this.radStandard.setSelection(true);
        }
        changeSearchKind(searchCriteria.getSearchKind());
      }
    }
    this.searchComposite.setSearchCriteria(searchCriteria);
  }

  public MxObjectSearchCriteria getSearchCriteria() {
    return this.searchComposite.getSearchCriteria();
  }

  protected void changeSearchKind(String searchKind) {
    boolean append = this.radAppend.getSelection();
    this.searchComposite.fillSearchCriteria(this.radAppend.getSelection());
    MxObjectSearchCriteria c = this.searchComposite.getSearchCriteria();

    if (searchKind.equals("findlike")) {
      this.gridSearch.exclude = true;
      this.gridSearch2.exclude = false;
      this.findLikeComposite.setVisible(true);
      this.standardComposite.setVisible(false);
      this.searchComposite = this.findLikeComposite;
    } else {
      this.gridSearch.exclude = false;
      this.gridSearch2.exclude = true;
      this.findLikeComposite.setVisible(false);
      this.standardComposite.setVisible(true);

      this.searchComposite = this.standardComposite;
    }
    this.searchComposite.setSearchCriteria(c);

    this.pnlMiddle.layout();
    this.pnlMiddle.pack(true);
    layout();
    this.radAppend.setSelection(append);
    this.radReplace.setSelection(!append);
  }
  class SearchChooser implements SelectionListener {
    SearchChooser() {
    }
    public void widgetDefaultSelected(SelectionEvent arg0) {
    }

    public void widgetSelected(SelectionEvent arg0) {
      Button radSelected = (Button)arg0.getSource();
      if (radSelected.getSelection()) {
        String searchKind = radSelected.equals(SearchMatrixBusinessObjectsComposite.this.radFindLike) ? "findlike" : "standard";
        SearchMatrixBusinessObjectsComposite.this.changeSearchKind(searchKind);
      }
    }
  }
}