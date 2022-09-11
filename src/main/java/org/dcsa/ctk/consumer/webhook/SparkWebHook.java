package org.dcsa.ctk.consumer.webhook;

import lombok.extern.slf4j.Slf4j;
import org.dcsa.ctk.consumer.config.AppProperty;
import spark.Service;

import static spark.Service.ignite;

@Slf4j
public class SparkWebHook {
    public void  startServer() {
        Service http = ignite()
                    .port(AppProperty.CALLBACK_PORT)
                    .threadPool(20);

        http.post(AppProperty.CALLBACK_PATH+"/:uuid", (req, res) -> {
            log.info("INTERNAL CALLBACK SEVER RECEIVED POST REQUEST WITH BODY: {}", req.body());
            res.header("Content-Type", "application/json");
            res.body("{ \"Successful\": \"Callback POST called successfully! }");
            res.status(201);
            return  res;
        });

        http.head(AppProperty.CALLBACK_PATH+"/:uuid", (req, res) -> {
            boolean newSubscription =  Boolean.parseBoolean(req.headers("newSubscription"));
            log.info("INTERNAL CALLBACK SEVER RECEIVED HEAD UUID:: {} IS NEW SUBSCRIPTION REQUEST {}", req.params(":uuid"),
                    String.valueOf(newSubscription).toUpperCase());
            res.status(200);
            res.body("{ \"Successful\": \"Callback HEAD received successfully! }");
            return res;
          /*

            if(newSubscription){
                log.info("INTERNAL CALLBACK SEVER RECEIVED HEAD UUID:: {} IS NEW SUBSCRIPTION REQUEST {}", req.params(":uuid"), newSubscription);
                res.status(201);
                res.body("{ \"Successful\": \"Callback HEAD received successfully! }");
            }else{
                log.info("INTERNAL CALLBACK SEVER RECEIVED HEAD UUID:: {} IS NEW SUBSCRIPTION REQUEST {}", req.params(":uuid"), newSubscription);
                String callBackUrl = SqlUtility.getCallBackUrl(AppProperty.CALLBACK_PATH+req.params(":uuid"));
                String callBackUuid = APIUtility.getCallBackUuid(callBackUrl);
                res.header("Content-Type", "application/json");
                if (req.params(":uuid").equals(callBackUuid)) {
                    log.info("INTERNAL CALLBACK SEVER GOT CORRECT REQUEST CALLBACK UUID: {}", req.params(":uuid"));
                    res.status(201);
                    res.body("{ \"Successful\": \"Callback HEAD called successfully! }");
                }
                else {
                    log.warn("INTERNAL CALLBACK SEVER GOT WRONG REQUEST CALLBACK UUID: {}", req.params(":uuid"));
                    res.status(400);
                    res.body("{ \"NOT ALLOWED\": \"THE CALLBACK URL IS NOT FOUND. CALL BACK IS NOT ALLOWED\" }");
                }
            }
            return res;
            */
        });
        http.awaitInitialization();
        log.info("Spark server started listening on port. "+ AppProperty.CALLBACK_PORT);
    }
}
