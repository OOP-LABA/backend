package org.uneev.charityboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uneev.charityboard.dto.ComplaintCreationDto;
import org.uneev.charityboard.dto.ComplaintResolveDto;
import org.uneev.charityboard.dto.ComplaintResponseDto;
import org.uneev.charityboard.entity.Complaint;
import org.uneev.charityboard.entity.ComplaintStatus;
import org.uneev.charityboard.entity.Post;
import org.uneev.charityboard.entity.User;
import org.uneev.charityboard.entity.UserProfile;
import org.uneev.charityboard.exception.ForbiddenActionException;
import org.uneev.charityboard.repository.ComplaintRepository;
import org.uneev.charityboard.repository.UserProfileRepository;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserService userService;
    private final UserProfileRepository userProfileRepository;
    private final PostService postService;

    public void createComplaint(ComplaintCreationDto complaintCreationDto, String reporterUsername) {
        if (complaintCreationDto == null || complaintCreationDto.getReason() == null || complaintCreationDto.getReason().isBlank()) {
            throw new ForbiddenActionException("Complaint reason is required");
        }

        User reporter = userService.getByUsername(reporterUsername).orElseThrow();
        UserProfile reporterProfile = reporter.getProfile();
        if (reporterProfile == null) throw new ForbiddenActionException("Profile not found");

        UserProfile targetUser = null;
        Post targetPost = null;

        if (complaintCreationDto.getTargetUsername() != null) {
            targetUser = userProfileRepository.findByUser_Username(complaintCreationDto.getTargetUsername()).orElseThrow();
        }
        if (complaintCreationDto.getTargetPostId() != null) {
            targetPost = postService.getById(complaintCreationDto.getTargetPostId()).orElseThrow();
        }
        if (targetUser == null && targetPost == null) {
            throw new ForbiddenActionException("You must specify targetUsername or targetPostId");
        }

        Complaint complaint = new Complaint();
        complaint.setReason(complaintCreationDto.getReason());
        complaint.setReporter(reporterProfile);
        complaint.setTargetUser(targetUser);
        complaint.setTargetPost(targetPost);
        complaint.setStatus(ComplaintStatus.OPEN);

        complaintRepository.save(complaint);
    }

    public List<ComplaintResponseDto> listComplaints(ComplaintStatus status) {
        List<Complaint> list = status == null
                ? complaintRepository.findAllByOrderByCreatedAtDesc()
                : complaintRepository.findAllByStatusOrderByCreatedAtDesc(status);

        return list.stream().map(this::toDto).toList();
    }

    public void resolveComplaint(long complaintId, ComplaintResolveDto resolveDto, String adminUsername) {
        if (resolveDto == null || resolveDto.getStatus() == null) {
            throw new ForbiddenActionException("Status is required");
        }
        if (resolveDto.getStatus() == ComplaintStatus.OPEN) {
            throw new ForbiddenActionException("Cannot set status back to OPEN");
        }

        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow();
        complaint.setStatus(resolveDto.getStatus());
        complaint.setAdminNote(resolveDto.getAdminNote());
        complaint.setResolvedAt(new Date());
        complaint.setResolvedBy(userService.getByUsername(adminUsername).orElseThrow());

        complaintRepository.save(complaint);

        if (resolveDto.isBanUser()) {
            UserProfile target = complaint.getTargetUser();
            if (target == null && complaint.getTargetPost() != null) {
                target = complaint.getTargetPost().getAuthor();
            }
            if (target != null && target.getUser() != null) {
                String reason = resolveDto.getBanReason();
                if (reason == null || reason.isBlank()) reason = "Banned by admin";
                userService.banUser(target.getUser().getUsername(), reason);
            }
        }
    }

    private ComplaintResponseDto toDto(Complaint complaint) {
        ComplaintResponseDto dto = new ComplaintResponseDto();
        dto.setId(complaint.getId() == null ? 0 : complaint.getId());
        dto.setReason(complaint.getReason());
        dto.setStatus(complaint.getStatus() != null ? complaint.getStatus().name() : null);
        dto.setCreatedAt(complaint.getCreatedAt());

        dto.setReporterUsername(
                complaint.getReporter() != null && complaint.getReporter().getUser() != null
                        ? complaint.getReporter().getUser().getUsername()
                        : null
        );

        dto.setTargetUsername(
                complaint.getTargetUser() != null && complaint.getTargetUser().getUser() != null
                        ? complaint.getTargetUser().getUser().getUsername()
                        : null
        );
        dto.setTargetPostId(complaint.getTargetPost() != null ? complaint.getTargetPost().getId() : null);

        dto.setAdminNote(complaint.getAdminNote());
        dto.setResolvedAt(complaint.getResolvedAt());
        dto.setResolvedByUsername(
                complaint.getResolvedBy() != null ? complaint.getResolvedBy().getUsername() : null
        );
        return dto;
    }
}

