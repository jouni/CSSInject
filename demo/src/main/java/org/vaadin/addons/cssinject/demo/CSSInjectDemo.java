package org.vaadin.addons.cssinject.demo;

import org.vaadin.addons.cssinject.CSSInject;

import com.vaadin.annotations.Title;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
@Title("CSSInject Add-on")
public class CssInjectDemo extends UI {

    private Thread t;
    private float sequence = 0;
    private boolean isRunning = true;

    private VerticalLayout layout;
    private CSSInject css;
    private ProgressIndicator poller;

    @Override
    protected void init(VaadinRequest request) {
        layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        setContent(layout);

        css = new CSSInject(getUI());
        css.setStyles(".v-label {color:red !important;}");

        Label colorMeDynamically = new Label("Hello Vaadin, in all different colors!");
        layout.addComponent(colorMeDynamically);
        colorMeDynamically.addStyleName(Reindeer.LABEL_H1);
        colorMeDynamically.setSizeUndefined();

        // Change the label color with an interval. We need a progress indicator
        // to get the updates via polling
        poller = new ProgressIndicator();
        poller.setIndeterminate(true);
        poller.setPollingInterval(500);
        // layout.addComponent(poller);

        // layout.setExpandRatio(colorMeDynamically, 1);
        // layout.setComponentAlignment(colorMeDynamically,
        // Alignment.MIDDLE_CENTER);
        // layout.setComponentAlignment(poller, Alignment.TOP_CENTER);

        // Do the actual color operation and set the CSS value
        // t = new Thread() {
        // @Override
        // public void run() {
        // try {
        // while (isRunning) {
        // Thread.sleep(500);
        //
        // synchronized (layout.getApplication()) {
        // sequence += 2;
        // Color c = Color.getHSBColor(sequence / 100, 0.8f,
        // 0.7f);
        // css.setValue(".v-app,.v-generated-body { background: rgb("
        // + c.getRed()
        // + ","
        // + c.getGreen()
        // + ","
        // + c.getBlue()
        // +
        // "); } .v-label { color: #fff; text-shadow: 0 1px 2px rgba(0,0,0,.4); }");
        // }
        // }
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
        // };
        // t.start();

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);

        Button toggle = new Button("Remove CSSInject",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (poller.isVisible()) {
                            poller.setVisible(false);
                            css.remove();
                            event.getButton().setCaption("Add CSSInject");
                        } else {
                            poller.setVisible(true);
                            String styles = css.getStyles();
                            css = new CSSInject(getUI());
                            css.setStyles(styles);
                            event.getButton().setCaption("Remove CSSInject");
                        }
                    }
                });
        toggle.setWidth("200px");
        buttons.addComponent(toggle);
        layout.addComponent(buttons);
        layout.setComponentAlignment(buttons, Alignment.TOP_CENTER);

        final ThemeResource themeStyles = new ThemeResource("../demo/extra.css");
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
        toggleThemeStyleSheet.setWidth("200px");
        buttons.addComponent(toggleThemeStyleSheet);

    }

}
