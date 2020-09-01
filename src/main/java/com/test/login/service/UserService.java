package com.test.login.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.login.model.User;
import com.test.login.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	public UserRepository userRepository;
	
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	public Optional<User> findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	public Optional<User> findUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}
	
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Transactional
	public User save(User user) {
		return userRepository.save(user);
	}

}
