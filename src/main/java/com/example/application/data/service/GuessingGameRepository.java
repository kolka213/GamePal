package com.example.application.data.service;

import com.example.application.data.entity.GuessingGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GuessingGameRepository extends JpaRepository<GuessingGame, UUID> {
}
