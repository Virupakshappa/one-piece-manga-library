package com.onepiece.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String reviewText;
    private double rating;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Manga manga;

	public Review() {
		
	}

	

	public Review(String reviewText, double rating, Student student, Manga manga) {
		this.reviewText = reviewText;
		this.rating = rating;
		this.student = student;
		this.manga = manga;
	}



	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getReviewText() {
		return reviewText;
	}


	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}


	public double getRating() {
		return rating;
	}


	public void setRating(double rating) {
		this.rating = rating;
	}


	public Student getStudent() {
		return student;
	}


	public void setStudent(Student student) {
		this.student = student;
	}


	public Manga getManga() {
		return manga;
	}


	public void setManga(Manga manga) {
		this.manga = manga;
	}


	@Override
	public String toString() {
		return "Review [id=" + id + ", reviewText=" + reviewText + ", rating=" + rating + ", student=" + student
				+ ", manga=" + manga + "]";
	}

    
}

