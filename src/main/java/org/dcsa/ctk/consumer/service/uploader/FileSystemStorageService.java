package org.dcsa.ctk.consumer.service.uploader;

import lombok.extern.java.Log;
import org.dcsa.ctk.consumer.init.AppProperty;
import org.dcsa.ctk.consumer.service.uploader.exception.StorageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Log
@Service
public class FileSystemStorageService implements StorageService {

	public String store(MultipartFile file, Path uploadPath) {
		Path destinationFile;
		try {
			if (file.isEmpty() || uploadPath == null) {
				throw new StorageException("Failed to store empty file for path.");
			}
			destinationFile = AppProperty.uploadPath.resolve(
					Paths.get(Objects.requireNonNull(file.getOriginalFilename())))
					.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(AppProperty.uploadPath.toAbsolutePath())) {
				throw new StorageException(
						"Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
		return  destinationFile.toString();

	}

}
