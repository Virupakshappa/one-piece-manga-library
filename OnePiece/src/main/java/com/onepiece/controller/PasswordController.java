package com.onepiece.controller;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.onepiece.dao.AdminDAO;
import com.onepiece.dao.StudentDAO;
import com.onepiece.exception.AdminException;
import com.onepiece.exception.StudentException;
import com.onepiece.model.Admin;
import com.onepiece.model.Student;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/password")
public class PasswordController {

	@Autowired
	AdminDAO adminDao;

	@Autowired
	StudentDAO studentDao;

	@GetMapping("/{role}")
	public String gotoChangePassForm(@PathVariable String role) {

		if (role.equals("admin")) {
			return "admin-change-pass";
		} else if (role.equals("student")) {
			return "student-change-pass";
		}

		return "home";
	}

	@PostMapping("/update/admin")
	public ModelAndView changeAdminPass(@RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword, HttpSession session) {
		ModelAndView mv = new ModelAndView();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		int adminID = (int) session.getAttribute("adminID");

		if (newPassword.equals(confirmPassword)) {
			try {
				Admin adminDetails = adminDao.getAdminDetails(adminID);
				adminDetails.setAdminPassword(passwordEncoder.encode(confirmPassword));
				adminDao.updateAdmin(adminDetails);
				mv.addObject("adminDetails", adminDetails);
				mv.addObject("passSuccess", "Password Updated successfully");
				mv.setViewName("admin-profile");
			} catch (AdminException | HibernateException e) {
				mv.addObject("passMissmatch", "Error Updating Admin" + e.getMessage());
				mv.setViewName("admin-change-pass");
			}
		} else {
			mv.addObject("passMissmatch", "Both the passwords should match");
			mv.setViewName("admin-change-pass");
		}

		return mv;
	}

	@PostMapping("/update/student")
	public ModelAndView changeStudentPass(@RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword, HttpSession session) {
		ModelAndView mv = new ModelAndView();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String StudentEmail = (String) session.getAttribute("studentEmail");

		if (newPassword.equals(confirmPassword)) {
			try {
				Student studentDetails = studentDao.getStudentByEmail(StudentEmail);
				studentDetails.setPassword(passwordEncoder.encode(newPassword));
				studentDao.updateStudent(studentDetails);
				mv.addObject("studentDetails", studentDetails);
				mv.addObject("passSuccess", "Password Updated successfully");
				mv.setViewName("student-profile");
			} catch (StudentException e) {
				mv.addObject("passMissmatch", "Error Updating Student" + e.getMessage());
				mv.setViewName("student-change-pass");
			}
		} else {
			mv.addObject("passMissmatch", "Both the passwords should match");
			mv.setViewName("student-change-pass");
		}

		return mv;
	}

}
