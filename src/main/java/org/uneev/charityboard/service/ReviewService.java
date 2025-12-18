package org.uneev.charityboard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.uneev.charityboard.dto.ReviewCreationDto;
import org.uneev.charityboard.dto.ReviewResponseDto;
import org.uneev.charityboard.entity.Post;
import org.uneev.charityboard.entity.PostStatus;
import org.uneev.charityboard.entity.Review;
import org.uneev.charityboard.entity.User;
import org.uneev.charityboard.entity.UserProfile;
import org.uneev.charityboard.exception.ForbiddenActionException;
import org.uneev.charityboard.repository.ReviewRepository;
import org.uneev.charityboard.repository.UserProfileRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final UserProfileRepository userProfileRepository;
    private final PostService postService;
    private final ModelMapper modelMapper;

    public void createReview(long postId, ReviewCreationDto reviewCreationDto, String username) {
        if (reviewCreationDto == null) throw new ForbiddenActionException("Review payload is required");
        if (reviewCreationDto.getRating() < 1 || reviewCreationDto.getRating() > 5) {
            throw new ForbiddenActionException("Rating must be between 1 and 5");
        }

        Post post = postService.getById(postId).orElseThrow();
        if (post.getStatus() != PostStatus.DONE) {
            throw new ForbiddenActionException("You can leave a review only after the task is DONE");
        }

        User user = userService.getByUsername(username).orElseThrow();
        UserProfile me = user.getProfile();
        if (me == null) throw new ForbiddenActionException("Profile not found");

        boolean amOwner = post.getAuthor() != null && post.getAuthor().getId().equals(me.getId());
        boolean amExecutor = post.getExecutor() != null && post.getExecutor().getId().equals(me.getId());

        if (!amOwner && !amExecutor) {
            throw new ForbiddenActionException("You can review only tasks you participated in");
        }

        UserProfile reviewee = amOwner ? post.getExecutor() : post.getAuthor();
        if (reviewee == null) {
            throw new ForbiddenActionException("Cannot determine who to review for this task");
        }

        Review review = new Review();
        review.setRating(reviewCreationDto.getRating());
        review.setContent(reviewCreationDto.getContent());
        review.setReviewer(me);
        review.setReviewee(reviewee);
        review.setPost(post);

        reviewRepository.save(review);
    }

    public List<ReviewResponseDto> getReviewsFor(String username) {
        UserProfile profile = userProfileRepository.findByUser_Username(username).orElseThrow();
        List<Review> reviews = reviewRepository.findAllByReviewee_IdOrderByCreatedAtDesc(profile.getId());

        return modelMapper.map(reviews, new TypeToken<List<ReviewResponseDto>>(){}.getType());
    }
}

