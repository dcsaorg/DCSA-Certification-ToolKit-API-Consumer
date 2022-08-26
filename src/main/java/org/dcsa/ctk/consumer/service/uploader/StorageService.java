package org.dcsa.ctk.consumer.service.uploader;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {
	String store(MultipartFile file, Path uploadPath);
}
