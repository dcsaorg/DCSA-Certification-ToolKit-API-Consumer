package org.dcsa.ctk.consumer.mock.service;

import org.dcsa.ctk.consumer.model.CheckListItem;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface Decorator<T> {
    T decorate(T object, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem);
}
