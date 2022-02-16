package org.dcsa.ctk.consumer.service.tnt.impl;

import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.enums.EquipmentEventTypeCode;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.dcsa.core.events.model.enums.TransportDocumentTypeCode;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.dcsa.core.validator.EnumSubset;
import org.dcsa.core.validator.ValidEnum;
import org.dcsa.ctk.consumer.constant.CheckListStatus;
import org.dcsa.ctk.consumer.constant.ResponseMockType;
import org.dcsa.ctk.consumer.exception.DecoratorException;
import org.dcsa.ctk.consumer.service.decorator.Decorator;
import org.dcsa.ctk.consumer.service.tnt.EventControllerService;
import org.dcsa.ctk.consumer.service.mock.MockService;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.util.APIUtility;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.dcsa.tnt.controller.EventController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@Validated
public class EventControllerServiceImpl implements EventControllerService {

    @Autowired
    EventController eventController;

    @Autowired
    Decorator<Map<String, Object>>  mapDecorator;

    @Autowired
    Decorator<List<Map<String, Object>> >  listDecorator;

    @Autowired
    MockService mockService;

    @Override
    public List<Map<String, Object>> findAll(
            @EnumSubset(anyOf = {"SHIPMENT", "TRANSPORT", "EQUIPMENT"})
                    String eventType,
            @ValidEnum(clazz = ShipmentEventTypeCode.class)
                    String shipmentEventTypeCode,
            @Size(max = 35)
                    String carrierBookingReference,
            @Size(max = 20)
                    String transportDocumentReference,
            @ValidEnum(clazz = TransportDocumentTypeCode.class)
                    String transportDocumentTypeCode,
            @ValidEnum(clazz = TransportEventTypeCode.class)
                    String transportEventTypeCode,
            @Size(max = 100)
                    String transportCallID,
            @Size(max = 7)
                    String vesselIMONumber,
            @Size(max = 50)
                    String carrierVoyageNumber,
            @Size(max = 5)
                    String carrierServiceCode,
            @ValidEnum(clazz = EquipmentEventTypeCode.class)
                    String equipmentEventTypeCode,
            @Size(max = 15)
                    String equipmentReference,
            @Min(1) int limit,
            ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        List<Map<String, Object>> responseList = null;
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            List<Event> actualResponse = eventController.findAll(response, request).collectList().toFuture().get();
            responseList = listDecorator.decorate(JsonUtility.convertToList(actualResponse), response, request, checkListItem);
            if (checkListItem != null)
                checkListItem.setStatus(CheckListStatus.COVERED);
            return responseList;
        } else {
            Map<String, Object> responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request );
            responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
            checkListItem.setStatus(CheckListStatus.COVERED);
            throw new DecoratorException(responseMap);
        }
    }
}
