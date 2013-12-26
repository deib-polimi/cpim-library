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
 * This interface exposes the methods used to manage the MessageQueue service on
 * the cloud, in an independent-platform way.
 * <p>
 * Once that the CloudMessageQueue is created, it must be loaded the
 * MessageConsumer, delegate to process and dispatch the message in the queue.
 * </p>
 * 
 */
public interface CloudMessageQueue {

	/**
	 * Adds a new message in the queue
	 * 
	 * @param msg
	 * @throws CloudMessageQueueException
	 *             - if the message is not added to the queue
	 */
	public void add(String msg) throws CloudMessageQueueException;;

	/**
	 * Returns the name of the queue.
	 * 
	 * @return name of queue
	 */
	public String getQueueName();

	/**
	 * Deletes all the messages that are contained in the queue.
	 * 
	 */
	public void purge();

	/**
	 * Returns a message if the queue is not empty.
	 * 
	 * @return message
	 * @see CloudMessage
	 */
	public CloudMessage getMessage();

	/**
	 * Deletes the message passed by parameter.
	 * 
	 * @param msg
	 *            - CloudMessage object that represent the entire message
	 * @throws CloudMessageQueueException
	 *             occurs if there are some problems in the deleting operation
	 */
	public void deleteMessage(CloudMessage msg)
			throws CloudMessageQueueException;

	/**
	 * Deletes the message with the given key.
	 * 
	 * @param msgId
	 * @throws CloudMessageQueueException
	 *             if there are some problems in the deleting operation
	 */
	public void deleteMessage(String msgId) throws CloudMessageQueueException;

}
