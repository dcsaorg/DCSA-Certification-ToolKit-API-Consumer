package org.dcsa.ctk.consumer.mock.service;

import org.dcsa.ctk.consumer.constants.ResponseMockType;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Map;

public interface MockService {
    Map<String, Object> getMockedResponse(ResponseMockType type, ServerHttpRequest request);
}
