package com.onepiece.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Student {

	private String firstName;
	private String lastName;

	@Id
	private String email;
	private int age;
	private String collegeName;
	private String password;

	@OneToMany(mappedBy = "borrowedBy", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Manga> borrowedMangas = new ArrayList<>();

	public Student() {

	}

	public Student(String firstName, String lastName, String email, int age, String collegeName, String password) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.age = age;
		this.collegeName = collegeName;
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getCollegeName() {
		return collegeName;
	}

	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Manga> getBorrowedMangas() {
		return borrowedMangas;
	}

	public void setBorrowedMangas(List<Manga> borrowedMangas) {
		this.borrowedMangas = borrowedMangas;
	}

}
