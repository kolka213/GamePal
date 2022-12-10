package com.example.application.data.entity;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MapGame extends AbstractEntity {

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @Column(name = "players")
    private List<String> players = new ArrayList<>();


    public MapGame() {

    }

    public MapGame(List<String> players) {
        this.players = players;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void addPlayers(String... player) {
        this.players.addAll(List.of(player));
    }

    public void removePlayer(String... player){
        this.players.removeAll(List.of(player));
    }
}
