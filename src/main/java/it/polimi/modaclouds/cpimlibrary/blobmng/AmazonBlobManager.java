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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AmazonBlobManager implements CloudBlobManager {

	private AmazonS3Client s3 = null; //corrispettivo di BlobstoreService bs=null;
	private String bucketName = null; //serve per non creare un bucket nuovo ogni volta
//	DatastoreService datastore=null;
//	FileService fileService=null;
	
	
	//setta parametri iniziali
	public AmazonBlobManager(AmazonS3Client s3) {
		if(this.bucketName == null) {
			try {
				Properties endpoints = new Properties();
				if(this.getClass().getResourceAsStream("/endpoints.properties")!=null) {
					endpoints.load(this.getClass().getResourceAsStream("/endpoints.properties"));
					if (endpoints.getProperty("S3")!=null)
						this.bucketName = endpoints.getProperty("S3").toLowerCase(Locale.ENGLISH);
				}
				if (this.bucketName == null)
					this.bucketName = "mycloudapplicationbucket";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.s3 = s3;
		if(!s3.doesBucketExist(bucketName)) {
			System.out.println("Creating bucket " + bucketName + "\n");
			s3.createBucket(bucketName);
		}
	}
	
	@Override
	public void uploadBlob(byte[] file, String fileName) {
		System.out.println("uploadBlob " + fileName + ".");
		//System.out.println("Creating bucket " + bucketName + "\n");
		//s3.putObject(new PutObjectRequest(bucketName, name, input, metadata));
		ByteArrayInputStream is = new ByteArrayInputStream(file);
		ObjectMetadata omd = new ObjectMetadata();
		omd.setContentLength(file.length);
		PutObjectRequest pur = new PutObjectRequest(bucketName, fileName, is, omd);
		s3.putObject(pur);
	}

	@Override
	public void deleteBlob(String fileName) {
		System.out.println("deleteBlog " + fileName + ".");
		s3.deleteObject(bucketName, fileName);
	}

	@Override
	public CloudDownloadBlob downloadBlob(String fileName) {
		System.out.println("downloadBlob " + fileName + ".");
		ObjectMetadata om = s3.getObjectMetadata(bucketName, fileName);
		InputStream is = s3.getObject(bucketName, fileName).getObjectContent();
		return new CloudDownloadBlob(fileName, is, om.getContentType(), om.getContentLength(), fileName);
	}

	@Override
	public ArrayList<String> getAllBlobFileName() {
		System.out.println("getAllBlobFileName.");
		if(s3 == null) {
			return null;
		}
		List<S3ObjectSummary> os_list = s3.listObjects(bucketName).getObjectSummaries();
		if (os_list == null) {
			System.out.println("No elements founded.");
			return null;
		}
		System.out.println(os_list.size() + " element/s in the bucket.");
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < os_list.size(); i++) {
			list.add(os_list.get(i).getKey());
		}
		return list;
	}
}	


	

	
	


