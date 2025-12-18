package org.uneev.charityboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uneev.charityboard.dto.UserProfileResponseDto;
import org.uneev.charityboard.dto.UserProfileUpdateDto;
import org.uneev.charityboard.entity.City;
import org.uneev.charityboard.entity.UserProfile;
import org.uneev.charityboard.repository.ReviewRepository;
import org.uneev.charityboard.repository.UserProfileRepository;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final CityService cityService;
    private final ReviewRepository reviewRepository;

    public UserProfileResponseDto getProfile(String username) {
        UserProfile profile = userProfileRepository.findByUser_Username(username).orElseThrow();

        UserProfileResponseDto dto = new UserProfileResponseDto();
        dto.setUsername(username);
        dto.setFirstName(profile.getFirstName());
        dto.setSecondName(profile.getSecondName());
        dto.setCity(profile.getCity() != null ? profile.getCity().getName() : null);

        dto.setHeadline(profile.getHeadline());
        dto.setAbout(profile.getAbout());
        dto.setSkills(profile.getSkills());
        dto.setPortfolio(profile.getPortfolio());
        dto.setContacts(profile.getContacts());

        dto.setRatingCount(reviewRepository.countByReviewee(profile));
        dto.setRatingAverage(reviewRepository.averageRatingFor(profile));

        return dto;
    }

    public void updateProfile(String username, UserProfileUpdateDto updateDto) {
        UserProfile profile = userProfileRepository.findByUser_Username(username).orElseThrow();

        if (updateDto.getFirstName() != null) profile.setFirstName(updateDto.getFirstName());
        if (updateDto.getSecondName() != null) profile.setSecondName(updateDto.getSecondName());

        if (updateDto.getCity() != null) {
            City city = cityService.getByName(updateDto.getCity()).orElseThrow();
            profile.setCity(city);
        }

        if (updateDto.getHeadline() != null) profile.setHeadline(updateDto.getHeadline());
        if (updateDto.getAbout() != null) profile.setAbout(updateDto.getAbout());
        if (updateDto.getSkills() != null) profile.setSkills(updateDto.getSkills());
        if (updateDto.getPortfolio() != null) profile.setPortfolio(updateDto.getPortfolio());
        if (updateDto.getContacts() != null) profile.setContacts(updateDto.getContacts());

        userProfileRepository.save(profile);
    }
}

