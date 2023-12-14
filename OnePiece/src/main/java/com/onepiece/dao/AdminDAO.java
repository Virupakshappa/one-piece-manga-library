package com.onepiece.dao;

import org.hibernate.HibernateException;
import org.springframework.stereotype.Component;

import com.onepiece.exception.AdminException;
import com.onepiece.model.Admin;

@Component
public class AdminDAO extends DAO {

	public int addAdmin(Admin admin) throws AdminException {

		try {

			begin();
			getSession().persist(admin);
			commit();

		} catch (HibernateException e) {
			System.out.println(e);

			if (e.getMessage().contains("Duplicate entry"))

				return -1;

		}finally {
			close();
		}
		return 1;
	}


	public Admin getAdminDetails(int adminID) throws AdminException{
		try {
			begin();
			Admin admin = getSession().get(Admin.class, adminID);

			if (admin != null) {
				commit();
				return admin;
			} else {
				return null;
			}
		} catch (HibernateException e) {
			rollback();
			return null;
		} finally {
			close();
		}
		
	}

	public int updateAdmin(Admin admin) throws AdminException{
		try {
			begin();
			getSession().merge(admin);
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
