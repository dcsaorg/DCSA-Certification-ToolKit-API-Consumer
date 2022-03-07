package org.dcsa.ctk.consumer.notification.sender;

import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;

public interface NotificationSubscriber extends Runnable{
    void setRequest(TNTEventSubscriptionTO req);
}
