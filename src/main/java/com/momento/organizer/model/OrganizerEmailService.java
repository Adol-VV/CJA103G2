package com.momento.organizer.model;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class OrganizerEmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendResetPasswordEmail(String toEmail, String token, HttpServletRequest request) {
		SimpleMailMessage message = new SimpleMailMessage();

		// 設定發信人 (需與 username 一致)
		message.setFrom("cja103g2@gmail.com");

		// 設定收信人，專題展示固定寄到測試信箱
		message.setTo("cja103g2@gmail.com");

		// 設定主題
		message.setSubject("【密碼重設】請點擊連結重設您的密碼");

		// 動態抓取網址
		String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();
		String resetLink = baseUrl + "/organizer/reset-password?token=" + token;

		message.setText("您好：\n\n請點擊以下連結以重設密碼（期限為 30 分鐘）：\n" + resetLink + "\n\n如果您沒有申請此服務，請忽略本信件。");

		mailSender.send(message);
	}

	/**
	 * 發送審核結果通知信
	 * 
	 * @param toEmail       申請者的 Email
	 * @param organizerName 主辦單位名稱 (稱呼用)
	 * @param isApproved    true=通過, false=駁回
	 * @param reason        駁回原因 (通過時可傳 null)
	 * @param request       HttpServletRequest 用於動態抓取網址
	 */
	public void sendAuditResultEmail(String toEmail, String organizerName, boolean isApproved, String reason,
			HttpServletRequest request) {

		String subject;

		// 動態抓取網址
		String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();

		// 用 StringBuilder 拚 HTML
		StringBuilder content = new StringBuilder();

		content.append("<h2>親愛的").append(organizerName).append("您好：</h2>");

		if (isApproved) {
			// 通過審核
			subject = "【Momento】通知：恭喜您！您所提交的主辦方申請已通過";
			content.append(
					"<p style='font-size: 16px;'>我們很高興地通知您，您的主辦單位申請已經 <strong style='font-size: 18px;color: green;'>通過審核</strong>！</p>");
			content.append("<br/>");
			content.append("<br/>");
			content.append("<p>您現在可以登入後台，開始建立您的精彩活動以及上架商品。</p>");
			content.append("<br/>");
			content.append("<a href='").append(baseUrl).append(
					"/organizer/login' style='background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>前往主辦方後台</a>");
		} else {
			// 申請駁回
			subject = "【Momento】通知：您的主辦單位申請未通過";
			content.append(
					"<p style='font-size: 16px;'>很抱歉通知您，您提交的主辦單位申請 <strong style='color: red;'>未能通過審核</strong>。</p>");
			content.append(
					"<div style='background-color: #fff3cd; border: 1px solid #ffeeba; padding: 10px; margin: 10px 0; border-radius: 5px;'>");
			content.append("<strong>駁回原因：</strong>").append(reason); // 這裡填入原因
			content.append("</div>");
			content.append("<p>請修正相關資訊後，重新提交申請，謝謝。</p>");
			content.append("<br/>");
			content.append("<a href='").append(baseUrl).append(
					"/organizer/apply' style='background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>重新申請主辦方</a>");
		}
		content.append("<br/><br/><hr/><p style='color: gray; font-size: 12px;'>此為系統自動發送，請勿直接回覆。</p>");
		// 呼叫底層工具寄出
		sendHtmlEmail(toEmail, subject, content.toString());
	}

	private void sendHtmlEmail(String toEmail, String subject, String content) {
		try {

			// 1. 建立一封自建信件
			MimeMessage message = mailSender.createMimeMessage();

			// 2. 用小幫手來設定內容 (true 表示支援 Multipart，也就是附件或 HTML)
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			// 寄件人
			helper.setFrom("cja103g2@gmail.com");

			// 設定收信人，專題展示固定寄到測試信箱
			helper.setTo("cja103g2@gmail.com");

			helper.setSubject(subject);

			helper.setText(content, true);

			mailSender.send(message);
		} catch (Exception e) {
			System.out.println("寄送HTML mail失敗：" + e.getMessage());
			e.getMessage();
		}
	}
}
