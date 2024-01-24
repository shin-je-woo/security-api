package com.jewoos.securityapi.repository.role;

import com.jewoos.securityapi.entity.Role;
import com.jewoos.securityapi.enums.RoleType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Role> findByRoleType(RoleType roleType);
}
