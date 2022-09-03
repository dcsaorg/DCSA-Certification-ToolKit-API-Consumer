package org.dcsa.ctk.consumer.service.uploader;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface FileUploadService {
    Mono<List<String>> getLines(FilePart filePart);

}
