package org.dcsa.ctk.consumer.mock.service;

import org.dcsa.core.events.model.enums.EquipmentEventTypeCode;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.dcsa.core.events.model.enums.TransportDocumentTypeCode;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.dcsa.core.validator.EnumSubset;
import org.dcsa.core.validator.ValidEnum;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public interface EventControllerService {
    List<Map<String, Object>> findAll(@EnumSubset(anyOf = {"SHIPMENT", "TRANSPORT", "EQUIPMENT"})
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
                        ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException;
}
