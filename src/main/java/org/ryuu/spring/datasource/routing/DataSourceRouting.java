package org.ryuu.spring.datasource.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.ryuu.spring.datasource.routing.RoutingPolicy.CONTEXT;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceRouting {
    String DEFAULT_KEY = "none";

    String key() default DEFAULT_KEY;

    RoutingPolicy policy() default CONTEXT;
}
