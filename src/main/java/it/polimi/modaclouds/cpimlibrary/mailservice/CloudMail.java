package it.polimi.modaclouds.cpimlibrary.mailservice;

/*
 * *****************************
 * cpim-library
 * *****************************
 * Copyright (C) 2013 deib-polimi
 * *****************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************
 */


/**
 * This class represents the mail.
 * 
 */
public class CloudMail {
	private String to = null;
	private String msg = null;
	private String subject = null;

	/**
	 * Instantiates a CloudMail, assigning the recipient, the subject and the
	 * text body of a mail.
	 * 
	 * @param to
	 *            - the recipient address
	 * @param subject
	 *            - mail subject
	 * @param msg
	 *            - message text
	 */
	public CloudMail(String to, String subject, String msg) {
		this.to = to;
		this.subject = subject;
		this.msg = msg;
	}

	/**
	 * @return the recipient address
	 */
	public String getTo() {
		return to;
	}

	/**
	 * Assigns the recipient address to the {@code CloudMail} object.
	 * 
	 * @param to
	 *            - recipient address
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @return the message text
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * Assigns the message text 
	 * @param msg
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * 
	 * @return the mail subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Assigns the mail subject
	 * @param subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

}
