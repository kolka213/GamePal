package com.example.application.data.service;

import com.example.application.data.entity.Words;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Transactional
    public Page<Words> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public void delete(Words word){
        repository.delete(word);
    }

    public Words update(Words word){
        return repository.save(word);
    }

    public Optional<Words> get(UUID uuid){
        return repository.findById(uuid);
    }

}
