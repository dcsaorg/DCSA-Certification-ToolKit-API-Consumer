package org.dcsa.ctk.consumer.model;

import lombok.Data;

@Data
public class ResponseDecoratorWrapper {
    String id;
    String description;
    int httpCode;
    String category;
    RequestMatcher requestMatcher;
    ResponseDecorator responseDecorator;
}
