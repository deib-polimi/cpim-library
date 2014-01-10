package it.polimi.modaclouds.cpimlibrary;

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


import it.polimi.modaclouds.cpimlibrary.exception.ParserConfigurationFileException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class parses the <u>configuration.xml</u>, the <u>persistence.xml</u>
 * and the <u>queue.xml</u> files that are contained in the META-INF folder.
 * 
 */
public class CloudMetadata {

	private static CloudMetadata instance = null;
	private String typeCloud = null;
	private String hostServerSmtp = null;
	private String portServerSmtp = null;
	private String usernameMail = null;
	private String pswMail = null;
	private String persistenceUnit = null;
	private String account = null;
	private String key = null;
	private String connection_string = null;
	//campo che identifica la connessione verso il secondo database che conterr� i file blob
	//utilizzato solo nel caso di glassfish
	private String blob_connection_string = null;
	private String memcacheAddr = null;
	private HashMap<String, QueueInfo> queueInfo = null;
	private HashMap<String, String> persistenceInfo = null;
	private String backend_name = null;

	public String getBackend_name() {
		return backend_name;
	}

	public void setBackend_name(String backend_name) {
		this.backend_name = backend_name;
	}

	/**
	 * Returns the value of the <i>account.name</i> property that is contained in the
	 * <u>persistence.xml</u> file.
	 * 
	 * @return a String representing the value of the <i>account.name</i> property in
	 *         the <u>persistence.xml</u> file if it exist, <b>null</b> otherwise.
	 * 
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * Returns the value of the <i>account.key</i> property that is contained in the
	 * <u>persistence.xml</u> file.
	 * 
	 * @return a String representing the value of the <i>account.key</i> property in
	 *         the <u>persistence.xml</u> file if it exists, <b>null</b> otherwise.
	 * 
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Returns the instance of the CloudMetadata object with the singleton
	 * technique. Calling this method for the first time, the <u>configuration.xml</u>, 
	 * the <u>persistence.xml</u> and the <u>queue.xml</u> files are parsed.
	 * 
	 * @return the instance of the CloudMetadata object.
	 * 
	 * @throws ParserConfigurationFileException
	 *             if in the <u>configuration.xml</u> or in the <u>persistence.xml</u>
	 *             occurs an error.
	 */
	public static CloudMetadata getCloudMetadata()
			throws ParserConfigurationFileException {
		if (instance == null)
			instance = new CloudMetadata();
		return instance;
	}

	/**
	 * Returns the name of the <i>persistence-unit</i> that is contained in the
	 * <u>persistence.xml</u> file.
	 * 
	 * @return a String representing the name of the <i>persistence-unit</i> in the
	 *         <u>persistence.xml</u> file if it exists, <b>null</b> otherwise.
	 * 
	 */
	public String getPersistenceUnit() {
		return persistenceUnit;
	}

	/**
	 * Returns the value of the <i>vendor</i> tag that is contained in the
	 * <u>configuration.xml</u> file.
	 * 
	 * @return a String representing the value of the <i>vendor</i> tag in the
	 *         <u>configuration.xml</u> file.
	 * 
	 */
	public String getTypeCloud() {
		return typeCloud;
	}

	/**
	 * Returns the value of the <i>host</i> attribute in the <i>server_smtp</i> tag that is
	 * contained in the <u>configuration.xml</u> file. This String represent the host
	 * address of the SMTP server used to the mail service.
	 * 
	 * @return a String representing the host address of the SMTP server used to
	 *         the mail service if it exists, <b>null</b> otherwise.
	 * 
	 * 
	 */
	public String getHostServerSmtp() {
		return hostServerSmtp;
	}

	/**
	 * Returns the value of the <i>port</i> attribute in the <i>server_smtp</i> tag that is
	 * contained in the <u>configuration.xml</u> file. This String represent the port
	 * of the SMTP server.
	 * 
	 * @return a String representing the port number of the SMTP server used to
	 *         the mail service
	 * 
	 * 
	 */
	public String getPortServerSmtp() {
		return portServerSmtp;
	}

	/**
	 * Returns the value of the <i>username</i> attribute in the <i>account_info</i> tag that
	 * is contained in the <u>configuration.xml</u> file. This String represent the
	 * mail address used to send the mail.
	 * 
	 * @return a String representing the mail address used to send the mail
	 * 
	 */
	public String getUsernameMail() {
		return usernameMail;
	}

	/**
	 * Returns an HashMap that contains all the information about the
	 * configuration of the <u>queue.xml</u> file used to configure the queues.
	 * 
	 * @return an HashMap that contains all the info of the queues setting by
	 *         the <u>queue.xml</u> file.
	 * 
	 * 
	 */
	public HashMap<String, QueueInfo> getQueueMedatada() {
		return queueInfo;
	}

