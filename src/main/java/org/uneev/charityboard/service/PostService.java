package org.uneev.charityboard.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.uneev.charityboard.dto.AcceptOfferDto;
import org.uneev.charityboard.dto.PostCreationDto;
import org.uneev.charityboard.dto.PostStatusUpdateDto;
import org.uneev.charityboard.dto.PostUpdateDto;
import org.uneev.charityboard.entity.Category;
import org.uneev.charityboard.entity.Comment;
import org.uneev.charityboard.entity.DepositStatus;
import org.uneev.charityboard.entity.Post;
import org.uneev.charityboard.entity.PostStatus;
import org.uneev.charityboard.entity.User;
import org.uneev.charityboard.exception.ForbiddenActionException;
import org.uneev.charityboard.exception.NoSuchPostException;
import org.uneev.charityboard.repository.CommentRepository;
import org.uneev.charityboard.repository.PostRepository;
import org.uneev.charityboard.repository.PostSpecifications;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    public void createPost(PostCreationDto postCreationDto, MultipartFile image, String username) {
        Post post = modelMapper.map(postCreationDto, Post.class);

        User user = userService.getByUsername(username).orElseThrow();
        Category category = categoryService.getOrCreate(postCreationDto.getCategory());

        post.setCategory(category);
        post.setAuthor(user.getProfile());

        if (image != null && !image.isEmpty()) {
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image attachments are supported");
            }
            try {
                post.setAvatarData(image.getBytes());
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to read uploaded image");
            }
            post.setAvatarContentType(contentType);
            post.setAvatarFilename(image.getOriginalFilename());
            post.setAvatar(null);
        }

        postRepository.save(post);
    }

    public List<Post> getAll() {
        return postRepository.findAll();
    }

    public List<Post> getAll(String search, String category, Long minGoal, Long maxGoal, String sort) {
        Specification<Post> specification = Specification.where(null);
        Specification<Post> searchSpec = PostSpecifications.titleOrContentContains(search);
        Specification<Post> categorySpec = PostSpecifications.hasCategoryName(category);
        Specification<Post> minSpec = PostSpecifications.goalGte(minGoal);
        Specification<Post> maxSpec = PostSpecifications.goalLte(maxGoal);

        if (searchSpec != null) specification = specification.and(searchSpec);
        if (categorySpec != null) specification = specification.and(categorySpec);
        if (minSpec != null) specification = specification.and(minSpec);
        if (maxSpec != null) specification = specification.and(maxSpec);

        Sort sortSpec = toSort(sort);
        return postRepository.findAll(specification, sortSpec);
    }

    public Optional<Post> getById(long id) {
        return postRepository.findById(id);
    }

    public void createPost(PostCreationDto postCreationDto, String username) {
        createPost(postCreationDto, null, username);
    }

    public void updatePost(long postId, PostUpdateDto postUpdateDto, String username, boolean isAdmin) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchPostException(String.format("No post with id '%d'", postId)));

        User user = userService.getByUsername(username).orElseThrow();
        boolean isOwner = post.getAuthor() != null
                && user.getProfile() != null
                && post.getAuthor().getId().equals(user.getProfile().getId());

        if (!isAdmin && !isOwner) {
            throw new ForbiddenActionException("You can edit only your own tasks");
        }
        if (!isAdmin && post.getStatus() != PostStatus.OPEN) {
            throw new ForbiddenActionException("You can edit only OPEN tasks");
        }

        if (postUpdateDto.getTitle() != null) post.setTitle(postUpdateDto.getTitle());
        if (postUpdateDto.getContent() != null) post.setContent(postUpdateDto.getContent());
        if (postUpdateDto.getAvatar() != null) post.setAvatar(postUpdateDto.getAvatar());
        if (postUpdateDto.getGoal() != null) post.setGoal(postUpdateDto.getGoal());
        if (postUpdateDto.getAccountDetails() != null) post.setAccountDetails(postUpdateDto.getAccountDetails());

        if (postUpdateDto.getCategory() != null) {
            Category category = categoryService.getOrCreate(postUpdateDto.getCategory());
            post.setCategory(category);
        }

        postRepository.save(post);
    }

    public void deletePost(long postId, String username, boolean isAdmin) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchPostException(String.format("No post with id '%d'", postId)));

        User user = userService.getByUsername(username).orElseThrow();
        boolean isOwner = post.getAuthor() != null
                && user.getProfile() != null
                && post.getAuthor().getId().equals(user.getProfile().getId());

        if (!isAdmin && !isOwner) {
            throw new ForbiddenActionException("You can delete only your own tasks");
        }
        if (!isAdmin && post.getStatus() != PostStatus.OPEN) {
            throw new ForbiddenActionException("You can delete only OPEN tasks");
        }

        postRepository.delete(post);
    }

    public void acceptOffer(long postId, long commentId, AcceptOfferDto acceptOfferDto, String username, boolean isAdmin) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchPostException(String.format("No post with id '%d'", postId)));

        User user = userService.getByUsername(username).orElseThrow();
        boolean isOwner = post.getAuthor() != null
                && user.getProfile() != null
                && post.getAuthor().getId().equals(user.getProfile().getId());

        if (!isAdmin && !isOwner) {
            throw new ForbiddenActionException("Only the task owner can accept an offer");
        }
        if (post.getStatus() != PostStatus.OPEN) {
            throw new ForbiddenActionException("You can accept offers only for OPEN tasks");
        }

        Comment offer = commentRepository.findByIdAndPost_Id(commentId, postId)
                .orElseThrow();

        if (offer.getAuthor() != null && post.getAuthor() != null
                && offer.getAuthor().getId().equals(post.getAuthor().getId())) {
            throw new ForbiddenActionException("You cannot accept your own offer");
        }

        post.setExecutor(offer.getAuthor());
        post.setStatus(PostStatus.IN_PROGRESS);

        Long deposit = acceptOfferDto != null && acceptOfferDto.getDepositAmount() != null
                ? acceptOfferDto.getDepositAmount()
                : post.getGoal();
        if (deposit == null) deposit = 0L;

        post.setDepositAmount(deposit);
        post.setDepositStatus(deposit > 0 ? DepositStatus.HELD : DepositStatus.NONE);

        postRepository.save(post);
    }

    public void updateStatus(long postId, PostStatusUpdateDto statusUpdateDto, String username, boolean isAdmin) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchPostException(String.format("No post with id '%d'", postId)));

        if (statusUpdateDto == null || statusUpdateDto.getStatus() == null) {
            throw new ForbiddenActionException("Status is required");
        }

        User user = userService.getByUsername(username).orElseThrow();
        boolean isOwner = post.getAuthor() != null
                && user.getProfile() != null
                && post.getAuthor().getId().equals(user.getProfile().getId());
        boolean isExecutor = post.getExecutor() != null
                && user.getProfile() != null
                && post.getExecutor().getId().equals(user.getProfile().getId());

        if (!isAdmin && !isOwner && !isExecutor) {
            throw new ForbiddenActionException("You can update status only for your own tasks");
        }

        PostStatus newStatus = statusUpdateDto.getStatus();
        if (!isAdmin) {
            if (newStatus == PostStatus.CANCELLED && !isOwner) {
                throw new ForbiddenActionException("Only the owner can cancel a task");
            }
            if (newStatus == PostStatus.DONE && !(isOwner || isExecutor)) {
                throw new ForbiddenActionException("Only the owner or executor can complete a task");
            }
        }

        post.setStatus(newStatus);
        if (newStatus == PostStatus.DONE && post.getDepositStatus() == DepositStatus.HELD) {
            post.setDepositStatus(DepositStatus.RELEASED);
        }
        if (newStatus == PostStatus.CANCELLED && post.getDepositStatus() == DepositStatus.HELD) {
            post.setDepositStatus(DepositStatus.REFUNDED);
        }

        postRepository.save(post);
    }

    private Sort toSort(String sort) {
        if ("goal_asc".equalsIgnoreCase(sort) || "budget_asc".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.ASC, "goal");
        }
        if ("goal_desc".equalsIgnoreCase(sort) || "budget_desc".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.DESC, "goal");
        }
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }
}
    
