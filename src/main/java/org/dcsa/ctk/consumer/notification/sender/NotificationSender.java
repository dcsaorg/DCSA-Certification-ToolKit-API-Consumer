package org.dcsa.ctk.consumer.notification.sender;

import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;

import java.util.ArrayList;
import java.util.List;

public class NotificationSender {

    private List<NotificationSubscriber> observers = new ArrayList<>();

    public void attach(NotificationSubscriber o) {
        observers.add(o);
    }

    public void notifySubscribers(TNTEventSubscriptionTO req) {
        for (NotificationSubscriber notification : observers) {
            notification.setRequest(req);
            Thread subscriber = new Thread(notification);
            subscriber.start();
        }
    }

}
