package org.dcsa.ctk.consumer.service.callback;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public interface ConsumerCallbackService {
    ResponseEntity<String> checkCallback(UUID id, TNTEventSubscriptionTO obj, ServerHttpResponse response, ServerHttpRequest request, Map<String, List<CheckListItem>> checkListItemMap) throws ExecutionException, InterruptedException, JsonProcessingException;

    ResponseEntity<Map<String, Object>> callCallback(UUID id, ServerHttpResponse response, ServerHttpRequest request, Map<String, List<CheckListItem>> checkListItemMap) throws ExecutionException, InterruptedException, JsonProcessingException;
}
