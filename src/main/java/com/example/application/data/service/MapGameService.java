package com.example.application.data.service;

import com.example.application.data.entity.MapGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MapGameService {

    private final MapGameRepository repository;

    @Autowired
    public MapGameService(MapGameRepository repository) {
        this.repository = repository;
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

    public void addPlayer(MapGame mapGame, String... players){
        mapGame.addPlayers(players);
        save(mapGame);
    }

    public void removePlayer(MapGame mapGame, String... players){
        mapGame.removePlayer(players);
        save(mapGame);
    }
}
