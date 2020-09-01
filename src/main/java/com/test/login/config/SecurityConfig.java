package com.test.login.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.test.login.security.JwtAuthenticationEntryPoint;
import com.test.login.security.JwtAuthenticationFilter;
import com.test.login.service.CustomUserDetailsService;

/* 
* <pre>
 * Revision History:
 * Version Date             Author           	Changes
 * -----------------------------------------------------------------------------
 * 1.0     29 Aug 2020       Sekhar		Initial coding
 *
 * </pre>
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	CustomUserDetailsService customUserDetailsService;

	@Autowired
	JwtAuthenticationEntryPoint unauthorizedHandler;
	
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{
		authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManager();
	}
	
	protected void configure(HttpSecurity http) throws Exception {
        http
        .cors()
            .and()
        .csrf()
            .disable()
        .exceptionHandling()
            .authenticationEntryPoint(unauthorizedHandler)
            .and()
        .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
        .authorizeRequests()
            .antMatchers("/",
                "/favicon.ico",
                "/**/*.png",
                "/**/*.gif",
                "/**/*.svg",
                "/**/*.jpg",
                "/**/*.html",
                "/**/*.css",
                "/**/*.js")
                .permitAll()
            .antMatchers("/api/**")
                .permitAll()
            .antMatchers(HttpMethod.GET, "/api/users/**")
                .permitAll()
            .anyRequest()
                .authenticated();

// Add our custom JWT security filter
http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

}

}
