package org.vaadin.cssinject.client;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.communication.SharedState;

public class CSSInjectState extends SharedState {

    public String styles;

    public List<String> styleSheetIds = new ArrayList<>();

}
