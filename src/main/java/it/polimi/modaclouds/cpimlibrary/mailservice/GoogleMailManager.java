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

import it.polimi.modaclouds.cpimlibrary.CloudMetadata;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * This class allows to send a mail through the mail service on the Google App Engine
 * platform.
 */
class GoogleMailManager extends CloudMailManager {
	
	@SuppressWarnings("unused")
	private String server=null;
	@SuppressWarnings("unused")
	private String port=null;
	private String username=null;
	@SuppressWarnings("unused")
	private String psw=null;
	
	/**
	 * Instantiates a {@code GoogleMailManager} through the metadata specified in
	 * the <u>configuration.xml</u> file in the <i>mail</i> tag.
	 * 
	 * @param metadata
	 *            - object that contains all the information setting.
	 */
	public GoogleMailManager(CloudMetadata metadata)
	{
		this.port=metadata.getPortServerSmtp();
		this.server=metadata.getHostServerSmtp();
		this.username=metadata.getUsernameMail();
		this.psw=metadata.getPswMail();
		
	}

	public void sendMail(CloudMail msgToSend) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username,"The MiC Team"));
			message.addRecipient(Message.RecipientType.TO,
					new InternetAddress(msgToSend.getTo()));
			message.setSubject(msgToSend.getSubject());
			message.setText(msgToSend.getMsg());
			Transport.send(message);

		
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e2) {
			e2.printStackTrace();
		} catch (UnsupportedEncodingException e3) {
			e3.printStackTrace();
		}
	}

}
