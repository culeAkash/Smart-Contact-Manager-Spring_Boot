package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@NotBlank(message = "Name must not be blank") // VALIDATION THAT NAME MUST NOT BE black, and if blank that message
													// must be printed
	@Size(min = 2, max = 20, message = "Minimum 2 and Maximum 20 characters are allowed!!!")
	private String name;

	@Column(unique = true)
	@NotBlank(message = "Email must not be blank")
	@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid Email!")
	private String email;

	@NotBlank(message = "Password must not be empty")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\S+$).{8,}$", message = "Password must contain atleast 1 uppercase, 1 lowercase, 1 special character and 1 digit ")
	private String password;

	private String role;
	private boolean enabled;
	private String imageUrl;
	@Column(length = 500)
	private String about;

	@JsonManagedReference
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user") // cascadeALL=> reflect changes in
																						// child table when user table
																						// is changed
	// fetch.LAZY=> when user table is loaded all contacts dont load automatically
	// but loaded on demand only,efficient has user can have may contacts
	// mappedBy => shows the user reference used in contact entity, saves a new
	// table for user-contact mapping
	private List<Contact> contacts = new ArrayList<Contact>();// A user can have many contacts

	public User() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getImageUrl() {
		return this.imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAbout() {
		return this.about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public List<Contact> getContacts() {
		return this.contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	@Override
	public String toString() {
		return "User [id=" + this.id + ", name=" + this.name + ", email=" + this.email + ", password=" + this.password
				+ ", role=" + this.role + ", enabled=" + this.enabled + ", imageUrl=" + this.imageUrl + ", about="
				+ this.about + ", contacts=" + this.contacts + "]";
	}

}
