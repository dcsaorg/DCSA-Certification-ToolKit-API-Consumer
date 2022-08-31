package org.dcsa.ctk.consumer.model.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.enums.EventClassifierCode;
import org.dcsa.ctk.consumer.model.enums.EventType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Data
@Table(name = "event")
public class Event {
    @Id
    @JsonProperty("eventID")
    @Column(name = "event_id")
    private UUID event_id;
    @Column(name = "event_type")
    private EventType eventType;
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
