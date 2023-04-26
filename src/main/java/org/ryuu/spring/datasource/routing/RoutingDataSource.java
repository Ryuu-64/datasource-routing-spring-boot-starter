package org.ryuu.spring.datasource.routing;

import lombok.AllArgsConstructor;
import org.ryuu.spring.datasource.routing.configuration.DatasourceRoutingConfig;
import org.ryuu.spring.datasource.routing.dto.jdbc.DataSourceDefinition;
import org.ryuu.spring.datasource.routing.context.DataSourceContextHolder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RoutingDataSource extends AbstractRoutingDataSource {
    private DatasourceRoutingConfig datasourceRoutingConfig;

    @Override
    public void afterPropertiesSet() {
        Map<Object, Object> targetDataSources = datasourceRoutingConfig
                .getDataSourceDefinitions()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            DataSourceDefinition definition = entry.getValue();
                            return DataSourceBuilder
                                    .create()
                                    .url(definition.getUrl())
                                    .username(definition.getUsername())
                                    .password(definition.getPassword())
                                    .driverClassName(definition.getDriverClassName())
                                    .build();
                        }
                ));
        setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getContext().getDataSourceKey();
    }
}
