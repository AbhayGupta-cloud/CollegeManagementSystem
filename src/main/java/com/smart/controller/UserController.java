package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.StudentRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private StudentRepository studentRepository;
	//method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		String userName=principal.getName();
		System.out.println("USERNAME:"+userName);
		//get the user using username
		User user=userRepository.getUserByUserName(userName);
		System.out.println(user);
		model.addAttribute("user",user);
	}
	//
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		return "normal/user_dashboard";	
	}
	//open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Principal principal,HttpSession session) {
		try {
		String name=principal.getName();
		User user=this.userRepository.getUserByUserName(name);
		//processing and uploading file
		if(file.isEmpty()) {
			//if the file is empty then try our message
			System.out.println("File Is Empty");
			contact.setImage("student.png");
			
			
		}else {
			//file the file to folder and update the name to contact
			contact.setImage(file.getOriginalFilename());
			File saveFile=new ClassPathResource("static/img").getFile();
			Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Image Uploaded Successfully");
		}
		contact.setUser(user);
		user.getContacts().add(contact);
		this.userRepository.save(user);
		System.out.println("DATA "+contact);
		System.out.println("Successfully added to database");
		//message attribute
		session.setAttribute("message",new Message("Student added successfully to database !! Add more students..","success"));
		}catch(Exception e) {
			System.out.println("ERROR "+e.getMessage());
			e.printStackTrace();
			//error message
			session.setAttribute("message",new Message("Something Went Wrong...!!Try Again","danger"));
		}
		
		return "normal/add_contact_form";
	}
	//show students
	//per page 5 students
	//current page=0
	@GetMapping("/show-students/{page}")
	public String showStudents(@PathVariable("page") Integer page,Model m,Principal principal) {
		m.addAttribute("title","Show Students Data");
		//contact list
//		String userName=principal.getName();
//		User user=this.userRepository.getUserByUserName(userName);
//		user.getContacts();
		String username=principal.getName();
		User user=this.userRepository.getUserByUserName(username);
		Pageable pageable=PageRequest.of(page, 5);
		Page<Contact> students=this.studentRepository.findStudentsByUser(user.getId(),pageable);
		m.addAttribute("students",students);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages", students.getTotalPages());
		return "normal/show_students";
	}
	//showing particular student details
	@RequestMapping("{cId}/student")
	public String showStudentDetails(@PathVariable("cId") Integer cId,Model model,Principal principal) {
		System.out.println("CID"+cId);
		Optional<Contact> contactOptional=this.studentRepository.findById(cId);
		Contact student=contactOptional.get();
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		if(user.getId()==student.getUser().getId()) {
			model.addAttribute("student",student);
			model.addAttribute("title",student.getName());
		}
		
		return "normal/student_detail";
	}
	//delete student
	@GetMapping("/delete/{cId}")
	public String deleteStudent(@PathVariable("cId") Integer cId,Model model,HttpSession session) {
		Optional<Contact> studentOptional=this.studentRepository.findById(cId);
		Contact student=studentOptional.get();
		student.setUser(null);
		this.studentRepository.delete(student);
		session.setAttribute("message",new Message("Student deleted successfully...","success"));
		return "redirect:/user/show-students/0";
	}
	//update student form handler
	@PostMapping("/update-student/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model model) {
		model.addAttribute("title","Update Student");
		Contact student=this.studentRepository.findById(cid).get();
		model.addAttribute("student",student);
		return "normal/update_form";
	}
	//update student
	@RequestMapping(value="/process-update",method=RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact student,@RequestParam("profileImage") MultipartFile file,Model m,HttpSession session) {
		try {
			//image selection
			if(!file.isEmpty()) {
				//file work..
				//rewrite
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("STUDENT NAME "+student.getName());
		System.out.println("Student Id "+student.getcId());
		
		return "";
	}
}
