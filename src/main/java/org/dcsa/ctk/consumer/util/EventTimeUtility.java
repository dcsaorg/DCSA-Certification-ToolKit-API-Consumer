package org.dcsa.ctk.consumer.util;

import org.dcsa.ctk.consumer.model.*;
import org.dcsa.ctk.consumer.model.enums.EventTimeDuration;
import org.dcsa.ctk.consumer.model.enums.TimeOffset;

import java.time.OffsetDateTime;
import java.util.UUID;

public class EventTimeUtility {
    public static FullShipment fullShipmentUpdateDate(FullShipment fullShipment, String timeOffset){
        EventTimeOffset eventTimeOffset = EventTimeOffset.makeEventTimeOffset(timeOffset);
        ShipmentEvent shipmentEvent = fullShipment.getShipmentEvent();
        EquipmentEvent equipmentEvent = fullShipment.getEquipmentEvent();
        TransportEvent transportEvent = fullShipment.getTransportEvent();
        shipmentEvent = shipmentEventUpdateTime(shipmentEvent, eventTimeOffset);
        fullShipment.setShipmentEvent(shipmentEvent);
        equipmentEvent = equipmentEventUpdateTime(equipmentEvent, eventTimeOffset);
        fullShipment.setEquipmentEvent(equipmentEvent);
        transportEvent = transportEventUpdateTime(transportEvent, eventTimeOffset);
        fullShipment.setTransportEvent(transportEvent);
        fullShipment = updateFullShipment(fullShipment);
        return  fullShipment;
    }

    private static FullShipment updateFullShipment(FullShipment fullShipment) {
        ShipmentEvent shipmentEvent =  fullShipment.getShipmentEvent();
        if(shipmentEvent.getEvent_id() == null){
            shipmentEvent.setEvent_id(UUID.randomUUID());
        }
        fullShipment.setShipmentEvent(shipmentEvent);
        EquipmentEvent equipmentEvent = fullShipment.getEquipmentEvent();
        if(equipmentEvent.getEvent_id() == null){
            equipmentEvent.setEvent_id(UUID.randomUUID());
        }
        if(equipmentEvent.getTransportCallID() == null && equipmentEvent.getTransportCall().getTransportCallID() == null){
            UUID transportCallId = UUID.randomUUID();
            equipmentEvent.getTransportCall().setTransportCallID(transportCallId);
            equipmentEvent.setTransportCallID(transportCallId);
        }
        String lastFacilityId = SqlUtility.getLastFacilityId();
        equipmentEvent.getTransportCall().setFacilityID(lastFacilityId);
        equipmentEvent.getTransportCall().setVesselIMONumber(SqlUtility.getLastVesselImo());
        fullShipment.setEquipmentEvent(equipmentEvent);
        TransportEvent transportEvent = fullShipment.getTransportEvent();
        if(transportEvent.getEvent_id() == null){
            transportEvent.setEvent_id(UUID.randomUUID());
        }
        if(transportEvent.getTransportCallID() == null && transportEvent.getTransportCall().getTransportCallID() == null){
            UUID transportCallId = UUID.randomUUID();
            transportEvent.getTransportCall().setTransportCallID(transportCallId);
            transportEvent.setTransportCallID(transportCallId);
        }
        transportEvent.getTransportCall().setFacilityID(lastFacilityId);
        fullShipment.setTransportEvent(transportEvent);
        return fullShipment;
    }

    private  static ShipmentEvent shipmentEventUpdateTime(ShipmentEvent shipmentEvent, EventTimeOffset eventTimeOffset){
        if(eventTimeOffset.getTimeOffset() ==  TimeOffset.PAST){
            shipmentEvent.setEventCreatedDateTime(setPastTime(OffsetDateTime.now(), eventTimeOffset, true).toString());
            shipmentEvent.setEventDateTime(setPastTime(shipmentEvent.getEventCreatedDateTime(), eventTimeOffset, false).toString());
        } else if(eventTimeOffset.getTimeOffset() ==  TimeOffset.FUTURE){
            shipmentEvent.setEventCreatedDateTime(setFutureTime(OffsetDateTime.now(), eventTimeOffset).toString());
            shipmentEvent.setEventDateTime(setFutureTime(shipmentEvent.getEventCreatedDateTime(), eventTimeOffset).toString());
        }
        return  shipmentEvent;
    }

