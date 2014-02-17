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
package it.polimi.modaclouds.cpimlibrary.taskqueuemng;

/**
 * This interface exposes the methods used to manage the TaskQueue service on
 * the cloud, in an independent-platform way.
 * <p>
 * Once that the CloudTask is added, it will be processed by the consumer
 * relative to the queue with rate configured in the <u>queue.xml</u> file.
 * </p>
 * 
 */
public interface CloudTaskQueue {

	/**
	 * Adds a new CloudTask in the queue
	 * 
	 * @param t
	 *            - Cloud Task to add
	 * @throws CloudTaskQueueException
	 *             - if the message is not added to the queue
	 */
	public void add(CloudTask t) throws CloudTaskQueueException;

	/**
	 * Returns the name of the queue.
	 * 
	 * @return name of queue
	 */
	public String getQueueName();

	/**
	 * Deletes all the CloudTask that are contained in the queue.
	 * 
	 * @throws CloudTaskQueueException
	 */
	public void purge() throws CloudTaskQueueException;

	/**
	 * Deletes the CloudTask passed by parameter.
	 * 
	 * @param t
	 *            - CloudTask
	 */
	public boolean delete(CloudTask t);

}
