package com.onepiece.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Admin {

	@Id
	private int adminID;
	
	private String adminEmailID;
	private String adminPassword;

	public Admin() {

	}

	public Admin(int adminID, String adminEmailID, String adminPassword) {
		super();
		this.adminEmailID = adminEmailID;
		this.adminPassword = adminPassword;
		this.adminID = adminID;

	}

	public int getAdminID() {
		return adminID;
	}

	public void setAdminID(int adminID) {
		this.adminID = adminID;
	}

	public String getAdminEmailID() {
		return adminEmailID;
	}

	public void setAdminEmailID(String adminEmailID) {
		this.adminEmailID = adminEmailID;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	

	@Override
	public String toString() {
		return "Admin [AdminID" + adminID +"adminEmailID=" + adminEmailID + ", adminPassword=" + adminPassword +"]";
	}

}
