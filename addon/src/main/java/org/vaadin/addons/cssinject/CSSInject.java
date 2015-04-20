package org.vaadin.addons.cssinject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.server.*;
import org.vaadin.addons.cssinject.shared.CSSInjectState;

import com.vaadin.ui.UI;

public class CSSInject extends AbstractExtension {

    private static final long serialVersionUID = 9172956539550353998L;

    private Map<Resource, String> styleSheets = new HashMap<Resource, String>();

    private int lastSyncIdRemoved = -1;

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
     * Attach a separate CSS style sheet to the Window where this CSSInject
     * instance is attached to. The style sheet will be added to the document's
     * HEAD element as a LINK element with the given resource as the href.
     * 
     * @param stylesheet
     * @return the same Resource which was passed as the argument
     */
    public Resource addStyleSheet(Resource stylesheet) {
        String key = "ss" + ++getState().styleSheetId;
        styleSheets.put(stylesheet, key);
        setResource(key, stylesheet);
        markAsDirty();
        return stylesheet;
    }

    /**
     * Remove the given style sheet resource.
     * 
     * completely, which will remove all of the stylesheets added through the
     * extension.
     * 
     * @param styleSheet
     */
    public void removeStyleSheet(Resource styleSheet) {
        String key = styleSheets.get(styleSheet);
        if (key == null) return;
        if (getState().removedKeys == null) {
            getState().removedKeys = new ArrayList<String>();
        }
        int currentSyncId = getUI().getConnectorTracker().getCurrentSyncId();
        if(currentSyncId != lastSyncIdRemoved) {
            getState().removedKeys.clear();
            lastSyncIdRemoved = currentSyncId;
        }
        styleSheets.remove(styleSheet);
        getState().removedKeys.add(key);
        getState().resources.remove(key);
        markAsDirty();
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);
        int currentSyncId = getUI().getConnectorTracker().getCurrentSyncId();
        if(currentSyncId > lastSyncIdRemoved) {
            lastSyncIdRemoved = currentSyncId;
        }
    }

    /**
     * Add a style sheet with the given url as a ThemeResource. The url should be
     * relative to the theme you use, e.g. if your theme folder is in
     * <code>VAADIN/themes/mytheme</code>, then the style sheet url will be
     * <code>VAADIN/themes/mytheme/styleSheetUrl</code>.
     * 
     * @param styleSheetUrl
     */
    public Resource addThemeStyleSheet(String styleSheetUrl) {
        return this.addStyleSheet(new ThemeResource(styleSheetUrl));
    }

    /**
     * completely, which will remove all of the style sheets added through the
     * extension.
     */
    public void removeThemeStyleSheet(String styleSheetUrl) {
        removeStyleSheet(new ThemeResource(styleSheetUrl));
    }

    /**
     * Check if this CSSInject has added the given style sheet in to the target
     * UI.
     * 
     * @param stylesheet
     * @return true if the given style sheet has been added to the UI by this CSSInject instance.
     */
    public boolean hasStyleSheet(Resource stylesheet) {
        if (styleSheets == null)
            return false;
        return styleSheets.containsKey(stylesheet);
    }

}