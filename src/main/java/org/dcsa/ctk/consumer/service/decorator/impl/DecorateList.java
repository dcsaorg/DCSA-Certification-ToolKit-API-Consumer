package org.dcsa.ctk.consumer.service.decorator.impl;

import org.dcsa.ctk.consumer.service.decorator.Decorator;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DecorateList implements Decorator<List<Map<String, Object>>> {
    @Autowired
    Decorator<Map<String,Object>> decoratorMap;
    @Override
    public List<Map<String, Object>> decorate(List<Map<String, Object>> object, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) {
        if (checkListItem == null)
            return object;
        if (object != null && !(object.isEmpty())) {
            int size = object.size();
            for (int i = 0; i < size; i++) {
                Map<String, Object> obj = object.get(i);
                decoratorMap.decorate(obj, response, request, checkListItem);
            }
        }
        return object;
    }


}
