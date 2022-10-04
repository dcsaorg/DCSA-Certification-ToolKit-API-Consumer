package org.dcsa.ctk.consumer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.ctk.consumer.config.AppProperty;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.model.enums.ValidationRequirementID;
import org.dcsa.ctk.consumer.reporter.Reporter;
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

    final Reporter reporter;

    final CustomLogger customLogger;

    final AppProperty appProperty;

    public TNTEventSubscriptionTOControllerProxy(TNTEventSubscriptionToService<Map<String, Object>> tntEventSubscriptionToService, Reporter reporter, CustomLogger customLogger, AppProperty appProperty) throws Exception {
        this.tntEventSubscriptionToService = tntEventSubscriptionToService;
        this.reporter = reporter;
        this.customLogger = customLogger;
        this.appProperty = appProperty;
        APIUtility.runWebHook();
    }

    @PostMapping(EVENT_SUBSCRIPTION)
    public ResponseEntity< Map<String, Object>> create(@RequestBody Object obj, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        Map<String, Object> responseMap = tntEventSubscriptionToService.create(JsonUtility.convertTo(TNTEventSubscriptionTO.class, obj), response, request);
        if(responseMap.get("subscriptionID") != null){
            return new ResponseEntity<>(responseMap, HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(EVENT_SUBSCRIPTION)
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> findAll(ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        return tntEventSubscriptionToService.findAll(response, request);
    }

    @PutMapping({EVENT_SUBSCRIPTION+"/{id}/secret"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSecret(@PathVariable String id, @RequestBody Object obj, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        tntEventSubscriptionToService.updateSecret(UUID.fromString(id), JsonUtility.convertTo(EventSubscriptionSecretUpdateTO.class, obj), response, request);
    }

    @GetMapping({EVENT_SUBSCRIPTION+"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> findById(@PathVariable String id, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = "/event-subscriptions/{id}";
        CheckListItem checkListItem = ConfigService.getCheckListItem(route, response, request);
        customLogger.init(null, response, request, checkListItem, route);
        Map<String, Object> responseMap = tntEventSubscriptionToService.findById(UUID.fromString(id), response, request, checkListItem);
        customLogger.log(responseMap, response, request);
        return responseMap;
    }

    @PutMapping({EVENT_SUBSCRIPTION+"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> update(@PathVariable String id, @RequestBody Object obj, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        Map<String, Object> responseMap = tntEventSubscriptionToService.update(UUID.fromString(id), JsonUtility.convertTo(TNTEventSubscriptionTO.class, obj), response, request);
        return responseMap;
    }

    @DeleteMapping({EVENT_SUBSCRIPTION+"/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable String id, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = "/event-subscriptions/{id}";
        CheckListItem checkListItem = ConfigService.getCheckListItem(route, response, request);
        customLogger.init(null, response, request, checkListItem, route);
        tntEventSubscriptionToService.delete(UUID.fromString(id), response, request, checkListItem);
        customLogger.log(null, response, request);
    }

}
