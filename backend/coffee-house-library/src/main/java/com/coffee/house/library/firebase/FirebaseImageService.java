package com.coffee.house.library.firebase;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FirebaseImageService implements IImageService {
	
	private final String BUCK_NAME = "coffee-house-ed789.appspot.com";

	private final String IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/" + BUCK_NAME + "/o/%s"
			+ "?alt=media&token=";
	
	@EventListener
    public void init(ApplicationReadyEvent event) {

        // initialize Firebase

        try {

            InputStream inputStream = new FileInputStream(new File("src/main/resources/firebase.json"));

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .setStorageBucket(BUCK_NAME)
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (Exception ex) {

            ex.printStackTrace();

        }
    }

	@Override
	public String getImageUrl(String name, String url) {
		String urlName = url.replace("/", "%2F");
		return String.format(IMAGE_URL, urlName.concat(name)).concat(UUID.randomUUID().toString());
	}

	@Override
	public String save(MultipartFile file, String url) throws IOException {
		Bucket bucket = StorageClient.getInstance().bucket();

		String name = generateFileName(file.getOriginalFilename());
		String urlName = url.concat(name);

		bucket.create(urlName, file.getBytes(), file.getContentType());

		return name;
	}

	@Override
	public String save(BufferedImage bufferedImage, String originalFileName) throws IOException {
		byte[] bytes = getByteArrays(bufferedImage, getExtension(originalFileName));

		Bucket bucket = StorageClient.getInstance().bucket();

		String name = generateFileName(originalFileName);

		bucket.create(name, bytes);

		return name;
	}

	@Override
	public void delete(String name, String url) throws IOException {
		Bucket bucket = StorageClient.getInstance().bucket();

		if (name == null) {
			throw new IOException("invalid file name");
		}

		Blob blob = bucket.get(url.concat(name));

		if (blob == null) {
			throw new IOException("file not found");
		}

		blob.delete();
	}

}
