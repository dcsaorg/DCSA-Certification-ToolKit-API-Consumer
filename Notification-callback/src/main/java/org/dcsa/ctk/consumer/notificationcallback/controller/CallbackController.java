package org.dcsa.ctk.consumer.notificationcallback.controller;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@AllArgsConstructor
@RequestMapping(path = CallbackController.API_VERSION, produces = {MediaType.APPLICATION_JSON_VALUE})
public class CallbackController {

    public static final String API_VERSION = "/v2";

    @RequestMapping(path = "/notification-endpoints/receive/{uuid}" ,method = { RequestMethod.HEAD})
    String head(@PathVariable String uuid){
        log.info("HEAD NOTIFICATION RECEIVED UUID: "+uuid);
        return  "HEAD NOTIFICATION RECEIVED UUID: "+uuid;
    }


    @PostMapping(path = "/notification-endpoints/receive/{uuid}")
    ResponseEntity<String> post(@PathVariable String uuid, @RequestBody Object obj){
        log.info("POST NOTIFICATION RECEIVED UUID: "+uuid);
        log.info("POST NOTIFICATION RECEIVED WITH BODY: "+obj.toString());
        return  new ResponseEntity<>("POST NOTIFICATION RECEIVED UUID: "+uuid, HttpStatus.CREATED);
    }

}
