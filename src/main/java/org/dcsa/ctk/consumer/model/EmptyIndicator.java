package org.dcsa.ctk.consumer.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.EmptyIndicatorCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "empty_indicator")
public class EmptyIndicator {
    @Id
    @Size(max = 5)
    @Column(name = "empty_indicator_code")
    private EmptyIndicatorCode emptyIndicatorCode;
}
