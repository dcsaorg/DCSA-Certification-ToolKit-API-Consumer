package org.dcsa.ctk.consumer.service.callback.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.dcsa.ctk.consumer.constant.CheckListStatus;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.service.callback.ConsumerCallbackService;
import org.dcsa.ctk.consumer.service.config.impl.ConfigService;
import org.dcsa.ctk.consumer.service.decorator.Decorator;
import org.dcsa.ctk.consumer.service.log.CustomLogger;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.dcsa.ctk.consumer.util.SqlUtility;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class ConsumerCallbackServiceImpl implements ConsumerCallbackService {

    final Decorator<Map<String, Object>> mapDecorator;

    final CustomLogger customLogger;

    public ConsumerCallbackServiceImpl(Decorator<Map<String, Object>> mapDecorator, CustomLogger customLogger) {
        this.mapDecorator = mapDecorator;
        this.customLogger = customLogger;
    }

    @Override
    public ResponseEntity<String> checkCallback(UUID id, TNTEventSubscriptionTO reqTntEventSubscriptionTO, ServerHttpResponse response, ServerHttpRequest request, Map<String, List<CheckListItem>> checkListItemMap) throws ExecutionException, InterruptedException, JsonProcessingException {
        Map<String, Object> responseMap;
        String route = "/check/callback";
        CheckListItem checkListItem = ConfigService.getNextCheckListItem(route, "POST", 200);
        TNTEventSubscriptionTO dbTntEventSubscriptionTO = SqlUtility.getEventSubscriptionBySubscriptionId(id.toString());

        if(dbTntEventSubscriptionTO.getSubscriptionID() != null){
            customLogger.init(dbTntEventSubscriptionTO, response, request, checkListItem, route);
            responseMap = mapDecorator.decorate(JsonUtility.convertToMap(reqTntEventSubscriptionTO), response, request, checkListItem);
            if (checkListItem != null){
                checkListItem.setStatus(CheckListStatus.COVERED);
            }
            customLogger.log(responseMap, response, request);
            if(isSameSecret(dbTntEventSubscriptionTO, reqTntEventSubscriptionTO)){
                checkListItem = ConfigService.getNextCheckListItem(route, "POST", 202);
                customLogger.init(dbTntEventSubscriptionTO, response, request, checkListItem, route);
                if (checkListItem != null){
                    checkListItem.setStatus(CheckListStatus.COVERED);
                }
                customLogger.log(responseMap, response, request);
                return  new ResponseEntity<>("Correct event subscription id "+id+" found as well as correct secret found that allows to invoke callback "+
                                                dbTntEventSubscriptionTO.getCallbackUrl(), HttpStatus.NO_CONTENT);
            }else{
                checkListItem = ConfigService.getNextCheckListItem(route, "POST", 403);
                customLogger.init(dbTntEventSubscriptionTO, response, request, checkListItem, route);
                if (checkListItem != null){
                    checkListItem.setStatus(CheckListStatus.COVERED);
                }
                customLogger.log(responseMap, response, request);
                return  new ResponseEntity<>("Correct event subscription id "+id+" but wrong secret found so forbids to invoke callback  "+
                        dbTntEventSubscriptionTO.getCallbackUrl(), HttpStatus.FORBIDDEN);
            }
        }else {
            checkListItem = ConfigService.getNextCheckListItem(route, "POST", 400);
            customLogger.init(reqTntEventSubscriptionTO, response, request, checkListItem, route);
            responseMap = mapDecorator.decorate(JsonUtility.convertToMap(reqTntEventSubscriptionTO), response, request, checkListItem);
            if (checkListItem != null){
                checkListItem.setStatus(CheckListStatus.COVERED);
            }
            customLogger.log(responseMap, response, request);
            return  new ResponseEntity<>("Event subscription id "+id+" not found", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isSameSecret(TNTEventSubscriptionTO bdTntEventSubscriptionTO, TNTEventSubscriptionTO reqTNTEventSubscriptionTO ){
        String dbSecret = Base64.getEncoder().encodeToString(bdTntEventSubscriptionTO.getSecret());
        String reqSecret = Base64.getEncoder().encodeToString(reqTNTEventSubscriptionTO.getSecret());
        if(dbSecret.equalsIgnoreCase(reqSecret)){
            return true;
        }else {
            return false;
        }
    }

}
