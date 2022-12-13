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

    @Column(name = "game_name")
    private String gameName;

    @Column(name = "private")
    private boolean isPrivate;

    @Column(name = "max_player_count")
    private Integer maxPLayerCount;


    public MapGame() {
    }

    public MapGame(List<String> players, String gameName, boolean isPrivate, Integer maxPLayerCount) {
        this.players = players;
        this.gameName = gameName;
        this.isPrivate = isPrivate;
        this.maxPLayerCount = maxPLayerCount;
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

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Integer getMaxPLayerCount() {
        return maxPLayerCount;
    }

    public void setMaxPLayerCount(Integer maxPLayerCount) {
        this.maxPLayerCount = maxPLayerCount;
    }
}
