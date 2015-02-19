package org.mxeclipse.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Logger;
import matrix.db.AttributeType;
import matrix.db.AttributeTypeItr;
import matrix.db.AttributeTypeList;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mxeclipse.MxEclipsePlugin;
import org.mxeclipse.model.MxTreeAssociation;
import org.mxeclipse.model.MxTreeAttribute;
import org.mxeclipse.model.MxTreeGroup;
import org.mxeclipse.model.MxTreeIndex;
import org.mxeclipse.model.MxTreePerson;
import org.mxeclipse.model.MxTreePolicy;
import org.mxeclipse.model.MxTreeProgram;
import org.mxeclipse.model.MxTreeRelationship;
import org.mxeclipse.model.MxTreeRole;
import org.mxeclipse.model.MxTreeSite;
import org.mxeclipse.model.MxTreeStateUserAccess;
import org.mxeclipse.model.MxTreeStore;
import org.mxeclipse.model.MxTreeType;
import org.mxeclipse.model.MxTreeUser;
import org.mxeclipse.model.MxTreeVault;
import org.mxeclipse.model.MxTreeWebCommand;
import org.mxeclipse.model.MxTreeWebMenu;
import org.mxeclipse.model.MxTreeWebSetting;
import org.mxeclipse.views.MxEclipseBusinessView;
import org.mxeclipse.views.MxEclipseObjectView;

public class MxEclipseUtils {
	private static Properties matrixIniProps = null;
	private static ImageRegistry imageRegistry = null;
	private static List adminTypes = null;
	private static Map adminTypeImageMap = null;
	public static final String BUNDLE_NAME = "org.mxeclipse.MxEclipseStringResource";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.mxeclipse.MxEclipseStringResource");

	private static List appVersion = new ArrayList();
	private static List libVersion = new ArrayList();
	private static Logger logger = MxEclipseLogger.getLogger();
	private static TreeMap<String, AttributeType> allAttributes;

	static {
		generateMatrixLibVersions();
	}

