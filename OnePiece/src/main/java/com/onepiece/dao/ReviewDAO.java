package com.onepiece.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import com.onepiece.exception.ReviewException;
import com.onepiece.model.Review;


@Component
public class ReviewDAO extends DAO {

	public List<Review> getReviewsForManga(int mangaID) throws ReviewException {
		try {
			begin();

			String hql = "FROM Review r WHERE r.manga.mangaID = :mangaID";
			Query<Review> query = getSession().createQuery(hql, Review.class);
			query.setParameter("mangaID", mangaID);

			List<Review> reviewList = query.list();
			commit();
			return reviewList;

		} catch (HibernateException e) {
			rollback();
			throw e;
		} finally {
			close();
		}
	}

}
