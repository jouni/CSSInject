package org.vaadin.addons.cssinject.shared;

import com.vaadin.shared.communication.SharedState;

import java.util.List;

public class CSSInjectState extends SharedState {

    private static final long serialVersionUID = 8236742409269917338L;

    public String styles;

    public int styleSheetId = -1;

    public List<String> removedKeys;

}
