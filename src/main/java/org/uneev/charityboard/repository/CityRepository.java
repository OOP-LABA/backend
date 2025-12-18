package org.uneev.charityboard.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.uneev.charityboard.entity.City;

import java.util.Optional;

@Repository
public interface CityRepository extends CrudRepository<City, Integer> {
    Optional<City> findByName(String name);
}
