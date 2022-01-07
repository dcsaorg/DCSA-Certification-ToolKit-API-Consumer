package org.dcsa.ctk.consumer.model;

import lombok.Data;

import java.util.Set;

@Data
public class RequestMatcher {
    Set<String> header;
    Set<String> queryParameter;
}
