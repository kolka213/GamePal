package com.example.application.data.service;

import com.example.application.data.entity.Words;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WordsRepository extends JpaRepository<Words, UUID> {
}
