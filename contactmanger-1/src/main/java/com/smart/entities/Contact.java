package com.smart.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;


@Entity
public class Contact {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;
	
	//name of the contact
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name should contain only characters!")
	@Column(name="contact_name")
	private String name;
	
	//second name of contact
	@Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name should contain only characters!")
	@Column(name="contact_sname")
	private String secondName;
	
	//work of contact
	@Column(name="contact_work")
	private String work;
	
	//email of contact
	@Email(regexp="^[A-Za-z0-9+_.-]+@(.+)$",message="Email should be format!!")
	@Column(name="contact_email",unique = true)
	private String email;
	
	//phone of contact
	@Column(name="contact_phone")
	private String phone;
	
	//image of contact
	@Lob
	@Column(name="contact_image")
	private String image;
	
	//description of contact
	@Column(name="description")
	private String description;
	
	@ManyToOne
	private User user;

	public int getcId() {
		return cId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	

}
