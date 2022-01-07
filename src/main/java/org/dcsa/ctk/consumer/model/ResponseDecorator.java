package org.dcsa.ctk.consumer.model;

import lombok.Data;

import java.util.Map;

@Data
public class ResponseDecorator {
    Map<String, String> header;
    Map<String,Object> body;
    Map<String, Object> patch;
    Map<String, Map<String,String>> format;
}
