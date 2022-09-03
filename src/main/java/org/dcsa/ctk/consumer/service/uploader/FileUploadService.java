package org.dcsa.ctk.consumer.service.uploader;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FileUploadService {
    Mono<List<String>> readJson(FilePart filePart);

}
