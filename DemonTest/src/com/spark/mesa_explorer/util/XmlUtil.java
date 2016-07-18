package com.spark.mesa_explorer.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Yun
 *
 */
public class XmlUtil {

	public static String getAttrValueAsString(Node element, String attrName) {
		return element.getAttributes().getNamedItem(attrName).getNodeValue().trim();
	}
	
	public static float getAttrValueAsFloat(Node element, String attrName) {
		return Float.parseFloat(getAttrValueAsString(element,attrName));
	}
	
	public static Element getElement(Element parent,String elementName){
		return (Element)parent.getElementsByTagName(elementName);
	}
	
	public static float getElementAsFloat(Element parent,String elementName){
		Element element = (Element)parent.getElementsByTagName(elementName).item(0);
		return Float.parseFloat(element.getTextContent().trim());
	}

}
