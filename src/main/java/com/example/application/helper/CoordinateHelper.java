package com.example.application.helper;

import com.vaadin.flow.component.map.configuration.Coordinate;

public class CoordinateHelper {

    static double lat1; // latitude of first coordinate
    static double lon1; // longitude of first coordinate
    static double lat2; // latitude of second coordinate
    static double lon2; // longitude of second coordinate
    static final double earthRadius = 6371; // radius of Earth in kilometers


    public static Double measureDistanceBetweenTwoPoints(Coordinate point1, Coordinate point2){
        lat1 = point1.getY();
        lon1 = point1.getX();

        lat2 = point2.getY();
        lon2 = point2.getX();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }
}
