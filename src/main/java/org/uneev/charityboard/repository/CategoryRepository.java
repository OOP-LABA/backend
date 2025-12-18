package org.uneev.charityboard.repository;

import org.springframework.data.repository.CrudRepository;
import org.uneev.charityboard.entity.Category;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
    Optional<Category> findByName(String name);
}
