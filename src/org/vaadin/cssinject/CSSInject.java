package org.vaadin.cssinject;

import java.util.ArrayList;

import org.vaadin.cssinject.client.ui.VCSSInject;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ClientWidget;

@SuppressWarnings("serial")
@ClientWidget(VCSSInject.class)
public class CSSInject extends AbstractComponent {

	private String styles;

	private ArrayList<Resource> styleSheets;

	private ArrayList<Resource> removeBuffer;

	public CSSInject() {
		this("");
	}

	/**
	 * Instantiate a new CSSInject with the given CSS string. The CSS will be
	 * applied once the CSSInject component is attached to the application.
	 * 
	 * @param styles
	 */
	public CSSInject(String styles) {
		this.styles = styles;
	}

	/**
	 * Set the CSS string for this CSSInject. This will be added to the Window
	 * where the CSSInject is attached as a STYLE element in the HEAD of the
	 * document.
	 * 
	 * @param text
	 */
	public void setValue(String text) {
		styles = text;
		requestRepaint();
	}

	/**
	 * Get the CSS string currently set to this CSSInject component.
	 * 
	 * @return
	 */
	public String getValue() {
		return styles;
	}

	/**
	 * Attach a separate CSS stylesheet to the Window where this CSSInject
	 * instance is attached to. The stylesheet will be added to the document's
	 * HEAD element as a LINK element with the given resource as the href.
	 * 
	 * @param stylesheet
	 */
	public Resource addStyleSheet(Resource stylesheet) {
		if (styleSheets == null) {
			styleSheets = new ArrayList<Resource>();
		}
		styleSheets.add(stylesheet);

		requestRepaint();
		return stylesheet;
	}

	/**
	 * Remove the given stylesheet resource.
	 * 
	 * @param styleSheet
	 * @return
	 */
	public boolean removeStyleSheet(Resource styleSheet) {
		if (removeBuffer == null) {
			removeBuffer = new ArrayList<Resource>();
		}
		removeBuffer.add(styleSheet);

		requestRepaint();
		return false;
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

	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);
		if (styles != null) {
			target.addAttribute("styles", styles);
		}
		if (styleSheets != null) {
			for (Resource stylesheet : styleSheets) {
				target.startTag("ss");
				target.addAttribute("src", stylesheet);
				if (removeBuffer != null && removeBuffer.contains(stylesheet)) {
					target.addAttribute("remove", true);
				}
				target.endTag("ss");
			}
		}
		if (removeBuffer != null) {
			for (Resource stylesheet : removeBuffer) {
				styleSheets.remove(stylesheet);
			}
			removeBuffer.clear();
		}

	}

}