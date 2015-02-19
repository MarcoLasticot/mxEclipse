package org.mxeclipse.utils;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.eclipse.core.runtime.IPath;
import org.mxeclipse.MxEclipsePlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class MxPersistUtils
{
	public static synchronized void persistObjects(List<IXMLPersistable> objects)
	{
		if (objects.size() == 0) return;
		IXMLPersistable sampleObject = (IXMLPersistable)objects.get(0);
		File path = getSavedFiltersFile(sampleObject.getClass().getName() + ".xml");
		try {
			Document doc = XmlUtils.createDocument();
			Element watcher = doc.createElement("root");
			doc.appendChild(watcher);
			for (Iterator iter = objects.iterator(); iter.hasNext(); ) {
				IXMLPersistable element = (IXMLPersistable)iter.next();
				element.toXML(doc, watcher);
			}

			Source source = new DOMSource(doc);
			Result result = new StreamResult(path);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		}
		catch (Exception e) {
			MxEclipseLogger.getLogger().severe("Error saving filters" + e.getMessage());
		}
	}

	public static synchronized List<IXMLPersistable> loadObjects(Class c) {
		List lst = new ArrayList();
		try
		{
			File savedFilters = getSavedFiltersFile(c.getName() + ".xml");
			if (savedFilters.exists()) {
				FileReader fr = new FileReader(savedFilters);
				Document doc = getDocument(fr);
				NodeList rootNodes = doc.getElementsByTagName("root");
				if (rootNodes.getLength() == 0) {
					throw new Exception("No proper root node found");
				}

				NodeList filterNodes = rootNodes.item(0).getChildNodes();
				for (int i = 0; i < filterNodes.getLength(); i++) {
					Node node = filterNodes.item(i);
					Object o = c.getConstructor(new Class[] { Node.class, Boolean.class }).newInstance(new Object[] { node, Boolean.FALSE });
					if ((o instanceof IXMLPersistable))
						lst.add((IXMLPersistable)o);
					else {
						throw new Exception("Object has to inherit IXMLPersistable");
					}
				}
			}
		}
		catch (Exception e)
		{
			MxEclipseLogger.getLogger().severe("Error loading information from persisted storage " + e.getMessage());
		}
		return lst;
	}

	private static File getSavedFiltersFile(String fileName) {
		IPath path = MxEclipsePlugin.getDefault().getStateLocation();
		path = path.addTrailingSeparator();
		path = path.append(fileName);
		return path.toFile();
	}

	protected static Document getDocument(Reader r) throws Exception
	{
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			Document document = parser.parse(new InputSource(r));
			return document;
		} catch (Exception e) {
			throw e;
		}

	}
}