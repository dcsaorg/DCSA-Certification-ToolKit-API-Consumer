package org.dcsa.ctk.consumer.notification.sender;

import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.ctk.consumer.callback.ApplicationContextProvider;
import org.dcsa.ctk.consumer.notification.sender.impl.EquipmentNotificationSubscriber;
import org.dcsa.ctk.consumer.notification.sender.impl.ShipmentNotificationSubscriber;
import org.dcsa.ctk.consumer.notification.sender.impl.TransportNotificationSubscriber;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public interface NotificationFactory {

    static NotificationSubscriber getNotification(List<EventType> eventType){
        RestTemplate restTemplate= ApplicationContextProvider.getApplicationContext().getBean(RestTemplate.class);
        if (eventType.contains(EventType.EQUIPMENT))
            return new EquipmentNotificationSubscriber(restTemplate);
        else if (eventType.contains(EventType.TRANSPORT))
            return new TransportNotificationSubscriber(restTemplate);
        else if (eventType.contains(EventType.SHIPMENT))
            return new ShipmentNotificationSubscriber(restTemplate);
        else
            return null;
    }
}
