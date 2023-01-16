package com.example.application.data.entity;

import com.vaadin.flow.component.map.configuration.Coordinate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Players extends AbstractEntity {

    private String player;

    private Coordinate coordinate;

    @ManyToOne
    @JoinColumn(name = "mapgame_id")
    private MapGame mapGame;

    @ManyToOne
    @JoinColumn(name = "guessing_id")
    private GuessingGame guessingGame;

    public Players() {
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public MapGame getMapGame() {
        return mapGame;
    }

    public void setMapGame(MapGame mapGame) {
        this.mapGame = mapGame;
    }

    public GuessingGame getGuessingGame() {
        return guessingGame;
    }

    public void setGuessingGame(GuessingGame guessingGame) {
        this.guessingGame = guessingGame;
    }
}
