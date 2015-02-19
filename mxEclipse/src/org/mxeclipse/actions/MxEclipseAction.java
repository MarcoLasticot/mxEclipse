package org.mxeclipse.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.dialogs.MatrixLoginDialog;
import org.mxeclipse.dialogs.MatrixProgramsListDialog;
import org.mxeclipse.dialogs.SearchMatrixAdminObjectsDialog;
import org.mxeclipse.matrix.MatrixOperations;
import org.mxeclipse.model.MxTreeProgram;
import org.mxeclipse.utils.MxEclipseLogger;
import org.mxeclipse.utils.MxEclipseUtils;
import org.mxeclipse.utils.MxEclispeMqlUtils;
import org.mxeclipse.views.MxEclipseObjectView;

public class MxEclipseAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	private Logger logger = MxEclipseLogger.getLogger();

	public void run(IAction action) {
		String actionId = action.getId();
		Context context = MxEclipsePlugin.getDefault().getContext();
		if ("mxEclipse.actions.MxEclipseMatrixBusinessAction".equalsIgnoreCase(actionId)) {
			if ((context != null) && (context.isConnected())) {
				SearchMatrixAdminObjectsDialog searchAdminTypeDlg = new SearchMatrixAdminObjectsDialog(this.window.getShell());
				searchAdminTypeDlg.open();
			} else {
				MessageDialog.openInformation(this.window.getShell(), 
						MxEclipseUtils.getString("MxEclipseAction.info.header.SearchAdminObjects"), 
						MxEclipseUtils.getString("MxEclipseAction.info.message.NoUserConnected"));
			}
		} else if ("mxEclipse.actions.MxEclipseUpdateProgramAction".equalsIgnoreCase(actionId)) {
			if ((context != null) && (context.isConnected())) {
				if (this.window != null)
					updateProgram(context);
			} else {
				MessageDialog.openInformation(this.window.getShell(), 
						MxEclipseUtils.getString("MxEclipseAction.info.header.UpdateProgram"), 
						MxEclipseUtils.getString("MxEclipseAction.info.message.NoUserConnected"));
			}
		} else if ("mxEclipse.actions.MxEclipseImportProgramsAction".equalsIgnoreCase(actionId)) {
			if ((context != null) && (context.isConnected())) {
				MatrixProgramsListDialog listDlg = new MatrixProgramsListDialog(this.window.getShell());
				listDlg.open();
			} else {
				MessageDialog.openInformation(this.window.getShell(), 
						MxEclipseUtils.getString("MxEclipseAction.info.header.ImportMatrixPrograms"), 
						MxEclipseUtils.getString("MxEclipseAction.info.message.NoUserConnected"));
			}
		} else if ("mxEclipse.actions.MxEclipseDisconnectMatrixAction".equalsIgnoreCase(actionId)) {
			if ((context != null) && (context.isConnected())) {
				try {
					String user = context.getUser();
					context.closeContext();
					context.disconnect();
					MessageDialog.openInformation(this.window.getShell(), 
							MxEclipseUtils.getString("MxEclipseAction.info.header.Disconnect"), 
							MxEclipseUtils.getString("MxEclipseAction.info.message.DisconnectedUser") + 
							user);
				} catch (MatrixException e) {
					String message = e.getMessage();
					Status status = new Status(4, "MxEclipse", 0, message, e);
					ErrorDialog.openError(this.window.getShell(), 
							MxEclipseUtils.getString("MxEclipseAction.error.header.DisconnectFailed"), 
							MxEclipseUtils.getString("MxEclipseAction.error.message.DisconnectFailed") + 
							context.getUser(), 
							status);
				} finally {
					context = null;
					MxEclipsePlugin.getDefault().setContext(context);
					MxEclipsePlugin.getDefault().setHost(null);
					MxEclipsePlugin.getDefault().setUser(null);
					MxEclipseObjectView.refreshViewStatusBar(this.window.getShell());
				}
			} else
				MessageDialog.openInformation(this.window.getShell(), 
						MxEclipseUtils.getString("MxEclipseAction.info.header.Disconnect"), 
						MxEclipseUtils.getString("MxEclipseAction.info.message.Disconnect"));
		} else if ("mxEclipse.actions.MxEclipseConnectMatrixAction".equalsIgnoreCase(actionId))
			if ((context != null) && (context.isConnected())) {
				MessageDialog.openInformation(this.window.getShell(), 
						MxEclipseUtils.getString("MxEclipseAction.info.header.ConnectMatrix"), 
						MxEclipseUtils.getString("MxEclipseAction.info.message.ConnectMatrix") + 
						context.getUser());
			} else {
				IPreferenceStore store = MxEclipsePlugin.getDefault().getPreferenceStore();
				boolean defLogin = store.getBoolean("MatrixDefaultLogin");
				if (!defLogin) {
					MatrixLoginDialog login = new MatrixLoginDialog(this.window.getShell());
					login.open();
					MxEclipseObjectView.refreshViewStatusBar(this.window.getShell());
				} else {
					loginDirect(store);
				}
			}
	}

	private void updateProgram(Context context) {
		IWorkbenchPage activePage = this.window.getActivePage();
		IEditorPart part = activePage.getActiveEditor();
		if (part != null) {
			IEditorInput input = part.getEditorInput();
			if ((input instanceof IFileEditorInput)) {
				IFile file = ((IFileEditorInput)input).getFile();
				try {
					IPath filePath = file.getLocation();
					String filename = file.getName();
					int dotpos = filename.indexOf(".");
					String fileExtn = filename.substring(dotpos + 1);

					IPreferenceStore store = MxEclipsePlugin.getDefault().getPreferenceStore();
					String hostName = store.getString("MatrixHost").trim();

					boolean bAlreadyStored = false;
					String packageName = "";
					if ((hostName.startsWith("http://")) || (hostName.startsWith("rmi://"))) {
						boolean remoteDialog = store.getBoolean("JpoUpdateWarnOnRemote");
						if ((remoteDialog) && (!MessageDialog.openQuestion(null, "Remote program update", "Are you sure that you want to update the program on a remote server?"))) {
							return;
						}
						packageName = MxTreeProgram.readJpoFromFileAndStoreToMatrix(filePath.toString(), filename, true);

						bAlreadyStored = true;
					} else {
						packageName = MxTreeProgram.readJpoFromFileAndStoreToMatrix(filePath.toString(), filename, false);
					}

					if ("java".equalsIgnoreCase(fileExtn)) {
						if (!bAlreadyStored) {
							String insertQuery = "insert program " + filePath.toString();
							MxEclispeMqlUtils.mqlCommand(context, insertQuery);
						}

						int mxJPOStrPos = filename.indexOf("_mxJPO");
						if (mxJPOStrPos != 1) {
							filename = filename.substring(0, mxJPOStrPos);
						}

						if (!packageName.equals("")) {
							filename = packageName + "." + filename;
						}
						String strQuery = "compile program " + filename + " force update";
						this.logger.logp(Level.FINE, "MxEclipseAction", "updateProgram", strQuery, strQuery);
						MxEclispeMqlUtils.mqlCommand(context, strQuery);
						String strSuccess = "Program " + filename + " saved to database.\n" + "Program " + filename + " compiled with no errors. ";
						MessageDialog.openInformation(new Shell(), "Success", strSuccess);
					} else {
						String existsQuery = "list program \"" + filename + "\"";
						String strResult = MxEclispeMqlUtils.mqlCommand(context, existsQuery);
						String strUpdateQuery = "";

						if (!bAlreadyStored) {
							if ((strResult != null) && (strResult.trim().length() > 0)) {
								strUpdateQuery = "modify program \"" + filename + "\" file " + filePath.toString();
							} else {
								strUpdateQuery = "add program \"" + filename + "\" description \"" + filename + "\" mql file " + filePath.toString();
							}
							MxEclispeMqlUtils.mqlCommand(context, strUpdateQuery);
						}
						String strSuccess = "Program " + filename + " saved to database.";
						MessageDialog.openInformation(new Shell(), "Success", strSuccess);
					}
				} catch (Exception e1) {
					String strError = e1.getMessage();
					Status status = new Status(4, "MxEclipse", 0, strError, new Exception(strError));
					ErrorDialog.openError(new Shell(), 
							MxEclipseUtils.getString("MxEclipseAction.error.header.UpdateProgramFailed"), 
							MxEclipseUtils.getString("MxEclipseAction.error.message.UpdateProgramFailed"), 
							status);
					this.logger.logp(Level.SEVERE, 
							"MxEclipseAction", 
							"updateProgram()", 
							e1.getMessage(), 
							e1.getStackTrace());
				}
			}
		}
	}

	private void loginDirect(IPreferenceStore store) {
		ProgressMonitorDialog pmd = null;
		try {
			MatrixOperations mxops = new MatrixOperations();
			mxops.setHost(store.getString("MatrixHost"));
			mxops.setUser(store.getString("MatrixUser"));
			mxops.setPassword(store.getString("MatrixUserPwd"));

			pmd = new ProgressMonitorDialog(this.window.getShell());
			pmd.open();
			pmd.run(true, true, mxops);

			Context context = MxEclipsePlugin.getDefault().getContext();
			if (context.isConnected()) {
				MessageDialog.openInformation(this.window.getShell(), 
						MxEclipseUtils.getString("MxEclipseAction.info.header.MatrixConnectSuccess"), 
						MxEclipseUtils.getString("MxEclipseAction.info.message.MatrixConnectSuccess"));
				pmd.close();
			}
		} catch (InvocationTargetException e) {
			if (pmd != null) {
				pmd.close();
			}
			String message = e.getCause().getMessage();
			Status status = new Status(4, "MxEclipse", 0, message, e);
			ErrorDialog.openError(this.window.getShell(), 
					MxEclipseUtils.getString("MxEclipseAction.error.header.ConnectionFailed"), 
					MxEclipseUtils.getString("MxEclipseAction.error.message.ConnectionFailed"), 
					status);
		} catch (InterruptedException e) {
			if (pmd != null) {
				pmd.close();
			}
			String message = e.getCause().getMessage();
			Status status = new Status(4, "MxEclipse", 0, message, e);
			ErrorDialog.openError(this.window.getShell(), 
					MxEclipseUtils.getString("MxEclipseAction.error.header.ConnectionFailed"), 
					MxEclipseUtils.getString("MxEclipseAction.error.message.ConnectionFailed"), 
					status);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow pwindow) {
		this.window = pwindow;
	}
}