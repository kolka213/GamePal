package com.example.application.data.service;

import com.example.application.data.entity.Words;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class WordsService {

    private final WordsRepository repository;

    public WordsService(WordsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<String> getRandomWords(){
        List<Words> words = repository.findAll();
        Collections.shuffle(words);
        return words.stream().map(Words::getWord).toList();
    }
}
