package com.smart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

@Controller
public class HomeController {

	@Autowired
	UserRepository userRepository;

	// for home page
	@GetMapping("/")
	public String home(ModelMap model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	// for about page
	@GetMapping("/about")
	public String about(ModelMap model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register - Smart Contact Manager");
		model.addAttribute("user", new User());// user is sent to signup page to map new values into this object and
												// send to database
		return "signup";

	}

	// handler for /registering user
	@PostMapping("/register")
	public String registerUser(@ModelAttribute("user") User user,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, ModelMap model) {// data
																											// coming
																											// from
																											// signup
																											// form will
																											// get
																											// mapped
																											// into the
																											// user
																											// object
																											// which
																											// match
																											// with the
																											// names in
																											// the user
																											// object

		if (!agreement) {
			System.out.println("Please accept terms and conditions");
		}

		// set not defined attributes of user
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		// we will use password encoder later when we will use spring security here

		// add user to database
		User user1 = this.userRepository.save(user);
		System.out.println(user1);
//		System.out.println("Agreement: "+agreement);
//		System.out.println(user);
//		model.addAttribute("user", user);
		return "signup";
	}
	/*
	 * the checkbox value coming from the form is not part of the user object hence
	 * it is handled seperately
	 */

}
