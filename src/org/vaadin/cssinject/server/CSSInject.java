package org.vaadin.cssinject.server;

import java.util.ArrayList;

import org.vaadin.cssinject.shared.CSSInjectState;

import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.UI;

public class CSSInject extends AbstractExtension {

    private static final long serialVersionUID = 9172956539550353998L;

    private ArrayList<Resource> styleSheets;

    public CSSInject(UI target) {
        extend(target);
    }

    public void extend(UI target) {
        super.extend(target);
    }

    @Override
    public CSSInjectState getState() {
        return (CSSInjectState) super.getState();
    }

    /**
     * Set the CSS string for this CSSInject. This will be added to the UI this
     * CSSInject is extending as a STYLE element in the HEAD of the document.
     * 
     * @param text
     */
    public void setStyles(String text) {
        getState().styles = text;
    }

    /**
     * Get the CSS string currently set to this CSSInject.
     * 
     * @return
     */
    public String getStyles() {
        return getState().styles;
    }

    /**
     * Attach a separate CSS stylesheet to the Window where this CSSInject
     * instance is attached to. The stylesheet will be added to the document's
     * HEAD element as a LINK element with the given resource as the href.
     * 
     * @param stylesheet
     * @return the same Resource which was passed as the argument
     */
    public Resource addStyleSheet(Resource stylesheet) {
        if (styleSheets == null) {
            styleSheets = new ArrayList<Resource>();
        }
        styleSheets.add(stylesheet);
        setResource("ss" + ++getState().styleSheetId, stylesheet);

        return stylesheet;
    }

    /**
     * Remove the given stylesheet resource. TODO doesn't work currently
     * 
     * @param styleSheet
     */
    public void removeStyleSheet(Resource styleSheet) {
        if (styleSheets == null) {
            styleSheets.remove(styleSheet);

            // TODO how to clear the resource from the state?
        }
    }

    /**
     * Add a stylesheet with the given url as a ThemeResource. The url should be
     * relative to the theme you use, e.g. if your theme folder is in
     * <code>VAADIN/themes/mytheme</code>, then the stylesheet url will be
     * <code>VAADIN/themes/mytheme/stylesheetUrl</code>.
     * 
     * @param styleSheetUrl
     */
    public Resource addThemeStyleSheet(String styleSheetUrl) {
        return this.addStyleSheet(new ThemeResource(styleSheetUrl));
    }

    /**
     * TODO doesn't work currently
     */
    public void removeThemeStyleSheet(String styleSheetUrl) {
        if (styleSheets != null) {
            for (Resource stylesheet : styleSheets) {
                if (stylesheet instanceof ThemeResource) {
                    ThemeResource r = (ThemeResource) stylesheet;
                    if (r.getResourceId().equals(styleSheetUrl)) {
                        removeStyleSheet(r);
                        break;
                    }
                }
            }
        }
    }

    public boolean hasStyleSheet(Resource stylesheet) {
        if (styleSheets == null)
            return false;
        return styleSheets.contains(stylesheet);
    }

}