package com.onepiece.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.onepiece.model.Student;

@Component
public class StudentValidator implements Validator {

	@Override
	public boolean supports(Class<?> aClass) {
		return aClass.equals(Student.class);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "error.invalid.firstName",
				"First Name Required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "error.invalid.lastName", "Last Name Required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.invalid.email", "Email Required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "age", "error.invalid.age", "Age Required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "collegeName", "error.invalid.collegeName",
				"College Name Required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.invalid.password", "Password Required");

		Student student = (Student) obj;

		if (student.getFirstName() != null && !student.getFirstName().matches("[a-zA-Z]+")) {
			errors.rejectValue("firstName", "error.invalid.firstName", "First Name must contain only alphabets");
		}

		if (student.getLastName() != null && !student.getLastName().matches("[a-zA-Z]+")) {
			errors.rejectValue("lastName", "error.invalid.lastName", "Last Name must contain only alphabets");
		}

		if (student.getAge() <= 0) {
			errors.rejectValue("age", "error.invalid.age", "Age should be greater than 0");
		}

		if (student.getEmail() != null && !student.getEmail().isEmpty()) {
			if (!(student.getEmail().endsWith("northeastern.edu") || student.getEmail().endsWith("gmail.com")|| student.getEmail().endsWith("bu.edu"))) {
				errors.rejectValue("email", "error.invalid.emailDomain", "Email must end with northeastern.edu or bu.edu or gmail.com");
			}
		}

		if (student.getPassword() != null && !student.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
			errors.rejectValue("password", "error.invalid.password",
					"Password must contain at least 1 uppercase, 1 lowercase, and 1 numeric character");
		}
	}
}
