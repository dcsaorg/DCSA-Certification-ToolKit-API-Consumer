package org.dcsa.ctk.consumer.model;

import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;

@Data
public class EventSubscription extends TNTEventSubscriptionTO {
    @JsonIgnore
    String plainSecret;
}
