package org.dcsa.ctk.consumer.mock.service;

import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public interface TNTEventSubscriptionToService<T> {

    T create(TNTEventSubscriptionTO obj, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException;

    List<T> findAll(ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException;

    Map<String, Object> findById(UUID id, ServerHttpResponse response, ServerHttpRequest request,CheckListItem checkListItem) throws ExecutionException, InterruptedException;

    void delete(UUID id, ServerHttpResponse response, ServerHttpRequest request,CheckListItem checkListItem) throws ExecutionException, InterruptedException;

    T update(UUID id, TNTEventSubscriptionTO obj, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException;

    void updateSecret(UUID id, EventSubscriptionSecretUpdateTO obj, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException;


}