	/**
	 * Returns the value of the <i>password</i> attribute in the <i>account_info</i> tag that
	 * is contained in the <u>configuration.xml</u> file.
	 * 
	 * @return a String representing the password related to the mail address
	 *         used to access to the SMTP server
	 * 
	 */
	public String getPswMail() {
		return pswMail;
	}
	
	//metodo che ritorna la stringa di connessione al db per i blob nel caso di glassfish
	public String getBlobConnectionString() {
		return this.blob_connection_string;
	}

	/**
	 * Returns the address of the Memcache server that is contained in the
	 * <i>memcache</i> tag in the <u>configuartion.xml</u> by the <i>address</i> and
	 * the <i>port</i> attribute of the <i>host</i> tag.
	 * 
	 * @return a String representing the host of the Memcache service
	 * 
	 * 
	 */
	public String getMemcacheAddr() {
		return memcacheAddr;
	}

	/**
	 * Returns all the value of the properties that are contained in the
	 * <i>persistence-unit</i> tag.
	 * 
	 * @return an HashMap that contains the value of the <i>property</i> tag in the
	 *         <i>persistence-unit</i> tag in the <u>persistence.xml</u> file.
	 * 
	 */
	public HashMap<String, String> getPersistenceInfo() {
		return persistenceInfo;
	}

