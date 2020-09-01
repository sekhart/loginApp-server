package com.test.login.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.login.enums.RoleName;
import com.test.login.model.Role;
import com.test.login.repository.RoleRepository;

@Service
public class RoleService {

	@Autowired
	public RoleRepository roleRepository;
	
	public Optional<Role> findByRoleName(RoleName name){
		return roleRepository.findByRoleName(name);
		
	}
}