    public static EquipmentEvent equipmentEventUpdateTime(EquipmentEvent equipmentEvent, EventTimeOffset eventTimeOffset){
        if(eventTimeOffset.getTimeOffset() ==  TimeOffset.PAST){
            equipmentEvent.setEventCreatedDateTime(setPastTime(OffsetDateTime.now(), eventTimeOffset, true));
            equipmentEvent.setEventDateTime(setPastTime(equipmentEvent.getEventCreatedDateTime(), eventTimeOffset, false));
        } else if(eventTimeOffset.getTimeOffset() ==  TimeOffset.FUTURE){
            equipmentEvent.setEventCreatedDateTime(setFutureTime(OffsetDateTime.now(), eventTimeOffset));
            equipmentEvent.setEventDateTime(setFutureTime(equipmentEvent.getEventCreatedDateTime(), eventTimeOffset));
        }
        return  equipmentEvent;
    }

    public static TransportEvent transportEventUpdateTime(TransportEvent transportEvent, EventTimeOffset eventTimeOffset){
        if(eventTimeOffset.getTimeOffset() ==  TimeOffset.PAST){
            transportEvent.setEventCreatedDateTime(setPastTime(OffsetDateTime.now(), eventTimeOffset, true));
            transportEvent.setEventDateTime(setPastTime(transportEvent.getEventCreatedDateTime(), eventTimeOffset, false));
        } else if(eventTimeOffset.getTimeOffset() ==  TimeOffset.FUTURE){
            transportEvent.setEventCreatedDateTime(setFutureTime(OffsetDateTime.now(), eventTimeOffset));
            transportEvent.setEventDateTime(setFutureTime(transportEvent.getEventCreatedDateTime(), eventTimeOffset));
        }
        return  transportEvent;
    }

    private static OffsetDateTime setPastTime(OffsetDateTime offsetDateTime, EventTimeOffset eventTimeOffset, boolean isCreatedDate){
        if(eventTimeOffset.getEventTimeDuration() ==  EventTimeDuration.DAY){
            if(!isCreatedDate){
                offsetDateTime = offsetDateTime.plusDays(eventTimeOffset.getAmount());
            }else {
                offsetDateTime = offsetDateTime.minusDays(eventTimeOffset.getAmount());
            }
        }else if(eventTimeOffset.getEventTimeDuration() ==  EventTimeDuration.MONTH) {
            if(!isCreatedDate){
                offsetDateTime = offsetDateTime.plusMonths(eventTimeOffset.getAmount());
            }else {
                offsetDateTime = offsetDateTime.minusMonths(eventTimeOffset.getAmount());
            }
        }else if(eventTimeOffset.getEventTimeDuration() ==  EventTimeDuration.YEAR){
            if(!isCreatedDate){
                offsetDateTime = offsetDateTime.plusYears(eventTimeOffset.getAmount());
            }else {
                offsetDateTime = offsetDateTime.minusYears(eventTimeOffset.getAmount());
            }
        }else if(eventTimeOffset.getEventTimeDuration() ==  EventTimeDuration.HOUR){
            if(!isCreatedDate){
                offsetDateTime = offsetDateTime.plusHours(eventTimeOffset.getAmount());
            }else {
                offsetDateTime = offsetDateTime.minusHours(eventTimeOffset.getAmount());
            }
        }
        return offsetDateTime;
    }
    private static OffsetDateTime setFutureTime(OffsetDateTime offsetDateTime, EventTimeOffset eventTimeOffset){
        if(eventTimeOffset.getEventTimeDuration() ==  EventTimeDuration.DAY){
            offsetDateTime = offsetDateTime.plusDays(eventTimeOffset.getAmount());
        }else if(eventTimeOffset.getEventTimeDuration() ==  EventTimeDuration.MONTH) {
            offsetDateTime = offsetDateTime.plusMonths(eventTimeOffset.getAmount());
        }else if(eventTimeOffset.getEventTimeDuration() ==  EventTimeDuration.YEAR){
            offsetDateTime = offsetDateTime.plusYears(eventTimeOffset.getAmount());
        }else if(eventTimeOffset.getEventTimeDuration() ==  EventTimeDuration.HOUR){
            offsetDateTime = offsetDateTime.plusHours(eventTimeOffset.getAmount());
        }
        return offsetDateTime;
    }
}
