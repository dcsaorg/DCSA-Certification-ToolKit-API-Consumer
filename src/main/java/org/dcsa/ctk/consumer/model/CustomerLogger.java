package org.dcsa.ctk.consumer.model;

import lombok.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "prototype")
@Data
public class CustomerLogger {
    String url;
    String httpMethod;
    int responseStatus;
    HttpHeaders requestHeader;
    HttpHeaders responseHeader;
    Object request;
    Object response;

/*    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public HttpHeaders getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(HttpHeaders requestHeader) {
        this.requestHeader = requestHeader;
    }

    public HttpHeaders getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(HttpHeaders responseHeader) {
        this.responseHeader = responseHeader;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }*/
}
