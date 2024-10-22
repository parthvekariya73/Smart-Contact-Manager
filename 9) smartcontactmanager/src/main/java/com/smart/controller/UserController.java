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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entties.Contact;
import com.smart.entties.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	// add user on all handler
	@ModelAttribute		// always call
	private void addCommanData(Model model, Principal principal) {
		String username = principal.getName();

		User u = this.userRepository.getUserByUserName(username);
		System.out.println("\n\nUSER :- "+ u);
		model.addAttribute("user", u);
	}


	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "Deshboard - SmartContactManager");
		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	// add image 
	private boolean uploadImage(MultipartFile file, Contact contact, RedirectAttributes redirectAttributes) {
		try {
			if (!file.isEmpty()) {
				String fileName = file.getOriginalFilename();

	            // Define the path to save the image in `target/classes/static/image/`
	            File saveDir = new ClassPathResource("static/image").getFile(); // Resolves `target/classes/static/image`
	            Path savePath = Paths.get(saveDir.getAbsolutePath() + File.separator + fileName);

	            // Save the file
	            Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

	            // Set the image name in the contact
	            contact.setImage(fileName);

	            System.out.println("Image uploaded to: " + savePath);
	            System.out.println("File uploaded successfully: " + fileName);
			} else {
				System.out.println("Default Image set...");
				contact.setImage("deafult.jpg");
	        }

		} catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("msg", new Message("File upload failed!", "danger"));
			return false;
	    }
		return true;
	}

	// process form
	@PostMapping("/process-contact")
	public String proccessContactForm(@ModelAttribute Contact contact,
									  @RequestParam("profileImage") MultipartFile file,
									  Principal principal, RedirectAttributes redirectAttributes)
	{
		try{
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			// uploading file
			if(uploadImage(file, contact, redirectAttributes)) {
				user.getContacts().add(contact);
				contact.setUser(user);
				this.userRepository.save(user);
	
				System.out.println("Contact added successfully...");
				System.out.println("Data :- "+ contact);
	
				// message success
				// session.setAttribute("msg", new Message("Your Contact is added !!","success"));	// error
				// redirectAttributes :- We use RedirectAttributes to store the message temporarily. The message will be available only on the next request and will then be removed automatically.
				redirectAttributes.addFlashAttribute("msg", new Message("Your Contact is added !!", "success"));
			}
		}catch(Exception e) {
			System.out.println("ERORR :- "+ e.getMessage());

			// message error
			// session.setAttribute("msg", new Message("Something went wrong !! Try again...","danger"));
		    redirectAttributes.addFlashAttribute("msg", new Message("Something went wrong !! Try again...", "danger"));
		}

		return "redirect:/user/add-contact";
	}

	// show contacts handler
	@GetMapping("/show-contacts/{page}")
	public String showContactHandler(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title","Show Contact");
		
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		
		// pagination
		// page -> current page
		// 5 -> contact per page 
		Pageable pageable = PageRequest.of(page, 5); 
		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);
		
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show_contacts";
	}

	// showing particular contact detail
	@GetMapping("/{cid}/contact")
	public String showContactDetail(@PathVariable("cid") Integer cid, Model model, Principal principal) {
		model.addAttribute("title","Contact");
		
		try {
			Optional<Contact> optionalContact = this.contactRepository.findById(cid);
			Contact contact = optionalContact.get();
			System.out.println("Show Contact :- " + contact);
			
			// check
			String username = principal.getName();
			User user = this.userRepository.getUserByUserName(username);
			
			if(user.getId() == contact.getUser().getId()) 
			{
				model.addAttribute("contact",contact);			
			}
		}catch(Exception e) {
			e.printStackTrace();
			String error = e.getMessage();
			model.addAttribute("error",error);
		}
		
		return "normal/contact_detail";
	}
	
	// delete contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model model, 
								Principal principal, RedirectAttributes redirectAttributes) 
	{
		try {
			String username = principal.getName();
			User user = this.userRepository.getUserByUserName(username);
			
			// Optional<Contact> optionalContact = this.contactRepository.findById(cid);
			// Contact contact = optionalContact.get();
			Contact contact = this.contactRepository.findById(cid).get();
			
			// check
			if(user.getId() == contact.getUser().getId()) {			
				// unlink user
				contact.setUser(null);
				
				// delete image
				// String imagename = contact.getImage();
				
				// delete contact
				this.contactRepository.delete(contact);
				System.out.println("Contact " + contact.getCid() + " Deleted Successfully...");
				redirectAttributes.addFlashAttribute("msg", new Message("Contact Deleted Successfully...","success"));
			}
		}catch(Exception e) {
			e.printStackTrace();
		    redirectAttributes.addFlashAttribute("msg", new Message("Something went wrong !! Try again...", "danger"));
		}
		
		return "redirect:/user/show-contacts/0";
	}
	
	
	// update form 
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model model) {
		model.addAttribute("title","Update Conatct");
		
		Contact contact = this.contactRepository.findById(cid).get();
		model.addAttribute("contact",contact);
		
		return "normal/update_form";
	}
	
	// update contact handler
	@PostMapping("/process-update/{cid}")
	public String updtaeHanlder(@PathVariable("cid") Integer cid ,@ModelAttribute Contact newContact, 
								Model model, Principal principal, RedirectAttributes redirectAttributes) 
	{
		System.out.println("New Contact :- "+ newContact);
		try {		
			User user = this.userRepository.getUserByUserName(principal.getName());
			newContact.setUser(user);
			if(newContact.getImage() == null) {
				newContact.setImage("default.jpg"); 
			}
			this.contactRepository.save(newContact);
			redirectAttributes.addFlashAttribute("msg", new Message("Contact Updated Successfully...","success"));
		}catch(Exception e) {
			e.printStackTrace();
		    redirectAttributes.addFlashAttribute("msg", new Message("Something went wrong !! Try again...", "danger"));
		}
			
		
		return "redirect:/user/"+ newContact.getCid() + "/contact";
	}
	
	
	
	// your profile handler
	@GetMapping("/profile")
	public String profile(Model model, Principal principal) {
		model.addAttribute("title","Profile");

		User user = this.userRepository.getUserByUserName(principal.getName());
		model.addAttribute("user",user);
		
		return "normal/profile";
	}
}





//
//// image check
//if(!file.isEmpty()) {
//		// delete old photo
//	File deleteFile = new ClassPathResource("static/image").getFile();
//	File file1 = new File(deleteFile, oldContact.getImage());
//	file1.delete();
//	System.out.println("Image Deleting...");
	
//	// update new photo
//	File saveFile = new ClassPathResource("static/image").getFile();
//	Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
//	Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//	contact.setImage(file.getOriginalFilename());
//	System.out.println("Image Updating...");
//}else {
//	contact.setImage(oldContact.getImage());
//}
