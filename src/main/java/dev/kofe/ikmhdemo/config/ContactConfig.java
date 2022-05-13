package dev.kofe.ikmhdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;

/**
 * Contact Config: the configuration to email messages sending
 */


@Configuration
public class ContactConfig {

    @Bean
    public JavaMailSender getJavaMailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setProtocol(Constant.EMAIL_SENDER_PROTOCOL);

        mailSender.setHost(Constant.EMAIL_SENDER_HOST_SERVER);
        mailSender.setPort(Constant.EMAIL_SENDER_PORT);

        mailSender.setUsername(Constant.EMAIL_SENDER_USERNAME);
        mailSender.setPassword(Constant.EMAIL_SENDER_PASSWORD);

        Properties props = mailSender.getJavaMailProperties();

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

}