	private CloudMetadata() throws ParserConfigurationFileException {
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		DocumentBuilder b;
		try {
			b = f.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			throw new ParserConfigurationFileException(e1.getMessage());
		}
		Document d = null;
		try {
			d = b.parse(findAssemblyConfig());
//			d.toString();
		} catch (SAXException e) {
			throw new ParserConfigurationFileException(e.getMessage());
		} catch (IOException e) {
			throw new ParserConfigurationFileException(e.getMessage());
		}
		Element root = d.getDocumentElement();
		if (!root.getNodeName().equals("configurations"))
			throw new ParserConfigurationFileException(
					"error in the root element!It Must be <configurations>");
		NodeList children = root.getChildNodes();
		if (children.getLength() == 0)
			throw new ParserConfigurationFileException(
					"We must almost choose the vendor!");

		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeName().equals("vendor")) {
				this.typeCloud = n.getTextContent();
			} else if (n.getNodeName().equals("services")) {
				NodeList servchildren = n.getChildNodes();
				for (int x = 0; x < servchildren.getLength(); x++) {
					Node n2 = servchildren.item(x);
					if (n2.getNodeName().equals("mail")) {
						NodeList mailchildren = n2.getChildNodes();
						for (int u = 0; u < mailchildren.getLength(); u++) {
							Node n3 = mailchildren.item(u);
							if (n3.getNodeName().equals("server_smtp")) {
								this.hostServerSmtp = n3.getAttributes()
										.getNamedItem("host").getNodeValue();
								this.portServerSmtp = n3.getAttributes()
										.getNamedItem("port").getNodeValue();

							} else if (n3.getNodeName().equals("account_info")) {
								if(n3.getAttributes().getNamedItem("username") != null){
									this.usernameMail = n3.getAttributes()
											.getNamedItem("username")
											.getNodeValue();
								}
								if(n3.getAttributes().getNamedItem("password") != null){
									this.pswMail = n3.getAttributes()
											.getNamedItem("password")
											.getNodeValue();
								}
							}

						}

					} else if (n2.getNodeName().equals("sql")) {
						NodeList sqlchildren = n2.getChildNodes();
						for (int w = 0; w < sqlchildren.getLength(); w++) {
							Node n3 = sqlchildren.item(w);

							if (n3.getNodeName().equals("connection")) {
								this.connection_string = n3.getAttributes()
										.getNamedItem("string").getNodeValue();
								// this.sqlpsw = n3.getAttributes()
								// .getNamedItem("password").getNodeValue();
								// this.sqlusername = n3.getAttributes()
								// .getNamedItem("username").getNodeValue();
								// this.database = n3.getAttributes()
								// .getNamedItem("database").getNodeValue();
							}
							
							//aggiunto per includere nel caso di glassfish il parsing della stringa di connessione al secondo database per i file blob
							else if (n3.getNodeName().equals("blobconnection")) {
								this.blob_connection_string = n3.getAttributes()
										.getNamedItem("string").getNodeValue();
							}

						}
					} else if (n2.getNodeName().equals("memcache")) {

						NodeList memchildren = n2.getChildNodes();
						for (int w = 0; w < memchildren.getLength(); w++) {
							Node n3 = memchildren.item(w);

							if (n3.getNodeName().equals("host")) {
								this.memcacheAddr = n3.getAttributes()
										.getNamedItem("address").getNodeValue()
										+ ":"
										+ n3.getAttributes()
												.getNamedItem("port")
												.getNodeValue();
							}

						}

					}
					else if (n2.getNodeName().equals("backend")) {
						this.backend_name = n2.getAttributes()
								.getNamedItem("name").getNodeValue();
					}
				}

			}
		}
		addQueueMetadata();
		addPersistenceMetadata();

	}

	private void addPersistenceMetadata()
			throws ParserConfigurationFileException {
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		DocumentBuilder b;
		persistenceInfo = new HashMap<String, String>();

		try {
			b = f.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new ParserConfigurationFileException(e.getMessage());
		}
		Document d;
		try {
			d = b.parse(findAssemblyPersistence());
		} catch (SAXException e) {
			throw new ParserConfigurationFileException(e.getMessage());
		} catch (IOException e) {
			throw new ParserConfigurationFileException(e.getMessage());
		}
		Element root = d.getDocumentElement();
		if (!root.getNodeName().equals("persistence")) {
			throw new ParserConfigurationFileException(
					"Error in the root element!!!IT MUST BE <persistence>");
		}
		NodeList children = root.getChildNodes();
		if (children.getLength() == 0) {
			throw new ParserConfigurationFileException(
					"THERE IS NO PERSISTENCE UNIT");
		}
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeName().equals("persistence-unit")) {
				this.persistenceUnit = n.getAttributes().getNamedItem("name")
						.getNodeValue();
				NodeList perstChildren = n.getChildNodes();
				for (int w = 0; w < perstChildren.getLength(); w++) {
					Node n2 = perstChildren.item(w);
					if (n2.getNodeName().equals("properties")) {
						NodeList propChild = n2.getChildNodes();
						for (int j = 0; j < propChild.getLength(); j++) {
							Node n3 = propChild.item(j);
							if (n3.getNodeName().equals("property")) {
								persistenceInfo.put(n3.getAttributes()
										.getNamedItem("name").getNodeValue(),
										n3.getAttributes()
												.getNamedItem("value")
												.getNodeValue());
							}
						}
					}
				}

			}
		}
		if (persistenceInfo.containsKey("account.name")) {
		
			this.account = persistenceInfo.get("account.name");

		}
		if (persistenceInfo.containsKey("account.key")) {
			this.key = persistenceInfo.get("account.key");

		}

	}

	private InputStream findAssemblyConfig() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return loader.getResourceAsStream("META-INF/configuration.xml");

	}

	private InputStream findAssemblyPersistence() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return loader.getResourceAsStream("META-INF/persistence.xml");
	}

	/**
	 * Returns the connection string that is contained in the <i>sql</i> tag in the
	 * <u>configuration.xml</u> file.
	 * 
	 * @return a String representing the connection string used to connecting to
	 *         the SQL database if it exists, <b>null</b> otherwise.
	 * 
	 */
	public String getConnectionString() {
		return this.connection_string;
	}

	private InputStream findAssemblyConfigQueue() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return loader.getResourceAsStream("META-INF/queue.xml");
	}

	private void addQueueMetadata() throws ParserConfigurationFileException {

		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();

		queueInfo = new HashMap<String, QueueInfo>();
		QueueInfo info = null;
		String name = null;
		try {
			DocumentBuilder b = f.newDocumentBuilder();
			Document d = b.parse(findAssemblyConfigQueue());

			Element root = d.getDocumentElement();
			NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node n = children.item(i);
				if (n.getNodeName().equals("queue")) {
					info = new QueueInfo();
					NodeList attribute = n.getChildNodes();
					for (int j = 0; j < attribute.getLength(); j++) {

						if (attribute.item(j).getNodeName().equals("name")) {
							name = attribute.item(j).getTextContent();

						} else if (attribute.item(j).getNodeName()
								.equals("rate")) {
							String rate = attribute.item(j).getTextContent();
							info.setRate(rate);
						} else if (attribute.item(j).getNodeName()
								.equals("mode")) {
							if (attribute.item(j).getTextContent()
									.toLowerCase().equals("push")) {
								info.setMode(ModeQueue.PUSH);
							} else if (attribute.item(j).getTextContent()
									.toLowerCase().equals("pull")) {
								info.setMode(ModeQueue.PULL);
							} else
								throw new ParserConfigurationFileException(
										"Error!! Mode in config queue is wrong (pull or push)");
						}
					}
					queueInfo.put(name, info);
				}
			}
		} catch (ParserConfigurationException e) {
			throw new ParserConfigurationFileException(e.getMessage());
		} catch (SAXException e) {
			throw new ParserConfigurationFileException(e.getMessage());
		} catch (IOException e) {
			throw new ParserConfigurationFileException(e.getMessage());
		}

	}

}