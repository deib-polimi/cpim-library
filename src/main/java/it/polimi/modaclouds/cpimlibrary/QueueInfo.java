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
package it.polimi.modaclouds.cpimlibrary;

/**
 * This class contains the information related to a queue setting present in the
 * <u>queue.xml</u> file.
 * 
 * 
 */
public class QueueInfo {

	private long rate;
	private ModeQueue mode = ModeQueue.PUSH;
	private String messageQueueConnectionString=null;
	private String messageQueueResource=null;

	public QueueInfo() {

	}

	/**
	 * Returns the mode of the queue.
	 * 
	 * @return PUSH, or PULL
	 * 
	 * 
	 */
	public ModeQueue getMode() {
		return mode;
	}

	/**
	 * Assigns the mode type of the queue.
	 * 
	 * @param mode
	 *            <ul>
	 *            <li>ModeQueue.PULL = MessageQueue</li>
	 *            <li>ModeQueue.PUSH = TaskQueue</li>
	 *            </ul>
	 * 
	 * 
	 */
	public void setMode(ModeQueue mode) {
		this.mode = mode;
	}


	public String getMessageQueueConnection() {
		return this.messageQueueConnectionString;
	}


	public void setMessageQueueConnection(String connectionString) {
		if(this.mode==ModeQueue.PULL)
			this.messageQueueConnectionString=connectionString;
			
	}
	
	public String getMessageQueueResource() {
		return this.messageQueueResource;
	}


	public void setMessageQueueResource(String queueResource) {
		if(this.mode==ModeQueue.PULL)
			this.messageQueueResource=queueResource;
	}
	

	/**
	 * Assigns the rate of the queue. <b>(only TaskQueue)</b>
	 * 
	 * @param rate
	 *            <ul>
	 *            <li>"#/s" - # per second</li>
	 *            <li>"#/m" - # per minute</li>
	 *            <li>"#/d" - # per day</li>
	 *            </ul>
	 * 
	 * 
	 */
	public void setRate(String rate) {
		String[] split = rate.split("/");
		Character unit = new Character(split[1].charAt(0));
		long num = 1000;
		switch (unit) {
		case 'd':
			num = num * 24;
		case 'h':
			num = num * 60;
		case 'm':
			num = num * 60;
		}
		this.rate = num / Long.parseLong(split[0]);

	}

	/**
	 * Return the rate assigned to the queue.
	 * 
	 * @return a <i>long</i> that represent the rate of the queue
	 * 
	 * 
	 */
	public long getRate() {
		return rate;
	}
}
