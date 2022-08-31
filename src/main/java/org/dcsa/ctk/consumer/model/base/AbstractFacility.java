package org.dcsa.ctk.consumer.model.base;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;


@Table(name = "abstract_facility")
@Entity
public class AbstractFacility implements Serializable {
    @Id
    @Column(name = "id")
    private UUID facilityID;
    @Size(max = 100)
    @Column(name = "facility_name")
    private String facilityName;
    @Size(max = 5)
    @Column(name = "un_location_code")
    @JsonProperty("UNLocationCode")
    private String unLocationCode;
    @Size(max = 4)
    @Column(name = "facility_bic_code")
    private String facilityBICCode;
    @Size(max = 4)
    @Column(name = "facility_smdg_code")
    private String facilitySMDGCode;
    @Column(name = "location_id")
    private String locationID;

    public AbstractFacility() {
    }

    public UUID getFacilityID() {
        return this.facilityID;
    }

    public String getFacilityName() {
        return this.facilityName;
    }

    public String getUnLocationCode() {
        return this.unLocationCode;
    }

    public String getFacilityBICCode() {
        return this.facilityBICCode;
    }

    public String getFacilitySMDGCode() {
        return this.facilitySMDGCode;
    }

    public String getLocationID() {
        return this.locationID;
    }

    public void setFacilityID(final UUID facilityID) {
        this.facilityID = facilityID;
    }

    public void setFacilityName(final String facilityName) {
        this.facilityName = facilityName;
    }

    @JsonProperty("UNLocationCode")
    public void setUnLocationCode(final String unLocationCode) {
        this.unLocationCode = unLocationCode;
    }

    public void setFacilityBICCode(final String facilityBICCode) {
        this.facilityBICCode = facilityBICCode;
    }

    public void setFacilitySMDGCode(final String facilitySMDGCode) {
        this.facilitySMDGCode = facilitySMDGCode;
    }

    public void setLocationID(final String locationID) {
        this.locationID = locationID;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof AbstractFacility)) {
            return false;
        } else {
            AbstractFacility other = (AbstractFacility)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$facilityID = this.getFacilityID();
                Object other$facilityID = other.getFacilityID();
                if (this$facilityID == null) {
                    if (other$facilityID != null) {
                        return false;
                    }
                } else if (!this$facilityID.equals(other$facilityID)) {
                    return false;
                }

                Object this$facilityName = this.getFacilityName();
                Object other$facilityName = other.getFacilityName();
                if (this$facilityName == null) {
                    if (other$facilityName != null) {
                        return false;
                    }
                } else if (!this$facilityName.equals(other$facilityName)) {
                    return false;
                }

                Object this$unLocationCode = this.getUnLocationCode();
                Object other$unLocationCode = other.getUnLocationCode();
                if (this$unLocationCode == null) {
                    if (other$unLocationCode != null) {
                        return false;
                    }
                } else if (!this$unLocationCode.equals(other$unLocationCode)) {
                    return false;
                }

                label62: {
                    Object this$facilityBICCode = this.getFacilityBICCode();
                    Object other$facilityBICCode = other.getFacilityBICCode();
                    if (this$facilityBICCode == null) {
                        if (other$facilityBICCode == null) {
                            break label62;
                        }
                    } else if (this$facilityBICCode.equals(other$facilityBICCode)) {
                        break label62;
                    }

                    return false;
                }

                label55: {
                    Object this$facilitySMDGCode = this.getFacilitySMDGCode();
                    Object other$facilitySMDGCode = other.getFacilitySMDGCode();
                    if (this$facilitySMDGCode == null) {
                        if (other$facilitySMDGCode == null) {
                            break label55;
                        }
                    } else if (this$facilitySMDGCode.equals(other$facilitySMDGCode)) {
                        break label55;
                    }

                    return false;
                }

                Object this$locationID = this.getLocationID();
                Object other$locationID = other.getLocationID();
                if (this$locationID == null) {
                    if (other$locationID != null) {
                        return false;
                    }
                } else if (!this$locationID.equals(other$locationID)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AbstractFacility;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $facilityID = this.getFacilityID();
        result = result * 59 + ($facilityID == null ? 43 : $facilityID.hashCode());
        Object $facilityName = this.getFacilityName();
        result = result * 59 + ($facilityName == null ? 43 : $facilityName.hashCode());
        Object $unLocationCode = this.getUnLocationCode();
        result = result * 59 + ($unLocationCode == null ? 43 : $unLocationCode.hashCode());
        Object $facilityBICCode = this.getFacilityBICCode();
        result = result * 59 + ($facilityBICCode == null ? 43 : $facilityBICCode.hashCode());
        Object $facilitySMDGCode = this.getFacilitySMDGCode();
        result = result * 59 + ($facilitySMDGCode == null ? 43 : $facilitySMDGCode.hashCode());
        Object $locationID = this.getLocationID();
        result = result * 59 + ($locationID == null ? 43 : $locationID.hashCode());
        return result;
    }

    public String toString() {
        UUID var10000 = this.getFacilityID();
        return "AbstractFacility(facilityID=" + var10000 + ", facilityName=" + this.getFacilityName() + ", unLocationCode=" + this.getUnLocationCode() + ", facilityBICCode=" + this.getFacilityBICCode() + ", facilitySMDGCode=" + this.getFacilitySMDGCode() + ", locationID=" + this.getLocationID() + ")";
    }
}
