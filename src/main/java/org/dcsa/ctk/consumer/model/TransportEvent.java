package org.dcsa.ctk.consumer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.enums.OperationsEventTypeCode;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "transport_event")
@Data
public class TransportEvent extends Event {

    @Column(name = "transport_event_type_code")
    private TransportEventTypeCode transportEventTypeCode;

    @Column(name = "delay_reason_code")
    private String delayReasonCode;

    @JsonProperty("changeRemark")
    @Column(name = "change_remark")
    private String changeRemark;

    @Column(name = "transport_call_id")
    private UUID transportCallID;

    private TransportCall transportCall;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<DocumentReferenceTO> documentReferences;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Reference> references;

    @Transient
    @JsonProperty("eventTypeCode")
    private OperationsEventTypeCode shipmentEventTypeCode;

    @JsonProperty("vesselScheduleChangeRemark")
    @Deprecated
    public String getVesselScheduleChangeRemark() {
        return changeRemark;
    }
    @JsonProperty("eventTypeCode")
    @Deprecated
    public TransportEventTypeCode getEventTypeCode() {
        return transportEventTypeCode;
    }
}
