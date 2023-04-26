package org.ryuu.spring.datasource.routing;

public class NoDataSourceKeyException extends RuntimeException {
    public NoDataSourceKeyException(String message) {
        super(message);
    }
}