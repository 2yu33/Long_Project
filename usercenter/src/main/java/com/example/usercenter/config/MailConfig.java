package com.example.usercenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * QQ邮箱验证的一些配置
 */
@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // 配置邮件发送器的必要属性 建议写在配置文件里
        mailSender.setHost("smtp.qq.com");
        mailSender.setUsername("2532578435@qq.com");
        mailSender.setPassword("ixgmmampzvyhdibh");

        // 如果需要，可以进行其他配置

        return mailSender;
    }
}
