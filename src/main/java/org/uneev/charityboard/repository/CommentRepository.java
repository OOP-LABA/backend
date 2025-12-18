package org.uneev.charityboard.repository;

import org.springframework.data.repository.CrudRepository;
import org.uneev.charityboard.entity.Comment;

import java.util.Optional;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    Optional<Comment> findByIdAndPost_Id(Long id, Long postId);
}
