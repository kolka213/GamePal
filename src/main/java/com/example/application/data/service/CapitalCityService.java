package com.example.application.data.service;

import com.example.application.data.entity.CapitalCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Random;

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

    @Transactional(readOnly = true)
    public CapitalCity findCityByName(String cityName){
        return repository.findCapitalCityByName(cityName);
    }
}
