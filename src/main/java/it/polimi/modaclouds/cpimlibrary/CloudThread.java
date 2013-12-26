package it.polimi.modaclouds.cpimlibrary;

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


import it.polimi.modaclouds.cpimlibrary.exception.ParserConfigurationFileException;

import java.util.logging.Logger;

import com.google.appengine.api.ThreadManager;

public class CloudThread {

	public static Thread getThread(Runnable runnable) {
		Logger l=Logger.getLogger("it.polimi.modaclouds.cpimlibrary");
		String vendor = null;
		try {
			vendor = CloudMetadata.getCloudMetadata().getTypeCloud();

			if (vendor.equals("Azure")) {
				return new Thread(runnable);
			} else if (vendor.equals("Google")) {
				return ThreadManager.createBackgroundThread(runnable);

			} else if (vendor.equals("Amazon")) {
				return new Thread(runnable);
			}
		} catch (ParserConfigurationFileException e) {
				l.info("Error! Vendor is not supported!");
		}
		return null;
	}

	private CloudThread() {
	}

}
