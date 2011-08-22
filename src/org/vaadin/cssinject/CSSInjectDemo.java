package org.vaadin.cssinject;

import java.awt.Color;

import com.vaadin.Application;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@SuppressWarnings("serial")
public class CSSInjectDemo extends Application {

    private Thread t;
    private float sequence = 0;
    private boolean isRunning = true;

    private VerticalLayout layout;
    private CSSInject css;
    private ProgressIndicator poller;

    @Override
    public void init() {
        layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        Window main = new Window("CSSInject Add-on", layout);
        setMainWindow(main);

        css = new CSSInject();
        layout.addComponent(css);

        Label colorMeDynamically = new Label(
                "Hello Vaadin, in all different colors!");
        layout.addComponent(colorMeDynamically);
        colorMeDynamically.addStyleName(Reindeer.LABEL_H1);
        colorMeDynamically.setSizeUndefined();

        // Change the label color with an interval. We need a progress indicator
        // to get the updates via polling
        poller = new ProgressIndicator();
        poller.setIndeterminate(true);
        poller.setPollingInterval(500);
        layout.addComponent(poller);

        layout.setExpandRatio(colorMeDynamically, 1);
        layout.setComponentAlignment(colorMeDynamically,
                Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(poller, Alignment.TOP_CENTER);

        // Do the actual color operation and set the CSS value
        t = new Thread() {
            @Override
            public void run() {
                try {
                    while (isRunning) {
                        Thread.sleep(500);

                        synchronized (layout.getApplication()) {
                            sequence += 2;
                            Color c = Color.getHSBColor(sequence / 100, 0.8f,
                                    0.7f);
                            css.setValue(".v-app,.v-generated-body { background: rgb("
                                    + c.getRed()
                                    + ","
                                    + c.getGreen()
                                    + ","
                                    + c.getBlue()
                                    + "); } .v-label { color: #fff; text-shadow: 0 1px 2px rgba(0,0,0,.4); }");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();

        Button toggle = new Button("Remove CSSInject",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (poller.isVisible()) {
                            poller.setVisible(false);
                            layout.removeComponent(css);
                            event.getButton().setCaption("Add CSSInject");
                        } else {
                            poller.setVisible(true);
                            layout.addComponent(css);
                            event.getButton().setCaption("Remove CSSInject");
                        }
                    }
                });
        layout.addComponent(toggle);
        layout.setComponentAlignment(toggle, Alignment.TOP_CENTER);

    }

    @Override
    public void close() {
        isRunning = false;
        super.close();
    }
}
