package org.vaadin.cssinject.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class VCSSInject extends Widget implements Paintable {

    private String id;
    private String styles;

    public VCSSInject() {
        setElement(DOM.createSpan());
        setWidth("0");
        setHeight("0");
        setStylePrimaryName("v-cssinject");
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        id = uidl.getId();
        if (!uidl.hasAttribute("cached")) {
            styles = uidl.getStringAttribute("styles");
            setStyles(styles, id);
        }
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
        super.onDetach();
    }

    @Override
    public void onAttach() {
        super.onAttach();
        if (styles != null && id != null) {
            setStyles(styles, id);
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