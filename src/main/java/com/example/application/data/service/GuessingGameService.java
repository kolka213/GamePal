package com.example.application.data.service;

import com.example.application.data.entity.GuessingGame;
import com.example.application.data.entity.Players;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuessingGameService {

    private final GuessingGameRepository repository;

    private final PlayersService playersService;

    @Autowired
    public GuessingGameService(GuessingGameRepository repository, PlayersService playersService) {
        this.repository = repository;
        this.playersService = playersService;
    }

    public void save(GuessingGame game){
        repository.save(game);
    }

    public GuessingGame update(GuessingGame game){
        return repository.save(game);
    }

    public void delete(GuessingGame game){
        repository.delete(game);
    }

    public void addPlayer(GuessingGame game, Players player){
        game.addPlayers(player);
        save(game);
    }

    public void removePlayer(GuessingGame game, Players player){
        playersService.delete(player);
        game.removePlayer(player);
        save(game);
    }

    public List<GuessingGame> getAll(){
        return repository.findAll();
    }
}
