package com.example.application.data.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class MapGame extends AbstractEntity {

    @Column(name = "game_name")
    private String gameName;

    @Column(name = "private")
    private boolean isPrivate;

    @Column(name = "max_player_count")
    private Integer maxPLayerCount;

    @OneToMany(mappedBy = "mapGame")
    private List<Players> players;

    @ManyToOne
    @JoinColumn(name = "capital__id")
    private CapitalCity capitalCity;


    public MapGame() {
    }

    public MapGame(List<Players> players, String gameName, boolean isPrivate, Integer maxPLayerCount) {
        this.players = players;
        this.gameName = gameName;
        this.isPrivate = isPrivate;
        this.maxPLayerCount = maxPLayerCount;
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

    public void setPlayers(List<Players> players) {
        this.players = players;
    }

    public CapitalCity getCapitalCity() {
        return capitalCity;
    }

    public void setCapitalCity(CapitalCity capitalCity) {
        this.capitalCity = capitalCity;
    }
}
