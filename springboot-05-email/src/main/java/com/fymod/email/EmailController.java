package com.fymod.email;

import java.io.File;
import java.io.FileOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

	@Autowired private JavaMailSender mailSender; 
	
	@Value("${spring.mail.username}")
    private String from;
	
	/**
	 * 发送文本邮件
	 */
	@GetMapping("/send1")
    public void send() {
		SimpleMailMessage message = new SimpleMailMessage();
	    message.setFrom(from);
	    message.setTo("me@zhaoguojian.com");
	    message.setSubject("主题");
	    message.setText("邮件内容");
	    mailSender.send(message);
	}
	
	/**
	 * 带附件的邮件
	 */
	@GetMapping("/send2")
	public void sendMail() throws Exception {
		MimeMessageHelper messageHelper = new MimeMessageHelper(mailSender.createMimeMessage(), true);// true表示支持复杂类型
		messageHelper.setFrom(from);// 邮件发信人
		messageHelper.setTo("me@zhaoguojian.com");// 邮件收信人
		messageHelper.setSubject("主题");// 邮件主题
		/**
		 * 正文部分，没有第二个参数或者第二个参数为false表示正文是普通文本
		 * 第二个参数是true表示正文部分是html
		 */
		String content = "图片测试 <img src='https://tvax2.sinaimg.cn/crop.0.0.512.512.50/abae8d07ly8fntztapslwj20e80e8dg6.jpg?KID=imgbed,tva&Expires=1578900531&ssig=gU7nWK0nsi' />";
		messageHelper.setText(content, true);
		File file = new File("测试文件.txt");
		if(!file.exists()) {
			file.createNewFile();
	        FileOutputStream fos = new FileOutputStream(file);
	        fos.write("测试用的文本".getBytes());
	        fos.close();
		}
		messageHelper.addAttachment(file.getName(), file);
		mailSender.send(messageHelper.getMimeMessage());// 正式发送邮件
	}
	
	
}
