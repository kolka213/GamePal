package com.example.application.data.service;

import com.example.application.data.entity.CapitalCity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CapitalCityRepository extends JpaRepository<CapitalCity, UUID> {

    CapitalCity findCapitalCityByName(String cityName);
}
