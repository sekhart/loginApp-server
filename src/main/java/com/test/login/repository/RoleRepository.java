package com.test.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.login.enums.RoleName;
import com.test.login.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Optional<Role> findByRoleName(RoleName roleName);

}
