package com.blend.routercore.exception;


public class NoRouteFoundException extends RuntimeException {

    public NoRouteFoundException(String detailMessage) {
        super(detailMessage);
    }
}
