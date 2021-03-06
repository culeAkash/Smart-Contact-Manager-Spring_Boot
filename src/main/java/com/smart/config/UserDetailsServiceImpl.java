package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// fetching user from database
		User user = this.repo.getUserByUserName(username);
		// if not such user found
		if (user == null) {
			throw new UsernameNotFoundException("Could not found user");
		}

		// return details of the customer
		CustomUserDetails customUser = new CustomUserDetails(user);
		return customUser;
	}

}
