package org.mxeclipse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mxeclipse.matrix.MatrixOperations;
import org.mxeclipse.utils.MxEclipseLogger;
import org.mxeclipse.views.MxEclipseObjectView;
import org.osgi.framework.BundleContext;

public class MxEclipsePlugin extends AbstractUIPlugin {
	private static MxEclipsePlugin plugin;
	private Context context;
	private String host;
	private String user;
	private ResourceBundle resourceBundle;

	public MxEclipsePlugin() {
		plugin = this;
	}

	public void start(BundleContext pbuncontext) throws Exception {
		super.start(pbuncontext);
	}

	public void stop(BundleContext pbuncontext) throws Exception {
		super.stop(pbuncontext);
		plugin = null;
		this.resourceBundle = null;
	}

	public static MxEclipsePlugin getDefault() {
		return plugin;
	}

	public static String getResourceString(String key) {
		ResourceBundle bundle = getDefault().getResourceBundle();
		try {
			return bundle != null ? bundle.getString(key) : key; } catch (MissingResourceException e) {
			}
		return key;
	}

	public ResourceBundle getResourceBundle() {
		try {
			if (this.resourceBundle == null) {
				this.resourceBundle = ResourceBundle.getBundle("org.mxeclipse.MxEclipsePluginResources");
			}
		} catch (MissingResourceException x) {
			this.resourceBundle = null;
		}
		return this.resourceBundle;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		String iconPath = "icons/";
		ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin("org.mxeclipse", iconPath + path);
		if (descriptor == null) {
			try {
				URL installURL = getDefault().getDescriptor().getInstallURL();
				URL url = new URL(installURL, iconPath + path);
				return ImageDescriptor.createFromURL(url);
			} catch (MalformedURLException e) {
				return ImageDescriptor.getMissingImageDescriptor();
			}
		}
		return descriptor;
	}

	public void loginDirect(IPreferenceStore store) throws MatrixException {
		MatrixOperations mxops = new MatrixOperations();
		mxops.setHost(store.getString("MatrixHost"));
		mxops.setUser(store.getString("MatrixUser"));
		mxops.setPassword(store.getString("MatrixUserPwd"));
		mxops.login();
		this.host = store.getString("MatrixHost");
		this.user = store.getString("MatrixUser");
		MxEclipseObjectView.refreshViewStatusBar(null);
	}

	public Context getContext() {
		if (this.context == null) {
			IPreferenceStore store = getDefault().getPreferenceStore();
			boolean silentLogin = store.getBoolean("AutomaticSilentLogin");
			if (silentLogin) {
				try {
					loginDirect(store);
				} catch (MatrixException e) {
					MxEclipseLogger.getLogger().severe("Error when tried to login " + e.getMessage());
				}
			}
		}
		return this.context;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}