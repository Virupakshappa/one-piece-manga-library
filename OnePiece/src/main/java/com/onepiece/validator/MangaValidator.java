package com.onepiece.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import com.onepiece.model.Manga;

@Component
public class MangaValidator implements Validator {

	private static final long TEN_MB_IN_BYTES = 10 * 1024 * 1024;

	@Override
	public boolean supports(Class<?> aClass) {
		return aClass.equals(Manga.class);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mangaID", "error.invalid.mangaID", "mangaID Required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "animeName", "error.invalid.animeName", "animeName Required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "writtenBy", "error.invalid.writtenBy", "writtenBy Required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "category", "error.invalid.category", "category Required");

		Manga manga = (Manga) obj;

		if (manga.getAnimeName() != null && !manga.getAnimeName().matches("[a-zA-Z\\s]+")) {
		    errors.rejectValue("animeName", "error.invalid.animeName", "animeName must contain only alphabets and spaces");
		}

		if (manga.getWrittenBy() != null && !manga.getWrittenBy().matches("[a-zA-Z\\s]+")) {
		    errors.rejectValue("writtenBy", "error.invalid.writtenBy", "writtenBy must contain only alphabets and spaces");
		}

		if (manga.getCategory() != null && !manga.getCategory().matches("[a-zA-Z]+")) {
			errors.rejectValue("category", "error.invalid.category", "category must contain only alphabets");
		}

		if (manga.getMangaID() <= 0) {
			errors.rejectValue("mangaID", "error.invalid.mangaID", "mangaID should be greater than 0");
		}
		MultipartFile mangaCover = manga.getMangaCover();
		if (mangaCover == null || mangaCover.isEmpty()) {
			errors.rejectValue("mangaCover", "error.invalid.mangaCover", "Admin must upload manga cover");
		} else {
			if (mangaCover.getSize() > TEN_MB_IN_BYTES) {
				errors.rejectValue("mangaCover", "error.invalid.mangaCover.size", "Manga cover must be less than 10MB");
			}

			String contentType = mangaCover.getContentType();
			if (contentType == null || !contentType.startsWith("image/")) {
				errors.rejectValue("mangaCover", "error.invalid.mangaCover.type", "Manga cover must be an image");
			}
		}

	}
}
