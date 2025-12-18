package org.uneev.charityboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uneev.charityboard.entity.City;
import org.uneev.charityboard.repository.CityRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;

    public Optional<City> getByName(String name) {
        return cityRepository.findByName(name);
    }

    public List<City> getAll() {
        return (List<City>) cityRepository.findAll();
    }
}
