package com.onepiece.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import com.onepiece.exception.MangaException;
import com.onepiece.model.Manga;
import com.onepiece.model.Student;

@Component
public class MangaDAO extends DAO {

	public List<Manga> searchedManga(String searchtext, String searchoption) throws MangaException {
		try {
			begin();
			String hql = "FROM Manga WHERE available = true ";
			switch (searchoption) {
			case "availableMangas":
				hql += "";
				break;
			case "category":
				hql += "AND category = :searchtext";
				break;
			case "mangaID":
				hql += "AND mangaID = :searchtext";
				break;
			case "writtenBy":
				hql += "AND writtenBy = :searchtext";
				break;
			case "animeName":
				hql += "AND animeName = :searchtext";
				break;
			default:
				throw new IllegalArgumentException("Invalid search option");
			}
			Query<Manga> query = getSession().createQuery(hql, Manga.class);
			if (!searchoption.equals("availableMangas")) {
				query.setParameter("searchtext", searchtext);
			}
			List<Manga> result = query.list();
			commit();
			return result;
		} catch (HibernateException e) {
			rollback();
			throw e;
		} finally {
			close();
		}
	}

	public int addManga(Manga manga) throws MangaException {

		try {

			begin();
			getSession().persist(manga);
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

	public int borrowManga(int mangaID, String studentEmail) throws MangaException {
		try {
			begin();
			Manga manga = getSession().get(Manga.class, mangaID);
			Student student = getSession().get(Student.class, studentEmail);

			if (manga != null && student != null) {
				manga.setAvailable(false);
				manga.setBorrowedBy(student);

				getSession().merge(manga);
				commit();
				return 1;
			} else {
				return -1;
			}
		} catch (HibernateException e) {
			rollback();
			return -1;
		} finally {
			close();
		}
	}

	public int deleteManga(int mangaID) throws MangaException {
		try {
			begin();
			Manga manga = getSession().get(Manga.class, mangaID);
			if (manga != null) {
				getSession().remove(manga);
				commit();
				return 1;
			} else {
				return 0;
			}
		} catch (HibernateException e) {
			rollback();
			throw new MangaException("Error deleting manga: " + e.getMessage(), e);
		} finally {
			close();
		}
	}

	public int returnManga(int mangaID) throws MangaException {
		try {
			begin();
			Manga manga = getSession().get(Manga.class, mangaID);

			if (manga != null) {
				manga.setAvailable(true);
				manga.setBorrowedBy(null);

				getSession().merge(manga);
				commit();
				System.out.println("=============Successsssss return================");
				return 1;

			} else {
				return -1;
			}
		} catch (HibernateException e) {
			rollback();
			return -1;
		} finally {
			close();
		}
	}

	public List<Manga> adminSearchedManga() throws MangaException {
		try {
			begin();
			String hql = "FROM Manga";
			Query<Manga> query = getSession().createQuery(hql, Manga.class);
			List<Manga> mangaList = query.list();
			commit();
			return mangaList;

		} catch (HibernateException e) {
			rollback();
			throw e;
		} finally {
			close();
		}
	}

	public Manga findMangaById(int mangaId) throws MangaException {
		try {
			begin();
			Manga manga = getSession().get(Manga.class, mangaId);
			commit();
			return manga;
		} catch (HibernateException e) {
			rollback();
			throw e;
		} finally {
			close();
		}
	}

	public void updateManga(Manga manga) throws MangaException {
		try {
			begin();
			getSession().merge(manga);
			commit();
		} catch (HibernateException e) {
			rollback();
		} finally {
			close();
		}
	}

}
