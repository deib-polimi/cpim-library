package it.polimi.modaclouds.cpimlibrary.blobmng;

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


import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

public class AmazonBlobManagerFactory extends CloudBlobManagerFactory {
	
	@Override
	public CloudBlobManager createCloudBlobManager() {
		AWSCredentials credentials = null;
		try {
			credentials = new PropertiesCredentials(
			        getClass().getClassLoader().getResourceAsStream("AwsCredentials.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AmazonBlobManager(new AmazonS3Client(credentials));
	}

}