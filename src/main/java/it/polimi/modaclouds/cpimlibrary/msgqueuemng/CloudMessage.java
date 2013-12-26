package it.polimi.modaclouds.cpimlibrary.msgqueuemng;

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


/**
 * This class represent a message contained in a queue in the Queue Message
 * service.
 * 
 */
public class CloudMessage {
	String msg = null;
	String id = null;

	/**
	 * Create a message with the given text.
	 * 
	 * @param msg
	 *            - message text
	 */
	public CloudMessage(String msg) {
		this.msg = msg;
		this.id = String.valueOf(msg.hashCode());
	}

	public CloudMessage() {

	}

	/**
	 * Returns the message text.
	 * 
	 * @return message text
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * Assigns the given message text.
	 * 
	 * @param msg
	 *            - message text
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * Returns the message id.
	 * 
	 * @return message id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Assigns the given message id.
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

}
