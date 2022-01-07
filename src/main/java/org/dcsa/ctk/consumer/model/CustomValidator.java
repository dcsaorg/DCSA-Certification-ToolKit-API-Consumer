package org.dcsa.ctk.consumer.model;

import org.dcsa.core.validator.EnumSubset;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CustomValidator {
    public void validaParam(@EnumSubset(anyOf = {"SHIPMENT", "TRANSPORT", "EQUIPMENT"})
                                    String eventType) {
        System.out.println("Hello");
    }
}
