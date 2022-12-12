package org.dcsa.ctk.consumer.controller;

import org.dcsa.ctk.consumer.ConsumerCtkApplication;
import org.dcsa.ctk.consumer.webhook.SparkWebHook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestartController {

    @GetMapping("/restart")
    public String restart() {
        SparkWebHook.stopServer();
        ConsumerCtkApplication.restart();
        return "CONSUMER CTK IS RESTARTED";
    }
}
