package com.smart.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

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
	public String signup(ModelMap model) {
		model.addAttribute("title", "Register - Smart Contact Manager");
		return "signup";

	}
}
