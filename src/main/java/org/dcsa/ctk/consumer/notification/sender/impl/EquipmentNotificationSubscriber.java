package org.dcsa.ctk.consumer.notification.sender.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.ctk.consumer.config.AppProperty;
import org.dcsa.ctk.consumer.notification.sender.NotificationSubscriber;
import org.dcsa.ctk.consumer.util.EventUtility;
import org.dcsa.ctk.consumer.util.SignatureUtility;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Base64;


@Slf4j
@RequiredArgsConstructor
public class EquipmentNotificationSubscriber implements NotificationSubscriber {

    private TNTEventSubscriptionTO req;

    private final RestTemplate restTemplate;

    @SneakyThrows
    @Override
    public void run() {
        Thread.sleep(AppProperty.NOTIFICATION_TRIGGER_TIME);
        String secret = Base64.getEncoder().encodeToString(req.getSecret());
        String callbackUrl = req.getCallbackUrl();
        String subscriptionId = req.getSubscriptionID().toString();
        String notificationBody = EventUtility.getEquipmentEvent();
        notificationBody = notificationBody.replace("SUB_ID_HERE", subscriptionId);
        String signature = SignatureUtility.getSignature(secret, notificationBody);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Notification-Signature", signature);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(notificationBody,headers);
        log.info("POST NOTIFICATION SENT");
        HttpStatus statusCode=restTemplate.exchange(callbackUrl, HttpMethod.POST,entity,String.class).getStatusCode();
        if(statusCode == HttpStatus.CREATED) {
            log.info("NOTIFICATION RECEIVED RESPONSE: {}",statusCode);
        }else if( statusCode == HttpStatus.METHOD_NOT_ALLOWED){
            log.error("ERROR NOTIFICATION RECEIVED RESPONSE:{}",statusCode);
        }
    }

    public void setRequest(TNTEventSubscriptionTO req) {
        this.req = req;
    }
}
