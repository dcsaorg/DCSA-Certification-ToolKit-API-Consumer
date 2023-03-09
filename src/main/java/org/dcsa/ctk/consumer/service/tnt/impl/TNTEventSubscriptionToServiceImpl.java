package org.dcsa.ctk.consumer.service.tnt.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.ctk.consumer.config.AppProperty;
import org.dcsa.ctk.consumer.constant.CheckListStatus;
import org.dcsa.ctk.consumer.constant.ResponseMockType;
import org.dcsa.ctk.consumer.exception.DecoratorException;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.model.enums.ValidationRequirementId;
import org.dcsa.ctk.consumer.service.callback.CallBackService;
import org.dcsa.ctk.consumer.service.config.impl.ConfigService;
import org.dcsa.ctk.consumer.service.decorator.Decorator;
import org.dcsa.ctk.consumer.service.log.CustomLogger;
import org.dcsa.ctk.consumer.service.mock.MockService;
import org.dcsa.ctk.consumer.service.tnt.TNTEventSubscriptionToService;
import org.dcsa.ctk.consumer.util.APIUtility;
import org.dcsa.ctk.consumer.util.EventUtility;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.dcsa.ctk.consumer.util.SqlUtility;
import org.dcsa.ctk.consumer.webhook.SparkWebHook;
import org.dcsa.tnt.controller.TNTEventSubscriptionTOController;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Validated
@RequiredArgsConstructor
public class TNTEventSubscriptionToServiceImpl implements TNTEventSubscriptionToService<Map<String, Object>> {

    final TNTEventSubscriptionTOController tntServer;

    final Decorator<Map<String, Object>> mapDecorator;

    final Decorator<List<Map<String, Object>>> listDecorator;

    final MockService mockService;

    final CallBackService callBackService;

    final CustomLogger customLogger;

