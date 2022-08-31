package org.dcsa.ctk.consumer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.enums.DocumentTypeCode;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Data
@Table(name = "shipment_event")
public class ShipmentEvent extends Event {
    @Column(name = "document_id")
    @Size(max = 100)
    private String documentID;

    @JsonProperty("shipmentEventTypeCode")
    @Column(name = "shipment_event_type_code")
    private ShipmentEventTypeCode shipmentEventTypeCode;

    @Column(name = "document_type_code")
    private DocumentTypeCode documentTypeCode;

    @Column(name = "reason")
    @Size(max = 250)
    private String reason;

    @Transient
    private List<Reference> references;

    @Transient
    private List<DocumentReferenceTO> documentReferences;

    @JsonProperty("shipmentInformationTypeCode")
    private String shipmentInformationTypeCode;

    private UUID shipmentID;

    @JsonProperty("eventTypeCode")
    private ShipmentEventTypeCode eventTypeCode;

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public ShipmentEventTypeCode getShipmentEventTypeCode() {
        return shipmentEventTypeCode;
    }

    public void setShipmentEventTypeCode(ShipmentEventTypeCode shipmentEventTypeCode) {
        this.shipmentEventTypeCode = shipmentEventTypeCode;
    }

    public DocumentTypeCode getDocumentTypeCode() {
        return documentTypeCode;
    }

    public void setDocumentTypeCode(DocumentTypeCode documentTypeCode) {
        this.documentTypeCode = documentTypeCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<Reference> getReferences() {
        return references;
    }

    public void setReferences(List<Reference> references) {
        this.references = references;
    }

    public List<DocumentReferenceTO> getDocumentReferences() {
        return documentReferences;
    }

    public void setDocumentReferences(List<DocumentReferenceTO> documentReferences) {
        this.documentReferences = documentReferences;
    }

    public String getShipmentInformationTypeCode() {
        return shipmentInformationTypeCode;
    }

    public void setShipmentInformationTypeCode(String shipmentInformationTypeCode) {
        this.shipmentInformationTypeCode = shipmentInformationTypeCode;
    }

    public UUID getShipmentID() {
        return shipmentID;
    }

    public void setShipmentID(UUID shipmentID) {
        this.shipmentID = shipmentID;
    }

    public ShipmentEventTypeCode getEventTypeCode() {
        return eventTypeCode;
    }

    public void setEventTypeCode(ShipmentEventTypeCode eventTypeCode) {
        this.eventTypeCode = eventTypeCode;
    }
}
