package org.mxeclipse.matrix;

import java.lang.reflect.InvocationTargetException;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.utils.MxEclipseUtils;

public class MatrixOperations implements IRunnableWithProgress {
	private String host;
	private String user;
	private String password;

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			monitor.beginTask("Connecting to Matrix Host " + this.host + " as user " + this.user, -1);
			Context ctx = new Context(this.host);
			ctx.setUser(this.user);
			ctx.setPassword(this.password);
			ctx.connect();
			while (!monitor.isCanceled()) {
				monitor.worked(-1);
				if (ctx.isConnected()) {
					MxEclipsePlugin.getDefault().setContext(ctx);
					MxEclipsePlugin.getDefault().setHost(this.host);
					MxEclipsePlugin.getDefault().setUser(this.user);
					MxEclipseUtils.triggerOnOff();
					break;
				}
			}
			monitor.done();
		} catch (MatrixException e) {
			throw new InvocationTargetException(e);
		}
	}

	public void login() throws MatrixException {
		Context ctx = new Context(this.host);
		ctx.setUser(this.user);
		ctx.setPassword(this.password);
		ctx.connect();
		if (ctx.isConnected()) {
			MxEclipsePlugin.getDefault().setContext(ctx);
			MxEclipseUtils.triggerOnOff();
		}
	}
}