    @Override
    public Map<String, Object> create(TNTEventSubscriptionTO req, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException, JsonProcessingException {
        boolean result = callBackService.doHeadRequest(req, true);
        Map<String, Object> responseMap = new HashMap<>();
        if(!checkApiVersion(req, response, request)){
            responseMap.put("WRONG API VERSION", "ONLY TNT API VERSION 2 SUPPORTED");
            return responseMap;
        }
        TNTEventSubscriptionTO res = null;
        if(result) {
            if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
                res = tntServer.create(req).toFuture().get();
                responseMap = mapDecorator.decorate(JsonUtility.convertToMap(res), response, request, checkListItem);
                if (checkListItem != null) {
                    customLogger.init(null, response, request, checkListItem, null);
                    customLogger.log(checkListItem, responseMap, response, request);
                    checkListItem.setStatus(CheckListStatus.CONFORMANT);
                }
                verifySecretFormat(res.getSecret(), response, request, responseMap);
                TNTEventSubscriptionTO dbTntEventSubscriptionTO = SqlUtility.getEventSubscriptionBySubscriptionId(res.getSubscriptionID().toString());
                if(checkSameSecret(dbTntEventSubscriptionTO, req)){
                    checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_API_CHK_8.getId());
                    customLogger.init(null, response, request, checkListItem, null);
                    customLogger.log(checkListItem, responseMap, response, request);
                    Objects.requireNonNull(checkListItem).setStatus(CheckListStatus.CONFORMANT);
                }
                if(EventUtility.checkEventDateDateAfterNow(AppProperty.EVENT_PATH)){
                    checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_SUB_CSM_7.getId());
                    customLogger.init(null, response, request, checkListItem, null);
                    customLogger.log(checkListItem, responseMap, response, request);
                    Objects.requireNonNull(checkListItem).setStatus(CheckListStatus.CONFORMANT);
                }

                if(responseMap.size() != 0) {
                    String timeStamp = ZonedDateTime.now().minus(1, ChronoUnit.HOURS).toString();
                    responseMap.put("eventDateTime", timeStamp);
                    responseMap.put("eventCreatedDateTime", timeStamp);
                }
                //async call, triggered after config time
                if(callBackService.sendNotification(req)){
                    checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_API_CHK_6.getId());
                    customLogger.init(null, response, request, checkListItem, null);
                    customLogger.log(checkListItem, responseMap, response, request);
                    checkListItem.setStatus(CheckListStatus.CONFORMANT);
                    SparkWebHook.setSubscriptionID(res.getSubscriptionID());
                }
            } else {
                responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request);
                responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
                throw new DecoratorException(responseMap);
            }
        }else{
            checkListItem = ConfigService.getCheckListItemBId(APIUtility.getRoute(request), Objects.requireNonNull(request.getMethod()).name(), ValidationRequirementId.TNT_2_2_API_CHK_10.getId()) ;
            if (checkListItem != null) {
                customLogger.init(null, response, request, checkListItem, null);
                customLogger.log(checkListItem, responseMap, response, request);
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            }
            responseMap.put("SUBSCRIPTION REGISTRATION FAILED", "THE CALLBACK URL DID NOT RESPOND");
        }
        callCallback(Objects.requireNonNull(res).getSubscriptionID(), response, request);
        return responseMap;
    }

    private boolean checkApiVersion(TNTEventSubscriptionTO tntEventSubscriptionTO,ServerHttpResponse response, ServerHttpRequest request ) throws JsonProcessingException {
        Pattern urlVersionPattern = Pattern.compile("v[0-9]");
        Matcher m = urlVersionPattern.matcher(request.getPath().toString());
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String route = "/event-subscriptions";
        CheckListItem checkListItem = null;
        boolean result = false;
        if (m.find()) {
            if(!m.group(0).isBlank()){
                checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_API_CHK_7.getId());
                result = true;
            }else{
                checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_API_CHK_9.getId());
            }
        }
        customLogger.init(tntEventSubscriptionTO, response, request, checkListItem, route);
        responseMap = mapDecorator.decorate(JsonUtility.convertToMap(tntEventSubscriptionTO), response, request, checkListItem);
        checkListItem.setStatus(CheckListStatus.CONFORMANT);
        customLogger.log(checkListItem, responseMap, response, request);
        return result;
    }

    private boolean checkSameSecret(TNTEventSubscriptionTO bdTntEventSubscriptionTO, TNTEventSubscriptionTO reqTNTEventSubscriptionTO ){
        String dbSecret = Base64.getEncoder().encodeToString(bdTntEventSubscriptionTO.getSecret());
        String reqSecret = Base64.getEncoder().encodeToString(reqTNTEventSubscriptionTO.getSecret());
        if(dbSecret.equalsIgnoreCase(reqSecret)){
            return true;
        }else {
            return false;
        }
    }
    private void verifySecretFormat(byte[] secretByte, ServerHttpResponse response, ServerHttpRequest request, Map<String, Object> responseMap) throws JsonProcessingException {
        if( APIUtility.isBase64Encoded(Base64.getEncoder().encodeToString(secretByte)) ){
            CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_SUB_CSM_3.getId());
            if(checkListItem != null){
                customLogger.init(null, response, request, checkListItem, null);
                customLogger.log(checkListItem, responseMap, response, request);
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            }
        }else{
            CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_SUB_CSM_6.getId());
            if(checkListItem != null){
                customLogger.init(null, response, request, checkListItem, null);
                customLogger.log(checkListItem, responseMap, response, request);
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            }
        }
        if(APIUtility.verifySignature(Base64.getEncoder().encodeToString(secretByte))){
            CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_SUB_CSM_4.getId());
            if(checkListItem != null){
                customLogger.init(null, response, request, checkListItem, null);
                customLogger.log(checkListItem, responseMap, response, request);
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            }
        }else{
            CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_SUB_CSM_5.getId());
            if(checkListItem != null){
                customLogger.init(null, response, request, checkListItem, null);
                customLogger.log(checkListItem, responseMap, response, request);
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            }
        }
    }

    @Override
    public List<Map<String, Object>> findAll(ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        List<Map<String, Object>> responseList;
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            List<TNTEventSubscriptionTO> actualResponse = tntServer.findAll(response, request).collectList().toFuture().get();
            if (actualResponse.size() == 0) checkListItem = null;
            responseList = listDecorator.decorate(JsonUtility.convertToList(actualResponse), response, request, checkListItem);
            if (checkListItem != null) {
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            }
            return responseList;
        } else {
            Map<String, Object> responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request);
            responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
            checkListItem.setStatus(CheckListStatus.CONFORMANT);
            throw new DecoratorException(responseMap);
        }
    }

    @Override
    public Map<String, Object> findById(UUID subscriptionId, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        Map<String, Object> responseMap;
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            TNTEventSubscriptionTO actualResponse = tntServer.findById(subscriptionId).toFuture().get();
            responseMap = mapDecorator.decorate(JsonUtility.convertToMap(actualResponse), response, request, checkListItem);
            if (checkListItem != null) {
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            }
            return responseMap;
        } else {
            responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request);
            responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
            checkListItem.setStatus(CheckListStatus.CONFORMANT);
            throw new DecoratorException(responseMap);
        }
    }

    @Override
    public void delete(UUID subscriptionId, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            tntServer.deleteById(subscriptionId).toFuture().get();
            if (checkListItem != null) {
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            }
        } else {
            Map<String, Object> responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request);
            responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
            checkListItem.setStatus(CheckListStatus.CONFORMANT);
            throw new DecoratorException(responseMap);
        }
    }

    @Override
    public Map<String, Object> update(UUID subscriptionId, TNTEventSubscriptionTO req, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        Map<String, Object> responseMap  = new HashMap<>();
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            TNTEventSubscriptionTO actualResponse = tntServer.update(subscriptionId, req).toFuture().get();
            if(actualResponse != null){
                callBackService.doHeadRequest(actualResponse,false);
                responseMap = mapDecorator.decorate(JsonUtility.convertToMap(actualResponse), response, request, checkListItem);
                if (checkListItem != null) {
                    checkListItem.setStatus(CheckListStatus.CONFORMANT);
                }
            }else {
                if (checkListItem != null) {
                    checkListItem.setStatus(CheckListStatus.CONFORMANT);
                }
                responseMap.put("404 (Not Found)", "EventSubscription not found");
            }
            return responseMap;
        } else {
            responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request);
            responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
            checkListItem.setStatus(CheckListStatus.CONFORMANT);
            throw new DecoratorException(responseMap);
        }
    }

    @Override
    public void updateSecret(UUID subscriptionId, EventSubscriptionSecretUpdateTO req, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException, JsonProcessingException {
        Map<String, Object> responseMap = new HashMap<>();
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            verifySecretFormat(req.getSecret(), response,request, responseMap);
            tntServer.updateSecret(subscriptionId, req).toFuture().get();
            if (checkListItem != null) {
                APIUtility.setDescription(checkListItem, subscriptionId.toString());
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            }
            callBackService.sendNotification(subscriptionId, req);
        } else {
            responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request);
            responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
            checkListItem.setStatus(CheckListStatus.CONFORMANT);
            throw new DecoratorException(responseMap);
        }
    }
    public ResponseEntity<Map<String, Object>> callCallback(UUID subscriptionId, ServerHttpResponse response, ServerHttpRequest request)
            throws ExecutionException, InterruptedException, JsonProcessingException{
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String route = request.getPath().toString().substring(0, request.getPath().toString().lastIndexOf("/"));
        TNTEventSubscriptionTO tntEventSubscriptionTO = SqlUtility.getEventSubscriptionBySubscriptionId(subscriptionId.toString());
        if(tntEventSubscriptionTO.getSubscriptionID() != null) {
            if(tntEventSubscriptionTO.getEventType() == null){
                tntEventSubscriptionTO.setEventType( List.of(EventType.EQUIPMENT, EventType.SHIPMENT, EventType.TRANSPORT, EventType.OPERATIONS));
            }
            if(callBackService.doHeadRequest(tntEventSubscriptionTO, false)){
                CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_SUB_CSM_1.getId());
                APIUtility.setDescription(Objects.requireNonNull(checkListItem), subscriptionId.toString());
                customLogger.init(tntEventSubscriptionTO, response, request, checkListItem, route);
                responseMap = mapDecorator.decorate(JsonUtility.convertToMap(tntEventSubscriptionTO), response, request, checkListItem);
                String timeStamp = ZonedDateTime.now().minus(1, ChronoUnit.HOURS).toString();
                responseMap.put("eventDateTime", timeStamp);
                customLogger.log(checkListItem, responseMap, response, request);
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            }else {
                CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_SUB_CSM_8.getId());
                customLogger.init(tntEventSubscriptionTO, response, request, checkListItem, route);
                responseMap = mapDecorator.decorate(JsonUtility.convertToMap(tntEventSubscriptionTO), response, request, checkListItem);
                customLogger.log(checkListItem, responseMap, response, request);
                Objects.requireNonNull(checkListItem).setStatus(CheckListStatus.CONFORMANT);
            }
            //async call
            if (callBackService.sendNotification(tntEventSubscriptionTO)) {
                CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_SUB_CSM_1.getId());
                APIUtility.setDescription(Objects.requireNonNull(checkListItem), subscriptionId.toString());
                customLogger.init(tntEventSubscriptionTO, response, request, checkListItem, route);
                responseMap = mapDecorator.decorate(JsonUtility.convertToMap(tntEventSubscriptionTO), response, request, checkListItem);
                String timeStamp = ZonedDateTime.now().minus(1, ChronoUnit.HOURS).toString();
                responseMap.put("eventDateTime", timeStamp);
                customLogger.log(checkListItem, responseMap, response, request);
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            } else {
                CheckListItem checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_SUB_CSM_8.getId());
                customLogger.init(tntEventSubscriptionTO, response, request, checkListItem, route);
                responseMap = mapDecorator.decorate(JsonUtility.convertToMap(tntEventSubscriptionTO), response, request, checkListItem);
                customLogger.log(checkListItem, responseMap, response, request);
                Objects.requireNonNull(checkListItem).setStatus(CheckListStatus.CONFORMANT);
            }
            return new ResponseEntity<>(responseMap, HttpStatus.FOUND);
        }else {
            responseMap.put("SUBSCRIPTION NOT FOUND", "PLEASE FIRST SUBSCRIBE FOR THE EVENT NOTIFICATION BY POST /event-subscriptions");
            return new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
        }
    }
}
