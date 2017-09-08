package com.airbnb.shared.dto.entity;
import java.util.Date;

public class Message{

	private Integer messageID;
	private Date dateCreated;
	private int isRead;
	private String messageText;
	private String subject;
	private Mailbox inbox;
	private Mailbox outbox;
	private User fromUser;

	private User toUser;

	public Message() {
	}

	public int getMessageID() {
		return this.messageID;
	}

	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}

	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public int getIsRead() {
		return this.isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public String getMessageText() {
		return this.messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}


	public User getFromUser() {
		return fromUser;
	}

	public User getToUser() {
		return toUser;
	}

	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}

	public void setToUser(User toUser) {
		this.toUser = toUser;
	}

	public Mailbox getInbox() {
		return inbox;
	}

	public void setInbox(Mailbox inbox) {
		this.inbox = inbox;
	}

	public Mailbox getOutbox() {
		return outbox;
	}

	public void setOutbox(Mailbox outbox) {
		this.outbox = outbox;
	}
}