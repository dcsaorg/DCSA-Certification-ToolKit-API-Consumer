package org.dcsa.ctk.consumer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.Seal;
import org.dcsa.core.events.model.enums.EmptyIndicatorCode;
import org.dcsa.core.events.model.enums.EquipmentEventTypeCode;
import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;


@Data
@Table(schema = "equipment_event")
public class EquipmentEvent extends Event {

    @Size(max = 4)
    @Column(name = "equipment_event_type_code")
    private EquipmentEventTypeCode equipmentEventTypeCode;

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

    @Transient
    @JsonProperty("eventTypeCode")
    private String eventTypeCode;

}

