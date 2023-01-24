package com.example.application.data.service;

import com.example.application.data.entity.MapGame;
import com.example.application.data.entity.Players;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MapGameService {

    private final MapGameRepository repository;

    private final PlayersService playersService;

    @Autowired
    public MapGameService(MapGameRepository repository, PlayersService playersService, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.playersService = playersService;
    }

    public void save(MapGame mapGame){
        repository.save(mapGame);
    }

    public Optional<MapGame> get(UUID uuid){
        return repository.findById(uuid);
    }

    public List<MapGame> getAll(){
        return repository.findAll();
    }

    public void addPlayer(MapGame mapGame, Players player){
        mapGame.addPlayers(player);
        save(mapGame);
    }

    public void removePlayer(MapGame mapGame, Players player){
        playersService.delete(player);
        mapGame.removePlayer(player);
        save(mapGame);
    }

    public void delete(MapGame game){
        repository.delete(game);
    }
}
