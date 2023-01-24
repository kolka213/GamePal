package com.example.application.data.entity;

import javax.persistence.Entity;

@Entity
public class Words extends AbstractEntity {

    private String word;

    public Words(String word) {
        this.word = word;
    }

    public Words() {
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
