package org.dcsa.ctk.consumer.model.enums;

import java.util.Arrays;

public enum UploadType {
    Carrier("carrier"),
    Facility("facility"),
    EventSubscriptionEquipmentEventType("eventSubscriptionEquipmentEventType"),
    Country("country"),
    FacilityType("facilityType"),
    EquipmentCode("equipmentCode"),
    UnLocation("unLocation"),
    ModeOfTransport("modeOfTransport"),
    ShipmentLocationType("shipmentLocationType"),
    ShipmentEventType("shipmentEventType"),
    EventClassifier("eventClassifier"),
    TransportEventType("transportEventType"),
    SmdgDelayReason("smdgDelayReason"),
    EmptyIndicator("emptyIndicator"),
    EquipmentEventType("equipmentEventType"),
    EquipmentEvent("equipmentEvent"),
    DocumentType("documentType"),
    TransportDocumentType("transportDocumentType"),
    ReferenceType("referenceType"),
    ReceiptDeliveryType("receiptDeliveryType"),
    Booking("booking"),
    Event("event"),
    ShipmentEvent("shipmentEvent"),
    TransportCall("transportCall"),
    OperationsEvent("operationsEvent"),
    JsonEvent("jsonEvent"),
    JsonShipmentEvent("jsonShipmentEvent"),
    JsonBooking("jsonBooking"),
    JsonEquipmentEvent("jsonEquipmentEvent"),
    JsonTransportCall("jsonTransportCall"),
    JsonOperationsEvent("jsonOperationsEvent"),
    JsonTransportEvent("jsonTransportEvent"),
    JsonFullShipment("jsonFullShipment");

    private String value;

    UploadType(String value) {
        this.value = value;
    }

    public static UploadType fromValue(String value) {
        for (UploadType uploadType : values()) {
            if (uploadType.value.equalsIgnoreCase(value)) {
                return uploadType;
            }
        }
        throw new IllegalArgumentException(
                "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
    }
}
