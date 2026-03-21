package com.mc.common.utils.email;

import com.mc.common.utils.spring.SpringUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * 邮件发送工具类
 * <p>
 * 封装 JavaMailSender，提供静态方法发送纯文本邮件。
 * 依赖 SpringUtils 获取 JavaMailSender Bean，适用于任何模块。
 * <p>
 * 使用前请确保 application.yml 中已正确配置 spring.mail.* 属性。
 *
 * @author caidu
 */
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    /**
     * 发送纯文本邮件
     *
     * @param to      收件人邮箱地址
     * @param subject 邮件主题
     * @param content 邮件正文（纯文本）
     * @return 发送成功返回 true，失败返回 false
     */
    public static boolean send(String to, String subject, String content) {
        try {
            JavaMailSender mailSender = SpringUtils.getBean(JavaMailSender.class);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(SpringUtils.getRequiredProperty("spring.mail.username"));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, false);
            mailSender.send(message);
            log.info("邮件发送成功 - to: {}, subject: {}", to, subject);
            return true;
        } catch (MessagingException e) {
            log.error("邮件发送失败 - to: {}, subject: {}", to, subject, e);
            return false;
        } catch (Exception e) {
            log.error("邮件发送异常 - to: {}, subject: {}", to, subject, e);
            return false;
        }
    }
}
