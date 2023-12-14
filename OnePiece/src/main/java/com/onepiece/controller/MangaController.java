package com.onepiece.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.onepiece.dao.MangaDAO;
import com.onepiece.dao.ReviewDAO;
import com.onepiece.dao.StudentDAO;
import com.onepiece.exception.MangaException;
import com.onepiece.exception.ReviewException;
import com.onepiece.exception.StudentException;
import com.onepiece.model.Manga;
import com.onepiece.model.Review;
import com.onepiece.model.Student;
import com.onepiece.service.MangaService;
import com.onepiece.validator.MangaValidator;
import com.onepiece.validator.ReviewValidator;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/manga")
public class MangaController {

	@Autowired
	MangaDAO mangaDao;

	@Autowired
	StudentDAO studentDao;

	@Autowired
	MangaValidator mangaVal;

	@Autowired
	ReviewValidator reviewVal;

	@Autowired
	ReviewDAO reviewDao;

	@Autowired
	MangaService mangaService;

	@GetMapping("/addManga")
	public String addManga(ModelMap model, Manga manga) {

		model.addAttribute(manga);
		return "manga-add";

	}

	@PostMapping("/addManga")
	public ModelAndView addManga(@ModelAttribute("manga") Manga manga, BindingResult bindRes) {

		ModelAndView modelAndView = new ModelAndView();

		mangaVal.validate(manga, bindRes);

		if (bindRes.hasErrors()) {
			modelAndView.setViewName("manga-add");
			return modelAndView;
		}

		String coverFileName = null;
		try {

			if (!manga.getMangaCover().isEmpty()) {
				coverFileName = manga.getMangaID() + ".jpg";
				manga.getMangaCover().transferTo(new File(
						"/Users/veeru/eclipse-workspace/OnePiece/src/main/resources/static/images", coverFileName));
			}
			manga.setCoverFileName(coverFileName);

			int result = mangaDao.addManga(manga);

			if (result > 0) {
				modelAndView.addObject("addedManga", manga);
				modelAndView.setViewName("manga-added-success");
			} else if (result == -1) {
				modelAndView.setViewName("manga-exists");
			} else {
				modelAndView.setViewName("manga-add-error");
			}
		} catch (MangaException e) {
			System.out.println("MangaException: " + e.getMessage());
		} catch (IllegalStateException | IOException e) {
			System.out.println(e.getMessage());
			modelAndView.setViewName("upload-error");
			return modelAndView;
		}
		return modelAndView;
	}

	@GetMapping("/browseManga")
	public String browseManga() {

		return "browse-mangas";
	}

	@PostMapping("/browseManga")
	public ModelAndView searchedManga(@RequestParam("searchtext") String searchtext,
			@RequestParam("searchoption") String searchoption) {
		ModelAndView modelAndView = new ModelAndView();
		try {
			List<Manga> searchedManga = mangaDao.searchedManga(searchtext, searchoption);

			if (searchedManga.isEmpty()) {
				modelAndView.setViewName("student-cannot-find-manga");
			} else {
				Map<Integer, String> mangaRatings = new HashMap<>();

				for (Manga manga : searchedManga) {

					List<Review> mangaReviews = manga.getReviews();
					double totalRating = 0;
					if (!mangaReviews.isEmpty()) {
						for (Review review : mangaReviews) {
							totalRating += review.getRating();
						}
						double averageRating = Math.round((totalRating / mangaReviews.size()) * 10) / 10.0;
						mangaRatings.put(manga.getMangaID(), String.valueOf(averageRating));
					} else {
						mangaRatings.put(manga.getMangaID(), "No ratings");
					}
				}

				modelAndView.addObject("searchedManga", searchedManga);
				modelAndView.addObject("mangaRatings", mangaRatings);
				modelAndView.setViewName("findMangaSuccessfully");
			}
		} catch (MangaException e) {
			System.err.println(e.getMessage());
			modelAndView.setViewName("error-browsing-manga");
		}

		return modelAndView;
	}

	@PostMapping("/borrowManga")
	public ModelAndView borrowManga(@RequestParam("mangaID") List<Integer> mangaIDs, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView();
		String email = (String) session.getAttribute("studentEmail");
		LocalDate today = LocalDate.now();

		try {
			List<Manga> borrowedMangas = new ArrayList<>();
			for (Integer mangaId : mangaIDs) {
				int result = mangaDao.borrowManga(mangaId, email);

				if (result < 0) {
					modelAndView.setViewName("error-borrowing-manga");
					return modelAndView;
				} else {
					Manga borrowedManga = mangaDao.findMangaById(mangaId);
					if (borrowedManga != null) {
						borrowedManga.setBorrowDate(today);

						mangaDao.updateManga(borrowedManga);

						borrowedMangas.add(borrowedManga);
					}
					modelAndView.addObject("result", result);
				}
			}

			modelAndView.addObject("borrowedMangas", borrowedMangas);
			modelAndView.setViewName("manga-borrow-success");

		} catch (MangaException e) {
			System.out.println(e.getMessage());
		}

		return modelAndView;
	}

	@PostMapping("/deleteManga")
	public ModelAndView deleteManga(@RequestParam("mangaID") List<Integer> mangaID) {

		ModelAndView modelAndView = new ModelAndView();
		System.out.println(mangaID + "==================================");
		try {

			for (Integer mangaId : mangaID) {

				int result = mangaDao.deleteManga(mangaId);

				if (result < 0) {
					modelAndView.setViewName("error-delete-manga");
					return modelAndView;
				} else {
					modelAndView.addObject("deleteSuccess", "Manga Deleted Successfully!!!");
					modelAndView.addObject("result", result);
					modelAndView.setViewName("manga-delete-success");
				}
			}
		} catch (MangaException e) {
			System.out.println(e);
		}

		return modelAndView;

	}

