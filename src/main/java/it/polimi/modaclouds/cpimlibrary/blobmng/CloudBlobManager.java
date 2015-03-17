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

import java.util.ArrayList;

/**
 * This abstract class exposes the method to manage the Blob service on the cloud in
 * an independent-platform way.
 *
 */
public interface CloudBlobManager {

	/**
	 * Upload a file on the cloud. The maximum size allowed is 32 MB.
	 *
	 * @param file
	 *            - byte array
	 * @param fileName
	 */
	public void uploadBlob(byte[] file, String fileName);

	/**
	 * Delete the Blob on cloud by giving the file name. Do nothing if the file
	 * does not exist.
	 *
	 * @param fileName
	 */
	public void deleteBlob(String fileName);

	/**
	 * Returns a CloudDowloadBlob that represent the Blob file stored on cloud.
	 *
	 * @param fileName
	 * @return a CloudDownload Blob or null if the Blob does not exist
	 * @see CloudDownloadBlob
	 */
	public CloudDownloadBlob downloadBlob(String fileName);

	/**
	 * Returns a list of strings that contains all the file name stored on
	 * cloud. <b>This operation is expensive</b>.
	 *
	 * @return list of file name
	 */
	public ArrayList<String> getAllBlobFileName();

    /**
     * Returns true if a blob exists with the given name.
     *
     * @param blobFileName the file name
     *
     * @return true if file exits, false otherwise
     */
    boolean fileExists(String blobFileName);
}
