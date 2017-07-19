package org.vaadin.cssinject;

import java.util.HashMap;
import java.util.Map;

import org.vaadin.cssinject.client.CSSInjectState;

import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.UI;

public class CSSInject extends AbstractExtension {

    private static final long serialVersionUID = 9172956539550353998L;

    private Map<String, Resource> styleSheets = new HashMap<>();
    private int nextId = 0;

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
        if (stylesheet == null) {
            throw new IllegalArgumentException("Stylesheet cannot be null");
        }

        // Generate next id
        String id = "ss" + nextId++;

        // Set stylesheet as resource
        setResource(id, stylesheet);

        // Store resource with id locally
        styleSheets.put(id, stylesheet);

        // Add id to list of IDs
        getState().styleSheetIds.add(id);

        return stylesheet;
    }

    /**
     * Remove the given stylesheet resource.
     *
     * @param styleSheet
     */
    public void removeStyleSheet(Resource styleSheet) {
        if (styleSheet == null) {
            throw new IllegalArgumentException("Stylesheet cannot be null");
        }

        // Remove given resource from local storage
        styleSheets.entrySet().removeIf(entry -> {
            if (styleSheet.equals(entry.getValue())) {

                // Remove stylesheet from resources
                setResource(entry.getKey(), null);

                // Remove from list of IDs
                getState().styleSheetIds.remove(entry.getKey());

                // Indicate that entry should be removed from the map
                return true;
            }
            return false;
        });
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

    public void removeThemeStyleSheet(String styleSheetUrl) {
        styleSheets.values().stream()
                .filter(resource -> (resource instanceof ThemeResource
                        && ((ThemeResource) resource).getResourceId()
                        .equals(styleSheetUrl))).findAny()
                .ifPresent(this::removeStyleSheet);
    }

    public boolean hasStyleSheet(Resource stylesheet) {
        return styleSheets.containsValue(stylesheet);
    }
}
