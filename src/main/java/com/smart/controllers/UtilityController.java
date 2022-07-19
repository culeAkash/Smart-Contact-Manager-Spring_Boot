package com.smart.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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

//controller for handling utility methods like delete and update

@Controller
@RequestMapping("/user")
public class UtilityController {

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

	// Handler for deleting contacts
	@GetMapping("/delete-contact/{cId}")
	public String deleteContact(@PathVariable("cId") int cId, Principal principal, HttpSession session)
			throws IOException {

		Optional<Contact> contactOptional = this.contactRepo.findById(cId);
		Contact contact = contactOptional.get();

		// We want that user of only that contact can delete the contact and no other
		// user
		String userName = principal.getName();
		User user = this.repo.getUserByUserName(userName);

		// Now if this user is same as the contact's user then he can delete it
		if (user.getId() == contact.getUser().getId()) {
//			contact.setUser(null)
			this.contactRepo.delete(contact);
			session.setAttribute("delete", new Message("Contact Deleted Successfully...", "alert-success"));

			// delete image from database too
			// Don't delete the image if it is the default image
			if (contact.getImageUrl() != null && !contact.getImageUrl().equals("contact.png")) {
				// get saved folder name
				File saveFile = new ClassPathResource("/static/images").getFile();

				// get file name
				String fileName = contact.getImageUrl();

				// make complete path of image
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
				Files.delete(path);
			}

			return "redirect:/user/show-contacts/0";
		}

		session.setAttribute("delete", new Message("Your are not authorized to delete this contact..", "alert-danger"));
		return "redirect:/user/show-contacts/0";

	}

	// Handler to hadnle update contact
	// Using postmapping, now user can only call this page by clicking on the button
	// but not by writing url manually, solving a security bug
	@PostMapping("/update-contact/{cId}")
	public String updateContact(@PathVariable("cId") int cId, Model model, Principal principal) {
		this.addUser(model, principal);

		Contact contact = this.contactRepo.findById(cId).get();
		model.addAttribute("title", "Update Contact - " + contact.getName());
		model.addAttribute("contact", contact);

		return "normal/update_contact";

	}

	// Hanlder for processing update contact
	@PostMapping("/process-update")
	public String updateProcess(@Valid @ModelAttribute Contact contact, BindingResult result,
			@RequestParam("image") MultipartFile file, Model model, Principal principal, HttpSession session) {
		// we need id of contact too so will form a hidden input in the form
		this.addUser(model, principal);

		System.out.println(contact.getName());
		System.out.println(contact.getcId());

		try {

			// if any validation is not satisfied
			if (result.hasErrors()) {
				model.addAttribute("contact", contact);
				model.addAttribute("title", "Update Contact - " + contact.getName());
				return "normal/update_contact";
			}

			// Get the old contact from database using the cId
			Contact oldContactDetails = this.contactRepo.findById(contact.getcId()).get();

			if (!file.isEmpty()) {

				// Delete the old image if it is not contact.png
				if (!oldContactDetails.getImageUrl().equals("contact.png")) {
					File saveFile = new ClassPathResource("/static/images").getFile();

					// get file name
					String fileName = oldContactDetails.getImageUrl();

					// make complete path of image
					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
					Files.delete(path);
				}

				// Add the new photo
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

			} else {
				// if file is empty set old image url
				contact.setImageUrl(oldContactDetails.getImageUrl());
			}

			// set user to new contact
			contact.setUser(oldContactDetails.getUser());
			// on showing contact_info page show this message too
			session.setAttribute("message", new Message("Contact Updated Successfully", "alert-success"));
			// update contact in database
			this.contactRepo.save(contact);
			return "redirect:/user/contact-info/" + contact.getcId();

		} catch (Exception e) {
			session.setAttribute("message", new Message("Something went wrong !!!" + e.getMessage(), "alert-danger"));
			return "normal/update_contact";

		}

	}

	// Handlers for user utility

