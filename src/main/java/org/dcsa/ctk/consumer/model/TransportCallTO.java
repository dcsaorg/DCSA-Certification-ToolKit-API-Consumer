package org.dcsa.ctk.consumer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.base.AbstractTransportCall;
import org.dcsa.core.events.model.enums.DCSATransportType;
import org.dcsa.core.events.model.enums.FacilityCodeListProvider;
import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class TransportCallTO extends AbstractTransportCall {

    @Size(max = 5)
    @Transient
    @JsonProperty("UNLocationCode")
    private String UNLocationCode;

    @Transient
    private Vessel vessel;

    @Transient
    @JsonProperty("carrierServiceCode")
    private String carrierServiceCode;

    @Transient
    @JsonProperty("carrierVoyageNumber")
    private String carrierVoyageNumber;

    @Size(max = 6)
    @Transient
    @JsonProperty("facilityCode")
    private String facilityCode;

    @Transient
    @JsonProperty("facilityCodeListProvider")
    private FacilityCodeListProvider facilityCodeListProvider;

    @JsonProperty("location")
    @Column(name = "location_id")
    private String location;

    @Transient
    @JsonProperty("modeOfTransport")
    private DCSATransportType modeOfTransport;
}

