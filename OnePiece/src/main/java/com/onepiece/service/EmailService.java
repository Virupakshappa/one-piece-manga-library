package com.onepiece.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    private TemplateEngine templateEngine;

    private HtmlEmail setupEmail() throws EmailException {
        HtmlEmail email = new HtmlEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator("vjanadri7@gmail.com", "ygmquffpvwdvfzyw"));
        email.setSSLOnConnect(true);
        email.setFrom("vjanadri7@gmail.com");
        return email;
    }

    public void sendEmailStudent(String studentEmail, String studentName) throws EmailException {
        HtmlEmail email = setupEmail();
        email.setSubject("Welcome to One Piece Manga Library!");

        Context context = new Context();
        context.setVariable("name", studentName);
        String processedHtml = templateEngine.process("emailTemplate", context);

        email.setHtmlMsg(processedHtml);
        email.addTo(studentEmail);
        email.send();
    }

    public void sendEmailAdmin(String adminEmailID, int adminID) throws EmailException {
        HtmlEmail email = setupEmail();
        email.setSubject("Welcome to One Piece Manga Library!");

        Context context = new Context();
        context.setVariable("adminID", adminID);
        String processedHtml = templateEngine.process("adminEmailTemplate", context);

        email.setHtmlMsg(processedHtml);
        email.addTo(adminEmailID);
        email.send();
    }
}
