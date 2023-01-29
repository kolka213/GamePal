package com.example.application.data.service;

import com.example.application.data.entity.Game;
import com.example.application.data.entity.GuessingGame;
import com.example.application.data.entity.MapGame;
import com.example.application.data.entity.Players;
import com.vaadin.flow.component.map.configuration.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlayersService {

    private final PlayersRepository repository;

    @Autowired
    public PlayersService(PlayersRepository repository) {
        this.repository = repository;
    }

    public Players save(String player, Coordinate coordinate, MapGame mapGame){
        Players players = new Players();
        players.setPlayerName(player);
        players.setCoordinate(coordinate);
        players.setMapGame(mapGame);

        return repository.save(players);
    }

    public Players save(String player, GuessingGame guessingGame){
        Players players = new Players();
        players.setPlayerName(player);
        players.setGuessingGame(guessingGame);

        return repository.save(players);
    }

    public Players update(Players player){
        return repository.save(player);
    }

    public void delete(Players player){
        repository.delete(player);
    }

    public Players getPlayerByName(String userName){
        return repository.findByPlayerName(userName);
    }


    @Transactional(readOnly = true)
    public List<Players> fetchAllPlayersFromGame(Game game){
        if (game instanceof MapGame) return repository.findAllByMapGame((MapGame) game);
        if (game instanceof GuessingGame) return repository.findAllByGuessingGame((GuessingGame) game);
        return null;
    }
}
