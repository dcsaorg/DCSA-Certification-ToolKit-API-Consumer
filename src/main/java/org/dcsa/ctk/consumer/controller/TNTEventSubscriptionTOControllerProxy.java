package org.dcsa.ctk.consumer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.reporter.CustomReporter;
import org.dcsa.ctk.consumer.service.config.impl.ConfigService;
import org.dcsa.ctk.consumer.service.log.CustomLogger;
import org.dcsa.ctk.consumer.service.tnt.TNTEventSubscriptionToService;
import org.dcsa.ctk.consumer.util.FileUtility;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/v2", produces = {MediaType.APPLICATION_JSON_VALUE})
public class TNTEventSubscriptionTOControllerProxy {

    final TNTEventSubscriptionToService<Map<String, Object>> tntEventSubscriptionToService;

    final CustomReporter customReporter;

    final CustomLogger customLogger;

    @PostMapping("/event-subscriptions")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@RequestBody Object obj, ServerHttpResponse response, ServerHttpRequest request) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route = "/event-subscriptions";
        CheckListItem checkListItem = ConfigService.getNextCheckListItem(route, response, request);
        customLogger.init(obj, response, request, checkListItem, route);
        Map<String, Object> responseMap = tntEventSubscriptionToService.create(JsonUtility.convertTo(TNTEventSubscriptionTO.class, obj), response, request, checkListItem);
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

    @GetMapping(value = "/report/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> download() throws IOException {
        String fileName = customReporter.generateTestReport("reports");

        ByteArrayResource resource = FileUtility.getFile("reports/" +
                fileName);
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(header)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }


}
