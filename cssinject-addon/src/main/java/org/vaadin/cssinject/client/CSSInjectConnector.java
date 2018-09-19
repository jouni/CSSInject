package org.vaadin.cssinject.client;

import java.util.HashMap;
import java.util.Map;

import org.vaadin.cssinject.CSSInject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.Node;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

@Connect(CSSInject.class)
public class CSSInjectConnector extends AbstractExtensionConnector {

    private Map<String, LinkElement> linkElements = new HashMap<>();

    @Override
    protected void extend(ServerConnector serverConnector) {
        // NOP
    }

    @OnStateChange("styles")
    private void onStylesChanged() {
        if (getState().styles != null) {
            setStyles(getState().styles, getConnectorId());
        }
    }

    @OnStateChange("styleSheetIds")
    private void onStyleSheetChanged() {
        // Add new elements
        for (String styleSheetId : getState().styleSheetIds) {
            if (!linkElements.containsKey(styleSheetId)) {
                LinkElement link = Document.get().createLinkElement();
                link.setHref(getResourceUrl(styleSheetId));
                link.setRel("stylesheet");
                link.setType("text/css");
                Document.get().getElementsByTagName("head").getItem(0)
                        .appendChild(link);
                linkElements.put(styleSheetId, link);
            }
        }

        // Remove removed elements
        linkElements.entrySet().removeIf(entry -> {
            if (!getState().styleSheetIds.contains(entry.getKey())) {
                entry.getValue().removeFromParent();
                return true;
            }
            return false;
        });
    }

    @Override
    public CSSInjectState getState() {
        return (CSSInjectState) super.getState();
    }

    private native void setStyles(String value, String id)
    /*-{
        var el;
        var elid = "v-cssinject-" + id;

        if (!(el = $doc.getElementById(elid))) {
            el = $doc.createElement("style");
            el.type = "text/css";
            el.id = elid;
            $doc.getElementsByTagName("head")[0].appendChild(el);
        }

        if (el.styleSheet)
            el.styleSheet.cssText = value;
        else {
            if (el.firstChild)
                el.removeChild(el.firstChild);
            var rules = $doc.createTextNode(value);
            el.appendChild(rules);
        }
    }-*/;

    @Override
    public void onUnregister() {
        removeStyleElement(getConnectorId());

        linkElements.values().forEach(Node::removeFromParent);
        linkElements.clear();

        super.onUnregister();
    }

    private native void removeStyleElement(String id)
    /*-{
        var el;
        var elid = "v-cssinject-" + id;

        if (el = $doc.getElementById(elid)) {
            $doc.getElementsByTagName("head")[0].removeChild(el);
        }
    }-*/;
}
