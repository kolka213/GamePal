package com.example.application.data.service;

import com.example.application.data.entity.GuessingGame;
import com.example.application.data.entity.MapGame;
import com.example.application.data.entity.Players;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlayersRepository extends JpaRepository<Players, UUID> {

    List<Players> findAllByMapGame(MapGame mapGame);
    List<Players> findAllByGuessingGame(GuessingGame guessingGame);
    Players findByPlayerName(String playerName);
}
