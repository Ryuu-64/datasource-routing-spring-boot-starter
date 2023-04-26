package org.ryuu.spring.datasource.routing.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.ryuu.spring.datasource.routing.DataSourceRouting;
import org.ryuu.spring.datasource.routing.context.DataSourceContextHolder;
import org.ryuu.spring.datasource.routing.util.DataSourceRoutingUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceRoutingAspect {
    @Pointcut(
            "@annotation(org.ryuu.spring.datasource.routing.DataSourceRouting)|| " +
            "within(@org.ryuu.spring.datasource.routing.DataSourceRouting *)"
    )
    public void dataSourceRoutingPointCut() {
    }

    @Around("dataSourceRoutingPointCut()")
    public Object dataSourceRouting(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        DataSourceRouting dataSourceRouting = signature.getMethod().getAnnotation(DataSourceRouting.class);
        if (dataSourceRouting == null) {
            Class<?> targetClass = point.getTarget().getClass();
            dataSourceRouting = AnnotationUtils.findAnnotation(targetClass, DataSourceRouting.class);
        }
        if (dataSourceRouting == null) {
            throw new IllegalStateException("DataSourceRouting not found.");
        }
        DataSourceRoutingUtils.trySetDataSourceKey(dataSourceRouting);
        try {
            return point.proceed();
        } finally {
            DataSourceContextHolder.getContext().clearDataSourceKey();
        }
    }
}
