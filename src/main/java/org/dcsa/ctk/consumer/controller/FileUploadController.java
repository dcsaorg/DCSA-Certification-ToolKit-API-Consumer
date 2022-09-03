package org.dcsa.ctk.consumer.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.ctk.consumer.service.uploader.FileUploadService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = FileUploadController.API_VERSION)
@RequiredArgsConstructor
public class FileUploadController {
    public static final String API_VERSION = "/v2";
    private static final String POST_JSON_SHIPMENT = "/uploadShipment";
    private final FileUploadService fileUploadService;

    @PostMapping(value = POST_JSON_SHIPMENT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<String> upload(@RequestPart("file") FilePart filePart) {
         var monoList = fileUploadService.getLines(filePart);
         var list = monoList.share().block();
        String result = Objects.requireNonNull(list).stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
        return Stream.of(result.split("\n"))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
