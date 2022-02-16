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
}
