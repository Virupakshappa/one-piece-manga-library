package com.onepiece.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
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

import com.onepiece.dao.MangaDAO;
import com.onepiece.dao.NotificationDAO;
import com.onepiece.dao.StudentDAO;
import com.onepiece.exception.MangaException;
import com.onepiece.exception.NotificationException;
import com.onepiece.exception.StudentException;
import com.onepiece.model.Manga;
import com.onepiece.model.Notification;
import com.onepiece.model.Student;
import com.onepiece.service.EmailService;
import com.onepiece.service.MangaService;
import com.onepiece.validator.StudentValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentController {

	@Autowired
	StudentDAO studentDao;

	@Autowired
	MangaDAO mangaDao;

	@Autowired
	StudentValidator studentval;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	MangaService mangaService;
	
	@Autowired
	NotificationDAO notificationDAO;

	@GetMapping("/login")
	public String showLoginForm() {
		return "student-login";
	}

	@GetMapping("/signup")
	public String showSignupForm(ModelMap model, Student student) {

		model.addAttribute(student);
		return "student-signup";
	}

	@PostMapping("/signup")
	public ModelAndView addStudent(@ModelAttribute("student") Student student, BindingResult bindResult) {

		studentval.validate(student, bindResult);

		if (bindResult.hasErrors()) {
			return new ModelAndView("student-signup");
		}

		ModelAndView modelAndView = new ModelAndView();
		try {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(student.getPassword());
			student.setPassword(hashedPassword);

			int result = studentDao.addStudent(student);
			if (result > 0) {
				modelAndView.addObject("student", student);
				modelAndView.setViewName("student-signup-success");
				emailService.sendEmailStudent(student.getEmail(), student.getFirstName());

			} else if (result == -1) {
				modelAndView.setViewName("student-account-exists");
			} else {
				modelAndView.setViewName("student-signup-error");
			}
		} catch (StudentException e) {
			System.out.println(e.getMessage());
		} 
			catch (EmailException e) {
			System.out.println(e.getMessage());
		}
		return modelAndView;
	}

	@PostMapping("/login")
	@ResponseBody
	public String loginStudent(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpServletRequest request) {

		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		session = request.getSession(true);

		try {
			Student storedStudent = studentDao.getStudentByEmail(email);

			if (storedStudent != null) {
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

				if (passwordEncoder.matches(password, storedStudent.getPassword())) {
					session.setAttribute("studentEmail", email);
					return "success";
				} else {
					return "error";
				}
			} else {
				return "error";
			}
		} catch (StudentException e) {
			System.out.println(e.getMessage());
			return "error";
		}

	}

	@GetMapping("/successpage")
	public String openStudentSuccessPage() {

		return "student-login-success";
	}

	@GetMapping("/dashboard")
	public ModelAndView gotoDashboard(HttpSession session) {
	    ModelAndView modelAndView = new ModelAndView("student-dashboard");

	    String studentEmailID = (String)session.getAttribute("studentEmail");
	    List<Notification> notifications;
		try {
			notifications = notificationDAO.getUnreadNotifications(studentEmailID);
		    modelAndView.addObject("notifications", notifications);

		} catch (NotificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return modelAndView;
	}

	@GetMapping("/borrowedMangas")
	public ModelAndView getBorrowedMangas(HttpSession session) {
		ModelAndView mv = new ModelAndView("student-borrowed-manga");

		String email = (String) session.getAttribute("studentEmail");

		System.out.println("========================================" + email);

		try {
			List<Manga> borrowedMangas = studentDao.getBorrowedMangas(email);
		    borrowedMangas.forEach(manga -> manga.setOverdue(mangaService.isMangaOverdue(manga)));


			mv.addObject("borrowedManga", borrowedMangas);
		} catch (StudentException e) {
			System.out.println(e.getMessage());
		}

		return mv;
	}

	@PostMapping("/returnManga")
	public ModelAndView returnManga(@RequestParam("mangaID") List<Integer> mangaIDs) {
		ModelAndView modelAndView = new ModelAndView();

		try {
			List<Manga> returnedMangas = new ArrayList<>();
			for (Integer mangaId : mangaIDs) {
				int result = mangaDao.returnManga(mangaId);

				if (result < 0) {
					modelAndView.setViewName("error-returning-manga");
					return modelAndView;
				} else {
					Manga returnedManga = mangaDao.findMangaById(mangaId);
					if (returnedManga != null) {
						returnedManga.setBorrowDate(null);
	                    mangaDao.updateManga(returnedManga);
						returnedMangas.add(returnedManga);
					}
					modelAndView.addObject("result", result);
				}
			}

			modelAndView.addObject("returnedMangas", returnedMangas);
			modelAndView.setViewName("manga-return-success");

		} catch (MangaException e) {
			System.out.println(e.getMessage());
		}

		return modelAndView;
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/student/login";
	}

	@GetMapping("/details/{email}")
	public ModelAndView viewStudentDetails(@PathVariable("email") String email) {
		ModelAndView modelAndView = new ModelAndView();

		try {
			Student student = studentDao.getStudentByEmail(email);

			if (student != null) {
				modelAndView.addObject("student", student);
				modelAndView.setViewName("studentDetails");
			} else {
				modelAndView.setViewName("studentNotFound");
			}
		} catch (StudentException e) {
			System.out.println(e.getMessage());
		}

		return modelAndView;
	}

	@GetMapping("/studentProfile")
	public ModelAndView showStudentProfileForm(HttpSession session) {

		ModelAndView mv = new ModelAndView();
		try {
			String studentEmail = (String) session.getAttribute("studentEmail");

			Student student = studentDao.getStudentByEmail(studentEmail);

			if (student != null) {
				mv.addObject("studentDetails", student);
				mv.setViewName("student-profile");
			} else
				mv.setViewName("student-dashboard");
		} catch (StudentException e) {
			System.out.println(e.getMessage());
		}
		return mv;

	}

	@PostMapping("/studentProfile/edit")
	public ModelAndView editStudentProfile(@ModelAttribute("studentDetails") Student student, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView("student-profile");

		String studentEmail = (String) session.getAttribute("studentEmail");
		try {
			Student newStudent = studentDao.getStudentByEmail(studentEmail);
			String stdPassword = newStudent.getPassword();

			student.setPassword(stdPassword);

			int result = studentDao.updateStudent(student);

			if (result < 0) {
				modelAndView.addObject("errorUpdate", "Error Updating Student Profile!!");
			} else {
				modelAndView.addObject("successUpdate", "Student Profile Updated!!");
			}

		} catch (StudentException e) {
			System.out.println(e.getMessage());
		}

		return modelAndView;

	}

	@PostMapping("/verifyPassword")
	public ModelAndView studentVerifyPassword(@RequestParam("currentPassword") String currentPassword,
			HttpSession session) {

		ModelAndView mv = new ModelAndView();
		String StudentEmail = (String) session.getAttribute("studentEmail");
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		try {
			Student student = studentDao.getStudentByEmail(StudentEmail);

			String realPassword = student.getPassword();
			if (passwordEncoder.matches(currentPassword, realPassword)) {
				mv.setViewName("student-change-pass");
			} else {
				mv.addObject("studentDetails", student);
				mv.addObject("passMissmatch", "Password should match the current password");

				mv.setViewName("student-profile");
			}
		} catch (StudentException e) {
			e.printStackTrace();
		}

		return mv;

	}
	@GetMapping("/readNotification/{notificationId}")
	public String readNotification(@PathVariable Long notificationId) {
	    try {
			notificationDAO.markAsRead(notificationId);
		} catch (NotificationException e) {
			e.printStackTrace();
		}
	    return "redirect:/student/borrowedMangas";
	}


}
