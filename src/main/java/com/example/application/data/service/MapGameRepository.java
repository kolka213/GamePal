package com.example.application.data.service;

import com.example.application.data.entity.MapGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MapGameRepository extends JpaRepository<MapGame, UUID> {
}
