package org.dcsa.ctk.consumer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.ctk.consumer.config.AppProperty;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.model.EventSubscription;
import org.dcsa.ctk.consumer.reporter.ExtentReportManager;
import org.dcsa.ctk.consumer.reporter.Reporter;
import org.dcsa.ctk.consumer.service.config.impl.ConfigService;
import org.dcsa.ctk.consumer.service.log.CustomLogger;
import org.dcsa.ctk.consumer.service.tnt.TNTEventSubscriptionToService;
import org.dcsa.ctk.consumer.util.APIUtility;
import org.dcsa.ctk.consumer.util.FileUtility;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.dcsa.ctk.consumer.util.SqlUtility;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


@RestController
@Slf4j
@RequestMapping(value = "/v2", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TNTEventSubscriptionTOControllerProxy {
    final TNTEventSubscriptionToService<Map<String, Object>> tntEventSubscriptionToService;

    final Reporter reporter;

    final CustomLogger customLogger;

    final AppProperty appProperty;

    public TNTEventSubscriptionTOControllerProxy(TNTEventSubscriptionToService<Map<String, Object>> tntEventSubscriptionToService, Reporter reporter, CustomLogger customLogger, AppProperty appProperty) throws Exception {
        this.tntEventSubscriptionToService = tntEventSubscriptionToService;
        this.reporter = reporter;
        this.customLogger = customLogger;
        this.appProperty = appProperty;
        if(AppProperty.PUB_SUB_FLAG){
           APIUtility.runWebHook();
        }
    }

    @PostMapping("/event-subscriptions")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@RequestBody Object obj, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = "/event-subscriptions";
        CheckListItem checkListItem = ConfigService.getNextCheckListItem(route, response, request);
        customLogger.init(obj, response, request, checkListItem, route);
        EventSubscription eventSubscription = APIUtility.getEventSubscription(obj);
        Map<String, Object> responseMap = tntEventSubscriptionToService.create(JsonUtility.convertTo(TNTEventSubscriptionTO.class, obj),
                                            response, request, checkListItem, eventSubscription);
        customLogger.log(responseMap, response, request);
        return responseMap;
    }

    @GetMapping("/event-subscriptions")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> findAll(ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = "/event-subscriptions";
        CheckListItem checkListItem = ConfigService.getNextCheckListItem(route, response, request);
        customLogger.init(null, response, request, checkListItem, route);
        List<Map<String, Object>> responseList = tntEventSubscriptionToService.findAll(response, request, checkListItem);
        customLogger.log(responseList, response, request);
        return responseList;
    }

    @PutMapping({"/event-subscriptions/{id}/secret"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSecret(@PathVariable String id, @RequestBody Object obj, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = "/event-subscriptions/{id}/secret";
        CheckListItem checkListItem = ConfigService.getNextCheckListItem(route, response, request);
        //SqlUtility.updateSecretBySubscriptionId(id, (((LinkedHashMap<?, ?>) obj).get((("secret"))).toString()));
        customLogger.init(obj, response, request, checkListItem, route);
        tntEventSubscriptionToService.updateSecret(UUID.fromString(id), JsonUtility.convertTo(EventSubscriptionSecretUpdateTO.class, obj), response, request, checkListItem);
        customLogger.log(null, response, request);
    }

    @GetMapping({"/event-subscriptions/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> findById(@PathVariable String id, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = "/event-subscriptions/{id}";
        CheckListItem checkListItem = ConfigService.getNextCheckListItem(route, response, request);
        customLogger.init(null, response, request, checkListItem, route);
        Map<String, Object> responseMap = tntEventSubscriptionToService.findById(UUID.fromString(id), response, request, checkListItem);
        customLogger.log(responseMap, response, request);
        return responseMap;
    }

    @PutMapping({"/event-subscriptions/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> update(@PathVariable String id, @RequestBody Object obj, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = "/event-subscriptions/{id}";
        CheckListItem checkListItem = ConfigService.getNextCheckListItem(route, response, request);
        customLogger.init(obj, response, request, checkListItem, route);
        Map<String, Object> responseMap = tntEventSubscriptionToService.update(UUID.fromString(id), JsonUtility.convertTo(TNTEventSubscriptionTO.class, obj), response, request, checkListItem);
        customLogger.log(responseMap, response, request);
        return responseMap;
    }

    @DeleteMapping({"/event-subscriptions/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable String id, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = "/event-subscriptions/{id}";
        CheckListItem checkListItem = ConfigService.getNextCheckListItem(route, response, request);
        customLogger.init(null, response, request, checkListItem, route);
        tntEventSubscriptionToService.delete(UUID.fromString(id), response, request, checkListItem);
        customLogger.log(null, response, request);
    }

    @GetMapping(value = "/download/report", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Object> downloadReport() throws IOException {
        HttpHeaders header = new HttpHeaders();
        String fileName = reporter.generateHtmlTestReport();
        ByteArrayResource resource = FileUtility.getFile(fileName);
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + ExtentReportManager.getReportName());
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(header)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

}
