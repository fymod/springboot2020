package com.fymod.email;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sun.mail.imap.IMAPStore;

@RestController
public class ReceiveController {
	
	@Value("${spring.mail.host}")
    private String host;
	@Value("${spring.mail.username}")
    private String from;
	@Value("${spring.mail.password}")
	private String password;
	
	
	/**
	 * 接收邮件，直接打印到控制台
	 */
	@GetMapping("/receive")
	public void resceive() throws Exception {
		Properties props = System.getProperties();
		props.setProperty("mail.imap.host", host);
		props.setProperty("mail.imap.port", "143");
		props.setProperty("mail.store.protocol", "imap");
		Session session = Session.getInstance(props);
		IMAPStore store = (IMAPStore) session.getStore("imap");
		store.connect(from, password);
		/**
		 * 获得收件箱INBOX 
		 * 除了收件箱，其他的可以使用以下方法查看到 
		 * Folder defaultFolder = store.getDefaultFolder();
		 * Folder[] allFolder = defaultFolder.list();
		 * for(int i = 0; i < allFolder.length; i++) {
		 * System.out.println(allFolder[i].getName()); 
		 * } 
		 * 比如垃圾箱：Junk 已删除：Deleted Messages 草稿：Drafts 已发送：Sent Messages
		 */
		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_WRITE); //Folder.READ_ONLY：只读权限 Folder.READ_WRITE：可读可写（可以修改邮件的状态）
		System.out.println("未读邮件数: " + folder.getUnreadMessageCount());
		System.out.println("邮件总数: " + folder.getMessageCount());
		// 得到收件箱中的所有邮件,并解析
		Message[] messages = folder.getMessages();
		parseMessage(messages);
		
