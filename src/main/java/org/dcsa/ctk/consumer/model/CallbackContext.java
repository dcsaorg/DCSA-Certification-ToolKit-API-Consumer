package org.dcsa.ctk.consumer.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Data
public class CallbackContext {
    private boolean headRequestReceived;
    private boolean notificationReceived;
    private CountDownLatch headRequestLock;
    private CountDownLatch notificationRequestLock;
    private Map<String,String> headers;
    private String notificationBody;
    private String secret;


    public CallbackContext() {
        init();
    }

    public void init() {
        headRequestReceived = false;
        notificationReceived = false;
        headRequestLock = new CountDownLatch(1);
        notificationRequestLock = new CountDownLatch(1);
        headers=new HashMap<>();
        notificationBody=null;
    }
    public void setHeadRequestCountDown(int countDown)
    {
        headRequestLock = new CountDownLatch(countDown);
    }

    public void setNotificationCountDown(int countDown)
    {
        notificationRequestLock = new CountDownLatch(countDown);
    }

}
