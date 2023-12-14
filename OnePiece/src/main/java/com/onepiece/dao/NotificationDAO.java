package com.onepiece.dao;


import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.stereotype.Component;

import com.onepiece.exception.NotificationException;
import com.onepiece.model.Notification;

@Component
public class NotificationDAO extends DAO {

    public void addNotification(Notification notification) throws NotificationException{
        try {
            begin();
            getSession().persist(notification);
            commit();
        } catch (HibernateException e) {
            rollback();
            throw new RuntimeException("Exception while creating notification: " + e.getMessage());
        }finally {
			close();
		}
    }

    public List<Notification> getUnreadNotifications(String studentEmail) throws NotificationException{
        try {
            begin();
            List<Notification> notifications = getSession().createQuery("FROM Notification WHERE studentEmail = :studentEmail AND readStatus = false", Notification.class)
                    .setParameter("studentEmail", studentEmail)
                    .getResultList();
            commit();
            return notifications;
        } catch (HibernateException e) {
            rollback();
            throw new RuntimeException("Exception while fetching notifications: " + e.getMessage());
        }finally {
			close();
		}
    }

    public void markAsRead(Long notificationId) throws NotificationException{
        try {
            begin();
            Notification notification = getSession().get(Notification.class, notificationId);
            if (notification != null) {
                notification.setReadStatus(true);
                getSession().merge(notification);
            }
            commit();
        } catch (HibernateException e) {
            rollback();
            throw new RuntimeException("Could not mark notification as read", e);
        }finally {
        	close();
        }
    }
}
