package org.dcsa.ctk.consumer.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name="country")
public class Country {
    @Id
    @Size(max = 2)
    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "country_name")
    private String countryName;
}
