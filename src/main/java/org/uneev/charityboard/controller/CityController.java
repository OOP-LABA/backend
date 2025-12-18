package org.uneev.charityboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uneev.charityboard.entity.City;
import org.uneev.charityboard.service.CityService;

import java.util.List;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping("")
    public List<City> getAllCities() {
        return cityService.getAll();
    }
}
