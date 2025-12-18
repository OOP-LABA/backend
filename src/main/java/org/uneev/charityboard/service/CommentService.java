package org.uneev.charityboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uneev.charityboard.dto.CommentCreationDto;
import org.uneev.charityboard.entity.Comment;
import org.uneev.charityboard.entity.Post;
import org.uneev.charityboard.entity.PostStatus;
import org.uneev.charityboard.entity.User;
import org.uneev.charityboard.exception.ForbiddenActionException;
import org.uneev.charityboard.repository.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

    public void createComment(CommentCreationDto commentDto, long id, String username) {
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());

        Post post = postService.getById(id).orElseThrow();
        comment.setPost(post);

        User user = userService.getByUsername(username).orElseThrow();
        comment.setAuthor(user.getProfile());

        if (post.getStatus() != PostStatus.OPEN) {
            throw new ForbiddenActionException("You can send offers only for OPEN tasks");
        }
        if (post.getAuthor() != null && user.getProfile() != null
                && post.getAuthor().getId().equals(user.getProfile().getId())) {
            throw new ForbiddenActionException("You cannot send an offer to your own task");
        }

        commentRepository.save(comment);
    }
}
