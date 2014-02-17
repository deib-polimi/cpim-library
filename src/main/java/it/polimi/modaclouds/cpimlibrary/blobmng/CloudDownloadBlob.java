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

import java.io.InputStream;

/**
 * This class represent the object that is returned when the download operation is called.
 * It contains the file name, the size, the type of content and the {@code InputStream} that represents the content of the file.
 *
 */
public class CloudDownloadBlob {

	private String fileName=null;
	private String contentType=null;
	private long size;
	private InputStream fileStream;
	private String blobKey;
	
	/**
	 * Creates a CloudDownloadBlob object containing the given attribute.
	 * 
	 * @param fileName
	 * @param fileStream
	 * @param contentType
	 * @param size
	 * @param blobKey
	 */
	public CloudDownloadBlob(String fileName, InputStream fileStream, String contentType, long size, String blobKey){
		this.fileName=fileName;
		this.contentType=contentType;
		this.fileStream=fileStream;
		this.size=size;
		this.blobKey=blobKey;
	}

/**
 * Returns the file name.
 * @return file name
 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Returns the {@code BlobKey} in String format. It is the key that is saved.
	 * 
	 * @return String that represent the {@code BlobKey}.
	 */
	public String getBlobKeyString() {
		return blobKey;
	}

	/**
	 * Returns the type of content
	 * @return String that represent the content type.
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Returns the size of the file
	 * 
	 * @return size of file
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Returns the {@code InputStream} that contains the file content
	 * @return stream of file content
	 */
	public InputStream getFileStream() {
		return fileStream;
	}

}
