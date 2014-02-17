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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.activation.MimetypesFileTypeMap;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;


class GoogleBlobManager implements CloudBlobManager {

	BlobstoreService bs=null;
	DatastoreService datastore=null;
	FileService fileService=null;

	public GoogleBlobManager() {
		bs=BlobstoreServiceFactory.getBlobstoreService();
		datastore= DatastoreServiceFactory.getDatastoreService();
		fileService = FileServiceFactory.getFileService();
	}

	
	@Override
	public CloudDownloadBlob downloadBlob(String fileName) {
		    try {
		    	BlobInfo bi=getBlobInfoByFileName(fileName);
		    	return new CloudDownloadBlob(fileName,new BlobstoreInputStream(bi.getBlobKey()),bi.getContentType(),bi.getSize(),bi.getBlobKey().getKeyString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
	}
	
	
	
	public void uploadBlob(byte[] file, String fileName) {
		fileName= fileName.replace(" ", "_");
		MimetypesFileTypeMap mftm = new MimetypesFileTypeMap();
		String contentType=mftm.getContentType(fileName);
		AppEngineFile blobToUpload;
		try {
		blobToUpload = fileService.createNewBlobFile(contentType, fileName);
		boolean lock = true;
		FileWriteChannel wc= fileService.openWriteChannel(blobToUpload, lock);
		blobToUpload = new AppEngineFile(blobToUpload.getFullPath());
		wc.write(ByteBuffer.wrap(file));
		wc.closeFinally();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		
	}
	
	@Override
	public ArrayList<String> getAllBlobFileName() {
		
		BlobInfoFactory bif= new BlobInfoFactory(datastore);
		Iterator<BlobInfo> iter=bif.queryBlobInfos();
		ArrayList<String> list= new ArrayList<String>();
		while(iter.hasNext())
		{
			list.add(iter.next().getFilename());
		}
		return list;
	}
	
	private BlobInfo getBlobInfoByFileName(String fileName){
		BlobInfoFactory bif = new BlobInfoFactory(datastore);
    	Iterator<BlobInfo>iter=bif.queryBlobInfos();
    	BlobInfo bi=null;
    	while(iter.hasNext()){
    		bi=iter.next();
    		if(bi.getFilename().compareTo(fileName)==0)
    			return bi;
    	}
		return bi;
	}


	@Override
	public void deleteBlob(String fileName) {
		
		BlobInfo bi=getBlobInfoByFileName(fileName);
		if (bi!=null)
		bs.delete(bi.getBlobKey());
	}

}	



	

	
	


