package org.dcsa.ctk.consumer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(schema = "vessel")
public class Vessel {
    @Id
    @NotNull
    @JsonProperty("vesselIMONumber")
    @Column(name = "vessel_imo_number")
    private String vesselIMONumber;

    @Size(max = 35)
    @JsonProperty("vesselName")
    @Column(name = "vessel_name")
    private String vesselName;

    @Size(max = 2)
    @JsonProperty("vesselFlag")
    @Column(name = "vessel_flag")
    private String vesselFlag;

    @Size(max = 10)
    @JsonProperty("vesselCallSignNumber")
    @Column(name = "vessel_call_sign")
    private String vesselCallSign;

    @JsonProperty("vesselOperatorCarrierCode")
    @Column(name = "vessel_operator_carrier_id")
    private UUID vesselOperatorCarrierID;

    @JsonProperty("vesselOperatorCarrierCodeListProvider")
    private String vesselOperatorCarrierCodeListProvider;

}

