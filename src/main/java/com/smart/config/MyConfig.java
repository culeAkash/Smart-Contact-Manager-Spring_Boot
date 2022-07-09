package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class MyConfig extends WebSecurityConfigurerAdapter {

	// This method will create a bean in the class container when called
	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {// we will use this to encode password in HOMECONTROLLER
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

		// Provide User Detail Service and Password encoder to the authentication
		// provider
		authenticationProvider.setUserDetailsService(this.getUserDetailsService());
		authenticationProvider.setPasswordEncoder(this.passwordEncoder());

		return authenticationProvider;
	}

	// configure method
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(this.authenticationProvider());
	}

	// route
	@Override
	protected void configure(HttpSecurity http) throws Exception {// used to declare which routes to be protected
		http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")// admin urls will be accessable by users with
																			// admin role only
				.antMatchers("/user/**").hasRole("USER")// user => USER role
				.antMatchers("/**").permitAll()// other urls = ALL
				.and().formLogin().loginPage("/signin")// role decision will be form based login,and added custom login
														// page
				.loginProcessingUrl("/dologin").defaultSuccessUrl("/user/index")// successfull url login page redirect
//				.failureUrl("/login-fail")// login failure page
				.and().csrf().disable();// csrf disabled
		;
	}

}
