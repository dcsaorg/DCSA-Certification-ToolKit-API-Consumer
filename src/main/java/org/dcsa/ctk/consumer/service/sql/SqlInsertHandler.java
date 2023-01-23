package org.dcsa.ctk.consumer.service.sql;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.java.Log;
import org.dcsa.core.events.model.Booking;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.Seal;
import org.dcsa.core.events.model.enums.EmptyIndicatorCode;
import org.dcsa.ctk.consumer.model.*;
import org.dcsa.ctk.consumer.model.enums.UploadType;
import org.dcsa.ctk.consumer.util.EventTimeUtility;
import org.dcsa.ctk.consumer.util.FileUtility;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.dcsa.ctk.consumer.util.SqlUtility;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Log
@Service
public class SqlInsertHandler {

    private static final String EVENT_INSERT_INTO = "INSERT INTO dcsa_im_v3_0.event (\n" +
                                                    "event_id,\n" +
                                                    "event_classifier_code,\n" +
                                                    "event_date_time\n" +
                                                    ") VALUES (\n";

    private static final String BOOKING_INSERT_INTO = "INSERT INTO dcsa_im_v3_0.booking (\n" +
                                                        "carrier_booking_reference,\n" +
                                                        "receipt_delivery_type_at_origin,\n" +
                                                        "receipt_delivery_type_at_destination,\n" +
                                                        "cargo_movement_type_at_origin,\n" +
                                                        "cargo_movement_type_at_destination,\n" +
                                                        "booking_request_datetime,\n" +
                                                        "service_contract,\n" +
                                                        "cargo_gross_weight,\n" +
                                                        "cargo_gross_weight_unit,\n" +
                                                        "commodity_type\n" +
                                                        ") VALUES (\n";

    private static final String SHIPMENT_EVENT_INSERT_INTO = "INSERT INTO dcsa_im_v3_0.shipment_event\n" +
                                                            "(event_id, \n" +
                                                            "event_classifier_code, \n" +
                                                            "event_created_date_time, \n" +
                                                            "event_date_time, \n" +
                                                            "document_id, \n" +
                                                            "shipment_event_type_code, \n" +
                                                            "document_type_code, \n" +
                                                            "reason) \n" +
                                                          //  "document_reference) \n" +
                                                            "VALUES(";

    private static final String TRANSPORT_CALL_INSERT_INTO = "INSERT INTO dcsa_im_v3_0.transport_call\n" +
                                                            "(id, \n" +
                                                          //  "transport_call_reference, \n"+
                                                            "transport_call_sequence_number, \n" +
                                                            "facility_id, \n" +
                                                            "facility_type_code, \n" +
                                                            "other_facility, \n" +
                                                            "location_id, \n" +
                                                            "mode_of_transport, \n" +
                                                            "vessel_imo_number)\n" +
                                                            "VALUES(";

    private static final String EQUIPMENT_EVENT_INSERT_INTO = "INSERT INTO dcsa_im_v3_0.equipment_event (\n" +
                                                                "event_id,\n" +
                                                                "event_classifier_code,\n" +
                                                                "event_created_date_time,\n" +
                                                                "event_date_time,\n" +
                                                                "equipment_reference,\n" +
                                                                "empty_indicator_code,\n" +
                                                                "transport_call_id,\n" +
                                                                "equipment_event_type_code) VALUES (";

    private static final String OPERATIONS_EVENT_INSERT_INTO = "INSERT INTO dcsa_im_v3_0.operations_event(\n" +
                                                                "event_id,\n" +
                                                                "event_classifier_code, \n" +
                                                                "event_created_date_time,\n" +
                                                                "event_date_time,\n" +
                                                                "operations_event_type_code,\n" +
                                                                "transport_call_id,\n" +
                                                                "publisher, \n" +
                                                                "publisher_role,\n" +
                                                                "port_call_service_type_code,\n" +
                                                                "event_location,\n" +
                                                                "facility_type_code,\n" +
                                                                "delay_reason_code,\n" +
                                                                "vessel_position,\n" +
                                                                "remark)\n" +
                                                                "VALUES (";

