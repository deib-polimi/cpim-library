/**
 * Copyright 2013 deib-polimi
 * Contact: deib-polimi <marco.miglierina@polimi.it>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.polimi.modaclouds.cpimlibrary.mailservice;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.diagnostics.Diagnostics;

public class GlassfishMailManager extends CloudMailManager {

	private String server = null;
	private String port = null;
	private String username = null;
	private String psw = null;
	
	public GlassfishMailManager(CloudMetadata metadata) {
		this.port=metadata.getPortServerSmtp();
		this.server=metadata.getHostServerSmtp();
		this.username=metadata.getUsernameMail();
		this.psw=metadata.getPswMail();
	}

	@Override
	public void sendMail(CloudMail msgToSend) {
		Properties props = new Properties();
		props.put("mail.smtp.host", server);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {

					protected PasswordAuthentication getPasswordAuthentication() {

						return new PasswordAuthentication(username, psw);
					}
				});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username, "The MiC Team"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					msgToSend.getTo()));
			message.setSubject(msgToSend.getSubject());
			message.setText(msgToSend.getMsg());
			Transport.send(message);
			new Diagnostics("mail inviata...").save();

		} catch (AddressException e) {
			new Diagnostics(e.getMessage()).save();
		} catch (MessagingException e2) {
			new Diagnostics(e2.getMessage()).save();
		} catch (UnsupportedEncodingException e3) {
			new Diagnostics(e3.getMessage()).save();
		}

	}

}
