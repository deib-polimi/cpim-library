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


public class AmazonTaskQueueMessage extends TaskQueueMessage {
	
	private String popReceipt=null;
	
	public AmazonTaskQueueMessage(String messageId,String messageText, String popReceipt){
		super(messageId,messageText);
		this.popReceipt=popReceipt;
	}
	
	public String getPopReceipt(){
		return popReceipt;
	}

}
