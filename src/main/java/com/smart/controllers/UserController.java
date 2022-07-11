package com.smart.controllers;

import java.security.Principal;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user") // all the handlers in this controller now, will start with /user and then the
							// handler url
//this is done to authenticate all the urls with user
public class UserController {

	@Autowired
	UserRepository repo;

//	@ModelAttribute // now this method will run for all handlers
	public User addUser(Model model, Principal principal) {
		String userName = principal.getName();// email of the user will be returned

		// get details from database using email
		User loggedInUser = this.repo.getUserByUserName(userName);
		System.out.println(loggedInUser);

		// Sending user data to view
		model.addAttribute("user", loggedInUser);
		return loggedInUser;

	}

	@GetMapping("/index") // url for this => /user/index
	public String dashboard(Model model, Principal principal) {

		User loggedInUser = this.addUser(model, principal);
		model.addAttribute("title", "Dashboard - " + loggedInUser.getName());

		return "normal/user_dashboard";
	}
	// Principal => it is Spring Security class which helps to get different details
	// about the logged in user

	// handler for ad contact in user dashboard

	@GetMapping("/add-contact")
	public String addContact(Model model, Principal principal) {

		User loggedInUser = this.addUser(model, principal);
		model.addAttribute("title", "Add New Contact - " + loggedInUser.getName());
		model.addAttribute("contact", new Contact());

		return "normal/add_contacts";
	}

	// handler to process contact form
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult result, Model model,
			Principal principal, HttpSession session) {
		User loggedInUser = this.addUser(model, principal);
		model.addAttribute("title", "Add New Contact - " + loggedInUser.getName());
		try {

			if (result.hasErrors()) {
				model.addAttribute("contact", contact);
				return "normal/add_contacts";
			}

			// get user details from principal
			String userName = principal.getName();
			// get user
			User userByUserName = this.repo.getUserByUserName(userName);
			contact.setUser(userByUserName);
			userByUserName.getContacts().add(contact);

			model.addAttribute("contact", new Contact());

			System.out.println(userByUserName);
			session.setAttribute("message", new Message("Contact Added Successfully", "alert-success"));
			return "normal/add_contacts";
		} catch (Exception e) {
			model.addAttribute("contact", contact);
			session.setAttribute("message", new Message("Something went wrong!!! " + e.getMessage(), "alert-danger"));
		}

		return "normal/add_contacts";
	}

}
