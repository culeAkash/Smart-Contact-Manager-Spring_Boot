package com.smart.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

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
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, // trigger validation
																								// induced in user bean
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, ModelMap model,
			HttpSession session) {// data coming from signup form will get mapped into the user
									// object which match
		// with the names in the user object

		try {
			if (!agreement) {// if chcekbox is not checked then throw an exception
//				System.out.println("Please accept terms and conditions");
				throw new Exception("Please accept terms and conditions");
			}

			// if error in respect to validation is present
			if (result.hasErrors()) {
//				System.out.println("hello");
				System.out.println("ERROR " + result.toString());
				model.addAttribute("user", user);
				return "signup";
			} // reflect this validation in signup page

			// set not defined attributes of user
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			// we will use password encoder later when we will use spring security here

			// takes password from the form encode the password and then put it to the user
			// object
			user.setPassword(this.passwordEncoder.encode(user.getPassword()));
			user.setImageUrl("user.png");

			// add user to database
			User user1 = this.userRepository.save(user);
			System.out.println(user1);
//			System.out.println("Agreement: "+agreement);
//			System.out.println(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Registration Successful", "alert-success"));// if registation
																										// successfull
			return "signup";
		} catch (Exception e) {
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!!! " + e.getMessage(), "alert-danger"));// exception
																														// raised
																														// hence
																														// registration
																														// unsuccessfull
			return "signup";
		} // now we have to show the message in views
	}
	/*
	 * the checkbox value coming from the form is not part of the user object hence
	 * it is handled seperately
	 */

	// user login controller
	@GetMapping("/signin")
	public String login(Model model) {
		model.addAttribute("title", "Login - Smart Contact Manager");
		return "login";
	}

	@GetMapping("/login-fail")
	public String failLogin(Model model) {
		model.addAttribute("title", "Login Failure - Smart Contact Manager");
		return "login-fail";
	}
}
