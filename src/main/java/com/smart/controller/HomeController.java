package com.smart.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {
//	@Autowired
//	private UserRepository userRepository;
//	@GetMapping("/test")
//	@ResponseBody
//	public String test() {
//		User user=new User();
//		user.setName("Abhay Gupta");
//		user.setEmail("abhaygupta@gmail.com");
//		userRepository.save(user);
//		return "Working";
//	}
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home - College Management System");
		return "home";
	}
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","About - College Management System");
		return "about";
	}
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title","Register - College Management System");
		model.addAttribute("user",new User());
		return "signup";
	}
	//handler for registering user
	@RequestMapping(value="/do_register",method=RequestMethod.POST)
	public String registerUser(@ModelAttribute("user") User user,@RequestParam(value="agreement",defaultValue="false")boolean agreement,Model model,HttpSession session) {
		try {
			if(!agreement) {
				System.out.println("You Have Not agreed the terms and conditions.");
				throw new Exception("You Have Not agreed the terms and conditions.");
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			System.out.println("Agreement "+agreement);
			System.out.println("USER "+user);
			User result=this.userRepository.save(user);
			model.addAttribute("user",new User());
			session.setAttribute("message", new Message("Registered Successfully ","alert-success"));
			return "signup";
		}catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something Went Wrong "+e.getMessage(),"alert-danger"));
			return "signup";
		}
	}
	//handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title","Login Page");
		return "login";
	}
}
