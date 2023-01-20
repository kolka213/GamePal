package com.example.application.data.entity;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GuessingGame extends Game {

    @OneToMany(mappedBy = "guessingGame")
    private List<Players> players;

    @ElementCollection(targetClass = String.class)
    private List<String> words = new ArrayList<>();

    @Column(name = "current_word")
    private String currentWord;

    public GuessingGame() {
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

    public List<String> getWords() {
        return words;
    }

    public void addWords(String... words) {
        this.words.addAll(List.of(words));
    }

    public void setWords(List<String> words){
        this.words = words;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }
}