		// 删除第一封邮件
		if(folder.getMessageCount() > 0) {
	        deleteMessage(messages[0]);
		}
		// 释放资源
		folder.close(true);
		store.close();
	}

	/**
	 * 解析邮件
	 */
	private void parseMessage(Message... messages) throws MessagingException, IOException {
		if (messages == null || messages.length < 1)
			throw new MessagingException("未找到要解析的邮件!");
		// 解析所有邮件
		int count = messages.length;
		for (int i = count - 1; i >= 0; i--) {
			MimeMessage msg = (MimeMessage) messages[i];
			System.out.println("------------------解析第" + msg.getMessageNumber() + "封邮件-------------------- ");
			// 如果有编码，可以先解码： MimeUtility.decodeText(msg.getSubject());
			System.out.println("主题: " + msg.getSubject());
			System.out.println("发件人名称: " + getFrom(msg)[0]);
			System.out.println("发件人邮箱: " + getFrom(msg)[1]);
			System.out.println("收件人：" + getReceiveAddress(msg, null));
			System.out.println("发送时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.getSentDate()));
			System.out.println("是否已读：" + msg.getFlags().contains(Flags.Flag.SEEN));
			System.out.println("邮件优先级：" + getPriority(msg));
			System.out.println("是否需要回执：" + (msg.getHeader("Disposition-Notification-To") != null ? true : false));
			System.out.println("邮件大小：" + msg.getSize() * 1024 + "kb");
			StringBuffer content = new StringBuffer(100);
			getMailTextContent(msg, content);
			System.out.println("邮件正文：" + content);
			boolean isContainerAttachment = isContainAttachment(msg);
			System.out.println("是否包含附件：" + isContainerAttachment);
			if (isContainerAttachment) {
				saveAttachment(msg, "/Users/luotaboshi/Desktop/"); // 保存附件的本地路径
			}
			System.out.println("------------------第" + msg.getMessageNumber() + "封邮件解析结束-------------------- ");
			System.out.println();

		}
	}

	/**
	 * 删除邮件
	 */
	private void deleteMessage(Message... messages) throws MessagingException, IOException {
		if (messages == null || messages.length < 1)
			throw new MessagingException("未找到要删除的邮件!");
		for (int i = 0, count = messages.length; i < count; i++) {
			Message message = messages[i];
			String subject = message.getSubject();
			message.setFlag(Flags.Flag.DELETED, true);
			System.out.println("删除邮件: " + subject);
		}
	}

	/**
	 * 获得邮件发件人
	 */
	private String[] getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
		String[] from = new String[2];
		Address[] froms = msg.getFrom();
		if (froms.length < 1)
			throw new MessagingException("没有发件人!");
		InternetAddress address = (InternetAddress) froms[0];
		from[0] = address.getPersonal(); // 需要解码的话：MimeUtility.decodeText(address.getPersonal())
		from[1] = address.getAddress();
		return from;
	}

	/**
	 * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人 
	 * Message.RecipientType.TO 收件人
	 * Message.RecipientType.CC 抄送 
	 * Message.RecipientType.BCC 密送
	 */
	public String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {
		StringBuffer receiveAddress = new StringBuffer();
		Address[] addresss = null;
		if (type == null) {
			addresss = msg.getAllRecipients();
		} else {
			addresss = msg.getRecipients(type);
		}
		if (addresss == null || addresss.length < 1)
			throw new MessagingException("没有收件人!");
		for (Address address : addresss) {
			InternetAddress internetAddress = (InternetAddress) address;
			receiveAddress.append(internetAddress.toUnicodeString()).append(",");
		}
		receiveAddress.deleteCharAt(receiveAddress.length() - 1);
		return receiveAddress.toString();
	}

	/**
	 * 判断邮件中是否包含附件
	 */
	private boolean isContainAttachment(Part part) throws MessagingException, IOException {
		boolean flag = false;
		if (part.isMimeType("multipart/*")) {
			MimeMultipart multipart = (MimeMultipart) part.getContent();
			int partCount = multipart.getCount();
			for (int i = 0; i < partCount; i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				String disp = bodyPart.getDisposition();
				if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
					flag = true;
				} else if (bodyPart.isMimeType("multipart/*")) {
					flag = isContainAttachment(bodyPart);
				} else {
					String contentType = bodyPart.getContentType();
					if (contentType.indexOf("application") != -1) {
						flag = true;
					}
					if (contentType.indexOf("name") != -1) {
						flag = true;
					}
				}
				if (flag)
					break;
			}
		} else if (part.isMimeType("message/rfc822")) {
			flag = isContainAttachment((Part) part.getContent());
		}
		return flag;
	}

	/**
	 * 获得邮件的优先级
	 * @return 1(High):紧急 3:普通(Normal) 5:低(Low)
	 */
	private String getPriority(MimeMessage msg) throws MessagingException {
		String priority = "普通";
		String[] headers = msg.getHeader("X-Priority");
		if (headers != null) {
			String headerPriority = headers[0];
			if (headerPriority.indexOf("1") != -1 || headerPriority.indexOf("High") != -1)
				priority = "紧急";
			else if (headerPriority.indexOf("5") != -1 || headerPriority.indexOf("Low") != -1)
				priority = "低";
			else
				priority = "普通";
		}
		return priority;
	}

	private void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
		System.out.println(part.getContentType());
		boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
		if (part.isMimeType("text/*") && !isContainTextAttach) {
			content.append(part.getContent().toString());
		} else if (part.isMimeType("message/rfc822")) {
			getMailTextContent((Part) part.getContent(), content);
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int partCount = multipart.getCount();
			for (int i = 0; i < partCount; i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				getMailTextContent(bodyPart, content);
			}
		}
	}

	/**
	 * 保存附件
	 */
	private void saveAttachment(Part part, String destDir)
			throws UnsupportedEncodingException, MessagingException, FileNotFoundException, IOException {
		if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int partCount = multipart.getCount();
			for (int i = 0; i < partCount; i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				String disp = bodyPart.getDisposition();
				if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
					InputStream is = bodyPart.getInputStream();
					saveFile(is, destDir, decodeText(bodyPart.getFileName()));
				} else if (bodyPart.isMimeType("multipart/*")) {
					saveAttachment(bodyPart, destDir);
				} else {
					String contentType = bodyPart.getContentType();
					if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
						saveFile(bodyPart.getInputStream(), destDir, decodeText(bodyPart.getFileName()));
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			saveAttachment((Part) part.getContent(), destDir);
		}
	}

	/**
	 * 读取输入流中的数据保存至指定目录
	 */
	private void saveFile(InputStream is, String destDir, String fileName)
			throws FileNotFoundException, IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destDir + fileName)));
		int len = -1;
		while ((len = bis.read()) != -1) {
			bos.write(len);
			bos.flush();
		}
		bos.close();
		bis.close();
	}

	/**
	 * 文本解码
	 */
	private String decodeText(String encodeText) throws UnsupportedEncodingException {
		if (encodeText == null || "".equals(encodeText)) {
			return "";
		} else {
			return MimeUtility.decodeText(encodeText);
		}
	}

}
