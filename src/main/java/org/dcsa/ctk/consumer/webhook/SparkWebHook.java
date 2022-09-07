package org.dcsa.ctk.consumer.webhook;

import org.dcsa.ctk.consumer.config.AppProperty;
import org.dcsa.ctk.consumer.model.CallbackContext;
import spark.Service;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import static spark.Service.ignite;

public class SparkWebHook {
    CallbackContext callbackContext;

    public void  startServer() {
        Service http = ignite()
                .port(Integer.parseInt(AppProperty.CALLBACK_PORT.trim()))
                .threadPool(20);

        http.post(AppProperty.CALLBACK_PATH+"/:uuid", (req, res) -> {
            res.status(201);
            if (req.params(":uuid").equals("456eacf9-8cda-412b-b801-4a41be7a6c35")) {
                res.header("Content-Type", "application/json");
                callbackContext.setNotificationReceived(true);
                callbackContext.setNotificationBody(req.body());
                Map<String, String> headers = new HashMap<>();
                for (String header : req.headers()) {
                    headers.put(header, req.headers(header));
                }
                callbackContext.setHeaders(headers);
                callbackContext.getNotificationRequestLock().countDown();
            }
            return "{\"status\":\"OK\"}";
        });

        http.head(AppProperty.CALLBACK_PATH+"/:uuid", (req, res) -> {
            res.header("Content-Type", "application/json");
            if (req.params(":uuid").equals("456eacf9-8cda-412b-b801-4a41be7a6c35")) {
                res.status(201);
                callbackContext.setHeadRequestReceived(true);
                callbackContext.getHeadRequestLock().countDown();
            }
            else if (req.params(":uuid").equals("307deecf-e599-4ff2-bf5a-fd47c171b8c4")) {
                res.status(400);
                callbackContext.setHeadRequestReceived(true);
                callbackContext.getHeadRequestLock().countDown();
            }
            return "{\"status\":\"OK\"}";
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
