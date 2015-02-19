package org.mxeclipse.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract interface IXMLPersistable
{
  public abstract void toXML(Document paramDocument, Node paramNode);
}