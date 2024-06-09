package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


//The @Controller annotation is used in Spring Boot to mark a class as a web request handler. 
//This means that the class will be responsible for handling incoming HTTP requests and returning responses.
@Controller
public class HomeController {

	/*The @Autowired annotation in Spring Boot is used to inject dependencies automatically. 
	 * It reduces the amount of boilerplate code required to configure and manage dependencies in Spring applications.
	 * */
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	/*The @GetMapping annotation is used to map HTTP GET requests to specific handler methods. It is a composed annotation that acts as a shortcut for @RequestMapping(method = RequestMethod.GET).
      The @GetMapping annotation is used to handle HTTP GET requests and retrieve data from RESTful APIs. 
      It is a convenient way to map HTTP GET requests to specific handler methods without having to explicitly specify the HTTP method.
	 * */
	// handler
	@GetMapping("/")
	public String home() {
		return "home";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "about - smart contact manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register - smart contact manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	/* The POST HTTP method is used to create a new resource on the server. 
	 *  When a client sends a POST request to a server, 
	 *  it is typically sending data that the server should use to create a new resource.
	 *  For example, a client might send a POST request to a server to create a new user account.
	 * */
	
	//Ques:-Why we use model Attribute in springBoot
	/*The @ModelAttribute annotation in Spring Boot is used to bind a method parameter or 
	 * method return value to a named model attribute, and then exposes it to a web view.
	 * 
	 * */
	
	/* (Model )It is also used to transfer data between the view and controller of the Spring MVC application.28 Feb 2022*/
	
	// handler for registering user
	@PostMapping(value = "/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {
			// Checking agreement
			if (!agreement) {
				System.out.println("You have not agreed the terms and conditions");
				throw new Exception("You have not agreed the terms and conditions");
			}

			// Validating form data
			if (result1.hasErrors()) {
				System.out.println("ERROR " + result1.toString());
				model.addAttribute("user", user);
				return "signup";  
			}
           
			// Setting default user role and image URL
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("Agreement" + agreement);
			System.out.println("USER" + user);
			
			// Saving user to database
			 this.userRepository.save(user);

			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registerd !!", "alert-success"));
			return "signup";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("something went wrong " + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}
	
	//handler for login page
	@GetMapping("/signin")
	public String customelogin(Model model) {
		model.addAttribute("tittle","Login Page");
		return "login";
	}

}
