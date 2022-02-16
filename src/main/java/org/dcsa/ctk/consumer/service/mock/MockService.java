package org.dcsa.ctk.consumer.service.mock;

import org.dcsa.ctk.consumer.constant.ResponseMockType;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Map;

public interface MockService {
    Map<String, Object> getMockedResponse(ResponseMockType type, ServerHttpRequest request);
}
