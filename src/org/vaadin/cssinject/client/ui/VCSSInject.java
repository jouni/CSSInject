package org.vaadin.cssinject.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class VCSSInject extends Widget implements Paintable {

	private String id;
	private String styles;
	private ArrayList<Element> styleSheets;

	public VCSSInject() {
		setElement(DOM.createSpan());
		setWidth("0");
		setHeight("0");
		setStylePrimaryName("v-cssinject");
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		id = uidl.getId();
		if (!uidl.hasAttribute("cached")) {
			if (uidl.hasAttribute("styles")) {
				styles = uidl.getStringAttribute("styles");
				setStyles(styles, id);
			}
			if (uidl.getChildCount() > 0) {
				for (Iterator<Object> it = uidl.getChildIterator(); it
						.hasNext();) {
					UIDL linkUidl = (UIDL) it.next();
					String url = client.translateVaadinUri(linkUidl
							.getStringAttribute("src"));
					boolean remove = linkUidl.hasAttribute("remove");
					if (url != null && !url.equals("")) {
						if (!remove && getAttachedStyleSheet(url) == null) {
							LinkElement link = Document.get()
									.createLinkElement();
							link.setHref(url);
							link.setRel("stylesheet");
							Document.get().getElementsByTagName("head")
									.getItem(0).appendChild(link);
							if (styleSheets == null) {
								styleSheets = new ArrayList<Element>();
							}
							styleSheets.add(link);
						} else if (remove) {
							Element link = getAttachedStyleSheet(url);
							if (link != null) {
								link.getParentElement().removeChild(link);
								styleSheets.remove(link);
							}
						}
					}
				}
			}
		}
	}

	private Element getAttachedStyleSheet(String url) {
		NodeList<Element> links = Document.get().getElementsByTagName("link");
		for (int i = 0; i < links.getLength(); i++) {
			Element link = links.getItem(i);
			if (link.getAttribute("href").equals(url)) {
				return link;
			}
		}
		return null;
	}

	private native void setStyles(String value, String id)
	/*-{
	    var el;
	    var elid = "v-cssinject-" + id;
	    
	    if(!(el = $doc.getElementById(elid))) {
	        el = $doc.createElement("style");
	        el.type = "text/css";
	        el.id = elid;
	        $doc.getElementsByTagName("head")[0].appendChild(el);
	    }
	    
	    if(el.styleSheet)
	        el.styleSheet.cssText = value;
	    else {
	        if(el.firstChild)
	            el.removeChild(el.firstChild);
	        var rules = $doc.createTextNode(value);
	        el.appendChild(rules);
	    }
	}-*/;

	@Override
	public void onDetach() {
		removeStyleElement(id);
		if (styleSheets != null) {
			for (Element link : styleSheets) {
				link.getParentElement().removeChild(link);
			}
		}
		super.onDetach();
	}

	@Override
	public void onAttach() {
		super.onAttach();
		if (styles != null && id != null) {
			setStyles(styles, id);
		}
		if (styleSheets != null) {
			for (Element link : styleSheets) {
				Document.get().getElementsByTagName("head").getItem(0)
						.appendChild(link);
			}
		}
	}

	private native void removeStyleElement(String id)
	/*-{
	    var el;
	    var elid = "v-cssinject-" + id;
	    
	    if(el = $doc.getElementById(elid)) {
	        $doc.getElementsByTagName("head")[0].removeChild(el);
	    }
	}-*/;
}