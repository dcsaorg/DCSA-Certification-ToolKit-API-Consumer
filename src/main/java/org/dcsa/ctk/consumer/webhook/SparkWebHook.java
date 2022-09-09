package org.dcsa.ctk.consumer.webhook;

import lombok.extern.java.Log;
import org.dcsa.ctk.consumer.config.AppProperty;
import org.dcsa.ctk.consumer.util.APIUtility;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.dcsa.ctk.consumer.util.SqlUtility;
import spark.Service;

import static spark.Service.ignite;

 @Log
public class SparkWebHook {
    public void  startServer() {
        Service http = ignite()
                    .port(AppProperty.CALLBACK_PORT)
                    .threadPool(20);

        http.post(AppProperty.CALLBACK_PATH+"/:uuid", (req, res) -> {
            String subscriptionId = JsonUtility.getSubscriptionId(req.body());
            String callBackUuid = SqlUtility.getSubscriptionCallBackUuid(subscriptionId);
            if (req.params(":uuid").equals(callBackUuid)) {
                res.header("Content-Type", "application/json");
                res.body("{ \"Successful\": \"Callback POST called successfully! }");
                res.status(201);
            }else {
                res.status(400);
                res.body("{ \"NOT ALLOWED\": \"THE CALLBACK URL IS NOT FOUND. CALL BACK IS NOT ALLOWED\" }");
            }
            return  res;
        });

        http.head(AppProperty.CALLBACK_PATH+"/:uuid", (req, res) -> {
            String callBackUrl = SqlUtility.getCallBackUrl(AppProperty.CALLBACK_PATH+req.params(":uuid"));
            String callBackUuid = APIUtility.getCallBackUuid(callBackUrl);
            res.header("Content-Type", "application/json");
            if (req.params(":uuid").equals(callBackUuid)) {
                res.status(201);
                res.body("{ \"Successful\": \"Callback HEAD called successfully! }");
            }
            else {
                res.status(400);
                res.body("{ \"NOT ALLOWED\": \"THE CALLBACK URL IS NOT FOUND. CALL BACK IS NOT ALLOWED\" }");
            }
            return res;
        });
        http.awaitInitialization();
        log.info("Spark server started listening on port. "+ AppProperty.CALLBACK_PORT);
    }
}
