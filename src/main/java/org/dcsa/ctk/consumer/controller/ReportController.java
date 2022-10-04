package org.dcsa.ctk.consumer.controller;

import lombok.AllArgsConstructor;
import org.dcsa.ctk.consumer.reporter.ExtentReportManager;
import org.dcsa.ctk.consumer.reporter.Reporter;
import org.dcsa.ctk.consumer.util.FileUtility;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping(path = ReportController.REQUEST_URL, produces = {MediaType.APPLICATION_JSON_VALUE})
@RestController
@AllArgsConstructor
public class ReportController {

    public static final String REQUEST_URL = "/conformance/report";
    final Reporter reporter;

    @GetMapping
    public ResponseEntity<Object> downloadReport() throws IOException {
        HttpHeaders header = new HttpHeaders();
        String fileName = reporter.generateHtmlTestReport();
        ByteArrayResource resource = FileUtility.getFile(fileName);
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + ExtentReportManager.getReportName());
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(header)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

}
