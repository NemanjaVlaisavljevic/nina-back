package com.nemanjav.back.email;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailSender {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;

    @Override
    @Async
    public void send(String to, String email) {
        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage , "utf-8");
            helper.setText(email , true);
            helper.setTo(to);
            helper.setSubject("Account activation.");
            javaMailSender.send(mimeMessage);
        }catch(MessagingException e){
            LOGGER.error("Failed to send email" , e);
            throw new IllegalStateException("Failed to send email!");
        }
    }
    @Async
    @Override
    public void sendOrderDetails(String to, String email) {
        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage , "utf-8");
            helper.setText(email , true);
            helper.setTo(to);
            helper.setSubject("Order Details");
            javaMailSender.send(mimeMessage);
        }catch(MessagingException e){
            LOGGER.error("Failed to send email" , e);
            throw new IllegalStateException("Failed to send email!");
        }
    }

    @Override
    public void sendCanceledOrder(String to, String email) {
        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage , "utf-8");
            helper.setText(email , true);
            helper.setTo(to);
            helper.setSubject("Order Canceled");
            javaMailSender.send(mimeMessage);
        }catch(MessagingException e){
            LOGGER.error("Failed to send email" , e);
            throw new IllegalStateException("Failed to send email!");
        }
    }


}
