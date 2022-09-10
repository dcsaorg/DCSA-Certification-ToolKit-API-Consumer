package org.dcsa.ctk.consumer.service.callback;

import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public interface CallBackService {

    boolean doHeadRequest(String callbackUrl) throws ExecutionException, InterruptedException;
    boolean sendNotification(TNTEventSubscriptionTO req) throws ExecutionException, InterruptedException;
    boolean sendNotification(UUID uuid, EventSubscriptionSecretUpdateTO req) throws ExecutionException, InterruptedException;
    TNTEventSubscriptionTO getEventSubscription(UUID id, TNTEventSubscriptionTO req) throws ExecutionException, InterruptedException;
    boolean isEventSubscriptionIdExist(UUID id);
}
