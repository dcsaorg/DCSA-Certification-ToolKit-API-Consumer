package org.dcsa.ctk.consumer.service.tnt.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
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
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.dcsa.tnt.controller.TNTEventSubscriptionTOController;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
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
        if(result) {
            TNTEventSubscriptionTO res;
            if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
                res = tntServer.create(req).toFuture().get();
                responseMap = mapDecorator.decorate(JsonUtility.convertToMap(res), response, request, checkListItem);
                if (checkListItem != null) {
                    customLogger.init(null, response, request, checkListItem, null);
                    customLogger.log(checkListItem, responseMap, response, request);
                    checkListItem.setStatus(CheckListStatus.CONFORMANT);
                }
                if(responseMap.size() != 0) {
                    String timeStamp = ZonedDateTime.now().minus(1, ChronoUnit.HOURS).toString();
                    responseMap.put("eventDateTime", timeStamp);
                    responseMap.put("eventCreatedDateTime", timeStamp);
                }
                //async call, triggered after config time
                if(callBackService.sendNotification(req)){
                    checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_SUB_CSM_1.getId());
                    customLogger.init(null, response, request, checkListItem, null);
                    customLogger.log(checkListItem, responseMap, response, request);
                    checkListItem.setStatus(CheckListStatus.CONFORMANT);
                }
                return responseMap;
            } else {
                responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request);
                responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
                throw new DecoratorException(responseMap);
            }
        }else{
            checkListItem = ConfigService.getCheckListItemBId(APIUtility.getRoute(request), Objects.requireNonNull(request.getMethod()).name(), ValidationRequirementId.TNT_2_2_SUB_CSM_HEAD_404.getId()) ;
            if (checkListItem != null) {
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            }
            responseMap.put("SUBSCRIPTION REGISTRATION FAILED", "THE CALLBACK URL DID NOT RESPOND");
        }
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
                checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_API_CHK_1.getId());
                result = true;
            }else{
                checkListItem = ConfigService.getCheckListItemByRequirementId(ValidationRequirementId.TNT_2_2_API_CHK_2.getId());
            }
        }
        customLogger.init(tntEventSubscriptionTO, response, request, checkListItem, route);
        responseMap = mapDecorator.decorate(JsonUtility.convertToMap(tntEventSubscriptionTO), response, request, checkListItem);
        checkListItem.setStatus(CheckListStatus.CONFORMANT);
        customLogger.log(checkListItem, responseMap, response, request);
        return result;
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
    public Map<String, Object> findById(UUID id, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        Map<String, Object> responseMap;
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            TNTEventSubscriptionTO actualResponse = tntServer.findById(id).toFuture().get();
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
    public void delete(UUID id, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        List<Map<Class<?>, String>> parametersList = null;
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            tntServer.deleteById(id).toFuture().get();
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
    public Map<String, Object> update(UUID id, TNTEventSubscriptionTO req, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        Map<String, Object> responseMap  = new HashMap<>();
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            TNTEventSubscriptionTO actualResponse = tntServer.update(id, req).toFuture().get();
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
    public void updateSecret(UUID id, EventSubscriptionSecretUpdateTO req, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        Map<String, Object> responseMap;
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            tntServer.updateSecret(id, req).toFuture().get();
            if (checkListItem != null) {
                checkListItem.setStatus(CheckListStatus.CONFORMANT);
            }
            callBackService.sendNotification(id, req);
        } else {
            responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request);
            responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
            checkListItem.setStatus(CheckListStatus.CONFORMANT);
            throw new DecoratorException(responseMap);
        }
    }
}
