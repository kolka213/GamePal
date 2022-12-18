package com.example.application.data.service;

import com.example.application.data.entity.MapGame;
import com.example.application.data.entity.Players;
import com.vaadin.flow.component.map.configuration.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayersService {

    private final PlayersRepository repository;

    @Autowired
    public PlayersService(PlayersRepository repository) {
        this.repository = repository;
    }

    public Players save(String player, Coordinate coordinate, MapGame mapGame){
        Players players = new Players();
        players.setPlayer(player);
        players.setCoordinate(coordinate);
        players.setMapGame(mapGame);

        return repository.save(players);
    }

    public Players update(Players player){
        return repository.save(player);
    }

    public void delete(Players player){
        repository.delete(player);
    }

    public HashMap<String, Coordinate> getAll(){
        HashMap<String, Coordinate> map = repository.findAll()
                .stream()
                .collect(Collectors.toMap(Players::getPlayer, Players::getCoordinate, (a, b) -> b, HashMap::new));
        return map;
    }

    public List<Players> fetchAllPlayersFromGame(MapGame mapGame){
        return repository.findAllByMapGame(mapGame);
    }
}
