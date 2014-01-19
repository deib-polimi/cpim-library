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


import it.polimi.modaclouds.cpimlibrary.CloudMetadata;
import it.polimi.modaclouds.cpimlibrary.ModeQueue;
import it.polimi.modaclouds.cpimlibrary.QueueInfo;
import it.polimi.modaclouds.cpimlibrary.exception.ParserConfigurationFileException;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class QueueServlet
 */
public class GlassfishQueueServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GlassfishQueueServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init(ServletConfig config) throws ServletException { 
       super.init(config);  // call the init method of base class
       
       CloudMetadata metadata = null;
		try {
			metadata = CloudMetadata.getCloudMetadata();
		} catch (ParserConfigurationFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Queue Servlet");
		for(String qname : metadata.getQueueMedatada().keySet())
		{
			System.out.println(qname);
			QueueInfo qinfo = metadata.getQueueMedatada().get(qname);
			if(qinfo.getMode().equals(ModeQueue.PUSH))
			{
				System.out.println("Start " + qname + " Internal Worker.");
				new GlassfishTaskHandler(qname, qinfo).start();
			}
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}


}