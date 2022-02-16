package org.dcsa.ctk.consumer.model;

import lombok.Data;

@Data
public class ResponseDecoratorWrapper {
    String id;
    int httpCode;
    String requirementID;
    String requirement;
    String description;
    RequestMatcher requestMatcher;
    ResponseDecorator responseDecorator;
}
