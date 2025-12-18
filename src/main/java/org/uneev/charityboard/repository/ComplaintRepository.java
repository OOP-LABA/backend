package org.uneev.charityboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.uneev.charityboard.entity.Complaint;
import org.uneev.charityboard.entity.ComplaintStatus;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findAllByStatusOrderByCreatedAtDesc(ComplaintStatus status);
    List<Complaint> findAllByOrderByCreatedAtDesc();
}

