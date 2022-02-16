package org.dcsa.ctk.consumer.service.log.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.ctk.consumer.service.config.impl.ConfigService;
import org.dcsa.ctk.consumer.service.log.CustomLogger;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.model.CustomerLogger;
import org.dcsa.ctk.consumer.util.APIUtility;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class CustomLoggerImpl implements CustomLogger {

    public  CustomerLogger init(Object obj, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem,String route) {
        CustomerLogger customerLogger = new CustomerLogger();
        if (obj != null)
            customerLogger.setRequest(obj);
        customerLogger.setRequestHeader(request.getHeaders());
        response.getHeaders().add("customerLogger", JsonUtility.getStringFormat(customerLogger));
        if (checkListItem != null)
            response.getHeaders().add("checkListItemKey", checkListItem.getId());
        if (route != null)
            response.getHeaders().add("routeKey", APIUtility.generateKey(route,request.getMethod().name()));
        return customerLogger;
    }

    public  CustomerLogger log(Object obj, ServerHttpResponse response, ServerHttpRequest request) throws JsonProcessingException {

        List<String> customerLoggerHeader = response.getHeaders().get("customerLogger");
        List<String> checkListItemHeader = response.getHeaders().get("checkListItemKey");
        List<String> routeKeyHeader = response.getHeaders().get("routeKey");
        CheckListItem checkListItem = null;
        if (checkListItemHeader != null) {
            checkListItem = ConfigService.getCheckListItem(checkListItemHeader.get(0));
            response.getHeaders().remove("checkListItemKey");
        }
        CustomerLogger customerLogger = null;
        if (customerLoggerHeader != null) {
            response.getHeaders().remove("customerLogger");
            customerLogger = JsonUtility.getObjectFromJson(CustomerLogger.class, customerLoggerHeader.get(0));
        }
        if(routeKeyHeader!=null)
            response.getHeaders().remove("routeKey");
        if (obj != null && customerLogger != null) {
            customerLogger.setResponse(obj);
            customerLogger.setResponseHeader(response.getHeaders());
            customerLogger.setUrl(request.getURI().toString());
            customerLogger.setHttpMethod(Objects.requireNonNull(request.getMethod()).name());
            customerLogger.setResponseStatus(response.getRawStatusCode());
        }
        if (checkListItem != null && customerLogger != null)
            checkListItem.setLog(customerLogger);
        log.info(JsonUtility.beautify(customerLogger));
        return customerLogger;
    }


}
