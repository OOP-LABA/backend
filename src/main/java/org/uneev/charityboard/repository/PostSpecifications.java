package org.uneev.charityboard.repository;

import org.springframework.data.jpa.domain.Specification;
import org.uneev.charityboard.entity.Post;

public final class PostSpecifications {

    private PostSpecifications() {
    }

    public static Specification<Post> titleOrContentContains(String search) {
        if (search == null || search.isBlank()) return null;
        String like = "%" + search.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(root.get("content")), like)
        );
    }

    public static Specification<Post> hasCategoryName(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) return null;
        return (root, query, cb) -> cb.equal(root.get("category").get("name"), categoryName.trim());
    }

    public static Specification<Post> goalGte(Long minGoal) {
        if (minGoal == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("goal"), minGoal);
    }

    public static Specification<Post> goalLte(Long maxGoal) {
        if (maxGoal == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("goal"), maxGoal);
    }
}

