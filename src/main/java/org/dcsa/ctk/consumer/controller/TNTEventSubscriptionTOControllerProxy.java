package org.dcsa.ctk.consumer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.ctk.consumer.config.AppProperty;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.model.enums.ValidationRequirementId;
import org.dcsa.ctk.consumer.service.config.impl.ConfigService;
import org.dcsa.ctk.consumer.service.log.CustomLogger;
import org.dcsa.ctk.consumer.service.tnt.TNTEventSubscriptionToService;
import org.dcsa.ctk.consumer.util.APIUtility;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.dcsa.ctk.consumer.controller.TNTEventSubscriptionTOControllerProxy.API_VERSION;
import static org.dcsa.ctk.consumer.controller.TNTEventSubscriptionTOControllerProxy.REQUEST_URL;


@RestController
@Slf4j
@RequestMapping(value = REQUEST_URL+API_VERSION, produces = {MediaType.APPLICATION_JSON_VALUE})
public class TNTEventSubscriptionTOControllerProxy {

    public static final String REQUEST_URL = "/tnt";
    public static final String API_VERSION = "/v2";

    private static final String EVENT_SUBSCRIPTION = "/event-subscriptions";

    final TNTEventSubscriptionToService<Map<String, Object>> tntEventSubscriptionToService;

    final CustomLogger customLogger;

    final AppProperty appProperty;

    public TNTEventSubscriptionTOControllerProxy(TNTEventSubscriptionToService<Map<String, Object>> tntEventSubscriptionToService, CustomLogger customLogger, AppProperty appProperty) {
        this.tntEventSubscriptionToService = tntEventSubscriptionToService;
        this.customLogger = customLogger;
        this.appProperty = appProperty;
        APIUtility.runWebHook();
    }

    @PostMapping(EVENT_SUBSCRIPTION)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@RequestBody Object obj, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = APIUtility.getRoute(request);
        CheckListItem checkListItem = ConfigService.getNextCheckListItem(route, response, request);
        customLogger.init(obj, response, request, checkListItem, route);
        Map<String, Object> responseMap = tntEventSubscriptionToService.create(JsonUtility.convertTo(TNTEventSubscriptionTO.class, obj), response, request, checkListItem);
        customLogger.log(checkListItem, responseMap, response, request);
        return responseMap;
    }

    @GetMapping(EVENT_SUBSCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> findAll(ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = APIUtility.getRoute(request);
        CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_API_CHK_2.getId());
        customLogger.init(null, response, request, checkListItem, route);
        List<Map<String, Object>> responseList = tntEventSubscriptionToService.findAll(response, request, checkListItem);
        customLogger.log(checkListItem, responseList, response, request);
        return responseList;
    }

    @PutMapping({EVENT_SUBSCRIPTION+"/{subscriptionId}/secret"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSecret(@PathVariable String subscriptionId, @RequestBody Object obj, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = "/event-subscriptions/{subscriptionId}/secret";
        CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_SUB_CSM_2.getId());
        customLogger.init(obj, response, request, checkListItem, route);
        tntEventSubscriptionToService.updateSecret(UUID.fromString(subscriptionId), JsonUtility.convertTo(EventSubscriptionSecretUpdateTO.class, obj), response, request, checkListItem);
        customLogger.log(checkListItem, null, response, request);
    }

    @GetMapping({EVENT_SUBSCRIPTION+"/{subscriptionId}"})
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> findById(@PathVariable String subscriptionId, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = "/event-subscriptions/{subscriptionId}";
        CheckListItem checkListItem = ConfigService.getNextCheckListItem(route, response, request);
        customLogger.init(null, response, request, checkListItem, route);
        Map<String, Object> responseMap = tntEventSubscriptionToService.findById(UUID.fromString(subscriptionId), response, request, checkListItem);
        customLogger.log(checkListItem, responseMap, response, request);
        return responseMap;
    }

    @PutMapping({EVENT_SUBSCRIPTION+"/{subscriptionId}"})
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> update(@PathVariable String subscriptionId, @RequestBody Object obj, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = "/event-subscriptions/{id}";
        CheckListItem checkListItem = ConfigService.getNextCheckListItem(route, response, request);
        customLogger.init(obj, response, request, checkListItem, route);
        Map<String, Object> responseMap = tntEventSubscriptionToService.update(UUID.fromString(subscriptionId), JsonUtility.convertTo(TNTEventSubscriptionTO.class, obj), response, request, checkListItem);
        customLogger.log(checkListItem, responseMap, response, request);
        return responseMap;
    }

    @DeleteMapping({EVENT_SUBSCRIPTION+"/{subscriptionId}"})
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable String subscriptionId, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = "/event-subscriptions/{subscriptionId}";
        CheckListItem checkListItem = ConfigService.getNextCheckListItem(route, response, request);
        customLogger.init(null, response, request, checkListItem, route);
        tntEventSubscriptionToService.delete(UUID.fromString(subscriptionId), response, request, checkListItem);
        customLogger.log(checkListItem, null, response, request);
    }
    @GetMapping("/callback/{id}")
    public ResponseEntity<Map<String, Object>> callback(@PathVariable String id, ServerHttpResponse response, ServerHttpRequest request)
            throws ExecutionException, InterruptedException, JsonProcessingException {
        return tntEventSubscriptionToService.callCallback(UUID.fromString(id),response, request);
    }

}
