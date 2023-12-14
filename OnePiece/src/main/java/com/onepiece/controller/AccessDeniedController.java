package com.onepiece.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AccessDeniedController {
	
	@GetMapping("/accessDenied")
	public String accessDenied() {
	    return "access-denied";
	}

    @GetMapping("/accessDenied/{role}")
    public String accessDenied(@PathVariable String role, Model model) {
        model.addAttribute("role", role);
        return "access-denied";
    }
}