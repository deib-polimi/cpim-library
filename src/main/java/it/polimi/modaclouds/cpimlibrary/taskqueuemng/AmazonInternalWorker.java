package it.polimi.modaclouds.cpimlibrary.taskqueuemng;

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


import it.polimi.modaclouds.cpimlibrary.QueueInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;


public class AmazonInternalWorker extends Thread {
	private String queueName;
	private QueueInfo queueInfo=null;
	private AmazonTaskQueue queue = null;
	private Integer NUM_ATTEMPT_DELETE=10;
	private CloudTask info = null;
	private AmazonSQSClient sqs = null;
	public AmazonInternalWorker() {

	}

	public AmazonInternalWorker(String queueName,QueueInfo queueInfo) {
		this.queueName = queueName;
		this.queueInfo = queueInfo;
	}

	public void run() {
		//queue = (AmazonTaskQueue) MF.getFactory().getTaskQueueFactory().getQueue(queueName);
		
		AWSCredentials credentials = null;
		try {
			credentials = new PropertiesCredentials(
					getClass().getClassLoader().getResourceAsStream("AwsCredentials.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.sqs = new AmazonSQSClient(credentials);
		queue = new AmazonTaskQueue(queueName, sqs);
		
		AmazonTaskQueueMessage msg = null;
         
		while (true) {
			System.out.println("Iteration..");
			msg = queue.getMessage();
			if (msg != null) {
				System.out.println("Message found");
				final AmazonTaskQueueMessage aqmsg = msg;
				new Thread() {
					
					///////////////////////////////////////THREAD ////////////////////////////
					public void run() {
						System.out.println("Run");
						handleTask(aqmsg);
						System.out.println("Finish");
						//this.interrupt();
					}
					
					private void handleTask(AmazonTaskQueueMessage msg) {
						info = parserMessage(msg.getMessageText());
						String parameters = "";
						boolean firstIt = true;
						for (String key : info.getParameters().keySet()) {
							if (firstIt) {
								parameters = parameters + "?";
								firstIt = false;
							} else {
								parameters = parameters + "&";
							}
							parameters = parameters + key + "=" + info.getParameters().get(key);
						}
						
						System.out.println("Starting servlet.");
						//creo l'url
						URI host = URI.create("http://localhost:8080/");
						String url = "http://" + host.getHost() + ":" + host.getPort() + host.getPath() + info.getServletUri().getPath() + parameters;
						System.out.println(url);
						
						URL iurl = null;
						HttpURLConnection uc = null;
						//DataOutputStream  out = null;
						BufferedReader rd = null;
						
						try {
							iurl = new URL(url);
							uc = (HttpURLConnection)iurl.openConnection();
							uc.setDoOutput(true);
						    
							uc.setRequestMethod(info.getMethod().toUpperCase());
							//uc.addRequestProperty("Content-Length", file_length.toString());
							
							//out = new DataOutputStream(uc.getOutputStream());
							//out.write(file_content, 0, file_length);
							//out.flush();
							//out.close();

							//uc.setReadTimeout(file_length/50);
							//System.out.println("Set timeout: " + uc.getReadTimeout() + "ms.");
							InputStream is = uc.getInputStream();
							
							rd = new BufferedReader(new InputStreamReader(is));
							
						    String line;
						    while ((line = rd.readLine()) != null) {
						    	System.out.println("Response: "+ line);
						    }
						    rd.close();
						    System.out.println("Response code: "+ uc.getResponseCode());
						    if (uc.getResponseCode() == 500){
						    	;;
						    }
						    
						} catch (RuntimeException e) {
							// TODO Auto-generated catch block
							System.out.println("RuntimeException error!!");
							System.out.println("------");
							e.printStackTrace();
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							System.out.println("MalformedURLException error!!");
							System.out.println("------");
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("IOException error!!");
							System.out.println("------");
							e.printStackTrace();
						}
						System.out.println("End of servlet.");
						
						/*
							try {

							StringBuffer sb = new StringBuffer();
							sb.append(info.getMethod() + " " + info.getHostUri()
									+ info.getServletUri() + parameters + " HTTP/1.1\r\n");
							sb.append(HttpHeader.HOST + ": " + info.getHostUri().getHost()
									+ ":" + info.getHostUri().getPort() + "\r\n");
							sb.append(HttpHeader.CONNECTION + ": "
									+ HttpHeader.CONNECTION_CLOSE + "\r\n");
							sb.append("\r\n");
							String request = sb.toString();

							System.out
									.println("********************REQUEST !!!!!************************");
							System.out.println(request);
							System.out
									.println("********************END REQUEST !!!!!!!!************************");
							SocketFactory socketFactory = SocketFactory.getDefault();
							InetAddress ip = InetAddress.getByName(info.getHostUri().getHost());
							Socket socket = socketFactory.createSocket(ip, info.getHostUri()
									.getPort());
							OutputStreamWriter osw = new OutputStreamWriter(
									socket.getOutputStream());
							int requestLength = request.length();
							BufferedWriter bw = new BufferedWriter(osw, requestLength);
							bw.write(request);
							bw.flush();

							InputStreamReader isr = new InputStreamReader(
									socket.getInputStream());
							BufferedReader br = new BufferedReader(isr, 8192);
							@SuppressWarnings("unused")
							HttpResult result = buildResult(HttpMethod.POST, br);

							br.close();
							isr.close();
							socket.close();
							*/
						
							int i=0;
							Boolean deleted=queue.deleteMessage(msg);
							while(!deleted && i<NUM_ATTEMPT_DELETE)
							{
								
								    i++;
								    deleted=queue.deleteMessage(msg);

							}
							if(!deleted)
								try {
									throw new CloudTaskQueueException("Deletion of Message "+msg.getMessageId()+" failed!!");
								} catch (CloudTaskQueueException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							else
								System.out.println("Message "+msg.getMessageId()+"Deleted at attempt number "+i);
							

                        /*
						} catch (Exception e) {
							new Diagnostics("Exception during queue management:"
									+ e.getMessage()).save();
						}
						*/

					}
					
					private CloudTask parserMessage(String messageText) {
						CloudTask t = new CloudTask();
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder db;
						try {
							db = dbf.newDocumentBuilder();

							InputSource is = new InputSource();
							is.setCharacterStream(new StringReader(messageText));

							Document doc = db.parse(is);
							Element root = doc.getDocumentElement();

							//String uri = getTextValue(root, "URL");
							//t.setHostUri(uri);

							String uriS = getTextValue(root, "SERVLET");
							t.setServletUri(uriS);

							String method = getTextValue(root, "METHOD");
							t.setMethod(method);

							NodeList par = root.getElementsByTagName("PARAMETER");
							for (int i = 0; i < par.getLength(); i++) {
								Element elPar = (Element) par.item(i);

								String key = getTextValue(elPar, "KEY");

								String value = getTextValue(elPar, "VALUE");

								t.setParameters(key, value);
							}

						} catch (ParserConfigurationException e) {
							e.printStackTrace();
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						return t;
					}
					
					private String getTextValue(Element ele, String tagName) {
						String textVal = null;
						NodeList nl = ele.getElementsByTagName(tagName);
						if (nl != null && nl.getLength() > 0) {
							Element el = (Element) nl.item(0);
							textVal = el.getFirstChild().getNodeValue();
						}

						return textVal;
					}
					
					///////////////////////////// THREAD ///////////////////////////////////
				}.start();
			}
			System.out.println("Sleeping..");
			try {
				Thread.sleep(queueInfo.getRate());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	

}
