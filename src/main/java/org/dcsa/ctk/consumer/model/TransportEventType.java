package org.dcsa.ctk.consumer.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Data
@Table(name = "transport_event_type")
public class TransportEventType {

    @Id
    @Column(name = "transport_event_type_code")
    @Size(max = 4)
    private String transportEventTypeCode;

    @Column(name = "transport_event_type_name")
    @Size(max = 30)
    private String transportEventTypeName;

    @Column(name = "transport_event_type_description")
    @Size(max = 250)
    private String transportEventTypeDescription;
}