    private static final String REFERENCES_INSERT_INTO ="INSERT INTO dcsa_im_v3_0.\"references\"\n" +
                                                        "(reference_type, \n" +
                                                        "reference_value, \n" +
                                                        "shipment_id, \n" +
                                                        "shipping_instruction_id, \n" +
                                                        "id)\n" +
                                                        "VALUES(";

    private static final String SEAL_INSERT_INTO = "INSERT INTO dcsa_im_v3_0.seal\n" +
                                                    "(shipment_equipment_id, \n" +
                                                    "seal_number, \n" +
                                                    "seal_source, \n" +
                                                    "seal_type, \n" +
                                                    "id)\n" +
                                                    "VALUES(";

    private static final String VESSEL_INSERT_INTO = "INSERT INTO dcsa_im_v3_0.vessel\n" +
                                                    "(vessel_imo_number, \n" +
                                                    "vessel_name, \n" +
                                                    "vessel_flag, \n" +
                                                    "vessel_call_sign_number, \n" +
                                                    "vessel_operator_carrier_id)\n" +
                                                    "VALUES(";

    private static final String TRANSPORT_EVENT_INSERT_INTO = "INSERT INTO dcsa_im_v3_0.transport_event\n" +
                                                            "(event_id, \n" +
                                                            "event_classifier_code, \n" +
                                                            "event_created_date_time, \n" +
                                                            "event_date_time, \n" +
                                                            "delay_reason_code, \n" +
                                                            "change_remark, \n" +
                                                            "transport_call_id, \n" +
                                                            "transport_event_type_code)\n" +
                                                            "VALUES(";

    private final ObjectMapper mapper = new ObjectMapper().
                                        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;

    public SqlInsertHandler() {
        mapper.registerModule(new JavaTimeModule());
    }
    public String insertJsonSqlData(String json, UploadType uploadType) {
        //String json = FileUtility.loadFileAsString(uploadFile);
       String result = "";
            switch (uploadType) {
                case JsonEvent -> {
                  result = processJsonEvent(json);
                }
            case JsonBooking -> {
                result = processJsonBooking(json);
            }
            case JsonShipmentEvent -> {
                result = processJsonShipmentEvent(json);
            }
            case JsonEquipmentEvent -> {
                result = processEquipmentEvent(json);
            }
            case JsonTransportCall -> {
                result = processJsonTransportCall(json);
            }
            case JsonOperationsEvent -> {
                result = processJsonOperationsEvent(json);
            }
            case JsonTransportEvent ->{
                result = processJsonTransportEvent(json);
            }
            case JsonFullShipment -> {
                result = processJsonFullShipmentEvent(json);
            }
        }
        return result;
    }

