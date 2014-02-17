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
package it.polimi.modaclouds.cpimlibrary.blobmng;

class AzureBlobManagerFactory extends CloudBlobManagerFactory {

	private String account;
	private String key;
	
	public AzureBlobManagerFactory(String account, String key) {
		this.account=account;
		this.key=key;
	}

	@Override
	public CloudBlobManager createCloudBlobManager() {
		try {
			return new AzureBlobManager(account,key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