	@GetMapping("/adminbrowse")
	public ModelAndView adminsearchedManga(Review review) {
		ModelAndView modelAndView = new ModelAndView();

		try {
			List<Manga> searchedManga = mangaDao.adminSearchedManga();

			if (searchedManga.isEmpty()) {
				modelAndView.setViewName("cannotFindManga");
			} else {
				Map<Integer, String> mangaRatings = new HashMap<>();

				for (Manga smanga : searchedManga) {
					smanga.setOverdue(mangaService.isMangaOverdue(smanga));

					List<Review> mangaReviews = smanga.getReviews();
					double totalRating = 0;
					if (!mangaReviews.isEmpty()) {
						for (Review reviewObj : mangaReviews) {
							totalRating += reviewObj.getRating();
						}
						double averageRating = Math.round((totalRating / mangaReviews.size()) * 10) / 10.0;
						mangaRatings.put(smanga.getMangaID(), String.valueOf(averageRating));
					} else {
						mangaRatings.put(smanga.getMangaID(), "No ratings");
					}
				}

				modelAndView.addObject(review);
				modelAndView.addObject("mangaRatings", mangaRatings);
				modelAndView.setViewName("admin-findmanga-success");
				modelAndView.addObject("searchedManga", searchedManga);

				for (Manga man : searchedManga) {
					System.out.println(man);
				}
			}
		} catch (MangaException e) {
			System.out.println(e.getMessage());
		}

		return modelAndView;
	}

	@GetMapping("/edit/{id}")
	public ModelAndView editMangaForm(@PathVariable("id") int mangaId) {
		ModelAndView modelAndView = new ModelAndView();

		try {
			Manga manga = mangaDao.findMangaById(mangaId);

			if (manga != null) {

				modelAndView.addObject("manga", manga);
				modelAndView.setViewName("editMangaForm");
			} else {
				modelAndView.setViewName("mangaNotFound");
			}
		} catch (MangaException e) {
			System.out.println(e.getMessage());
		}

		return modelAndView;
	}

	@PostMapping("/edit")
	public String editManga(@ModelAttribute Manga manga, @RequestParam("mangaCover") MultipartFile file) {

		String coverFileName = manga.getMangaID() + ".jpg";
		manga.setCoverFileName(coverFileName);

		if (!file.isEmpty()) {
			try {
				file.transferTo(new File("/Users/veeru/eclipse-workspace/OnePiece/src/main/resources/static/images",
						coverFileName));
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		}

		try {
			mangaDao.updateManga(manga);
		} catch (MangaException e) {
			System.out.println(e.getMessage());
		}
		return "redirect:/manga/adminbrowse";
	}

	@GetMapping("/allreviews")
	public ModelAndView viewAllReviews(@RequestParam("mangaID") int mangaID, Review review) {
		ModelAndView modelAndView = new ModelAndView("review-manga");

		List<Review> reviews;
		try {
			reviews = reviewDao.getReviewsForManga(mangaID);
			modelAndView.addObject("mangaID", mangaID);

			if (!reviews.isEmpty()) {
				double totalRating = 0;
				for (Review reviewObj : reviews) {
					totalRating += reviewObj.getRating();
				}
				double averageRating = totalRating / reviews.size();
				averageRating = Math.round(averageRating * 10) / 10.0;
				modelAndView.addObject("averageRating", averageRating);
				modelAndView.addObject("reviews", reviews);
				modelAndView.addObject(review);

			} else {
				modelAndView.addObject(review);

				modelAndView.addObject("noReviews", "No Reviews Found");

			}
		} catch (ReviewException e) {
			e.printStackTrace();
		}
		return modelAndView;

	}

	@GetMapping("/reviewManga")
	public ModelAndView showReviewPage(@RequestParam("mangaID") int mangaID, Review review) {
		ModelAndView modelAndView = new ModelAndView("review-manga");
		modelAndView.addObject(review);
		modelAndView.addObject("mangaID", mangaID);
		return modelAndView;
	}

	@PostMapping("/reviewManga")
	public ModelAndView reviewManga(@RequestParam("mangaID") int mangaID, @ModelAttribute("review") Review review,
			BindingResult bindRes, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("mangaID", mangaID);

		reviewVal.validate(review, bindRes);

		if (bindRes.hasErrors()) {
			modelAndView.setViewName("review-manga");
			return modelAndView;
		}

		String email = (String) session.getAttribute("studentEmail");//

		try {
			Student student = studentDao.getStudentByEmail(email);
			Manga manga = mangaDao.findMangaById(mangaID);

			if (manga != null && manga.getBorrowedBy() != null && manga.getBorrowedBy().getEmail().equals(email)) {
				Review newReview = new Review(review.getReviewText(), review.getRating(), student, manga);
				manga.getReviews().add(newReview);
				mangaDao.updateManga(manga);

				modelAndView.addObject("borrowedManga", manga);
				modelAndView.addObject("reviewSuccess", "Thanks for adding Review and Ratings");
				modelAndView.setViewName("student-borrowed-manga");
			} else {
				modelAndView.addObject("havetoBorrow", "You have to borrow this manga to leave a review or rate");
				modelAndView.setViewName("review-manga");
			}
		} catch (IllegalArgumentException e) {
			modelAndView.addObject("mangaID", mangaID);
			modelAndView.addObject("havetobeStudent", "You have to login as student to submit review/ratings");
			modelAndView.setViewName("review-manga");
			System.out.println(e.getMessage());
		}

		catch (StudentException | MangaException e) {
			System.out.println(e.getMessage());
			modelAndView.addObject("reviewError", "Error submitting review");
			modelAndView.setViewName("review-manga");
		}

		return modelAndView;
	}

}
