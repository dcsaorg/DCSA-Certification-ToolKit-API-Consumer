package org.dcsa.ctk.consumer.mock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.model.CustomerLogger;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;


public interface CustomLogger {

    CustomerLogger init(Object obj, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem, String route);

    CustomerLogger log(Object obj, ServerHttpResponse response, ServerHttpRequest request) throws JsonProcessingException;


}
