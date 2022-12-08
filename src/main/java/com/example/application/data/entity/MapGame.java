package com.example.application.data.entity;

import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;

@Entity
public class MapGame extends AbstractEntity{

    @ElementCollection
    private List<UserDetails> players;


    public MapGame() {

    }

    public MapGame(List<UserDetails> players) {
        this.players = players;
    }

    public List<UserDetails> getPlayers() {
        return players;
    }

    public void setPlayers(List<UserDetails> players) {
        this.players = players;
    }
}
