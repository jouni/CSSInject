package org.vaadin.cssinject;

import org.vaadin.cssinject.client.ui.VCSSInject;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ClientWidget;

@SuppressWarnings("serial")
@ClientWidget(VCSSInject.class)
public class CSSInject extends AbstractComponent {

	private String styles;

	public CSSInject() {
		this("");
	}

	public CSSInject(String styles) {
		this.styles = styles;
	}

	public void setValue(String text) {
		styles = text;
		requestRepaint();
	}

	public String getValue() {
		return styles;
	}

	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);
		target.addAttribute("styles", styles);
	}

}