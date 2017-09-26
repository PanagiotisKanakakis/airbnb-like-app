package com.airbnb.shared.dto.entity;

import java.util.ArrayList;
import java.util.List;


public class Mailbox {

	private Integer mailboxId;

	private User user;

	private List<Message> inbox = new ArrayList<>();

	private List<Message> outbox = new ArrayList<>();

	public Mailbox() {
	}

	public Integer getMailboxId() {
		return mailboxId;
	}

	public void setMailboxId(Integer mailboxId) {
		this.mailboxId = mailboxId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Message> getInbox() {
		return inbox;
	}

	public void setInbox(List<Message> inbox) {
		this.inbox = inbox;
	}

	public List<Message> getOutbox() {
		return outbox;
	}

	public void setOutbox(List<Message> outbox) {
		this.outbox = outbox;
	}


}