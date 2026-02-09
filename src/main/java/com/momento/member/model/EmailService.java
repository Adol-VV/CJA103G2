package com.momento.member.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendResetPasswordEmail(String toEmail, String token, HttpServletRequest request) {
		SimpleMailMessage message = new SimpleMailMessage();

		// 設定發信人 (需與 username 一致)
		message.setFrom("cja103g2@gmail.com");
		// 設定收信人，這邊寫死方便測試
		message.setTo("cja103g2@gmail.com");
		// 設定主題
		message.setSubject("【密碼重設】請點擊連結重設您的密碼");

		// 設定內容
		String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		String resetLink = baseUrl +"/member/reset-password?token=" + token;
		message.setText("您好：\n\n請點擊以下連結以重設密碼（期限為 30 分鐘）：\n" + resetLink + "\n\n如果您沒有申請此服務，請忽略本信件。");

		mailSender.send(message);
	}
}
