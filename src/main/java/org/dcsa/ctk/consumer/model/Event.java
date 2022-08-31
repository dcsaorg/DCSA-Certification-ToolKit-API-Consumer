package org.dcsa.ctk.consumer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.Seal;
import org.dcsa.core.events.model.enums.EmptyIndicatorCode;
import org.dcsa.core.events.model.enums.EquipmentEventTypeCode;
import org.dcsa.core.events.model.enums.EventClassifierCode;
import org.dcsa.ctk.consumer.model.enums.EventType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;


@Data
@Table(schema = "event")
public class Event {

    @Id
    @JsonProperty("eventID")
    @Column(name = "event_id")
    private UUID event_id;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Column(name = "event_date_time")
    private OffsetDateTime eventDateTime;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Column(name = "event_created_date_time")
    @CreatedDate
    private OffsetDateTime eventCreatedDateTime;

    @Column(name = "event_classifier_code")
    private EventClassifierCode eventClassifierCode;

    @Column(name = "carrier_booking_reference")
    private String carrierBookingReference;

    @Transient
    private boolean isNewRecord;

    @Size(max = 4)
    @Column(name = "equipment_event_type_code")
    private EquipmentEventTypeCode equipmentEventTypeCode;

    @Column(name = "event_type")
    private EventType eventType;

    @Size(max = 15)
    @Column(name = "equipment_reference")
    private String equipmentReference;

    @Size(max = 5)
    @Column(name = "empty_indicator_code")
    private EmptyIndicatorCode emptyIndicatorCode;

    @Size(max = 100)
    @Column(name = "transport_call_id")
    private UUID transportCallID;

    @Transient
    private TransportCall transportCall;

    @Transient
    private List<DocumentReferenceTO> documentReferences;

    @Transient
    private List<Reference> references;

    @Transient
    private List<Seal> seals;

    @Transient
    @JsonProperty("ISOEquipmentCode")
    private String isoEquipmentCode;

/*    @Column(name = "event_classifier_code")
    private EventClassifierCode eventClassifierCode;

    @Column(name = "event_date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime eventDateTime;

    @Column(name = "event_created_date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @CreatedDate
    private OffsetDateTime eventCreatedDateTime;*/

/*    @Transient
    @JsonProperty("eventTypeCode")
    private String eventTypeCode;*/

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = getOffsetDateTime(eventDateTime);
    }

    public void setEventCreatedDateTime(String eventCreatedDateTime) {
        if(eventCreatedDateTime.equalsIgnoreCase("current_timestamp")){
            this.eventCreatedDateTime = OffsetDateTime.now();
        }else{
            this.eventCreatedDateTime = getOffsetDateTime(eventCreatedDateTime);
        }
    }

    private OffsetDateTime getOffsetDateTime(String dateTime){
        StringBuilder pattern = new StringBuilder("yyyy-MM-dd'T'HH:mm:ss");
        dateTime = dateTime.replaceAll("TIMESTAMP", "").trim();
        dateTime = dateTime.replaceAll("DATE", "").trim();
        dateTime = dateTime.replaceAll("\'", "").trim();
        String[] tokens =  dateTime.split("\\.");
        if(tokens.length > 1){
            String lastToken =  tokens[tokens.length -1];
            pattern.append(".");
            for(int i=0; i < lastToken.length()-1; i++){
                pattern.append("S");
            }
            pattern.append("\'Z\'");
        }else{
            if(dateTime.contains("T") || dateTime.contains("Z")){
                pattern.append("\'Z\'");
            }else {
                pattern.replace(pattern.indexOf("\'"), pattern.indexOf("\'") + 3, " ");
            }
        }
        ZoneId zoneId = ZoneId.of("UTC");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern.toString());
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        ZoneOffset offset = zoneId.getRules().getOffset(localDateTime);
        return OffsetDateTime.of(localDateTime, offset);
    }

    public UUID getEvent_id() {
        return event_id;
    }

    public void setEvent_id(UUID event_id) {
        this.event_id = event_id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public OffsetDateTime getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(OffsetDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public OffsetDateTime getEventCreatedDateTime() {
        return eventCreatedDateTime;
    }

    public void setEventCreatedDateTime(OffsetDateTime eventCreatedDateTime) {
        this.eventCreatedDateTime = eventCreatedDateTime;
    }

    public EventClassifierCode getEventClassifierCode() {
        return eventClassifierCode;
    }

    public void setEventClassifierCode(EventClassifierCode eventClassifierCode) {
        this.eventClassifierCode = eventClassifierCode;
    }

    public String getCarrierBookingReference() {
        return carrierBookingReference;
    }

    public void setCarrierBookingReference(String carrierBookingReference) {
        this.carrierBookingReference = carrierBookingReference;
    }

    public boolean isNewRecord() {
        return isNewRecord;
    }

    public void setNewRecord(boolean newRecord) {
        isNewRecord = newRecord;
    }
}

