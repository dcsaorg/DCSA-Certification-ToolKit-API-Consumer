package org.dcsa.ctk.consumer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.dcsa.ctk.consumer.mock.service.impl.ConfigService;
import org.dcsa.ctk.consumer.mock.service.CustomLogger;
import org.dcsa.ctk.consumer.mock.service.EventControllerService;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/proxy/events", produces = {MediaType.APPLICATION_JSON_VALUE})
public class EventControllerProxy {
   @Autowired
   EventControllerService eventControllerService;

   @Autowired
   ConfigService configService;
   @Autowired
   CustomLogger customLogger;


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
        String route="/proxy/events";
        CheckListItem checkListItem=configService.getNextCheckListItem(route,response,request);
        customLogger.init(null,response,request,checkListItem, route);
        List<Map<String, Object>> result =
                eventControllerService.findAll(
                eventType,shipmentEventTypeCode,carrierBookingReference,transportDocumentReference,transportDocumentTypeCode
                ,transportEventTypeCode,transportCallID,vesselIMONumber,carrierVoyageNumber,carrierServiceCode,equipmentEventTypeCode,equipmentReference,limit,
                response, request,checkListItem);
        customLogger.log(result,response,request);
        return result;

    }

}
