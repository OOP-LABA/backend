package org.uneev.charityboard.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uneev.charityboard.dto.*;
import org.uneev.charityboard.entity.Post;
import org.uneev.charityboard.exception.NoSuchPostException;
import org.uneev.charityboard.service.CommentService;
import org.uneev.charityboard.service.PostService;
import org.uneev.charityboard.service.ReviewService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final ReviewService reviewService;
    private final ModelMapper modelMapper;


    @GetMapping("")
    public List<PostResponseDto> getAllPosts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long minGoal,
            @RequestParam(required = false) Long maxGoal,
            @RequestParam(required = false, defaultValue = "newest") String sort
    ) {
        List<Post> posts = postService.getAll(search, category, minGoal, maxGoal, sort);

        return posts.stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public PostResponseDto getPostById(@PathVariable long id) {
        Optional<Post> post = postService.getById(id);
        if (post.isEmpty()) {
            throw new NoSuchPostException(
                    String.format("No post with id '%d'", id)
            );
        }

        return toResponse(post.get());
    }

    @GetMapping("/{id}/avatar")
    public ResponseEntity<byte[]> getPostAvatar(@PathVariable long id) {
        Post post = postService.getById(id).orElseThrow(() ->
                new NoSuchPostException(String.format("No post with id '%d'", id))
        );

        byte[] data = post.getAvatarData();
        if (data == null || data.length == 0) {
            return ResponseEntity.notFound().build();
        }

        MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;
        if (post.getAvatarContentType() != null && !post.getAvatarContentType().isBlank()) {
            contentType = MediaType.parseMediaType(post.getAvatarContentType());
        }

        return ResponseEntity.ok()
                .contentType(contentType)
                .body(data);
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfoDto> createPost(
            @RequestBody PostCreationDto postCreationDto,
            Principal principal
    ) {
        postService.createPost(postCreationDto, principal.getName());

        return new ResponseEntity<>(
                new ResponseInfoDto(HttpStatus.CREATED.value(), "Post successfully created!"),
                HttpStatus.CREATED
        );
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseInfoDto> createPostMultipart(
            @RequestParam String title,
            @RequestParam(required = false) String content,
            @RequestParam String category,
            @RequestParam long goal,
            @RequestParam String accountDetails,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Principal principal
    ) {
        PostCreationDto dto = new PostCreationDto();
        dto.setTitle(title);
        dto.setContent(content);
        dto.setCategory(category);
        dto.setGoal(goal);
        dto.setAccountDetails(accountDetails);

        postService.createPost(dto, image, principal.getName());

        return new ResponseEntity<>(
                new ResponseInfoDto(HttpStatus.CREATED.value(), "Post successfully created!"),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseInfoDto> updatePost(
            @PathVariable long id,
            @RequestBody PostUpdateDto postUpdateDto,
            Principal principal,
            org.springframework.security.core.Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        postService.updatePost(id, postUpdateDto, principal.getName(), isAdmin);
        return ResponseEntity.ok(new ResponseInfoDto(HttpStatus.OK.value(), "Post successfully updated!"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseInfoDto> deletePost(
            @PathVariable long id,
            Principal principal,
            org.springframework.security.core.Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        postService.deletePost(id, principal.getName(), isAdmin);
        return ResponseEntity.ok(new ResponseInfoDto(HttpStatus.OK.value(), "Post successfully deleted!"));
    }

    @PostMapping("/{postId}/comments/{commentId}/accept")
    public ResponseEntity<ResponseInfoDto> acceptOffer(
            @PathVariable long postId,
            @PathVariable long commentId,
            @RequestBody(required = false) AcceptOfferDto acceptOfferDto,
            Principal principal,
            org.springframework.security.core.Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        postService.acceptOffer(postId, commentId, acceptOfferDto, principal.getName(), isAdmin);
        return ResponseEntity.ok(new ResponseInfoDto(HttpStatus.OK.value(), "Offer accepted!"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseInfoDto> updateStatus(
            @PathVariable long id,
            @RequestBody PostStatusUpdateDto postStatusUpdateDto,
            Principal principal,
            org.springframework.security.core.Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        postService.updateStatus(id, postStatusUpdateDto, principal.getName(), isAdmin);
        return ResponseEntity.ok(new ResponseInfoDto(HttpStatus.OK.value(), "Status updated!"));
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<ResponseInfoDto> leaveReview(
            @PathVariable long id,
            @RequestBody ReviewCreationDto reviewCreationDto,
            Principal principal
    ) {
        reviewService.createReview(id, reviewCreationDto, principal.getName());
        return new ResponseEntity<>(
                new ResponseInfoDto(HttpStatus.CREATED.value(), "Review successfully created!"),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ResponseInfoDto> leaveComment(
            @PathVariable long id,
            @RequestBody CommentCreationDto commentCreationDto,
            Principal principal
    ) {
        commentService.createComment(commentCreationDto, id, principal.getName());

        return new ResponseEntity<>(
                new ResponseInfoDto(HttpStatus.CREATED.value(), "Comment successfully created!"),
                HttpStatus.CREATED
        );
    }

    private PostResponseDto toResponse(Post post) {
        PostResponseDto dto = modelMapper.map(post, PostResponseDto.class);

        boolean hasStoredAvatar =
                post.getAvatarContentType() != null
                        || post.getAvatarFilename() != null;

        if (hasStoredAvatar && post.getId() != null) {
            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/posts/{id}/avatar")
                    .buildAndExpand(post.getId())
                    .toUriString();
            try {
                dto.setAvatar(new URL(url));
            } catch (MalformedURLException ignored) {
                // keep existing dto.avatar
            }
        }

        return dto;
    }
}
