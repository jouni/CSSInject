package org.vaadin.cssinject.client;

import org.vaadin.cssinject.server.CSSInject;
import org.vaadin.cssinject.shared.CSSInjectState;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.NodeList;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

@Connect(CSSInject.class)
public class CSSInjectConnector extends AbstractExtensionConnector {

    @Override
    public void onStateChanged(StateChangeEvent event) {
        super.onStateChanged(event);
        if (getState().styles != null) {
            setStyles(getState().styles, getConnectorId());
        }
        if (getState().styleSheetId >= 0) {
            for (int i = 0; i <= getState().styleSheetId; i++) {
                if (getAttachedStyleSheet(getResourceUrl("ss" + i)) == null) {
                    LinkElement link = Document.get().createLinkElement();
                    link.setHref(getResourceUrl("ss" + i));
                    link.setRel("stylesheet");
                    link.setType("text/css");
                    Document.get().getElementsByTagName("head").getItem(0)
                            .appendChild(link);
                }
            }
        }
    }

    @Override
    public CSSInjectState getState() {
        return (CSSInjectState) super.getState();
    }

    // public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
    // id = uidl.getId();
    // if (!uidl.hasAttribute("cached")) {
    // if (uidl.hasAttribute("styles")) {
    // styles = uidl.getStringAttribute("styles");
    // setStyles(styles, id);
    // }
    // if (uidl.getChildCount() > 0) {
    // for (Iterator<Object> it = uidl.getChildIterator(); it
    // .hasNext();) {
    // UIDL linkUidl = (UIDL) it.next();
    // String url = client.translateVaadinUri(linkUidl
    // .getStringAttribute("src"));
    // boolean remove = linkUidl.hasAttribute("remove");
    // if (url != null && !url.equals("")) {
    // if (!remove && getAttachedStyleSheet(url) == null) {
    // LinkElement link = Document.get()
    // .createLinkElement();
    // link.setHref(url);
    // link.setRel("stylesheet");
    // link.setType("text/css");
    // Document.get().getElementsByTagName("head")
    // .getItem(0).appendChild(link);
    // if (styleSheets == null) {
    // styleSheets = new ArrayList<Element>();
    // }
    // styleSheets.add(link);
    // } else if (remove) {
    // Element link = getAttachedStyleSheet(url);
    // if (link != null) {
    // link.getParentElement().removeChild(link);
    // styleSheets.remove(link);
    // }
    // }
    // }
    // }
    // }
    // }
    // }

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
    public void onUnregister() {
        removeStyleElement(getConnectorId());
        if (getState().styleSheetId >= 0) {
            for (int i = 0; i <= getState().styleSheetId; i++) {
                Element attachedStyleSheet = getAttachedStyleSheet(getResourceUrl("ss"
                        + i));
                if (attachedStyleSheet != null) {
                    attachedStyleSheet.removeFromParent();
                }
            }
        }
        super.onUnregister();
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