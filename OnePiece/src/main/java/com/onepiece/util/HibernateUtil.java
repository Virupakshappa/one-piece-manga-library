package com.onepiece.util;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.springframework.stereotype.Component;

import com.onepiece.model.Admin;
import com.onepiece.model.Manga;
import com.onepiece.model.Notification;
import com.onepiece.model.Review;
import com.onepiece.model.Student;




@Component
public class HibernateUtil {
	
	  private static SessionFactory sessionFactory;
	    public static SessionFactory getSessionFactory() {
	        if (sessionFactory == null) {
	            try {
	                Configuration configuration = new Configuration();

	                Properties settings = new Properties();
	                settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
	                settings.put(Environment.URL, "jdbc:mysql://localhost:3306/one_piece?createDatabaseIfNotExist=true");
	                settings.put(Environment.USER, "root");
	                settings.put(Environment.PASS, "Shashi1998");
	                settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");

	                settings.put(Environment.SHOW_SQL, "true");

	                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

	                settings.put(Environment.HBM2DDL_AUTO, "update");

	                configuration.setProperties(settings);

	                configuration.addAnnotatedClass(Admin.class);
	                configuration.addAnnotatedClass(Student.class);
	                configuration.addAnnotatedClass(Manga.class);
	                configuration.addAnnotatedClass(Review.class);
	                configuration.addAnnotatedClass(Notification.class);


	                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
	                    .applySettings(configuration.getProperties()).build();

	                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        return sessionFactory;
	    }
}