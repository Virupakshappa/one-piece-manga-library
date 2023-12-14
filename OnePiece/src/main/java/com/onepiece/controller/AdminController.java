package com.onepiece.controller;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.onepiece.dao.AdminDAO;
import com.onepiece.dao.MangaDAO;
import com.onepiece.dao.NotificationDAO;
import com.onepiece.exception.AdminException;
import com.onepiece.exception.MangaException;
import com.onepiece.exception.NotificationException;
import com.onepiece.model.Admin;
import com.onepiece.model.Manga;
import com.onepiece.model.Notification;
import com.onepiece.service.EmailService;
import com.onepiece.service.MangaService;
import com.onepiece.validator.AdminValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	AdminDAO adminDao;

	@Autowired
	AdminValidator adminVal;

	@Autowired
	EmailService emailService;
	
	@Autowired
	MangaDAO mangaDao;
	
	@Autowired
	MangaService mangaService;

	@GetMapping("/signup")
	public String showSignupForm(ModelMap model, Admin admin) {

		model.addAttribute(admin);

		return "admin-signup";
	}

	@PostMapping("/signup")
	public ModelAndView addAdminPost(@ModelAttribute("admin") Admin admin, BindingResult binResult) {

		ModelAndView modelAndView = new ModelAndView();

		adminVal.validate(admin, binResult);

		if (binResult.hasErrors()) {
			modelAndView.setViewName("admin-signup");
			return modelAndView;
		}

		try {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(admin.getAdminPassword());
			admin.setAdminPassword(hashedPassword);

			int result = adminDao.addAdmin(admin);

			if (result > 0) {
				modelAndView.addObject("admin", admin);
				modelAndView.setViewName("admin-signup-success");
				emailService.sendEmailAdmin(admin.getAdminEmailID(), admin.getAdminID());

			} else if (result == -1) {
				modelAndView.addObject("adminID", admin.getAdminID());
				modelAndView.setViewName("admin-account-exists");
			} else {
				modelAndView.setViewName("admin-signup-error");
			}
		} catch (AdminException e) {
			e.printStackTrace();
		}
		catch (EmailException e) {
			System.out.println(e.getMessage());
		}
		return modelAndView;

	}

	@GetMapping("/login")
	public String showLoginForm() {
		return "admin-login";
	}

	@PostMapping("/login")
	@ResponseBody
	public String loginAdmin(@RequestParam("adminID") int adminID, @RequestParam("adminPassword") String adminPassword,
			HttpServletRequest request) {

		System.out.println("=======admin id========== " + adminID);
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		session = request.getSession(true);

		try {
			Admin storedAdmin = adminDao.getAdminDetails(adminID);

			if (storedAdmin == null) {
				return "error-admin";
			}
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

			if (passwordEncoder.matches(adminPassword, storedAdmin.getAdminPassword())) {
				session.setAttribute("adminID", adminID);
				return "success";
			} else {
				return "error-password";
			}

		} catch (AdminException e) {
			// Handle exception
			return "error";
		}
	}

	@GetMapping("/successpage")
	public String openAdminSuccessPage() {

		return "admin-login-success";
	}

	@GetMapping("/dashboard")
	public String openAdminDashboard() {

		return "admin-dashboard";
	}

	@GetMapping("/adminProfile")
	public ModelAndView showAdminProfileForm(HttpSession session) {

		ModelAndView mv = new ModelAndView();
		try {
			int adminID = (int) session.getAttribute("adminID");

			Admin admin = adminDao.getAdminDetails(adminID);

			if (admin != null) {
				mv.addObject("adminDetails", admin);
				mv.setViewName("admin-profile");
			} else
				mv.setViewName("admin-dashboard");
		} catch (AdminException e) {
			System.out.println(e.getMessage());
		}
		return mv;

	}

	@PostMapping("/adminProfile/edit")
	public ModelAndView editAdminProfile(@ModelAttribute("adminDetails") Admin admin, BindingResult bindRes,
			HttpSession session) {
		ModelAndView modelAndView = new ModelAndView();

		try {

			int adminID = (int) session.getAttribute("adminID");

			Admin newAdmin = adminDao.getAdminDetails(adminID);
			String adminPass = newAdmin.getAdminPassword();

			admin.setAdminPassword(adminPass);

			int result = adminDao.updateAdmin(admin);

			if (result < 0) {
				modelAndView.setViewName("error-updating-admin");
			} else {
				modelAndView.setViewName("admin-update-success");
			}

		} catch (AdminException e) {
			System.out.println(e.getMessage());
		}

		return modelAndView;

	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/admin/login";
	}

	@PostMapping("/verifyPassword")
	public ModelAndView adminVerifyPassword(@RequestParam("currentPassword") String currentPassword, HttpSession session) {

		ModelAndView mv = new ModelAndView();
		int adminID = (int) session.getAttribute("adminID");
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		try {
			Admin admin = adminDao.getAdminDetails(adminID);

			String realPassword = admin.getAdminPassword();
			if (passwordEncoder.matches(currentPassword, realPassword)) {
				mv.setViewName("admin-change-pass");

			} else {
				mv.addObject("adminDetails", admin);
				mv.addObject("passMissmatch", "Password should match the current password");
				
				mv.setViewName("admin-profile");
			}
		} catch (AdminException e) {
			e.printStackTrace();
		}

		return mv;

	}
	@GetMapping("/notifyOverdue/{mangaId}")
	public String notifyOverdue(@PathVariable int mangaId, Model model) {
	    System.out.println("====inside notify");
	    try {
	        Manga manga = mangaDao.findMangaById(mangaId);
	        
	        if (manga != null && mangaService.isMangaOverdue(manga)) {
	            String studentEmail = manga.getBorrowedBy().getEmail();
	            String message = "Please return the manga '" + manga.getAnimeName() + "' because it's overdue.";

	            model.addAttribute("manga", manga);

	            Notification notification = new Notification();
	            notification.setStudentEmail(studentEmail);
	            notification.setMessage(message);

	            NotificationDAO notificationDAO = new NotificationDAO();
	            notificationDAO.addNotification(notification);

	            return "admin-notified-success";

	        } else {
	            return "redirect:/manga/adminbrowse";
	        }
	    } catch (MangaException | NotificationException e) {
	        System.out.println(e.getMessage());
	        return "redirect:/manga/adminbrowse";
	    }
	}


}
