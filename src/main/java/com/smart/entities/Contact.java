package com.smart.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;

	@NotBlank(message = "Name must not be blank") // VALIDATION THAT NAME MUST NOT BE black, and if blank that message
	// must be printed
	@Size(min = 2, max = 20, message = "Minimum 2 and Maximum 20 characters are allowed!!!")
	private String name;

	private String nickName;

	@NotBlank(message = "Please add suitable Work Title")
	private String work;

	@NotBlank(message = "Email must not be blank")
	@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Please Enter Valid Email!")
	private String email;

	@NotBlank(message = "Phone Number must not be empty")
	@Pattern(regexp = "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}", message = "Please Enter Valid Phone Number")
	private String phone;

	private String imageUrl;
	@Column(length = 1000)
	private String description;

	@ManyToOne
	private User user;// foreign key of user in Contact
	// A contact can have only one user

	public Contact() {
		super();
	}

	@Override
	public String toString() {
		return "Contact [cId=" + this.cId + ", name=" + this.name + ", nickName=" + this.nickName + ", work="
				+ this.work + ", email=" + this.email + ", phone=" + this.phone + ", imageUrl=" + this.imageUrl
				+ ", description=" + this.description + "]";
	}

	public int getcId() {
		return this.cId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickName() {
		return this.nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getWork() {
		return this.work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImageUrl() {
		return this.imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
