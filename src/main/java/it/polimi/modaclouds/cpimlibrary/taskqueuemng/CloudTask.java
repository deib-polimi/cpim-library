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


import java.net.URI;
import java.util.HashMap;

/**
 * This class represents the object used in the CloudTaskQueue. This object is
 * used to construct the specific object to insert in the Queue Service in the
 * platform selected.
 * 
 */
public class CloudTask {

	//private URI hosturi = null;
	private URI servletpath = null;
	private HashMap<String, String> parameters = null;
	private String method = null;
	private String taskName = null;
	


	/**
	 * Returns the servlet URI that indicates the address of the servlet used to
	 * perform the task. This address and the host address together compose the
	 * full path of the resource responsible to the task execution.
	 * 
	 * @return address of the servlet
	 */
	public URI getServletUri() {
		return servletpath;
	}

	/**
	 * Assigns the servlet address to the task. It represents the address of the
	 * servlet used to perform the task.<br/>
	 * For example "/servletName".
	 * 
	 * @param uri
	 *            - address of the servlet
	 */
	public void setServletUri(String uri) {

		this.servletpath = URI.create(uri);
	}

	/**
	 * Returns all the parameters added at that time to the task.
	 * 
	 * @return an HashMap that contains all the parameters as key-value pairs
	 */
	public HashMap<String, String> getParameters() {
		return parameters;
	}

	/**
	 * Add a parameter to the task request.
	 * 
	 * @param key
	 *            - name of parameter
	 * @param value
	 *            - value of parameter
	 */
	public void setParameters(String key, String value) {
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}
		parameters.put(key, value);
	}

	/**
	 * Returns the method which will be called the task
	 * 
	 * @return calling method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Assigns the method which will be called the task. <b>CloudTask.POST</b>
	 * 
	 * @param method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	public static String POST = "POST";

	/**
	 * Assigns the name to this Task
	 * 
	 * @param name
	 */
	public void setTaskName(String name) {
		taskName = name;
	}

	/**
	 * Returns the name of the task
	 * 
	 * @return name of the task
	 */
	public String getTaskName() {
		return taskName;
	}

}
