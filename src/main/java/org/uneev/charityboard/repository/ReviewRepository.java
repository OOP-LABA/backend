package org.uneev.charityboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.uneev.charityboard.entity.Review;
import org.uneev.charityboard.entity.UserProfile;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByReviewee_IdOrderByCreatedAtDesc(Long revieweeId);

    long countByReviewee(UserProfile reviewee);

    @Query("select avg(r.rating) from Review r where r.reviewee = :reviewee")
    Double averageRatingFor(@Param("reviewee") UserProfile reviewee);
}

