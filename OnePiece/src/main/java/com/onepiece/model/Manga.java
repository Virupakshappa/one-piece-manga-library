package com.onepiece.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

@Entity
public class Manga {

	@Id
	private int mangaID;
	private String animeName;
	private String writtenBy;
	private String category;
	private boolean available;

	@Transient
	private MultipartFile mangaCover;

	private String coverFileName;

	@ManyToOne
	private Student borrowedBy;

	@OneToMany(mappedBy = "manga", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Review> reviews = new ArrayList<>();

	private LocalDate borrowDate;

	@Transient
	private boolean isOverdue;

	public Manga(int mangaID, String animeName, String writtenBy, String category, boolean available,
			MultipartFile mangaCover, String coverFileName) {
		super();
		this.mangaID = mangaID;
		this.animeName = animeName;
		this.writtenBy = writtenBy;
		this.category = category;
		this.available = available;
		this.mangaCover = mangaCover;
		this.coverFileName = coverFileName;
	}

	public Manga(int mangaID, String animeName, String writtenBy, String category, boolean available,
			MultipartFile mangaCover, String coverFileName, Student borrowedBy, List<Review> reviews) {
		super();
		this.mangaID = mangaID;
		this.animeName = animeName;
		this.writtenBy = writtenBy;
		this.category = category;
		this.available = available;
		this.mangaCover = mangaCover;
		this.coverFileName = coverFileName;
		this.borrowedBy = borrowedBy;
		this.reviews = reviews;
	}

	public Manga() {
	}

	public boolean isOverdue() {
		return isOverdue;
	}

	public void setOverdue(boolean isOverdue) {
		this.isOverdue = isOverdue;
	}

	public LocalDate getBorrowDate() {
		return borrowDate;
	}

	public void setBorrowDate(LocalDate borrowDate) {
		this.borrowDate = borrowDate;
	}

	public int getMangaID() {
		return mangaID;
	}

	public void setMangaID(int mangaID) {
		this.mangaID = mangaID;
	}

	public String getAnimeName() {
		return animeName;
	}

	public void setAnimeName(String animeName) {
		this.animeName = animeName;
	}

	public String getWrittenBy() {
		return writtenBy;
	}

	public void setWrittenBy(String writtenBy) {
		this.writtenBy = writtenBy;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public MultipartFile getMangaCover() {
		return mangaCover;
	}

	public void setMangaCover(MultipartFile mangaCover) {
		this.mangaCover = mangaCover;
	}

	public String getCoverFileName() {
		return coverFileName;
	}

	public void setCoverFileName(String coverFileName) {
		this.coverFileName = coverFileName;
	}

	public Student getBorrowedBy() {
		return borrowedBy;
	}

	public void setBorrowedBy(Student borrowedBy) {
		this.borrowedBy = borrowedBy;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	@Override
	public String toString() {
		return "Manga [mangaID=" + mangaID + ", animeName=" + animeName + ", writtenBy=" + writtenBy + ", category="
				+ category + "]";
	}

}
