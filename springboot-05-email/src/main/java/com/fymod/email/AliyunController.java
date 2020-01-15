package com.fymod.email;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AliyunController {

	@GetMapping("/aliyun")
	public void aliyun() throws IOException {
		final Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", "smtpdm.aliyun.com");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.port", "465");
		props.put("mail.user", "system@mail.fymod.com"); //阿里云后台配置的邮箱
		props.put("mail.password", "Kongjianma2019A"); //阿里云后台配置的密码
		Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				String userName = props.getProperty("mail.user");
				String password = props.getProperty("mail.password");
				return new PasswordAuthentication(userName, password);
			}
		};
		Session mailSession = Session.getInstance(props, authenticator);
		MimeMessage message = new MimeMessage(mailSession) {
		};
		try {
			// 第一个参数是阿里云后台配置的邮箱，第二个参数是显示的用户名
			InternetAddress from = new InternetAddress("system@mail.fymod.com", "系统发送邮件");
			message.setFrom(from);
			// 收件人
			InternetAddress to = new InternetAddress("me@zhaoguojian.com");
			message.setRecipient(MimeMessage.RecipientType.TO, to);
			message.setSubject("这是主题"); //主题
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("见附件");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			messageBodyPart = new MimeBodyPart();
			// 测试的文件
			File file = new File("测试文件2020.txt");
			if(!file.exists()) {
				file.createNewFile();
		        FileOutputStream fos = new FileOutputStream(file);
		        fos.write("测试用的文本2020".getBytes());
		        fos.close();
			}
			FileDataSource source = new FileDataSource(file);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(MimeUtility.encodeText(file.getName()));
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			Transport.send(message);
		} catch (MessagingException e) {
			String err = e.getMessage();
			System.out.println(err);
		}
	}
	
}
