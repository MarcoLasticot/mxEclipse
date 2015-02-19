package org.mxeclipse.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlUtils
{
  public static Element createElementEmpty(Document doc, String tagName)
  {
    Element elem = doc.createElement(tagName);
    return elem;
  }

  public static Element createElementWithText(Document doc, String tagName, String text) {
    Element elem = doc.createElement(tagName);
    elem.appendChild(doc.createTextNode(text));
    return elem;
  }

  public static Document createDocument() throws ParserConfigurationException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.newDocument();
  }
}