package com.smart.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
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

	@Autowired
	private ContactRepository contactRepo;

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
	public String processContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult result,
			@RequestParam("image") MultipartFile file, // for storing imagee file
			Model model, Principal principal, HttpSession session) {

		// sending user name to base
		User loggedInUser = this.addUser(model, principal);
		model.addAttribute("title", "Add New Contact - " + loggedInUser.getName());

		// if no exceptions
		try {

			// if any validation is not satisfied
			if (result.hasErrors()) {
				model.addAttribute("contact", contact);
				return "normal/add_contacts";
			}

			// get user details from principal
			String userName = principal.getName();
			// get user from database
			User userByUserName = this.repo.getUserByUserName(userName);

			// Processing and Uploading image...
			if (file.isEmpty()) {
				// if file is empty
				// add default image
				System.out.println("Image is empty");
				contact.setImageUrl("contact.png");
			} else {

				String fileNameTemp = file.getOriginalFilename();
				String fileName = fileNameTemp.substring(0, fileNameTemp.indexOf('.')) + contact.getcId()
						+ fileNameTemp.substring(fileNameTemp.indexOf('.'));

				// adding filename to database
				contact.setImageUrl(fileName);

				// get save folder name
				File saveFile = new ClassPathResource("/static/images").getFile();

				// make complete path of image
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);

				// add file to images folder
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("File uploaded to images");
			}

			// set user into contact as both has bidirectional relationship
			contact.setUser(userByUserName);
			// add the contact to contact list of the user
			userByUserName.getContacts().add(contact);
			// update the user
			System.out.println(userByUserName.getContacts());
			this.repo.save(userByUserName);

			model.addAttribute("contact", new Contact());

			System.out.println(userByUserName);

			// if contact added successfull show this
			session.setAttribute("message", new Message("Contact Added Successfully", "alert-success"));
			return "normal/add_contacts";
		} catch (Exception e) {
			model.addAttribute("contact", contact);
			// if any error show this
			session.setAttribute("message", new Message("Something went wrong!!! " + e.getMessage(), "alert-danger"));
			return "normal/add_contacts";
		}

	}

	// hadnler for show contacts
	// contact per page= n[10]
	// Current Page = page
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") int page, Model model, Principal principal, HttpSession session) {
		User loggedInUser = this.addUser(model, principal);
		model.addAttribute("title", "Contacts - " + loggedInUser.getName());

		// Contact ki list bhejni h, Principal ka use karke bhej sakte h via user but
		// hmlog yaha ContactRepo banake karenge
		String username = principal.getName();
		User user = this.repo.getUserByUserName(username);
		System.out.println(user);

		// contact per page= n[10]
		// Current Page = page
		Pageable pageable = PageRequest.of(page, 3);// PageRequest is a child class of Pageable,now pass pageable to
													// contactrepo function as it has now
		// both current page and page size

		// Sirf uss user ka contact nikalna h jo logged in h, uske liye ek custom method
		// bana repo me
		Page<Contact> contacts = this.contactRepo.findContactsByUser(user.getId(), pageable);
		if (contacts.isEmpty()) {
			model.addAttribute("contacts", contacts);
			session.setAttribute("message", new Message("No Contacts here yet, Add ", "alert-warning"));
			return "normal/show_contacts";
		}

		// We get two more attributes for implementing pagination in show contacts page
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		System.out.println(contacts);
		model.addAttribute("contacts", contacts);

		return "normal/show_contacts";
	}

	// Handler for showing particular contact details
	@GetMapping("/contact-info/{cId}")
	public String info(@PathVariable("cId") int cId, Model model, Principal principal) {
		this.addUser(model, principal);
		// get contact according to cID
//		Contact contact = this.contactRepo.getContactById(cId);

		// We can get contact by id directly from contactRepo
		Contact contact = this.contactRepo.getById(cId);

		/*
		 * We have a bug at this point, If the user tries to access some random contact
		 * by giving some random id in the url and if contact with that id is present in
		 * database then he can also see it which is a major security problem
		 *
		 * Now, we will send contact to the contact info page only if contact's user is
		 * same as session user
		 */

		String userName = principal.getName();
		User user = this.repo.getUserByUserName(userName);

		// We will send data only of user== contact's user

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", "Contact - " + contact.getName());
		} else {
			model.addAttribute("title", "Not Authorized");
		}

		return "normal/contact_info";
	}

}
