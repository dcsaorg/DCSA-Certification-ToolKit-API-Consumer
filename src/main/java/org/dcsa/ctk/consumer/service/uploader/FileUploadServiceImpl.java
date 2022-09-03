package org.dcsa.ctk.consumer.service.uploader;

import lombok.extern.slf4j.Slf4j;
import org.dcsa.ctk.consumer.model.enums.UploadType;
import org.dcsa.ctk.consumer.service.sql.SqlInsertHandler;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final SqlInsertHandler sqlInsertHandler;

    public FileUploadServiceImpl(SqlInsertHandler sqlInsertHandler) {
        this.sqlInsertHandler = sqlInsertHandler;
    }
    public Mono<List<String>> readJson(FilePart filePart) {
       return filePart.content()
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
               .map( item -> insertIntoDb(item, filePart.filename()))
                .collectList();
    }
    private String insertIntoDb(String json, String fileName)  {
        return sqlInsertHandler.insertJsonSqlData(json, UploadType.JsonFullShipment);
    }
}
