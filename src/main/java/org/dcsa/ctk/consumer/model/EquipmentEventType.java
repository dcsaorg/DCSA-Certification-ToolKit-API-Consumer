package org.dcsa.ctk.consumer.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "equipment_event_type")
public class EquipmentEventType {
    @Id
    @Size(max = 4)
    @Column(name = "equipment_event_type_code")
    private String equipmentEventTypeCode;

    @Column(name = "equipment_event_type_name")
    @Size(max = 30)
    private String equipmentEventTypeName;

    @Column(name = "equipment_event_type_description")
    @Size(max = 300)
    private String  equipmentEventTypeDescription;
}
