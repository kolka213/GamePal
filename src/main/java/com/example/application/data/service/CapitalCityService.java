package com.example.application.data.service;

import com.example.application.data.entity.CapitalCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CapitalCityService {

    private final CapitalCityRepository repository;

    private final Random random = new Random();

    @Autowired
    public CapitalCityService(CapitalCityRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CapitalCity> getRandomCapitalCities(){
        List<CapitalCity> capitalCities = repository.findAll();
        Collections.shuffle(capitalCities);
        return capitalCities;
    }

    @Transactional
    public Page<CapitalCity> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public CapitalCity update(CapitalCity city){
        return repository.save(city);
    }

    public Optional<CapitalCity> get(UUID uuid){
        return repository.findById(uuid);
    }

    @Transactional(readOnly = true)
    public CapitalCity findCityByName(String cityName){
        return repository.findCapitalCityByName(cityName);
    }
}
