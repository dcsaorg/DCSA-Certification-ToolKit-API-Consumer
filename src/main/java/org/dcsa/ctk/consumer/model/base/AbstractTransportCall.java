package org.dcsa.ctk.consumer.model.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.enums.FacilityTypeCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(schema = "transport_call")
public class AbstractTransportCall {
    @Id
    @JsonProperty("transportCallID")
    @Column(name = "id")
    protected UUID transportCallID;

    @Column(name = "transport_call_sequence_number")
    private Integer transportCallSequenceNumber;

    @JsonProperty("facilityTypeCode")
    @Size(max = 4)
    @Column(name = "facility_type_code")
    private FacilityTypeCode facilityTypeCode;

    @JsonProperty("facilityCode")
    @Column(name = "facility_id")
    private String facilityID;


    @Size(max = 50)
    @Column(name = "other_facility")
    private String otherFacility;

    @Column(name = "location_id")
    private String locationID;

    @JsonIgnore
    @Column(name = "mode_of_transport_code")
    @Size(max = 3)
    private String modeOfTransportID;

    @JsonProperty("vesselIMONumber")
    @Column(name = "vessel_imo_number")
    private String vesselIMONumber;

    public UUID getId() {
        return transportCallID;
    }

    public void setId(UUID id) {
        this.transportCallID = id;
    }

    public Integer getTransportCallSequenceNumber() {
        return transportCallSequenceNumber;
    }

    public void setTransportCallSequenceNumber(Integer transportCallSequenceNumber) {
        this.transportCallSequenceNumber = transportCallSequenceNumber;
    }

    public String getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(String facilityID) {
        //this.facilityID = UUID.fromString(facilityID);
        this.facilityID = facilityID;
    }

    public FacilityTypeCode getFacilityTypeCode() {
        return facilityTypeCode;
    }

    public void setFacilityTypeCode(FacilityTypeCode facilityTypeCode) {
        this.facilityTypeCode = facilityTypeCode;
    }

    public String getOtherFacility() {
        return otherFacility;
    }

    public void setOtherFacility(String otherFacility) {
        this.otherFacility = otherFacility;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getModeOfTransportID() {
        return modeOfTransportID;
    }

    public void setModeOfTransportID(String modeOfTransportID) {
        this.modeOfTransportID = modeOfTransportID;
    }

    public String getVesselIMONumber() {
        return vesselIMONumber;
    }

    public void setVesselIMONumber(String vesselIMONumber) {
        this.vesselIMONumber = vesselIMONumber;
    }
}