    public String getJsonData(UploadType type, String timeOffset) {
        String jsonString = "";
        switch (type) {
            case JsonEvent -> {
            }
            case Country -> {
                jsonString = getJsonCountry();
            }
            case EmptyIndicator -> {
                jsonString = getJsonEmptyIndicator();
            }
            case JsonFullShipment -> {
                jsonString = getJsonFullShipment(timeOffset);
            }
        }
        return jsonString;
    }
    private String getJsonCountry() {
        Country country = new Country();
        country.setCountryName("Netherlands");
        country.setCountryCode("NL");
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(country);
        } catch (Exception e) {
            log.warning("failed to get country json");
        }
        return jsonString;
    }

    private String getJsonFullShipment(String timeOffset){
        String fullShipmentJson = FileUtility.loadResourceAsString("fullShipment.json");
        try {
            FullShipment fullShipment = mapper.readValue(fullShipmentJson, FullShipment.class);
           if(!timeOffset.isBlank()) {
               fullShipment = EventTimeUtility.fullShipmentUpdateDate(fullShipment, timeOffset);
               fullShipmentJson = JsonUtility.getStringFormObject(fullShipment);
           }
        } catch (Exception e) {
            log.warning("failed process fullShipment: " + e.getMessage());
            e.printStackTrace();
            return "failed process fullShipment: " + e.getMessage();
        }
        return fullShipmentJson;
    }

    private String getJsonEmptyIndicator() {
        EmptyIndicator emptyIndicator = new EmptyIndicator();
        emptyIndicator.setEmptyIndicatorCode(EmptyIndicatorCode.LADEN);
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(emptyIndicator);
        } catch (Exception e) {
            log.warning("failed to get emptyIndicator json");
            return "failed to get emptyIndicator json";
        }
        StringBuilder emptyIndicationCode = new StringBuilder(jsonString);
        StringBuilder comment  = new StringBuilder(",\n\"_comment\": \"possible emptyIndicatorCode values are  ");
        EmptyIndicatorCode[] enums = EmptyIndicatorCode.values();
        for(int i=0; i < enums.length; i++){
            comment.append(enums[i]+", ");
            comment.append(" ");
        }
        comment.append("\"}");
        return emptyIndicationCode.replace(emptyIndicationCode.length()-1, emptyIndicationCode.length(), comment.toString()).toString();
    }

    private String processJsonEvent(String json) {
        try {
            Event event = mapper.readValue(json, Event.class);
             return insetIntoEvent(event);
        } catch (Exception e) {
            log.warning("failed process event: " + e.getMessage());
            return "failed process event: " + e.getMessage();
        }
    }

    private String processJsonShipmentEvent(String json) {
        String result = "";
        try {
            ShipmentEvent shipmentEvent = mapper.readValue(json, ShipmentEvent.class);
            result = insetIntoShipmentEvent(shipmentEvent);
        } catch (Exception e) {
            log.warning("failed process event: " + e.getMessage());
            result = "failed process event: " + e.getMessage();
        }
        return result;
    }

    private String insetIntoEvent(Event event) throws Exception {
        String eventSql = EVENT_INSERT_INTO + StringUtils.quote(event.getEventType().toString()) + "," +
                StringUtils.quote(event.getEventClassifierCode().name()) + "," +
                event.getEventDateTime() + ")";
        int rows = SqlUtility.updateRow(eventSql);
        if (rows > 0) {
            log.log(Level.INFO, "A new event has been inserted. "+event.getEvent_id().toString());
            return "\nA new event has been inserted "+event.getEvent_id().toString();
        } else {
            throw new Exception("Json file processing exception for event " + event.getEvent_id().toString());
        }
    }
    private String insetIntoShipmentEvent(ShipmentEvent shipmentEvent) {
       // String shipmentId =  SqlUtility.getShipmentIdByCarrierBookingReference(shipmentEvent.getDocumentID());
        String result = "";
        String shipmentEventSql = SHIPMENT_EVENT_INSERT_INTO + StringUtils.quote(shipmentEvent.getEvent_id().toString()) + "," +
                                StringUtils.quote(shipmentEvent.getEventClassifierCode().name()) + "," +
                                "TIMESTAMP "+StringUtils.quote(shipmentEvent.getEventCreatedDateTime().toString()) + "," +
                                "TIMESTAMP "+StringUtils.quote(shipmentEvent.getEventDateTime().toString()) + "," +
                                StringUtils.quote(shipmentEvent.getDocumentID())+ "," +
                                StringUtils.quote(shipmentEvent.getShipmentEventTypeCode().toString()) + "," +
                                StringUtils.quote(shipmentEvent.getDocumentTypeCode().toString()) + "," +
                                StringUtils.quote(shipmentEvent.getReason()) + ")";
        int rows = SqlUtility.updateRow(shipmentEventSql);
        if (rows > 0) {
            log.log(Level.INFO, "A new shipmentEvent has been inserted.");
            result = "A new shipmentEvent is inserted with id: " + shipmentEvent.getEvent_id().toString();
        } else {
            result = "shipmentEvent processing exception for shipmentEvent " + shipmentEvent.getEvent_id().toString();
        }
        return  result;
    }

    private String processJsonBooking(String json) {
       String result = "";
        try {
            Booking booking = mapper.readValue(json, Booking.class);
            result = insetIntoBooking(booking);
        } catch (Exception e) {
            log.warning("failed process event: " + e.getMessage());
            result = "failed process event: " + e.getMessage();
        }
        return result;
    }

    private String processEquipmentEvent(String json) {
        StringBuilder result = new StringBuilder();
        try {
            EquipmentEvent equipmentEvent = mapper.readValue(json, EquipmentEvent.class);
            result.append(processEquipmentEvent(equipmentEvent));
        } catch (Exception e) {
            log.warning("failed process event: " + json);
            return "failed process "+e.getMessage();
        }
        return result.toString();
    }

    private String processEquipmentEvent(EquipmentEvent equipmentEvent) {
        AtomicReference<StringBuffer> result = new AtomicReference<>(new StringBuffer());
        try {
            var references =  equipmentEvent.getReferences();
            references.forEach( reference -> {
                reference.setReferenceID(UUID.randomUUID());
                SqlUtility.checkDeleteShipmentIfExist(reference.getShipmentID());
                if(reference.getShipmentID() == null){
                    reference.setShipmentID(SqlUtility.getShipmentIdByReferenceType(reference.getReferenceType().name()));
                }
                if( !reference.getShipmentID().isEmpty()) {
                    if (!SqlUtility.isShipmentExist(reference.getShipmentID())) {
                        result.get().append(SqlUtility.insertShipment(reference.getShipmentID(),
                                equipmentEvent.getEventCreatedDateTime().toString(),
                                equipmentEvent.getEventCreatedDateTime().toString()));
                    }
                    try {
                        if(!SqlUtility.isReferenceExist(reference.getShipmentID())) {
                            result.get().append(insetIntoReferences(reference));
                        }
                    } catch (Exception e) {
                        log.severe("Failed to insert into equipmentEvent");
                        result.get().append("Failed to insert into equipmentEvent ").append(e.getMessage());
                    }
                }
            });
            var seals =  equipmentEvent.getSeals();
            seals.forEach(seal -> {
                seal.setId(UUID.randomUUID());
                try {
                    if(seal.getShipmentEquipmentID() != null && SqlUtility.isShipmentEquipmentExist(seal.getShipmentEquipmentID().toString()) ){
                        result.get().append(insetIntoSeal(seal));
                    }
                } catch (Exception e) {
                    log.severe("Failed to insert into seal");
                    result.get().append("Failed to insert into seal ").append(e.getMessage());
                }
            });
            TransportCall transportCall = equipmentEvent.getTransportCall();
            if(transportCall.getTransportCallID() == null){
                transportCall.setTransportCallID(UUID.randomUUID());
            }
            result.get().append(processTransportCall(transportCall));
            if(SqlUtility.isEquipmentEventExist(equipmentEvent.getEvent_id().toString())){
                result.get().append("\nThe equipmentEvent already existed with eventId: ").append(equipmentEvent.getEvent_id().toString()).append(". ");
            }else {
                if(SqlUtility.isEquipmentReferenceExist(equipmentEvent.getEquipmentReference())){
                    if(SqlUtility.isTransportCallIdExist(equipmentEvent.getTransportCallID().toString())){
                        result.get().append(insetIntoEquipmentEvent(equipmentEvent));
                    }else{
                        result.get().append("TransportCallID "+equipmentEvent.getTransportCallID().toString()).append(" does not exist in TransportCall table");
                    }

                }else{
                    result.get().append("equipment_reference not exist in equipment");
                }

            }
        } catch (Exception e) {
            log.warning("failed process event: " + equipmentEvent.getEvent_id().toString());
            return "failed process "+e.getMessage();
        }
        return result.get().toString();
    }
    private String processJsonTransportCall(String json) {
        StringBuilder result = new StringBuilder();
        try {
            TransportCall transportCall = mapper.readValue(json, TransportCall.class);
            if(transportCall.getId() == null){
                transportCall.setTransportCallID(UUID.randomUUID());
            }
            result.append(processTransportCall(transportCall));
        } catch (Exception e) {
            log.warning("failed process event: " + json);
            result.append("failed process event: ").append(json);
        }
        return result.toString();
    }

    private String processTransportCall(TransportCall transportCall) throws Exception {
        StringBuilder result = new StringBuilder();
        SqlUtility.checkDeleteTransportCallIfExist((transportCall.getId().toString()));
        if(transportCall.getTransportCallID() != null){
            if(SqlUtility.isFacilityIdExist(transportCall.getFacilityID())){
                result.append(insetIntoTransportCall(transportCall));
            }else{
                result.append("\nfacility id "+transportCall.getFacilityID()).append(" does not exit.\n");
            }
        }else{
            result.append("TransportCall has null facility_id");
        }

        return result.toString();
    }

    private String processJsonOperationsEvent(String json) {
        String result = "";
        try {
            OperationsEvent operationsEvent = mapper.readValue(json, OperationsEvent.class);
            result = insetIntoOperationsEvent(operationsEvent);
        } catch (Exception e) {
            log.warning("failed process event: " + json);
            result = "failed process event: " + json;
        }
        return result;
    }

    private String processJsonTransportEvent(String json) {
        String result = "";
        try {
            TransportEvent transportEvent = mapper.readValue(json, TransportEvent.class);
            result = insetIntoTransportEvent(transportEvent);
        } catch (Exception e) {
            log.warning("\nfailed process transportEvent: " + json);
            result = "failed process transportEvent: " + json;
        }
        return result;
    }

    private String insetIntoBooking(Booking booking) throws Exception {
        String bookingSql = BOOKING_INSERT_INTO + StringUtils.quote(booking.getId()) + "," +
                StringUtils.quote(booking.getReceiptDeliveryTypeAtOrigin().name()) + "," +
                StringUtils.quote(booking.getReceiptDeliveryTypeAtDestination().name()) + "," +
                StringUtils.quote(booking.getCargo_movement_type_at_origin().name()) + "," +
                StringUtils.quote(booking.getCargo_movement_type_at_destination().name()) + "," +
                booking.getBookingDateTime() + "," +
                StringUtils.quote(booking.getServiceContract()) + "," +
                booking.getCargoGrossWeight() + "," +
                StringUtils.quote(booking.getCargoGrossWeightUnit().name()) + "," +
                StringUtils.quote(booking.getCommodityType()) + ")";
        int rows = SqlUtility.updateRow(bookingSql);
        if (rows > 0) {
            log.log(Level.INFO, "A new booking has been inserted.");
            return "\nA new booking has been inserted. "+booking.getId();
        } else {
            throw new Exception("Json file processing exception for booking " + booking.getId());
        }
    }

    private String insetIntoReferences(Reference reference) throws Exception {
        String result = "";
        SqlUtility.checkDeleteReferences(reference.getReferenceID().toString());
        String referencesSql = REFERENCES_INSERT_INTO + StringUtils.quote(reference.getReferenceType().name()) + "," +
                StringUtils.quote(reference.getReferenceValue()) + "," +
                StringUtils.quote(reference.getShipmentID()) + "," +
                StringUtils.quote(reference.getShippingInstructionID()) + "," +
                StringUtils.quote(reference.getReferenceID().toString()) + ")";
        int rows = SqlUtility.updateRow(referencesSql);
        if (rows > 0) {
            log.log(Level.INFO, "A new reference has been inserted.");
            result = "\nA new reference is inserted with id: "+reference.getReferenceID() ;
        } else {
            throw new Exception("Json file processing exception for reference " + reference.getReferenceID().toString());
        }
        return result;
    }

    private String insetIntoSeal(Seal seal) throws Exception {
        String sealSql = SEAL_INSERT_INTO + StringUtils.quote(seal.getShipmentEquipmentID().toString()) + "," +
                StringUtils.quote(seal.getSealNumber()) + "," +
                StringUtils.quote(seal.getSealSource()) + "," +
                StringUtils.quote(seal.getSealType()) + "," +
                StringUtils.quote(seal.getId().toString()) + ")";
        int rows = SqlUtility.updateRow(sealSql);
        if (rows > 0) {
            log.log(Level.INFO, "A new seal has been inserted.");
            return "\nA new seal is inserted with id: "+seal.getShipmentEquipmentID().toString();
        } else {
            throw new Exception("Json file processing exception for seal " + seal.getId().toString());
        }
    }

    private String insetIntoVessel(Vessel vessel) throws Exception {
        String vesselSql = VESSEL_INSERT_INTO + StringUtils.quote(vessel.getVesselIMONumber()) + "," +
                StringUtils.quote(vessel.getVesselName()) + "," +
                StringUtils.quote(vessel.getVesselFlag()) + "," +
                StringUtils.quote(vessel.getVesselCallSign()) + "," +
                StringUtils.quote(vessel.getVesselOperatorCarrierID().toString()) + ")";
        int rows = SqlUtility.updateRow(vesselSql);
        if (rows > 0) {
            log.log(Level.INFO, "A new vessel has been inserted. "+vessel.getVesselIMONumber());
            return "\nA new vessel has been inserted. "+vessel.getVesselIMONumber();
        } else {
            throw new Exception("Json file processing exception for vessel " + vessel.getVesselIMONumber());
        }
    }

    private String insetIntoTransportCall(TransportCall transportCall) throws Exception {
        String transportCallSql = TRANSPORT_CALL_INSERT_INTO + StringUtils.quote(transportCall.getId().toString()) + "," +
                transportCall.getTransportCallSequenceNumber() + "," +
                StringUtils.quote(transportCall.getFacilityID().toString()) + "," +
                StringUtils.quote(transportCall.getFacilityTypeCode().name()) + "," +
                StringUtils.quote(transportCall.getOtherFacility()) + "," +
                StringUtils.quote(transportCall.getLocationID()) + "," +
                StringUtils.quote(transportCall.getModeOfTransportID()) + "," +
                StringUtils.quote(transportCall.getVesselIMONumber()) + ")";
        int rows = SqlUtility.updateRow(transportCallSql);
        if (rows > 0) {
            log.log(Level.INFO, "A transportCall has been inserted with ID: "+transportCall.getId().toString());
            return "\nA new transportCall is inserted with id: "+transportCall.getId().toString();
        } else {
            throw new Exception("Json file processing exception for booking " + transportCall.getFacilityID().toString());
        }
    }

    private String insetIntoEquipmentEvent(EquipmentEvent equipmentEvent) throws Exception {
        String equipmentEventSql = EQUIPMENT_EVENT_INSERT_INTO + StringUtils.quote(equipmentEvent.getEvent_id().toString()) + "," +
                StringUtils.quote(equipmentEvent.getEventClassifierCode().name()) + "," +
                "TIMESTAMP "+StringUtils.quote(equipmentEvent.getEventCreatedDateTime().toString()) + "," +
                "TIMESTAMP "+StringUtils.quote(equipmentEvent.getEventDateTime().toString()) + "," +
                StringUtils.quote(equipmentEvent.getEquipmentReference()) + "," +
                StringUtils.quote(equipmentEvent.getEmptyIndicatorCode().name()) + "," +
                StringUtils.quote(equipmentEvent.getTransportCallID().toString()) + "," +
                StringUtils.quote(equipmentEvent.getEquipmentEventTypeCode().name()) + ")";
        int rows = SqlUtility.updateRow(equipmentEventSql);
        if (rows > 0) {
            log.log(Level.INFO, "A new equipmentEvent has been inserted with ID: "+equipmentEvent.getEvent_id().toString());
            return "\nA new equipmentEvent is inserted with id: "+equipmentEvent.getEvent_id().toString();
        } else {
            throw new Exception("Json file processing exception for equipmentEvent " + equipmentEvent.getEvent_id().toString());
        }
    }

    private String insetIntoOperationsEvent(OperationsEvent operationsEvent) throws Exception {
        String operationsEventSql = OPERATIONS_EVENT_INSERT_INTO + StringUtils.quote(operationsEvent.getEventID().toString()) + "," +
                StringUtils.quote(operationsEvent.getEventClassifierCode().name()) + "," +
                "TIMESTAMP "+StringUtils.quote(operationsEvent.getEventCreatedDateTime().toString()) + "," +
                "TIMESTAMP "+StringUtils.quote(operationsEvent.getEventDateTime().toString()) + "," +
                StringUtils.quote(operationsEvent.getOperationsEventTypeCode().name()) + "," +
                StringUtils.quote(operationsEvent.getTransportCallID().toString()) + "," +
                StringUtils.quote(operationsEvent.getPublisherID()) + "," +
                StringUtils.quote(operationsEvent.getPublisherRole().name()) + "," +
                StringUtils.quote(operationsEvent.getPortCallServiceTypeCode().name()) + "," +
                StringUtils.quote(operationsEvent.getEventLocationID()) + "," +
                StringUtils.quote(operationsEvent.getFacilityTypeCode().name()) + "," +
                StringUtils.quote(operationsEvent.getDelayReasonCode()) + "," +
                StringUtils.quote(operationsEvent.getVesselPositionID()) + "," +
                StringUtils.quote(operationsEvent.getRemark()) + ")";
        int rows = SqlUtility.updateRow(operationsEventSql);
        if (rows > 0) {
            log.log(Level.INFO, "A new equipmentEvent has been inserted.");
            return "\nA new equipmentEvent has been inserted. "+operationsEvent.getEventID().toString();
        } else {
            throw new Exception("Json file processing exception for equipmentEvent " + operationsEvent.getEventID().toString());
        }
    }

    private String insetIntoTransportEvent(TransportEvent transportEvent) throws Exception {
        String transportEventSql = TRANSPORT_EVENT_INSERT_INTO + StringUtils.quote(transportEvent.getEvent_id().toString()) + "," +
                StringUtils.quote(transportEvent.getEventClassifierCode().name()) + "," +
                "TIMESTAMP "+StringUtils.quote(transportEvent.getEventCreatedDateTime().toString()) + "," +
                "TIMESTAMP "+StringUtils.quote(transportEvent.getEventDateTime().toString()) + "," +
                StringUtils.quote(transportEvent.getDelayReasonCode()) + "," +
                StringUtils.quote(transportEvent.getChangeRemark()) + "," +
                StringUtils.quote(transportEvent.getTransportCallID().toString()) + "," +
                StringUtils.quote(transportEvent.getTransportEventTypeCode().name()) + ")";
        int rows = SqlUtility.updateRow(transportEventSql);
        if (rows > 0) {
            log.log(Level.INFO, "A new transportEvent has been inserted."+transportEvent.getEvent_id().toString());
            return "\nA new transportEvent is inserted with Id: "+transportEvent.getEvent_id().toString();
        } else {
            throw new Exception("transportEvent processing exception  " + transportEvent.getEvent_id().toString());
        }
    }

    private String processJsonFullShipmentEvent(String json) {
        try {
            FullShipment fullShipment = mapper.readValue(json, FullShipment.class);
            return insertJsonFullShipment(fullShipment);
        } catch (Exception e) {
            log.warning("failed process fullShipment: " + json+" "+e.getMessage());
            return "failed process fullShipment: "+e.getMessage();
        }
    }
    private String insertJsonFullShipment(FullShipment fullShipment) {
        StringBuilder result = new StringBuilder();
        ShipmentEvent shipmentEvent = fullShipment.getShipmentEvent();
        try {
            if(SqlUtility.isShipmentEventExist(shipmentEvent.getEvent_id().toString())){
                SqlUtility.deleteShipmentEventByEventId(shipmentEvent.getEvent_id().toString());
            }
            result.append(insetIntoShipmentEvent(shipmentEvent));
        } catch (Exception e) {
            log.severe("Failed to insert shipmentEvent: "+shipmentEvent.getEvent_id());
            return "\nFailed to insert shipmentEvent: "+shipmentEvent.getEvent_id()+" "+e.getMessage();
        }
        EquipmentEvent equipmentEvent = fullShipment.getEquipmentEvent();
        result.append(processEquipmentEvent(equipmentEvent));

        TransportEvent transportEvent = fullShipment.getTransportEvent();
        try {
            if(SqlUtility.isTransportEventExist(transportEvent.getEvent_id().toString())){
                SqlUtility.deleteTransportEventByTransportCallId(List.of(transportEvent.getTransportCall().getTransportCallID().toString()));
            }
            result.append(processTransportCall(transportEvent.getTransportCall()));
            if(SqlUtility.isTransportCallIdExist(transportEvent.getTransportCallID().toString())){
                result.append(insetIntoTransportEvent(transportEvent));
            }else{
                result.append("TransportCallId "+transportEvent.getTransportCallID().toString()).append(" does not exist in TransportCall table");
            }

        } catch (Exception e) {
            log.severe("Failed to insert transportEvent: "+transportEvent.getEvent_id()+" "+e.getMessage());
            result.append("Failed to insert transportEvent: ").append(transportEvent.getEvent_id())
                    .append(" ").append(e.getMessage());
        }
        return result.toString();
    }
}
