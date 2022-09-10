package org.dcsa.ctk.consumer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.dcsa.ctk.consumer.service.callback.ConsumerCallbackService;
import org.dcsa.ctk.consumer.service.config.impl.ConfigService;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.dcsa.ctk.consumer.controller.CallBackController.API_VERSION;

@RestController
@RequestMapping(path = API_VERSION, produces = {MediaType.APPLICATION_JSON_VALUE})
public class CallBackController {
    public static final String API_VERSION = "/v2";
    final ConsumerCallbackService consumerCallbackService;

    public CallBackController( ConsumerCallbackService consumerCallbackService) {
        this.consumerCallbackService = consumerCallbackService;
    }

    @PostMapping({"/check/callback/{id}"})
    public ResponseEntity<String> checkCallback(@PathVariable String id, @RequestBody Object obj, ServerHttpResponse response, ServerHttpRequest request)
            throws ExecutionException, InterruptedException, JsonProcessingException {
       return consumerCallbackService.checkCallback(UUID.fromString(id), JsonUtility.convertTo(TNTEventSubscriptionTO.class, obj),
               response, request, ConfigService.checkListItemMap);
    }

}
