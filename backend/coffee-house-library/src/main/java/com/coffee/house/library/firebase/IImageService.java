package com.coffee.house.library.firebase;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public interface IImageService {
	String getImageUrl(String name, String url);

	String save(MultipartFile file, String url) throws IOException;

	String save(BufferedImage bufferedImage, String originalFileName) throws IOException;

	void delete(String name,String url) throws IOException;

	default String getExtension(String originalFileName) {
		return StringUtils.getFilenameExtension(originalFileName);
	}

	default String generateFileName(String originalFileName) {
		return UUID.randomUUID().toString() + getExtension(originalFileName);
	}

	default byte[] getByteArrays(BufferedImage bufferedImage, String format) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {

			ImageIO.write(bufferedImage, format, baos);

			baos.flush();

			return baos.toByteArray();

		} catch (IOException e) {
			throw e;
		} finally {
			baos.close();
		}
	}
}
