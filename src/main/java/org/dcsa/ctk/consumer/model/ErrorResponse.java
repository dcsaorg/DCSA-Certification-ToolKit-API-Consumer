package org.dcsa.ctk.consumer.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ErrorResponse {
    String httpMethod;
    String requestUri;
    Map<String, String> errors;
    int statusCode;
    String statusCodeText;
    String errorDateTime;

    public ErrorResponse() {
        httpMethod = "GET";
        requestUri = "http://127.0.0.1:9090/v2/proxy/event-subscriptions/9729a5a2-d0d9-4a9a-a7d0-280033349821";
        statusCode = 500;
        statusCodeText = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        errorDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZ").format(new Date());
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("reason", statusCodeText.replaceAll(" ", ""));
        errors.put("message", statusCodeText);
        this.errors = errors;
    }
}
