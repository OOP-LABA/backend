package org.uneev.charityboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.uneev.charityboard.entity.Category;
import org.uneev.charityboard.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Optional<Category> getByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Category getOrCreate(String name) {
        String normalized = name == null ? "" : name.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }

        return categoryRepository.findByName(normalized).orElseGet(() -> {
            Category category = new Category();
            category.setName(normalized);
            category.setIsVerifiable(false);
            try {
                return categoryRepository.save(category);
            } catch (DataIntegrityViolationException e) {
                return categoryRepository.findByName(normalized)
                        .orElseThrow(() -> new IllegalArgumentException("Failed to create category"));
            }
        });
    }
    
    public List<Category> getAll() {
        return (List<Category>) categoryRepository.findAll();
    }
}
