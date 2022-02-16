package org.dcsa.ctk.consumer.service.mock.impl;

import org.dcsa.ctk.consumer.constant.ResponseMockType;
import org.dcsa.ctk.consumer.service.mock.MockService;
import org.dcsa.ctk.consumer.model.ErrorResponse;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MockServiceImpl implements MockService {


    public  Map<String, Object> getMockedResponse(ResponseMockType type, ServerHttpRequest request) {
        Map<String, Object> mockedResponse=null;
        switch (type) {
            case ERROR_RESPONSE: {
                ErrorResponse res = new ErrorResponse();
                res.setRequestUri(request.getURI().toString());
                res.setHttpMethod(request.getMethod().toString());
                mockedResponse=JsonUtility.convertToMap(res);
            }
        }
        return mockedResponse;
    }

}
