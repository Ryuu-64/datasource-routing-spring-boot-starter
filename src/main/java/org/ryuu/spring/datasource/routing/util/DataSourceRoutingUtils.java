package org.ryuu.spring.datasource.routing.util;

import org.ryuu.spring.datasource.routing.DataSourceRouting;
import org.ryuu.spring.datasource.routing.NoDataSourceKeyException;
import org.ryuu.spring.datasource.routing.RoutingPolicy;
import org.ryuu.spring.datasource.routing.context.DataSourceContext;
import org.ryuu.spring.datasource.routing.context.DataSourceContextHolder;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

public class DataSourceRoutingUtils {
    private DataSourceRoutingUtils() {
    }

    public static void trySetDataSourceKey(DataSourceRouting dataSourceRouting) {
        Objects.requireNonNull(dataSourceRouting, "dataSourceRouting");
        DataSourceContext context = DataSourceContextHolder.getContext();
        RoutingPolicy policy = dataSourceRouting.policy();
        String key = dataSourceRouting.key();
        switch (policy) {
            case FORCED:
                context.setDataSourceKey(key);
                break;
            case FALLBACK:
                if (ObjectUtils.isEmpty(context.getDataSourceKey())) {
                    if (key.equals(DataSourceRouting.DEFAULT_KEY)) {
                        throw new NoDataSourceKeyException("No data source key found in the context");
                    }
                    context.setDataSourceKey(key);
                }
                break;
            case CONTEXT:
                if (ObjectUtils.isEmpty(context.getDataSourceKey())) {
                    throw new NoDataSourceKeyException("No data source key found in the context");
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + policy);
        }
    }
}
