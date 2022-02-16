package org.dcsa.ctk.consumer.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.exception.DeleteException;
import org.dcsa.core.exception.NotFoundException;
import org.dcsa.core.exception.UpdateException;
import org.dcsa.ctk.consumer.constant.CheckListStatus;
import org.dcsa.ctk.consumer.service.config.impl.ConfigService;
import org.dcsa.ctk.consumer.service.log.CustomLogger;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.model.ErrorResponse;
import org.dcsa.ctk.consumer.util.JsonUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.r2dbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebInputException;

import javax.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvise {

    @Autowired
    CustomLogger customLogger;
    @ExceptionHandler({DecoratorException.class})
    public ResponseEntity<Map<String, Object>> handleDecoratorException(Exception ex, ServerHttpResponse response, ServerHttpRequest request) throws JsonProcessingException {
        Map<String, Object> responseMap = ((DecoratorException) ex).getErrorResponse();
        log.info(JsonUtility.beautify(customLogger.log(responseMap, response, request)));
        return new ResponseEntity<>(responseMap, Objects.requireNonNull(response.getStatusCode()));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Map<String, Object>> handle(Exception ex, ServerHttpResponse response, ServerHttpRequest request) throws JsonProcessingException {
        HttpStatus httpCode = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof ExecutionException)
            ex = (Exception) ex.getCause();
        if (ex instanceof DeleteException || ex instanceof UpdateException || ex instanceof CreateException || ex instanceof ServerWebInputException || ex instanceof BadSqlGrammarException || ex instanceof ConstraintViolationException) {
            httpCode = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof NotFoundException) {
            httpCode = HttpStatus.NOT_FOUND;
        }
        if (response.getRawStatusCode() != null)
            response.setStatusCode(httpCode);
        response.getHeaders().remove("checkListItemKey");
        List<String> routeKeyHeader = response.getHeaders().get("routeKey");
        if (routeKeyHeader != null) {
            CheckListItem checkListItem = ConfigService.getCheckListItemForHttpCode(routeKeyHeader.get(0), httpCode.value());
            if (checkListItem != null) {
                response.getHeaders().add("checkListItemKey", checkListItem.getId());
                checkListItem.setStatus(CheckListStatus.COVERED);
            }
        }
        Map<String, Object> responseMap = getMockedErrorResponse(ex, response, request);
        log.info(JsonUtility.beautify(customLogger.log(responseMap, response, request)));
        return new ResponseEntity<>(responseMap, Objects.requireNonNull(response.getStatusCode()));
    }

    public  Map<String, Object> getMockedErrorResponse(Exception ex, ServerHttpResponse response, ServerHttpRequest request) {
        ErrorResponse res = new ErrorResponse();
        res.setRequestUri(request.getURI().toString());
        res.setHttpMethod(request.getMethod().toString());
        HttpStatus httpCode = response.getStatusCode();
        res.setStatusCode(httpCode.value());
        res.setStatusCodeText(httpCode.getReasonPhrase());
        Map<String, String> error = new LinkedHashMap<>();
        error.put("reason", httpCode.getReasonPhrase().replaceAll(" ", ""));
        error.put("message", ex.getLocalizedMessage());
        res.setErrors(error);
        return JsonUtility.convertToMap(res);
    }
}
