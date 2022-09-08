package org.dcsa.ctk.consumer.service.callback.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.core.exception.CreateException;
import org.dcsa.ctk.consumer.notification.sender.NotificationFactory;
import org.dcsa.ctk.consumer.notification.sender.NotificationSender;
import org.dcsa.ctk.consumer.notification.sender.NotificationSubscriber;
import org.dcsa.ctk.consumer.service.callback.CallBackService;
import org.dcsa.tnt.controller.TNTEventSubscriptionTOController;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

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
    public boolean doHeadRequest(String callbackUrl) throws ExecutionException, InterruptedException {

        if (pubSubFlag) {
            log.info("CallbackUrl received :{}", callbackUrl);
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                HttpEntity<String> entity = new HttpEntity<>(headers);
                HttpStatus statusCode=restTemplate
                        .exchange(callbackUrl, HttpMethod.HEAD,entity,String.class).getStatusCode();
                log.info("head request status:"+statusCode);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
                if(e.getMessage().contains("400")){
                    //
                    System.out.printf("go it");
                }
            }
        }
        return true;
    }

    @Override
    public boolean sendNotification(TNTEventSubscriptionTO req) throws ExecutionException, InterruptedException {
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
