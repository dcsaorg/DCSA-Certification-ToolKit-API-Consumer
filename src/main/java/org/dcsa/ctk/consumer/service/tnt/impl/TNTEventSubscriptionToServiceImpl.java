package org.dcsa.ctk.consumer.service.tnt.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.ctk.consumer.constant.CheckListStatus;
import org.dcsa.ctk.consumer.constant.ResponseMockType;
import org.dcsa.ctk.consumer.exception.DecoratorException;
import org.dcsa.ctk.consumer.model.CheckListItem;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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
    public Map<String, Object> create(TNTEventSubscriptionTO req, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        boolean result = callBackService.doHeadRequest(req.getCallbackUrl(), true);
        Map<String, Object> responseMap = new HashMap<>();
        if(result) {
            TNTEventSubscriptionTO res;
            if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
                res = tntServer.create(req).toFuture().get();
                responseMap = mapDecorator.decorate(JsonUtility.convertToMap(res), response, request, checkListItem);
                if (checkListItem != null) {
                    checkListItem.setStatus(CheckListStatus.COVERED);
                }
                callBackService.sendNotification(req);//async call, triggered after config time
            } else {
                responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request);
                responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
                checkListItem.setStatus(CheckListStatus.COVERED);
                throw new DecoratorException(responseMap);
            }
        }else{
            String route = request.getPath().toString().replace("/v2", "");
            checkListItem = ConfigService.getNextCheckListItem(route, request.getMethod().name(), 400);
            if (checkListItem != null) {
                checkListItem.setStatus(CheckListStatus.COVERED);
            }
            responseMap.put("SUBSCRIPTION REGISTRATION FAILED", "THE CALLBACK URL DID NOT RESPOND");
        }
        return responseMap;
    }

    @Override
    public List<Map<String, Object>> findAll(ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        List<Map<String, Object>> responseList;
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            List<TNTEventSubscriptionTO> actualResponse = tntServer.findAll(response, request).collectList().toFuture().get();
            if (actualResponse.size() == 0) checkListItem = null;
            responseList = listDecorator.decorate(JsonUtility.convertToList(actualResponse), response, request, checkListItem);
            if (checkListItem != null)
                checkListItem.setStatus(CheckListStatus.COVERED);
            return responseList;
        } else {
            Map<String, Object> responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request);
            responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
            checkListItem.setStatus(CheckListStatus.COVERED);
            throw new DecoratorException(responseMap);
        }
    }

    @Override
    public Map<String, Object> findById(UUID id, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        Map<String, Object> responseMap;
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            TNTEventSubscriptionTO actualResponse = tntServer.findById(id).toFuture().get();
            responseMap = mapDecorator.decorate(JsonUtility.convertToMap(actualResponse), response, request, checkListItem);
            if (checkListItem != null)
                checkListItem.setStatus(CheckListStatus.COVERED);
            return responseMap;
        } else {
            responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request);
            responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
            checkListItem.setStatus(CheckListStatus.COVERED);
            throw new DecoratorException(responseMap);
        }
    }

    @Override
    public void delete(UUID id, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            tntServer.deleteById(id).toFuture().get();
            if (checkListItem != null)
                checkListItem.setStatus(CheckListStatus.COVERED);
        } else {
            Map<String, Object> responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request);
            responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
            checkListItem.setStatus(CheckListStatus.COVERED);
            throw new DecoratorException(responseMap);
        }
    }

    @Override
    public Map<String, Object> update(UUID id, TNTEventSubscriptionTO req, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        Map<String, Object> responseMap  = new HashMap<>();
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            TNTEventSubscriptionTO actualResponse = tntServer.update(id, req).toFuture().get();
            if(actualResponse != null){
                callBackService.doHeadRequest(actualResponse.getCallbackUrl(),false);
                responseMap = mapDecorator.decorate(JsonUtility.convertToMap(actualResponse), response, request, checkListItem);
                if (checkListItem != null) {
                    checkListItem.setStatus(CheckListStatus.COVERED);
                }
            }else {
                if (checkListItem != null) {
                    checkListItem.setStatus(CheckListStatus.COVERED);
                }
                responseMap.put("404 (Not Found)", "EventSubscription not found");
            }
            return responseMap;
        } else {
            responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request);
            responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
            checkListItem.setStatus(CheckListStatus.COVERED);
            throw new DecoratorException(responseMap);
        }
    }

    @Override
    public void updateSecret(UUID id, EventSubscriptionSecretUpdateTO req, ServerHttpResponse response, ServerHttpRequest request, CheckListItem checkListItem) throws ExecutionException, InterruptedException {
        Map<String, Object> responseMap;
        if (checkListItem == null || APIUtility.isReferenceCallRequired(checkListItem.getResponseDecoratorWrapper().getHttpCode())) {
            tntServer.updateSecret(id, req).toFuture().get();
            if (checkListItem != null)
                checkListItem.setStatus(CheckListStatus.COVERED);
          callBackService.sendNotification(id, req);
        } else {
            responseMap = mockService.getMockedResponse(ResponseMockType.ERROR_RESPONSE, request);
            responseMap = mapDecorator.decorate(responseMap, response, request, checkListItem);
            checkListItem.setStatus(CheckListStatus.COVERED);
            throw new DecoratorException(responseMap);
        }
    }
}
