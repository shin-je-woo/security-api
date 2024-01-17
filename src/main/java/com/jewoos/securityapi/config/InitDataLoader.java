package com.jewoos.securityapi.config;

import com.jewoos.securityapi.entity.Role;
import com.jewoos.securityapi.enums.RoleType;
import com.jewoos.securityapi.repository.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitDataLoader implements ApplicationListener<ApplicationReadyEvent> {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        createRoleIfNotFound(RoleType.ROLE_USER);
        createRoleIfNotFound(RoleType.ROLE_ADMIN);
    }

    private Role createRoleIfNotFound(RoleType roleType) {
        Role role = roleRepository.findByRoleType(roleType)
                .orElseGet(() -> new Role(roleType));

        return roleRepository.save(role);
    }
}
