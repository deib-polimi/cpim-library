package it.polimi.modaclouds.cpimlibrary.taskqueuemng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;



import it.polimi.modaclouds.cpimlibrary.QueueInfo;

public class GlassfishTaskHandler extends Thread {

	private GlassfishTaskQueue queue=null;
	private QueueInfo info=null;
	
	public GlassfishTaskHandler(GlassfishTaskQueue glassfishTaskQueue,
			QueueInfo queueInfo) {
		this.queue=glassfishTaskQueue;
		this.info=queueInfo;
		this.run();
		
	}
	
	public void run()
	{
		while(true)
		{
			
			final CloudTask toExecute=this.queue.getNext();

			if (toExecute!=null) {
				System.out.println("Task found");
				new Thread() {
					
					///////////////////////////////////////THREAD ////////////////////////////
					public void run() {
						System.out.println("Run");
						handleTask(toExecute);
						System.out.println("Finish");
						//this.interrupt();
					}
					
					private void handleTask(CloudTask te) {
						String parameters = "";
						boolean firstIt = true;
						for (String key : te.getParameters().keySet()) {
							if (firstIt) {
								parameters = parameters + "?";
								firstIt = false;
							} else {
								parameters = parameters + "&";
							}
							parameters = parameters + key + "=" + te.getParameters().get(key);
						}
						
						System.out.println("Starting servlet.");
						//creo l'url
						URI host = URI.create("http://localhost:8080/");
						String url = "http://" + host.getHost() + ":" + host.getPort() + host.getPath() + te.getServletUri().getPath() + parameters;
						System.out.println(url);
						
						URL iurl = null;
						HttpURLConnection uc = null;
						//DataOutputStream  out = null;
						BufferedReader rd = null;
						
						try {
							iurl = new URL(url);
							uc = (HttpURLConnection)iurl.openConnection();
							uc.setDoOutput(true);
						    
							uc.setRequestMethod(te.getMethod().toUpperCase());

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
		

					}
					
			
					
					
					///////////////////////////// THREAD ///////////////////////////////////
				}.start();
			}
			System.out.println("Sleeping..");
			try {
				Thread.sleep(this.info.getRate());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
