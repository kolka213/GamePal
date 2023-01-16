package com.example.application.data.service;

import com.example.application.data.entity.GuessingGame;
import com.example.application.data.entity.Players;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GuessingGameService {

    private final GuessingGameRepository repository;

    private final PlayersService playersService;

    @Autowired
    public GuessingGameService(GuessingGameRepository repository, PlayersService playersService) {
        this.repository = repository;
        this.playersService = playersService;
    }

    public Optional<GuessingGame> get(UUID uuid){
        return repository.findById(uuid);
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
        game.removePlayer(player);
        save(game);
        playersService.delete(player);
    }

    public List<GuessingGame> getAll(){
        return repository.findAll();
    }
}
