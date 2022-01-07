package org.dcsa.ctk.consumer.exceptions;

import org.dcsa.ctk.consumer.model.ErrorResponse;

import java.util.Map;

public class DecoratorException extends RuntimeException{
    private Map<String,Object> errorResponse;
    public DecoratorException(Map<String,Object>  errorResponse) {
        this.errorResponse=errorResponse;
    }
    public Map<String,Object> getErrorResponse()
    {
        return this.errorResponse;
    }
}