	// handler for updating user
	@PostMapping("/update-user/{id}")
	public String updateUserForm(@PathVariable("id") int id, Model model, Principal principal) {
		User user = this.addUser(model, principal);

		model.addAttribute("title", "Update User - " + user.getName());
		model.addAttribute("user", user);

		return "normal/update_user";
	}

	// Handler to process User Update form
	@PostMapping("/process-update-user")
	public String processUserUpdate(@Valid @ModelAttribute("user") User user, BindingResult result, Model model,
			Principal principal, @RequestParam("image") MultipartFile file, HttpSession session) {
		User oldUserDetails = this.addUser(model, principal);

		try {

			// if there is validation error in the update form
			if (result.hasErrors()) {
				model.addAttribute("user", user);
				model.addAttribute("title", "Update User - " + user.getName());
				System.out.println("valid error");
				return "normal/update_user";
			}

			// if the image file is empty keep the old file else change the file
			if (!file.isEmpty()) {

				// Delete the old image if it is not user.png
				if (oldUserDetails.getImageUrl() != null && !oldUserDetails.getImageUrl().equals("user.png")) {
					File saveFile = new ClassPathResource("/static/images/user").getFile();

					// get file name
					String fileName = oldUserDetails.getImageUrl();

					// make complete path of image
					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
					Files.delete(path);
				}

				// Add the new photo
				String fileNameTemp = file.getOriginalFilename();
				String fileName = fileNameTemp.substring(0, fileNameTemp.indexOf('.')) + user.getId()
						+ fileNameTemp.substring(fileNameTemp.indexOf('.'));

				// adding filename to database
				user.setImageUrl(fileName);

				// get save folder name
				File saveFile = new ClassPathResource("/static/images/user").getFile();

				// make complete path of image
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);

				// add file to images folder
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			} else {
				// if file is empty set old image url
				user.setImageUrl(oldUserDetails.getImageUrl());
			}

			// Bidirectionally change all the user and contacts contact and user
			// respectively
			for (Contact c : oldUserDetails.getContacts()) {
				c.setUser(user);
				user.getContacts().add(c);
			}

			if (!user.getEmail().equals(oldUserDetails.getEmail())) {
				this.repo.save(user);
				return "redirect:/signin";
			} // if there is change of email then principal also changes hence have to login
				// else no login required
			System.out.print(user);

			// on showing profile page show this message too
			session.setAttribute("message", new Message("User Updated Successfully", "alert-success"));

			// update user in database
			this.repo.save(user);

			// set title and user for profile page
			model.addAttribute("title", user.getName());
			model.addAttribute("user", user);

			System.out.println(principal.getName());

			return "/normal/profile";

		} catch (Exception e) {

			session.setAttribute("message", new Message("Something went wrong!!!" + e.getMessage(), "alert-danger"));
			System.out.println("error");
			e.printStackTrace();
			return "normal/update_user";
		}

	}

	// Handler to delete User Account
	@GetMapping("/delete-user")
	public String deleteUser(Principal principal, HttpSession session, Model model) {
		// Get user from principal
		User user = this.repo.getUserByUserName(principal.getName());

		try {

			// delete user image
			if (user.getImageUrl() != null && !user.getImageUrl().equals("user.png")) {
				File saveFile = new ClassPathResource("/static/images/user").getFile();

				// get file name
				String fileName = user.getImageUrl();

				// make complete path of image
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
				Files.delete(path);
			}

			// delete all contacts of the user
			for (Contact c : user.getContacts()) {
				this.contactRepo.delete(c);
			}

			this.repo.delete(user);

			return "redirect:/logout";

		} catch (Exception e) {
			session.setAttribute("message", new Message("Something went wrong!!!" + e.getMessage(), "alert-danger"));
			e.printStackTrace();
			model.addAttribute("user", user);
			model.addAttribute("title", user.getName());
			return "normal/profile";
		}

	}

}
