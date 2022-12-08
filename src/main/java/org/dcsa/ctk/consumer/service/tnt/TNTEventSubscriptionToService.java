package org.dcsa.ctk.consumer.service.tnt;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public interface TNTEventSubscriptionToService<T> {

    T create(TNTEventSubscriptionTO obj, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException, JsonProcessingException;

    List<T> findAll(ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException;

    Map<String, Object> findById(UUID subscriptionId, ServerHttpResponse response, ServerHttpRequest request,CheckListItem checkListItem) throws ExecutionException, InterruptedException;

    void delete(UUID subscriptionId, ServerHttpResponse response, ServerHttpRequest request,CheckListItem checkListItem) throws ExecutionException, InterruptedException;

    T update(UUID subscriptionId, TNTEventSubscriptionTO obj, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException;

    void updateSecret(UUID subscriptionId, EventSubscriptionSecretUpdateTO obj, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException, JsonProcessingException;

    ResponseEntity<Map<String, Object>> callCallback(UUID subscriptionId, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException;

}
