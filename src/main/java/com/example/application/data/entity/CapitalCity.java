package com.example.application.data.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Collection;

@Entity(name = "capital_cities")
public class CapitalCity extends AbstractEntity {
    private String name;
    private double latitude;
    private double longitude;

    @OneToMany(mappedBy = "gameCapitalCity")
    private Collection<MapGame> mapGame;

    public CapitalCity(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CapitalCity() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public Collection<MapGame> getMapGame() {
        return mapGame;
    }

    public void setMapGame(Collection<MapGame> mapGame) {
        this.mapGame = mapGame;
    }
}
