package it.polimi.modaclouds.cpimlibrary.taskqueuemng;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;






import it.polimi.modaclouds.cpimlibrary.QueueInfo;
import it.polimi.modaclouds.cpimlibrary.mffactory.MF;

public class GlassfishTaskHandler extends Thread {

	private String name=null;
	private QueueInfo info=null;
	private GlassfishTaskQueue queue = null;
	private String backend=null;
	
	public GlassfishTaskHandler(String queueName,
			QueueInfo queueInfo, String backend) {
		this.name=queueName;
		this.info=queueInfo;
		this.backend=backend;

		
	}
	
	public void run()
	{
		
		queue = (GlassfishTaskQueue) MF.getFactory().getTaskQueueFactory().getQueue(name);

		
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
						URI host = URI.create(backend);
						String url = "http://" + host.getHost() + ":" + host.getPort() + host.getPath() + te.getServletUri().getPath()  + parameters;
						System.out.println(url);
					
						URL iurl = null;
						HttpURLConnection uc = null;
						//DataOutputStream  out = null;
						
						try {
							iurl = new URL(url);
							uc = (HttpURLConnection)iurl.openConnection();
							//da inserire per evitre l errore di input output??
							uc.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
							uc.setRequestProperty("Accept","*/*");
							
							uc.setDoOutput(true);
						    
							uc.setRequestMethod(te.getMethod().toUpperCase());

							/*
							InputStream is = uc.getInputStream();
							
							rd = new BufferedReader(new InputStreamReader(is));
							
						    String line;
						    while ((line = rd.readLine()) != null) {
						    	System.out.println("Response: "+ line);
						    }
						    rd.close();
						    */
						    System.out.println("Response code: "+ uc.getResponseCode());
						    /*
						    if (uc.getResponseCode() == 500){
						    	;;
						    }
						    */
						    
						} catch (RuntimeException e) {
							System.out.println("RuntimeException error!!");
							System.out.println("------");
							e.printStackTrace();
						} catch (MalformedURLException e) {
							System.out.println("MalformedURLException error!!");
							System.out.println("------");
							e.printStackTrace();
						} catch (IOException e) {
							System.out.println("IOException error!!");
							System.out.println("------");
							e.printStackTrace();
						}
						System.out.println("End of servlet.");
		

					}
					
			
					
					
					///////////////////////////// THREAD ///////////////////////////////////
				}.start();
			}
			try {
				Thread.sleep(this.info.getRate());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
