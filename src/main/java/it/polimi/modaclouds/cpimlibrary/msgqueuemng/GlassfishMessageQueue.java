package it.polimi.modaclouds.cpimlibrary.msgqueuemng;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class GlassfishMessageQueue implements CloudMessageQueue {

	private TemporaryQueue destination=null;
	
	public GlassfishMessageQueue(String queueName) {
		
		try {
		
		Context jndiContext = new InitialContext();
		QueueConnectionFactory connectionFactory = (QueueConnectionFactory) jndiContext.lookup("GlassfishMessageQueueConnectionFactory");
		QueueConnection connection = (QueueConnection) connectionFactory.createConnection();
		connection.start();
		QueueSession session = (QueueSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		TemporaryQueue temporaryQueue = session.createTemporaryQueue();       
		jndiContext.bind(queueName, temporaryQueue);    
		this.destination = temporaryQueue;
		connection.close(); 
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add(String msg) throws CloudMessageQueueException {
		try {
			
			Context jndiContext = new InitialContext();
			QueueConnectionFactory connectionFactory = (QueueConnectionFactory) jndiContext.lookup("GlassfishMessageQueueConnectionFactory");
			QueueConnection connection = (QueueConnection) connectionFactory.createConnection();
			connection.start();
			QueueSession session = (QueueSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			TextMessage txtMsg;
			txtMsg = session.createTextMessage();
			txtMsg.setText(msg);
			session.createSender(this.destination).send(txtMsg);
			connection.close(); 


			} catch (NamingException e) {
				e.printStackTrace();
			} catch (JMSException e) {
				e.printStackTrace();
			}
	}

	@Override
	public String getQueueName() {
		try {
			return this.destination.getQueueName();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void purge() {
		try {
			
			Context jndiContext = new InitialContext();
			QueueConnectionFactory connectionFactory = (QueueConnectionFactory) jndiContext.lookup("GlassfishMessageQueueConnectionFactory");
			QueueConnection connection = (QueueConnection) connectionFactory.createConnection();
			connection.start();
			QueueSession session = (QueueSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			QueueBrowser browser = session.createBrowser(this.destination);

			Enumeration<?> enum1 = browser.getEnumeration();
		    MessageConsumer consumer = session.createConsumer(this.destination);
		    

			while(enum1.hasMoreElements())
			{
			      consumer.receive();
			   
			}
			
			connection.close(); 

			} catch (NamingException e) {
				e.printStackTrace();
			} catch (JMSException e) {
				e.printStackTrace();
			}
	}

	@Override
	public CloudMessage getMessage() {
				
		try {
					
			Context jndiContext = new InitialContext();
			QueueConnectionFactory connectionFactory = (QueueConnectionFactory) jndiContext.lookup("GlassfishMessageQueueConnectionFactory");
			QueueConnection connection = (QueueConnection) connectionFactory.createConnection();
			connection.start();
			QueueSession session = (QueueSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			QueueReceiver queueReceiver = session.createReceiver(this.destination); 
			TextMessage txtMsg=(TextMessage) queueReceiver.receive(); //controllare cosa riceve (dovrebbe essere il prossimo in coda)
			CloudMessage toReturn=new CloudMessage(txtMsg.getText());
			
			connection.close(); 
			
			return toReturn;
			} catch (NamingException e) {
				e.printStackTrace();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		return null;
	}

	@Override
	public void deleteMessage(CloudMessage msg)
			throws CloudMessageQueueException {
		try {
			
			Context jndiContext = new InitialContext();
			QueueConnectionFactory connectionFactory = (QueueConnectionFactory) jndiContext.lookup("GlassfishMessageQueueConnectionFactory");
			QueueConnection connection = (QueueConnection) connectionFactory.createConnection();
			connection.start();
			QueueSession session = (QueueSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			QueueBrowser browser = session.createBrowser(this.destination);

			Enumeration<?> enum1 = browser.getEnumeration();
			
			while(enum1.hasMoreElements())
			{
			   TextMessage mex = (TextMessage)enum1.nextElement();
			   if(mex.getText().equals(msg.getMsg()))
			   {
			       MessageConsumer consumer = session.createConsumer(this.destination, "id='" +   mex.getStringProperty("id") + "'");
			      consumer.receive(1000);
			   }
			}
			
			connection.close(); 

			} catch (NamingException e) {
				e.printStackTrace();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		
	}

	@Override
	public void deleteMessage(String msgId) throws CloudMessageQueueException {
		try {
			
			Context jndiContext = new InitialContext();
			QueueConnectionFactory connectionFactory = (QueueConnectionFactory) jndiContext.lookup("GlassfishMessageQueueConnectionFactory");
			QueueConnection connection = (QueueConnection) connectionFactory.createConnection();
			connection.start();
			QueueSession session = (QueueSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			QueueBrowser browser = session.createBrowser(this.destination);

			Enumeration<?> enum1 = browser.getEnumeration();
			
			while(enum1.hasMoreElements())
			{
			   TextMessage mex = (TextMessage)enum1.nextElement();
			   if(mex.getStringProperty("id").equals(msgId))
			   {
			       MessageConsumer consumer = session.createConsumer(this.destination, "id='" +   mex.getStringProperty("id") + "'");
			      consumer.receive(1000);
			   }
			}
			
			connection.close(); 

			} catch (NamingException e) {
				e.printStackTrace();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		
	}

}
