package org.dcsa.ctk.consumer.webhook;

import org.dcsa.ctk.consumer.config.AppProperty;
import org.dcsa.ctk.consumer.model.CallbackContext;
import org.dcsa.ctk.consumer.util.APIUtility;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.dcsa.ctk.consumer.util.SqlUtility;
import spark.Service;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import static spark.Service.ignite;

public class SparkWebHook {
    CallbackContext callbackContext;

    public void  startServer() {
        Service http = ignite()
                .port(AppProperty.CALLBACK_PORT)
                .threadPool(20);

        http.post(AppProperty.CALLBACK_PATH+"/:uuid", (req, res) -> {
            String subscriptionId = JsonUtility.getSubscriptionId(req.body());
            String callBackUuid = SqlUtility.getSubscriptionCallBackUuid(subscriptionId);
            if (req.params(":uuid").equals(callBackUuid)) {
                res.header("Content-Type", "application/json");
                callbackContext.setNotificationReceived(true);
                callbackContext.setNotificationBody(req.body());
                Map<String, String> headers = new HashMap<>();
                for (String header : req.headers()) {
                    headers.put(header, req.headers(header));
                }
                callbackContext.setHeaders(headers);
                callbackContext.getNotificationRequestLock().countDown();
                res.status(201);
            }else {
                res.status(400);
            }
            return  res;
        });

        http.head(AppProperty.CALLBACK_PATH+"/:uuid", (req, res) -> {
            String callBackUrl = SqlUtility.getCallBackUrlIfExist(AppProperty.CALLBACK_PATH+req.params(":uuid"));
            String callBackUuid = APIUtility.getCallBackUuid(callBackUrl);
            res.header("Content-Type", "application/json");
            if (req.params(":uuid").equals(callBackUuid)) {
                res.status(201);
                callbackContext.setHeadRequestReceived(true);
                callbackContext.getHeadRequestLock().countDown();
            }
            else {
                res.status(400);
                callbackContext.setHeadRequestReceived(true);
                callbackContext.getHeadRequestLock().countDown();
            }
            return res;
        });
        http.awaitInitialization();
        System.out.println("Server Started listening on port. "+ AppProperty.CALLBACK_PORT);
    }

    public void stopServer() {
        Spark.stop();
        Spark.awaitStop();
        if (callbackContext != null)
            callbackContext.init();
        System.out.println("Server Stopped");
    }

    public void setContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }
}
