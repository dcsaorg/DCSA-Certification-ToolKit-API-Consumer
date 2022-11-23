package org.dcsa.ctk.consumer.service.callback.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.ctk.consumer.constant.CheckListStatus;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.model.enums.ValidationRequirementID;
import org.dcsa.ctk.consumer.service.callback.CallBackService;
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

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class ConsumerCallbackServiceImpl implements ConsumerCallbackService {

    final CallBackService callBackService;
    final Decorator<Map<String, Object>> mapDecorator;

    final CustomLogger customLogger;

    public ConsumerCallbackServiceImpl(CallBackService callBackService, Decorator<Map<String, Object>> mapDecorator, CustomLogger customLogger) {
        this.callBackService = callBackService;
        this.mapDecorator = mapDecorator;
        this.customLogger = customLogger;
    }

    @Override
    public ResponseEntity<String> checkCallback(UUID id, TNTEventSubscriptionTO reqTntEventSubscriptionTO, ServerHttpResponse response, ServerHttpRequest request, Map<String, List<CheckListItem>> checkListItemMap) throws ExecutionException, InterruptedException, JsonProcessingException {
        Map<String, Object> responseMap;
        String route = request.getPath().toString().substring(0, request.getPath().toString().lastIndexOf("/"));
        CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(route, Objects.requireNonNull(request.getMethod()).name(), ValidationRequirementID.TNT_2_2_API_SUB_CSM_200.getValue());
        TNTEventSubscriptionTO dbTntEventSubscriptionTO = SqlUtility.getEventSubscriptionBySubscriptionId(id.toString());

        if (dbTntEventSubscriptionTO.getSubscriptionID() != null) {

            customLogger.init(dbTntEventSubscriptionTO, response, request, checkListItem, route);
            responseMap = mapDecorator.decorate(JsonUtility.convertToMap(reqTntEventSubscriptionTO), response, request, checkListItem);
            if (checkListItem != null){
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            }
            customLogger.log(responseMap, response, request);
            String checkResult = checkSameSecret(dbTntEventSubscriptionTO, reqTntEventSubscriptionTO);
            if(checkResult.contains("Wrong")){
                checkListItem = ConfigService.getCheckListItemByRequirementId(route, request.getMethod().name(), ValidationRequirementID.TNT_2_2_API_SUB_CSM_403.getValue());
                customLogger.init(dbTntEventSubscriptionTO, response, request, checkListItem, route);
                if (checkListItem != null){
                    checkListItem.setStatus(CheckListStatus.CONFORMANT);
                }
                customLogger.log(responseMap, response, request);
                return  new ResponseEntity<>("Correct event subscription id "+id+".\n" +checkResult+" Forbidden to invoke callback",
                                            HttpStatus.NOT_FOUND);
            }else{
                checkListItem = ConfigService.getCheckListItemByRequirementId(route, request.getMethod().name(), ValidationRequirementID.TNT_2_2_API_SUB_CSM_202.getValue());
                customLogger.init(dbTntEventSubscriptionTO, response, request, checkListItem, route);
                if (checkListItem != null){
                    checkListItem.setStatus(CheckListStatus.CONFORMANT);
                }
                customLogger.log(responseMap, response, request);
                return  new ResponseEntity<>("Correct event subscription id "+id+" found.\n"+checkResult,
                        HttpStatus.NO_CONTENT);
            }
      }else {
            checkListItem = ConfigService.getCheckListItemByRequirementId(route, request.getMethod().name(), ValidationRequirementID.TNT_2_2_API_SUB_CSM_400.getValue()    );
            customLogger.init(reqTntEventSubscriptionTO, response, request, checkListItem, route);
            responseMap = mapDecorator.decorate(JsonUtility.convertToMap(reqTntEventSubscriptionTO), response, request, checkListItem);
            if (checkListItem != null){
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            }
            customLogger.log(responseMap, response, request);
            return  new ResponseEntity<>("Event subscription id "+id+" not found", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Map<String, Object>> callCallback(UUID id, ServerHttpResponse response, ServerHttpRequest request)
            throws ExecutionException, InterruptedException, JsonProcessingException{
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String route = request.getPath().toString().substring(0, request.getPath().toString().lastIndexOf("/"));
        TNTEventSubscriptionTO tntEventSubscriptionTO = SqlUtility.getEventSubscriptionBySubscriptionId(id.toString());
        if(tntEventSubscriptionTO.getSubscriptionID() != null) {
            if(tntEventSubscriptionTO.getEventType() == null){
                tntEventSubscriptionTO.setEventType( List.of(EventType.EQUIPMENT, EventType.SHIPMENT, EventType.TRANSPORT, EventType.OPERATIONS));
            }
            if(callBackService.doHeadRequest(tntEventSubscriptionTO, false)){
                CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(route, request.getMethod().name(), ValidationRequirementID.TNT_2_2_CSM_HEAD_200.getValue()) ;
                customLogger.init(tntEventSubscriptionTO, response, request, checkListItem, route);
                responseMap = mapDecorator.decorate(JsonUtility.convertToMap(tntEventSubscriptionTO), response, request, checkListItem);
                String timeStamp = ZonedDateTime.now().minus(1, ChronoUnit.HOURS).toString();
                responseMap.put("eventDateTime", timeStamp);
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
                customLogger.log(responseMap, response, request);
            }else {
                CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(route, request.getMethod().name(), ValidationRequirementID.TNT_2_2_CSM_HEAD_404.getValue()) ;
                customLogger.init(tntEventSubscriptionTO, response, request, checkListItem, route);
                responseMap = mapDecorator.decorate(JsonUtility.convertToMap(tntEventSubscriptionTO), response, request, checkListItem);
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
                customLogger.log(responseMap, response, request);
            }

            if (callBackService.sendNotification(tntEventSubscriptionTO)) { //async call, triggered after config time
                CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(route, request.getMethod().name(), ValidationRequirementID.TNT_2_2_CSM_POST_200.getValue()) ;
                customLogger.init(tntEventSubscriptionTO, response, request, checkListItem, route);
                responseMap = mapDecorator.decorate(JsonUtility.convertToMap(tntEventSubscriptionTO), response, request, checkListItem);
                String timeStamp = ZonedDateTime.now().minus(1, ChronoUnit.HOURS).toString();
                responseMap.put("eventDateTime", timeStamp);
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
                customLogger.log(responseMap, response, request);
            } else {
                CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(route, request.getMethod().name(), ValidationRequirementID.TNT_2_2_CSM_POST_400.getValue()) ;
                customLogger.init(tntEventSubscriptionTO, response, request, checkListItem, route);
                responseMap = mapDecorator.decorate(JsonUtility.convertToMap(tntEventSubscriptionTO), response, request, checkListItem);
                customLogger.log(responseMap, response, request);
            }
            return new ResponseEntity<>(responseMap, HttpStatus.NO_CONTENT);
        }else {
            responseMap.put("SUBSCRIPTION NOT FOUND", "PLEASE FIRST SUBSCRIBE FOR THE EVENT NOTIFICATION BY POST /event-subscriptions");
            return new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
        }
    }

    private String checkSameSecret(TNTEventSubscriptionTO bdTntEventSubscriptionTO, TNTEventSubscriptionTO reqTNTEventSubscriptionTO ){
        String dbSecret = Base64.getEncoder().encodeToString(bdTntEventSubscriptionTO.getSecret());
        String reqSecret = Base64.getEncoder().encodeToString(reqTNTEventSubscriptionTO.getSecret());
        StringBuilder result = new StringBuilder();
        if(dbSecret.equalsIgnoreCase(reqSecret)){
            result.append("Correct secret found. ");
        }else {
            result.append("Wrong secret found. ");
        }
        result.append(checkSameCallbackUrl(bdTntEventSubscriptionTO, reqTNTEventSubscriptionTO));
        return result.toString();
    }

    private String checkSameCallbackUrl(TNTEventSubscriptionTO bdTntEventSubscriptionTO, TNTEventSubscriptionTO reqTNTEventSubscriptionTO ){
        if(bdTntEventSubscriptionTO.getCallbackUrl().equalsIgnoreCase(reqTNTEventSubscriptionTO.getCallbackUrl())){
            return "Same callback url found: "+ bdTntEventSubscriptionTO.getCallbackUrl();
        }else {
            return "Wrong callback url found: "+ reqTNTEventSubscriptionTO.getCallbackUrl();
        }
    }

}
