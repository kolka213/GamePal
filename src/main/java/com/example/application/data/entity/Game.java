package com.example.application.data.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Game extends AbstractEntity{

    @Column(name = "game_name")
    private String gameName;

    @Column(name = "private")
    private boolean isPrivate;

    @Column(name = "max_player_count")
    private Integer maxPLayerCount;

    @Column(name = "password")
    private String password;

    public Game(String gameName, boolean isPrivate, Integer maxPLayerCount, String password) {
        this.gameName = gameName;
        this.isPrivate = isPrivate;
        this.maxPLayerCount = maxPLayerCount;
        this.password = password;
    }

    public Game() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
