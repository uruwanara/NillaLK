package lk.nilla.services;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class FileService {

	public FileService() {
		// TODO Auto-generated constructor stub
	}

	public static String uploadObject(String name, byte[] file, String contentType) throws IOException {
		  
	    String projectId = "nillalk";
	    String bucketName = System.getenv("CloudBucket");
	
	    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
	    BlobId blobId = BlobId.of(bucketName, name);
	    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
	    Blob newFile = storage.create(blobInfo, file);
	    
	    return newFile.getSelfLink();
	    
	}
	
	public static URL getObjectURI(String objectName) {
		
		String projectId = "nillalk";
	    String bucketName = System.getenv("CloudBucket");

	    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

	    BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName)).build();
	    URL url = storage.signUrl(blobInfo, 7, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature());

	    return url;

	}

}
