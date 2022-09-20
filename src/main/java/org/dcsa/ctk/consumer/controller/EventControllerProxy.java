package org.dcsa.ctk.consumer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.dcsa.ctk.consumer.constant.CheckListStatus;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.model.enums.ValidationRequirementID;
import org.dcsa.ctk.consumer.service.config.impl.ConfigService;
import org.dcsa.ctk.consumer.service.decorator.Decorator;
import org.dcsa.ctk.consumer.service.log.CustomLogger;
import org.dcsa.ctk.consumer.service.tnt.EventControllerService;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v2/events", produces = {MediaType.APPLICATION_JSON_VALUE})
public class EventControllerProxy {
   final EventControllerService eventControllerService;
   final CustomLogger customLogger;

    final Decorator<Map<String, Object>> mapDecorator;

    @GetMapping
    public List<Map<String, Object>>  findAll(
            @RequestParam(value = "eventType", required = false)
                    String eventType,
            @RequestParam(value = "shipmentEventTypeCode", required = false)
                    String shipmentEventTypeCode,
            @RequestParam(value = "carrierBookingReference", required = false)
                    String carrierBookingReference,
            @RequestParam(value = "transportDocumentReference", required = false)
                    String transportDocumentReference,
            @RequestParam(value = "transportDocumentTypeCode", required = false)
                    String transportDocumentTypeCode,
            @RequestParam(value = "transportEventTypeCode", required = false)
                    String transportEventTypeCode,
            @RequestParam(value = "transportCallID", required = false)
                    String transportCallID,
            @RequestParam(value = "vesselIMONumber", required = false)
                    String vesselIMONumber,
            @RequestParam(value = "carrierVoyageNumber", required = false)
                    String carrierVoyageNumber,
            @RequestParam(value = "carrierServiceCode", required = false)
                    String carrierServiceCode,
            @RequestParam(value = "equipmentEventTypeCode", required = false)
                    String equipmentEventTypeCode,
            @RequestParam(value = "equipmentReference", required = false)
                    String equipmentReference,
            @RequestParam(value = "limit", defaultValue = "1", required = false)
                    int limit,
            ServerHttpResponse response,
            ServerHttpRequest request
    ) throws ExecutionException, InterruptedException, JsonProcessingException {
        String route="/events";
        CheckListItem checkListItem= ConfigService.getCheckListItem(route,response,request);
        customLogger.init(null,response,request,checkListItem, route);
        List<Map<String, Object>> result =
                eventControllerService.findAll(
                eventType,shipmentEventTypeCode,carrierBookingReference,transportDocumentReference,transportDocumentTypeCode
                ,transportEventTypeCode,transportCallID,vesselIMONumber,carrierVoyageNumber,carrierServiceCode,equipmentEventTypeCode,equipmentReference,limit,
                response, request,checkListItem);
        customLogger.log(result,response,request);
        return result;
    }

    private void checkApiVersion(TNTEventSubscriptionTO tntEventSubscriptionTO, ServerHttpResponse response, ServerHttpRequest request ) throws JsonProcessingException {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String route = "/event-subscriptions";
        var v = request.getHeaders().get("X-Api-Key");
        AtomicBoolean correctApiVersion = new AtomicBoolean(false);
        request.getHeaders().get("X-Api-Key").forEach( e -> {
            if(e.equalsIgnoreCase("2")){
                correctApiVersion.set(true);
            }
        });
        if(correctApiVersion.get()){
            CheckListItem checkListItem = ConfigService.getCheckListItem(route, "GET", ValidationRequirementID.TNT_2_2_API_PRV_1.getValue());
            if (checkListItem != null) {
                customLogger.init(tntEventSubscriptionTO, response, request, checkListItem, route);
                responseMap = mapDecorator.decorate(JsonUtility.convertToMap(tntEventSubscriptionTO), response, request, checkListItem);
                checkListItem.setStatus(CheckListStatus.CONFRONTED);
            }
            customLogger.log(responseMap, response, request);
        }
    }

}
