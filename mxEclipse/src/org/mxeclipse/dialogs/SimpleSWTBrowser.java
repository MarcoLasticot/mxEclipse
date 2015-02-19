package org.mxeclipse.dialogs;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SimpleSWTBrowser
{
  public static final String APP_TITLE = "Simple SWT Browser";
  public static final String HOME_URL = "http://www.eclipse.org/vep/";
  private Shell sShell = null;

  private Button backButton = null;

  private Button forwardButton = null;

  private Button stopButton = null;

  private Text locationText = null;

  private Button goButton = null;

  private Browser browser = null;

  private Button homeButton = null;

  private Label statusText = null;

  private ProgressBar progressBar = null;

  private Button refreshButton = null;

  private void createBrowser()
  {
    GridData gridData3 = new GridData();
    this.browser = new Browser(this.sShell, 2048);
    gridData3.horizontalSpan = 7;
    gridData3.horizontalAlignment = 4;
    gridData3.verticalAlignment = 4;
    gridData3.grabExcessVerticalSpace = true;
    this.browser.setLayoutData(gridData3);
    this.browser.addTitleListener(new TitleListener() {
      public void changed(TitleEvent e) {
        SimpleSWTBrowser.this.sShell.setText("Simple SWT Browser - " + e.title);
      }
    });
    this.browser
      .addLocationListener(new LocationListener() {
      public void changing(LocationEvent e) {
        SimpleSWTBrowser.this.locationText.setText(e.location);
      }

      public void changed(LocationEvent e)
      {
      }
    });
    this.browser
      .addProgressListener(new ProgressListener() {
      public void changed(ProgressEvent e) {
        if ((!SimpleSWTBrowser.this.stopButton.isEnabled()) && (e.total != e.current)) {
          SimpleSWTBrowser.this.stopButton.setEnabled(true);
        }
        SimpleSWTBrowser.this.progressBar.setMaximum(e.total);
        SimpleSWTBrowser.this.progressBar.setSelection(e.current);
      }

      public void completed(ProgressEvent e)
      {
        SimpleSWTBrowser.this.stopButton.setEnabled(false);
        SimpleSWTBrowser.this.backButton.setEnabled(SimpleSWTBrowser.this.browser.isBackEnabled());
        SimpleSWTBrowser.this.forwardButton.setEnabled(SimpleSWTBrowser.this.browser.isForwardEnabled());
        SimpleSWTBrowser.this.progressBar.setSelection(0);
      }
    });
    this.browser
      .addStatusTextListener(new StatusTextListener()
    {
      public void changed(StatusTextEvent e) {
        SimpleSWTBrowser.this.statusText.setText(e.text);
      }
    });
    this.browser.setUrl("http://www.eclipse.org/vep/");
  }

  public static void main(String[] args)
  {
    Display display = 
      Display.getDefault();
    SimpleSWTBrowser thisClass = new SimpleSWTBrowser();
    thisClass.createSShell();
    thisClass.sShell.open();

    while (!thisClass.sShell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }

  private void createSShell()
  {
    this.sShell = new Shell();
    GridLayout gridLayout1 = new GridLayout();
    GridData gridData2 = new GridData();
    GridData gridData4 = new GridData();
    GridData gridData5 = new GridData();
    GridData gridData6 = new GridData();
    GridData gridData7 = new GridData();
    GridData gridData8 = new GridData();
    this.backButton = new Button(this.sShell, 16388);
    this.forwardButton = new Button(this.sShell, 131076);
    this.stopButton = new Button(this.sShell, 0);
    this.refreshButton = new Button(this.sShell, 0);
    this.homeButton = new Button(this.sShell, 0);
    this.locationText = new Text(this.sShell, 2048);
    this.goButton = new Button(this.sShell, 0);
    createBrowser();
    this.progressBar = new ProgressBar(this.sShell, 2048);
    this.statusText = new Label(this.sShell, 0);
    this.sShell.setText("Simple SWT Browser");
    this.sShell.setLayout(gridLayout1);
    gridLayout1.numColumns = 7;
    this.backButton.setEnabled(false);
    this.backButton.setToolTipText("Navigate back to the previous page");
    this.backButton.setLayoutData(gridData6);
    this.forwardButton.setEnabled(false);
    this.forwardButton.setToolTipText("Navigate forward to the next page");
    this.forwardButton.setLayoutData(gridData5);
    this.stopButton.setText("Stop");
    this.stopButton.setEnabled(false);
    this.stopButton.setToolTipText("Stop the loading of the current page");
    this.goButton.setText("Go!");
    this.goButton.setLayoutData(gridData8);
    this.goButton.setToolTipText("Navigate to the selected web address");
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = 4;
    gridData2.verticalAlignment = 2;
    this.locationText.setLayoutData(gridData2);
    this.locationText.setText("http://www.eclipse.org/vep/");
    this.locationText.setToolTipText("Enter a web address");
    this.homeButton.setText("Home");
    this.homeButton.setToolTipText("Return to home page");
    this.statusText.setText("Done");
    this.statusText.setLayoutData(gridData7);
    gridData4.horizontalSpan = 5;
    this.progressBar.setLayoutData(gridData4);
    this.progressBar.setEnabled(false);
    this.progressBar.setSelection(0);
    gridData5.horizontalAlignment = 4;
    gridData5.verticalAlignment = 4;
    gridData6.horizontalAlignment = 4;
    gridData6.verticalAlignment = 4;
    gridData7.horizontalSpan = 1;
    gridData7.grabExcessHorizontalSpace = true;
    gridData7.horizontalAlignment = 4;
    gridData7.verticalAlignment = 2;
    gridData8.horizontalAlignment = 3;
    gridData8.verticalAlignment = 2;
    this.refreshButton.setText("Refresh");
    this.refreshButton.setToolTipText("Refresh the current page");
    this.sShell.setSize(new Point(553, 367));
    this.locationText
      .addMouseListener(new MouseAdapter() {
      public void mouseUp(MouseEvent e) {
        SimpleSWTBrowser.this.locationText.selectAll();
      }
    });
    this.locationText.addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent e)
      {
        if ((e.character == '\n') || (e.character == '\r')) {
          e.doit = false;
          SimpleSWTBrowser.this.browser.setUrl(SimpleSWTBrowser.this.locationText.getText());
        }
      }
    });
    this.refreshButton
      .addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e) {
        SimpleSWTBrowser.this.browser.refresh();
      }
    });
    this.locationText
      .addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e) {
        SimpleSWTBrowser.this.browser.setUrl(SimpleSWTBrowser.this.locationText.getText());
      }
    });
    this.stopButton
      .addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e) {
        SimpleSWTBrowser.this.browser.stop();
      }
    });
    this.backButton
      .addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e) {
        SimpleSWTBrowser.this.browser.back();
      }
    });
    this.forwardButton
      .addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e) {
        SimpleSWTBrowser.this.browser.forward();
      }
    });
    this.homeButton
      .addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e) {
        SimpleSWTBrowser.this.browser.setUrl("http://www.eclipse.org/vep/");
      }
    });
    this.goButton
      .addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e) {
        SimpleSWTBrowser.this.browser.setUrl(SimpleSWTBrowser.this.locationText.getText());
      }
    });
  }
}