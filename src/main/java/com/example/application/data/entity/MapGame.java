package com.example.application.data.entity;

import com.vaadin.flow.component.map.configuration.Coordinate;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class MapGame extends Game {

    @OneToMany(mappedBy = "mapGame")
    private List<Players> players;

    @ElementCollection(targetClass = HashMap.class)
    private Map<String, Coordinate> capitalCities;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private CapitalCity gameCapitalCity;


    public MapGame() {
    }

    public List<Players> getPlayers() {
        return players;
    }

    public void addPlayers(Players player) {
        this.players.add(player);
    }

    public void removePlayer(Players player){
        this.players.remove(player);
    }

    public void setPlayers(List<Players> players) {
        this.players = players;
    }

    public Map<String, Coordinate> getCapitalCities() {
        return capitalCities;
    }

    public void setCapitalCities(Collection<CapitalCity> capitalCities) {
        this.capitalCities = capitalCities
                .stream()
                .collect(Collectors.toMap(CapitalCity::getName, city ->
                        new Coordinate(city.getLongitude(), city.getLatitude()), (a, b) -> b, HashMap::new));
    }

    public CapitalCity getGameCapitalCity() {
        return gameCapitalCity;
    }

    public void setGameCapitalCity(CapitalCity gameCapitalCity) {
        this.gameCapitalCity = gameCapitalCity;
    }

}
