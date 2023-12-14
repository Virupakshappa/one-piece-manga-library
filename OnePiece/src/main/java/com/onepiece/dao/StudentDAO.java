package com.onepiece.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import com.onepiece.exception.StudentException;
import com.onepiece.model.Manga;
import com.onepiece.model.Student;

@Component
public class StudentDAO extends DAO {

	public int addStudent(Student student) throws StudentException {

		try {

			begin();
			getSession().persist(student);
			commit();

		} catch (HibernateException e) {
			System.out.println(e);

			if (e.getMessage().contains("Duplicate entry"))

				return -1;

		} finally {
			close();
		}
		return 1;
	}

	public List<Manga> getBorrowedMangas(String email) throws StudentException {
		try {
			begin();
			String hql = "from Student where email = :email";
			Query<Student> query = getSession().createQuery(hql, Student.class);
			query.setParameter("email", email);
			Student student = (Student) query.uniqueResult();
			commit();
			return student != null ? student.getBorrowedMangas() : null;
		} catch (HibernateException e) {
			rollback();
			return null;
		} finally {
			close();
		}
	}

	public Student getStudentByEmail(String email) throws StudentException {
		try {
			begin();
			Student student = getSession().get(Student.class, email);
			commit();
			return student;
		} catch (HibernateException e) {
			rollback();
			throw new RuntimeException("Exception while accessing the database", e);
		} finally {
			close();
		}
	}

	public int updateStudent(Student student) throws StudentException {
		try {
			begin();
			getSession().merge(student);
			commit();
			return 1;
		} catch (HibernateException e) {
			rollback();
			return -1;
		} finally {
			close();
		}

	}

}
