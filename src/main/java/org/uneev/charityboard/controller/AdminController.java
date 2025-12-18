package org.uneev.charityboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uneev.charityboard.dto.*;
import org.uneev.charityboard.entity.ComplaintStatus;
import org.uneev.charityboard.entity.User;
import org.uneev.charityboard.service.ComplaintService;
import org.uneev.charityboard.service.PostService;
import org.uneev.charityboard.service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ComplaintService complaintService;
    private final PostService postService;

    @GetMapping("/users")
    public List<AdminUserResponseDto> listUsers() {
        return userService.getAll().stream().map(this::toDto).toList();
    }

    @PostMapping("/users/{username}/ban")
    public ResponseEntity<ResponseInfoDto> banUser(
            @PathVariable String username,
            @RequestBody(required = false) BanUserDto banUserDto
    ) {
        String reason = banUserDto != null ? banUserDto.getReason() : null;
        userService.banUser(username, reason);
        return ResponseEntity.ok(new ResponseInfoDto(HttpStatus.OK.value(), "User banned"));
    }

    @PostMapping("/users/{username}/unban")
    public ResponseEntity<ResponseInfoDto> unbanUser(@PathVariable String username) {
        userService.unbanUser(username);
        return ResponseEntity.ok(new ResponseInfoDto(HttpStatus.OK.value(), "User unbanned"));
    }

    @GetMapping("/complaints")
    public List<ComplaintResponseDto> listComplaints(
            @RequestParam(required = false) ComplaintStatus status
    ) {
        return complaintService.listComplaints(status);
    }

    @PostMapping("/complaints/{id}/resolve")
    public ResponseEntity<ResponseInfoDto> resolveComplaint(
            @PathVariable long id,
            @RequestBody ComplaintResolveDto complaintResolveDto,
            Principal principal
    ) {
        complaintService.resolveComplaint(id, complaintResolveDto, principal.getName());
        return ResponseEntity.ok(new ResponseInfoDto(HttpStatus.OK.value(), "Complaint updated"));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ResponseInfoDto> deletePost(
            @PathVariable long id,
            Principal principal
    ) {
        postService.deletePost(id, principal.getName(), true);
        return ResponseEntity.ok(new ResponseInfoDto(HttpStatus.OK.value(), "Post deleted"));
    }

    private AdminUserResponseDto toDto(User user) {
        AdminUserResponseDto dto = new AdminUserResponseDto();
        dto.setId(user.getId() == null ? 0 : user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setBanned(user.isBanned());
        dto.setBanReason(user.getBanReason());
        dto.setBannedAt(user.getBannedAt());
        dto.setRoles(
                user.getRoles() == null
                        ? List.of()
                        : user.getRoles().stream().map(r -> r.getName()).toList()
        );

        if (user.getProfile() != null) {
            dto.setFirstName(user.getProfile().getFirstName());
            dto.setSecondName(user.getProfile().getSecondName());
            dto.setCity(user.getProfile().getCity() != null ? user.getProfile().getCity().getName() : null);
        }

        return dto;
    }
}

