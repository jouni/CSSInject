package org.vaadin.addons.cssinject.client;

import org.vaadin.addons.cssinject.CSSInject;
import org.vaadin.addons.cssinject.shared.CSSInjectState;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.NodeList;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

@Connect(CSSInject.class)
public class CSSInjectConnector extends AbstractExtensionConnector {

    private static final long serialVersionUID = 9105506300908878649L;

    @Override
    protected void extend(ServerConnector target) {
        // NOP
    }

    @Override
    public void onStateChanged(StateChangeEvent event) {
        super.onStateChanged(event);
        if (getState().styles != null) {
            setStyles(getState().styles, getConnectorId());
        }
        if (getState().styleSheetId >= 0) {
            for (String key : getState().resources.keySet()) {
                String resource = getResourceUrl(key);
                if (getAttachedStyleSheet(resource) == null) {
                    LinkElement link = Document.get().createLinkElement();
                    link.setHref(resource);
                    link.setRel("stylesheet");
                    link.setType("text/css");
                    link.setAttribute("id", key + "_" + getConnectorId());
                    Document.get().getElementsByTagName("head").getItem(0)
                            .appendChild(link);
                }
            }
        }
        if(getState().removedKeys != null) {
            for(String linkElId : getState().removedKeys) {
                getState().resources.remove(linkElId);
                Element link = Document.get().getElementById(linkElId + "_" + getConnectorId());
                if(link != null)
                    link.removeFromParent();
            }
            getState().removedKeys.clear();
            getState().removedKeys = null;
        }
    }

    @Override
    public CSSInjectState getState() {
        return (CSSInjectState) super.getState();
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
        
        if(el.styleSheet) {
            el.styleSheet.cssText = value;
        } else {
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