package org.uneev.charityboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uneev.charityboard.dto.ResponseInfoDto;
import org.uneev.charityboard.dto.ReviewResponseDto;
import org.uneev.charityboard.dto.UserProfileResponseDto;
import org.uneev.charityboard.dto.UserProfileUpdateDto;
import org.uneev.charityboard.service.ReviewService;
import org.uneev.charityboard.service.UserProfileService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;
    private final ReviewService reviewService;

    @GetMapping("/me")
    public UserProfileResponseDto getMyProfile(Principal principal) {
        return userProfileService.getProfile(principal.getName());
    }

    @PutMapping("/me")
    public ResponseEntity<ResponseInfoDto> updateMyProfile(
            @RequestBody UserProfileUpdateDto userProfileUpdateDto,
            Principal principal
    ) {
        userProfileService.updateProfile(principal.getName(), userProfileUpdateDto);
        return ResponseEntity.ok(new ResponseInfoDto(HttpStatus.OK.value(), "Profile updated!"));
    }

    @GetMapping("/{username}")
    public UserProfileResponseDto getProfile(@PathVariable String username) {
        return userProfileService.getProfile(username);
    }

    @GetMapping("/{username}/reviews")
    public List<ReviewResponseDto> getProfileReviews(@PathVariable String username) {
        return reviewService.getReviewsFor(username);
    }
}

