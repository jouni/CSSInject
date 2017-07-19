package org.vaadin.cssinject.demo;

import java.awt.Color;

import javax.servlet.annotation.WebServlet;

import org.vaadin.cssinject.CSSInject;

import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@Title("CSSInject Add-on")
public class CSSInjectDemo extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = CSSInjectDemo.class)
    public static class Servlet extends VaadinServlet {
    }

    private static final int POLL_INTERVAL = 500;

    private Thread t;
    private float sequence = 0;
    private boolean isRunning = true;

    private VerticalLayout layout;
    private CSSInject css;

    @Override
    protected void init(VaadinRequest request) {
        layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        setContent(layout);

        css = new CSSInject(getUI());
        css.setStyles(".v-label {color:red;}");

        Label colorMeDynamically = new Label(
                "Hello Vaadin, in all different colors!");
        layout.addComponent(colorMeDynamically);
        colorMeDynamically.addStyleName(ValoTheme.LABEL_H1);
        colorMeDynamically.setSizeUndefined();

        // Change the label color with an interval. Get the updates via polling
        UI.getCurrent().setPollInterval(POLL_INTERVAL);

        layout.setExpandRatio(colorMeDynamically, 1);
        layout.setComponentAlignment(colorMeDynamically,
                Alignment.MIDDLE_CENTER);

        // Do the actual color operation and set the CSS value
        t = new Thread() {
            @Override
            public void run() {
                try {
                    while (isRunning) {
                        Thread.sleep(POLL_INTERVAL);

                        synchronized (layout) {
                            sequence += 2;
                            Color c = Color.getHSBColor(sequence / 100, 0.8f,
                                    0.7f);
                            css.setStyles(".v-app.cssinjectdemo {"
                                    + " background-color: rgb("
                                    + c.getRed() + ","
                                    + c.getGreen() + ","
                                    + c.getBlue() + ");}"
                                    + ".v-app.cssinjectdemo .v-label {"
                                    + " color: #fff; text-shadow: 0 1px 2px rgba(0,0,0,.4); }");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);

        final ThemeResource themeStyles = new ThemeResource(
                "../demo/extra.css");
        Button toggleThemeStyleSheet = new Button("Add themes/demo/extra.css",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (css.hasStyleSheet(themeStyles)) {
                            css.removeStyleSheet(themeStyles);
                            event.getButton().setCaption(
                                    "Add themes/demo/extra.css");
                        } else {
                            css.addStyleSheet(themeStyles);
                            event.getButton().setCaption(
                                    "Remove themes/demo/extra.css");
                        }
                    }
                });
        toggleThemeStyleSheet.setWidth("300px");

        Button toggle = new Button("Remove CSSInject",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (css.getParent() != null) {
                            css.remove();
                            event.getButton().setCaption("Add CSSInject");
                            UI.getCurrent().setPollInterval(-1);

                            toggleThemeStyleSheet.setEnabled(false);
                            toggleThemeStyleSheet
                                    .setCaption("Add themes/demo/extra.css");
                        } else {
                            String styles = css.getStyles();
                            css = new CSSInject(getUI());
                            css.setStyles(styles);
                            event.getButton().setCaption("Remove CSSInject");
                            UI.getCurrent().setPollInterval(POLL_INTERVAL);

                            toggleThemeStyleSheet.setEnabled(true);
                        }
                    }
                });
        toggle.setWidth("300px");

        buttons.addComponents(toggle, toggleThemeStyleSheet);

        layout.addComponent(buttons);
        layout.setComponentAlignment(buttons, Alignment.TOP_CENTER);
    }
}
