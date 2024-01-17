package com.jewoos.securityapi.repository.role;

import com.jewoos.securityapi.entity.Role;
import com.jewoos.securityapi.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleType(RoleType roleType);
}
