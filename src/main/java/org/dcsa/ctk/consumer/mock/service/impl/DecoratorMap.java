package org.dcsa.ctk.consumer.mock.service.impl;

import org.apache.poi.ss.formula.functions.T;
import org.dcsa.ctk.consumer.mock.service.Decorator;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.model.ResponseDecorator;
import org.dcsa.ctk.consumer.util.APIUtility;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DecoratorMap<R> implements Decorator<Map<String, Object> >  {

    @Override
    public    Map<String, Object> decorate(Map<String, Object> map, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) {
        if (checkListItem == null)
            return map;
        ResponseDecorator responseDecorator = checkListItem.getResponseDecoratorWrapper().getResponseDecorator();
        if (responseDecorator != null) {
            Map<String, Object> body = responseDecorator.getBody();
            if(body!=null)
                return body;
            Map<String, Object> patch = responseDecorator.getPatch();
            Map<String, String> headers = responseDecorator.getHeader();
            Map<String, Map<String, String>> format = responseDecorator.getFormat();
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    if (!(response.getHeaders().containsKey(entry.getKey())))
                        response.getHeaders().add(entry.getKey(), entry.getValue());
                }
            }
            if (patch != null) {
                map = JsonUtility.addFieldToMap(map, patch);
            }
            if (format != null) {
                for (Map.Entry<String, Map<String, String>> s : format.entrySet()) {
                    if (s.getKey().equals("date")) {
                        for (Map.Entry<String, String> entrySet : s.getValue().entrySet()) {
                            String currentValue = (String) map.get(entrySet.getKey());
                            String newValue = APIUtility.changeDateFormat(entrySet.getValue(), currentValue);
                            map.put(entrySet.getKey(), newValue);
                        }
                    }
                }
            }
        }
        response.setRawStatusCode(checkListItem.getResponseDecoratorWrapper().getHttpCode());
        return map;
    }
    private List<Map<String, Object>> decorate(List<Map<String, Object>> list, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) {
        if (checkListItem == null)
            return list;
        if (list != null && !(list.isEmpty())) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                Map<String, Object> obj = list.get(i);
                decorate(obj, response, request, checkListItem);
            }
        }
        return list;
    }



}
