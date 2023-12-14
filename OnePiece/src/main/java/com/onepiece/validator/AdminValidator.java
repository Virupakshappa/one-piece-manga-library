package com.onepiece.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.onepiece.model.Admin;

@Component
public class AdminValidator implements Validator {

	@Override
	public boolean supports(Class<?> aClass) {
		return aClass.equals(Admin.class);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adminID", "error.invalid.adminID", "adminID Required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adminEmailID", "error.invalid.adminEmailID",
				"adminEmailID Required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adminPassword", "error.invalid.adminPassword",
				"adminPassword Required");

		Admin admin = (Admin) obj;

		if (admin.getAdminID() <= 0) {
			errors.rejectValue("adminID", "error.invalid.adminID", "adminID should be greater than 0");
		}
		if (admin.getAdminEmailID() != null && !admin.getAdminEmailID().isEmpty()) {
			if (!(admin.getAdminEmailID().endsWith("northeastern.edu") || admin.getAdminEmailID().endsWith("gmail.com")
					|| admin.getAdminEmailID().endsWith("bu.edu"))) {
				errors.rejectValue("adminEmailID", "error.invalid.adminEmailID",
						"Email must end with northeastern.edu or bu.edu or gmail.com");
			}
		}
		if (admin.getAdminPassword() != null
				&& !admin.getAdminPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
			errors.rejectValue("adminPassword", "error.invalid.adminPassword",
					"Password must contain at least 1 uppercase, 1 lowercase, and 1 numeric character");
		}
	}
}
