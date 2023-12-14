package com.onepiece.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private String studentEmail;
    private boolean readStatus = false;
    
    public Notification() {
    	
    }
    
	public Notification(Long id, String message, String studentEmail, boolean readStatus) {
		super();
		this.id = id;
		this.message = message;
		this.studentEmail = studentEmail;
		this.readStatus = readStatus;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStudentEmail() {
		return studentEmail;
	}
	public void setStudentEmail(String studentEmail) {
		this.studentEmail = studentEmail;
	}
	public boolean isReadStatus() {
		return readStatus;
	}
	public void setReadStatus(boolean readStatus) {
		this.readStatus = readStatus;
	}

	@Override
	public String toString() {
		return "Notification [id=" + id + ", message=" + message + ", studentEmail=" + studentEmail + ", readStatus="
				+ readStatus + "]";
	}

    
    
}
