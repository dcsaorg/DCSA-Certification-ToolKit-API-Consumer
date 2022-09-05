package org.dcsa.ctk.consumer.model;

import lombok.Data;

@Data
public class EventSubscription {
    String subscriptionId;
    String callbackUrl;
    String secret;
}
