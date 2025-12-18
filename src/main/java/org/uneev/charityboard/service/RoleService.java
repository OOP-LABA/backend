package org.uneev.charityboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.uneev.charityboard.entity.Role;
import org.uneev.charityboard.repository.RoleRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    Optional<Role> getUserRole() {
        return roleRepository.findByName("ROLE_USER");
    }
}
