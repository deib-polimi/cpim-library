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

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpleemail.AWSJavaMailTransport;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.ListVerifiedEmailAddressesResult;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;

public class AmazonMailManager extends CloudMailManager {

	private String username=null;
	private AmazonSimpleEmailService ses = null;
	private AWSCredentials credentials;
	
	public AmazonMailManager(CloudMetadata metadata)
	{
		this.username=metadata.getUsernameMail();
		this.credentials = null;
		try {
			credentials = new PropertiesCredentials(getClass().getClassLoader().getResourceAsStream("AwsCredentials.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.ses = new AmazonSimpleEmailServiceClient(this.credentials);
		verifyEmailAddress(this.username);
	}
	@Override
	public  void sendMail(CloudMail msgToSend) {
		 
		 if(!verifyEmailAddress(this.username) || !verifyEmailAddress(msgToSend.getTo())) 
			 return;
		
		 Properties props = new Properties();
		 props.setProperty("mail.transport.protocol", "aws");
		 props.setProperty("mail.aws.user", credentials.getAWSAccessKeyId());
		 props.setProperty("mail.aws.password", credentials.getAWSSecretKey());
		
		 Session session = Session.getInstance(props);
		 
		 try {
	            // Create a new Message
	            Message msg = new MimeMessage(session);
	            msg.setFrom(new InternetAddress(this.username));
	            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(msgToSend.getTo()));
	            msg.setSubject(msgToSend.getSubject());
	            msg.setText(msgToSend.getMsg());
	            msg.saveChanges();

	            // Reuse one Transport object for sending all your messages
	            // for better performance
	            Transport t = new AWSJavaMailTransport(session, null);
	            t.connect();
	            t.sendMessage(msg, null);

	            // Close your transport when you're completely done sending
	            // all your messages
	            t.close();
	        } catch (AddressException e) {
	            e.printStackTrace();
	            System.out.println("Caught an AddressException, which means one or more of your "
	                    + "addresses are improperly formatted.");
	        } catch (MessagingException e) {
	            e.printStackTrace();
	            System.out.println("Caught a MessagingException, which means that there was a "
	                    + "problem sending your message to Amazon's E-mail Service check the "
	                    + "stack trace for more information.");
	        }
		 
	}
	
	public boolean verifyEmailAddress(String address) {
        ListVerifiedEmailAddressesResult verifiedEmails = ses.listVerifiedEmailAddresses();
        boolean ret;
        if (verifiedEmails.getVerifiedEmailAddresses().contains(address)) ret = true;
        else {
        	ses.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress(address));
        	System.out.println("Please check the email address " + address + " to verify it");
        	ret = false;
        }
        return ret;
    }

}