	private static void generateMatrixLibVersions() {
		StringTokenizer tkzr = new StringTokenizer("V6R2012x", ".");
		while (tkzr.hasMoreTokens()) {
			libVersion.add(tkzr.nextToken());
		}
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
		}
		return '!' + key + '!';
	}

	public static URL newURL(String urlName) throws MalformedURLException {
		try {
			return new URL(urlName); 
		} catch (MalformedURLException e) {
			throw e;
		}
	}

	public static ImageRegistry getImageRegistry() throws Exception {
		if (imageRegistry == null) {
			imageRegistry = new ImageRegistry();
			if (getAdminTypeImageMap() != null) {
				Iterator itImages = adminTypeImageMap.keySet().iterator();
				while (itImages.hasNext()) {
					String adminType = (String)itImages.next();
					if ("All".equalsIgnoreCase(adminType)) {
						Image image = PlatformUI.getWorkbench().getSharedImages().getImage("IMG_OBJ_FILE");
						imageRegistry.put(adminType, image);
					} else {
						ImageDescriptor imgDes = MxEclipsePlugin.getImageDescriptor(getImageForAdminType(adminType));
						imageRegistry.put(adminType, imgDes);
					}
				}
			}
		}
		return imageRegistry;
	}

	public static List getAdminTypes() {
		if (adminTypes == null) {
			adminTypes = new ArrayList();
			adminTypes.add("All");
			adminTypes.add("Attribute");
			adminTypes.add("Association");
			adminTypes.add("Type");
			adminTypes.add("Relationship");
			adminTypes.add("Format");
			adminTypes.add("Person");
			adminTypes.add("Group");
			adminTypes.add("Role");
			adminTypes.add("Rule");
			adminTypes.add("Policy");
			adminTypes.add("Process");
			adminTypes.add("Program");
			adminTypes.add("Wizard");
			adminTypes.add("Page");
			adminTypes.add("Resource");
			adminTypes.add("Report");
			adminTypes.add("Form");
			adminTypes.add("WebForm");
			adminTypes.add("Command");
			adminTypes.add("Menu");
			adminTypes.add("Inquiry");
			adminTypes.add("Interface");
			adminTypes.add("Table");
			adminTypes.add("Channel");
			adminTypes.add("Portal");
			adminTypes.add("Index");
		}
		return adminTypes;
	}

	public static List tokenizeStringToList(String str, String delim) {
		StringTokenizer tkzr = new StringTokenizer(str, delim);
		List tokens = new ArrayList();
		if (tkzr.hasMoreTokens()) {
			do
				tokens.add(tkzr.nextToken());
			while (tkzr.hasMoreTokens());
		} else {
			tokens.add(str);
		}
		return tokens;
	}

	public static Map getAdminTypeImageMap() {
		if (adminTypeImageMap == null) {
			adminTypeImageMap = new HashMap();
			adminTypeImageMap.put("Attribute", "attrib.gif");
			adminTypeImageMap.put("Attribute_Gray", "attrib_gray.gif");
			adminTypeImageMap.put("Association", "association.gif");
			adminTypeImageMap.put("Type", "type.gif");
			adminTypeImageMap.put("Type_Gray", "type_gray.gif");
			adminTypeImageMap.put("Relationship", "relation.gif");
			adminTypeImageMap.put("Format", "format.gif");
			adminTypeImageMap.put("Person", "person.gif");
			adminTypeImageMap.put("Group", "group.gif");
			adminTypeImageMap.put("Role", "role.gif");
			adminTypeImageMap.put("Rule", "rule.gif");
			adminTypeImageMap.put("Policy", "policy.gif");
			adminTypeImageMap.put("Policy_Gray", "policy_gray.gif");
			adminTypeImageMap.put("State", "state.gif");
			adminTypeImageMap.put("Process", "process.gif");
			adminTypeImageMap.put("Program", "program.gif");
			adminTypeImageMap.put("Wizard", "wizard.gif");
			adminTypeImageMap.put("Page", "page.gif");
			adminTypeImageMap.put("Resource", "resource.gif");
			adminTypeImageMap.put("Report", "report.gif");
			adminTypeImageMap.put("Form", "form.gif");
			adminTypeImageMap.put("WebForm", "wform.gif");
			adminTypeImageMap.put("Command", "command.gif");
			adminTypeImageMap.put("Menu", "menu.gif");
			adminTypeImageMap.put("Inquiry", "inquiry.gif");
			adminTypeImageMap.put("Interface", "interface.gif");
			adminTypeImageMap.put("Table", "table.gif");
			adminTypeImageMap.put("Column", "column.gif");
			adminTypeImageMap.put("Channel", "channel.gif");
			adminTypeImageMap.put("Portal", "portal.gif");
			adminTypeImageMap.put("Index", "index.gif");
		}

		return adminTypeImageMap;
	}

	public static String getImageForAdminType(String admType) {
		if (adminTypeImageMap == null) {
			getAdminTypeImageMap();
		}
		return (String)adminTypeImageMap.get(admType);
	}

	public static int getLibPrimaryMajorVersion() {
		return Integer.parseInt((String)libVersion.get(0));
	}

	public static int getLibSecondaryMajorVersion() {
		return Integer.parseInt((String)libVersion.get(1));
	}

	public static int getLibPrimaryMinorVersion() {
		return Integer.parseInt((String)libVersion.get(2));
	}

	public static int getLibSecondaryMinorVersion() {
		return Integer.parseInt((String)libVersion.get(3));
	}

	public static int getAppPrimaryMajorVersion() {
		return Integer.parseInt((String)appVersion.get(0));
	}

	public static int getAppSecondaryMajorVersion() {
		return Integer.parseInt((String)appVersion.get(1));
	}

	public static int getAppPrimaryMinorVersion() {
		return Integer.parseInt((String)appVersion.get(2));
	}

	public static int getAppSecondaryMinorVersion() {
		return Integer.parseInt((String)appVersion.get(3));
	}

	public static TreeMap getAllAttributes() {
		if (allAttributes == null) {
			allAttributes = new TreeMap();
			Context context = MxEclipsePlugin.getDefault().getContext();
			try {
				AttributeTypeList lstAttributes = AttributeType.getAttributeTypes(context, true);
				AttributeTypeItr itAttributes = new AttributeTypeItr(lstAttributes);
				while (itAttributes.next()) {
					AttributeType attribute = itAttributes.obj();
					allAttributes.put(attribute.getName(), attribute);
				}
			} catch (MatrixException e) {
				Logger logger = MxEclipseLogger.getLogger();
				logger.severe(e.getMessage());
			}
		}

		return allAttributes;
	}

	public static void triggerOnOff() throws MatrixException {
		IPreferenceStore store = MxEclipsePlugin.getDefault().getPreferenceStore();
		boolean triggerOff = store.getBoolean("TriggerOff");
		MQLCommand command = new MQLCommand();
		Context ctx = MxEclipsePlugin.getDefault().getContext();
		if (ctx != null)
			command.executeCommand(ctx, "trigger " + (triggerOff ? "off;" : "on;"));
	}

	public static boolean isTriggersOff() {
		IPreferenceStore store = MxEclipsePlugin.getDefault().getPreferenceStore();
		return store.getBoolean("TriggerOff");
	}

	public static void clearCache() {
		MxTreeAssociation.clearCache();
		MxTreeAttribute.clearCache();
		MxTreeGroup.clearCache();
		MxTreePerson.clearCache();
		MxTreePolicy.clearCache();
		MxTreeProgram.clearCache();
		MxTreeRelationship.clearCache();
		MxTreeRole.clearCache();
		MxTreeSite.clearCache();
		MxTreeStateUserAccess.clearCache();
		MxTreeStore.clearCache();
		MxTreeType.clearCache();
		MxTreeUser.clearCache();
		MxTreeVault.clearCache();
		MxTreeIndex.clearCache();
		MxTreeWebCommand.clearCache();
		MxTreeWebMenu.clearCache();
		MxTreeWebSetting.clearCache();

		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		try {
			MxEclipseObjectView view = (MxEclipseObjectView)page.showView("org.mxeclipse.views.MxEclipseObjectView");
			view.clearAll();

			MxEclipseBusinessView bview = (MxEclipseBusinessView)page.showView("org.mxeclipse.views.MxEclipseBusinessView");
			bview.clearAll();
		} catch (PartInitException e) {
			MessageDialog.openError(null, "Clear Cache", "Error when trying to clear views! " + e.getMessage());
		}
	}
}