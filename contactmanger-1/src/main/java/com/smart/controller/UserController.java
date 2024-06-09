package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

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
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	// comman method for all handler
	// method for adding commman data to response
	@ModelAttribute
	public void addCommanData(Model model, Principal principal) {
		String username = principal.getName();

		System.out.println("USERNAME::" + username);

		User user = this.userRepository.getUserByEmail(username);

		System.out.println("USER" + user);

		// get the user using username(Email)
		// here sending user data from the controller to view where we can access and
		// print
		model.addAttribute("user", user);

	}

	// handler for user dashboard
	// principal interface represents the abstract notion of a principal,
	// whichcan be used to represent any entity, such as an individual,
	// acorporation, and a login id.
	// DashBoard Home
	@GetMapping("/index")
	public String dashboard(Model model) {
		model.addAttribute("tittle", "User DashBoard");
		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {

		model.addAttribute("tittle", "Add contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// handler for add form process
	@PostMapping("/process-contact")
	public String addFormProcess(@Valid @ModelAttribute("contact") Contact contact, BindingResult result,
			@RequestParam("profileImage") MultipartFile imageFile, Principal principal, HttpSession session) {

		if (result.hasErrors()) {
			System.out.println("Error" + result.toString());
			return "normal/add_contact_form";
		}

		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByEmail(name);

			// Set user for the contact
			contact.setUser(user);

			// processing and uploading file
			if (imageFile.isEmpty()) {
				// if the file is empty then try our message
				System.out.println("File is empty");
				contact.setImage("contact.png");

			} else {
				// file the file to folder and update the name to contact
				contact.setImage(imageFile.getOriginalFilename());
				File file = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(file.getAbsolutePath() + File.separator + imageFile.getOriginalFilename());

				Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.print("--------------Image is uploaded----------------");

			}

			// Add the contact to the user's contacts list
			user.getContacts().add(contact);

			// Save the user with the new contact
			this.userRepository.save(user);

			System.out.println("Contact added to the database");

			session.setAttribute("message", new Message("Your Contact is added Successfully!!", "success"));
		} catch (Exception e) {
			// Handle exception
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
			// error message
			session.setAttribute("message", new Message("Something went wrong!! Try Again..", "danger"));
		}

		return "normal/add_contact_form";
	}

	// handler for show view contact page
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page, Model m, Principal principle) {
		m.addAttribute("tittle", "Show user contact");
		// contact ki list ko bejna hai
		String username = principle.getName();
		User user = this.userRepository.getUserByEmail(username);

		// get object of pagerequest
		// current page
		// contact per page
		Pageable pageable = PageRequest.of(page, 6);

		Page<Contact> pageofContact = this.contactRepository.findContactUser(user.getId(), pageable);

		m.addAttribute("contacts", pageofContact);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", pageofContact.getTotalPages());

		return "normal/show_contact";
	}

	// handler for delete
	@GetMapping("/delete/{contactid}")
	public String deleteContact(@PathVariable("contactid") Integer cId, Model model, Principal principle) {

	    // Retrieve the contact by its ID
	    Optional<Contact> findById = this.contactRepository.findById(cId);

	    // Check if the contact is present, then get the contact object
	    Contact contact = findById.get();

	    // Get the username of the currently authenticated user
	    String userName = principle.getName();

	    // Retrieve the user object using the username
	    User user = this.userRepository.getUserByEmail(userName);

	    // Check if the contact belongs to the authenticated user before deleting
	    if (user.getId() == contact.getUser().getId()) {
	        this.contactRepository.delete(contact);
	    }

	    // Redirect to the contacts list page after deleting
	    return "redirect:/user/show-contacts/0";
	}

	// handler for update contact
	@PostMapping("/update-form/{cid}")
	public String openUpdateForm(@PathVariable("cid") Integer cId, Model m) {

		m.addAttribute("tittle", "Update Contact");

		Contact contact = this.contactRepository.findById(cId).get();

		m.addAttribute("contact", contact);

		return "normal/update_form";
	}

	// handler for update form process
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,
	                            @RequestParam("profileImage") MultipartFile imageFile, Model model, HttpSession session,
	                            Principal principle) {

	    // Log the contact name for debugging purposes
	    System.out.println(contact.getName());

	    try {
	        // Retrieve the old contact details from the database using the contact ID
	        Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get();

	        // Check if a new image file has been uploaded
	        if (!imageFile.isEmpty()) {
	            // File handling operations
	            
	            // Obtain the directory where images are stored
	            File file = new ClassPathResource("static/img").getFile();

	            // Construct the path to the new image file
	            Path path = Paths.get(file.getAbsolutePath() + File.separator + imageFile.getOriginalFilename());

	            // Copy the new image file to the destination, replacing any existing file with the same name
	            Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

	            // Set the image filename in the contact object to the new image file's name
	            contact.setImage(imageFile.getOriginalFilename());

	            // Set a success message in the session to notify the user of the successful update
	            session.setAttribute("message", new Message("Contact Updated Successfully", "success"));
	            System.out.println("--------------Image is uploaded----------------");

	        } else {
	            // If no new image file is uploaded, retain the old image filename
	            contact.setImage(oldcontactDetail.getImage());
	        }

	        // Associate the contact with the currently authenticated user
	        User user = this.userRepository.getUserByEmail(principle.getName());
	        contact.setUser(user);

	        // Save the updated contact details to the database
	        this.contactRepository.save(contact);
	    } catch (Exception e) {
	        // Handle any exceptions that occur during the process
	        e.printStackTrace();
	    }

	    // Redirect the user to the contact list view
	    return "redirect:/user/show-contacts/0";
	}
	
	//handler for show profile of user
	@GetMapping("/show-profile")
	public String showProfile(Model model) {
		
		model.addAttribute("tittle","show profile");
		return "normal/show_profile";
	}

}
