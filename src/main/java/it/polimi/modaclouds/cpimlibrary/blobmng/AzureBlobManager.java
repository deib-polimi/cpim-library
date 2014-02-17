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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.activation.MimetypesFileTypeMap;

import com.windowsazure.samples.Metadata;
import com.windowsazure.samples.MetadataCollection;
import com.windowsazure.samples.blob.AzureBlob;
import com.windowsazure.samples.blob.AzureBlobCollection;
import com.windowsazure.samples.blob.ContainerAccess;
import com.windowsazure.samples.blob.condition.DeleteBlobCondition;
import com.windowsazure.samples.blob.condition.PutBlobCondition;
import com.windowsazure.samples.blob.data.BlobData;
import com.windowsazure.samples.blob.data.OctetBlobData;
import com.windowsazure.samples.internal.authentication.DirectConnectToken;

class AzureBlobManager implements CloudBlobManager {

	private com.windowsazure.samples.blob.AzureBlobManager abm = null;
	
	private final String DEFAULT_CONTAINER = "default-container";

	public AzureBlobManager(String account, String key) throws Exception {
		abm = new com.windowsazure.samples.blob.AzureBlobManager(new DirectConnectToken(account, key));
	}

//	private void createContainerIfNotExist(String nameContainer) {
//		abm.createContainer(nameContainer, null, null);
//	}
//
//	private void deleteContainer(String containerName) {
//		abm.deleteContainer(containerName, null);
//	}

	public void uploadBlob(byte[] file, String fileName) {
		fileName = fileName.replace(" ", "_");
		abm.createContainer(DEFAULT_CONTAINER, null, ContainerAccess.BLOB);

		PutBlobCondition pbc = new PutBlobCondition();
		MetadataCollection mc = new MetadataCollection();
		String contentType = MimetypesFileTypeMap.getDefaultFileTypeMap()
				.getContentType(fileName);
		mc.add(Metadata.buildValid("fileName", fileName));
		mc.add(Metadata.buildValid("size", Integer.toString(file.length)));
		mc.add(Metadata.buildValid("contentType", contentType));
		BlobData bd = new OctetBlobData(file);
		abm.putBlockBlob(DEFAULT_CONTAINER, fileName, null, mc, bd, pbc);
	}


	@Override
	public CloudDownloadBlob downloadBlob(String fileName) {
		AzureBlob ab = abm.getBlockBlob(DEFAULT_CONTAINER, fileName);
		CloudDownloadBlob download = null;
		MetadataCollection mc = ab.getMetadata();
		String contentType = mc.getMetadata("contentType").getValue();
		int size = Integer.parseInt(mc.getMetadata("size").getValue());
		InputStream stream = new ByteArrayInputStream(OctetBlobData
				.fromBlob(ab).getBytes());
		BufferedInputStream buffer = new BufferedInputStream(stream);
		download = new CloudDownloadBlob(ab.getBlobName(), buffer, contentType,
				size, null);
		return download;

	}

	@Override
	public ArrayList<String> getAllBlobFileName() {
		AzureBlobCollection abc = abm.listAllBlobs(DEFAULT_CONTAINER);
		ArrayList<String> list = new ArrayList<String>();
		for (AzureBlob ab : abc.getBlobs())
			list.add(ab.getBlobName());
		return list;
	}

	@Override
	public void deleteBlob(String fileName) {
		DeleteBlobCondition dc = new DeleteBlobCondition();
		abm.deleteBlob(DEFAULT_CONTAINER, fileName, null,dc);

	}

}
