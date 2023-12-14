package com.onepiece.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.onepiece.model.Review;

@Component
public class ReviewValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Review.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Review review = (Review) obj;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "reviewText", "error.invalid.reviewText", "Review text is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rating", "error.invalid.rating", "Rating is required.");

        if (review.getRating() < 1 || review.getRating() > 5) {
            errors.rejectValue("rating", "error.invalid.rating", "Rating must be between 1 and 5.");
        }

    }
}
