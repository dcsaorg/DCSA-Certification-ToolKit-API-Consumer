package org.dcsa.ctk.consumer.service.callback.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.ctk.consumer.notification.sender.NotificationFactory;
import org.dcsa.ctk.consumer.notification.sender.NotificationSender;
import org.dcsa.ctk.consumer.notification.sender.NotificationSubscriber;
import org.dcsa.ctk.consumer.service.callback.CallBackService;
import org.dcsa.ctk.consumer.util.APIUtility;
import org.dcsa.ctk.consumer.util.SqlUtility;
import org.dcsa.tnt.controller.TNTEventSubscriptionTOController;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallBackServiceImpl implements CallBackService {
    @Value("${spring.pubSubFlag}")
    boolean pubSubFlag;
    private final TNTEventSubscriptionTOController tntServer;
    private final RestTemplate restTemplate;

    @Override
    public boolean doHeadRequest(String callbackUrl, boolean newSubscription) {
        if (pubSubFlag) {
            boolean result;
            log.info("CALLBACK URL REQUEST RECEIVED: {}, IS IT A NEW SUBSCRIPTION: {} ", callbackUrl,  String.valueOf(newSubscription).toUpperCase());
            if(newSubscription){
                result = performHttpHead(callbackUrl,newSubscription);
            }else{
                result = headRequestForSavedSubscription(callbackUrl, newSubscription);
            }
            return result;
        }
        return true;
    }

    boolean headRequestForSavedSubscription(String callbackUrl, boolean newSubscription){
        boolean result = false;
        String dbCallbackUrl = SqlUtility.getCallBackUrl(callbackUrl);
        String dbCallbackUuid = APIUtility.getCallBackUuid(dbCallbackUrl);
        String callbackUuid = APIUtility.getCallBackUuid(callbackUrl);
        if (dbCallbackUuid.equals(dbCallbackUuid)) {
            log.info("FOUND CORRECT REQUEST CALLBACK UUID: {}", dbCallbackUuid);
            result = performHttpHead(callbackUrl, newSubscription);
        }else {
            log.warn("FOUND WRONG CALLBACK UUID: {} NO HEAD REQUEST DONE,", callbackUuid);
            result = false;
        }
        return result;
    }
    boolean performHttpHead(String callbackUrl, boolean newSubscription){
        boolean result = false;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("newSubscription", ""+newSubscription);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        log.info("HEAD NOTIFICATION SENT");
        try {
            HttpStatus statusCode = restTemplate.exchange(callbackUrl, HttpMethod.HEAD, entity, String.class)
                    .getStatusCode();
            log.info("GOT HEAD RESPONSE STATUS: " + statusCode);
            result = true;
        } catch (Exception e) {
            log.warn("THE CALLBACK URL RESPONDED "+e.getMessage().toUpperCase());
             result = false;
        }
        return result;
    }

    @Override
    public boolean sendNotification(TNTEventSubscriptionTO req) {
        if (pubSubFlag) {
            NotificationSubscriber eventNotification = NotificationFactory.getNotification(req.getEventType());
            NotificationSender notificationSender = new NotificationSender();
            notificationSender.attach(eventNotification);
            notificationSender.notifySubscribers(req);
        }
        return true;
    }
    @Override
    public boolean sendNotification(UUID id, EventSubscriptionSecretUpdateTO secret) throws ExecutionException, InterruptedException {
        if (pubSubFlag) {
            TNTEventSubscriptionTO req = tntServer.findById(id).toFuture().get();
            req.setSecret(secret.getSecret());
            sendNotification(req);
        }
        return true;
    }
}
