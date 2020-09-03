package com.test.login.controller;

import com.test.login.enums.RoleName;
import com.test.login.exception.AppException;
import com.test.login.model.Role;
import com.test.login.model.User;
import com.test.login.payloads.*;
import com.test.login.security.CurrentUser;
import com.test.login.security.JwtTokenProvider;
import com.test.login.security.UserPrincipal;
import com.test.login.service.RoleService;
import com.test.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

	@Autowired
	public UserService userService;

	@Autowired
	public RoleService roleService;

	@Autowired
	JwtTokenProvider tokenProvider;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	PasswordEncoder passwordEncoder;

	@GetMapping("/msg")
	public String getMsg() {

		return "Hello world!";
	}

	@PostMapping("/auth/signin")
	public ResponseEntity<?> authenticateUser(
			@Valid @RequestBody LoginRequest loginReq) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(
						loginReq.getUsernameOrEmail(), loginReq.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = tokenProvider.generateToken(authentication);
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
	}

	@PostMapping("/auth/signup")
	@Transactional
	public ResponseEntity<?> registerUser(
			@Valid @RequestBody SignUpRequest signupReq) {

		if (userService.existsByUsername(signupReq.getUsername())) {
			return new ResponseEntity(
					new ApiResponse(false, "Username already Taken!"),
					HttpStatus.BAD_REQUEST);
		}

		if (userService.existsByEmail(signupReq.getEmail())) {
			return new ResponseEntity(
					new ApiResponse(false, "Email already used!"),
					HttpStatus.BAD_REQUEST);
		}

		User user = new User(signupReq.getUsername(), signupReq.getFirstName(),
				signupReq.getLastName(), signupReq.getEmail(),
				signupReq.getAddress(), signupReq.getPassword());

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		Role role = roleService.findByRoleName(RoleName.ROLE_USER)
				.orElseThrow(() -> new AppException("User Role not set."));

		user.setRoles(Collections.singleton(role));

		User saveUser = userService.save(user);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/auth/signin").buildAndExpand(saveUser.getUsername())
				.toUri();

		return ResponseEntity.created(location)
				.body(new ApiResponse(true, "User Registered Successfully!"));

	}
}
