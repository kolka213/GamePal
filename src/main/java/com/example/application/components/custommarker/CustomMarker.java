package com.example.application.components.custommarker;

import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.style.Icon;

public class CustomMarker extends MarkerFeature {

    private String userName;

    public CustomMarker(Coordinate coordinates, String userName, Icon icon) {
        super(coordinates, icon);
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
