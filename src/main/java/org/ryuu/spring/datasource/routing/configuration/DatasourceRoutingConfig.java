package org.ryuu.spring.datasource.routing.configuration;

import lombok.Getter;
import lombok.Setter;
import org.ryuu.spring.datasource.routing.dto.jdbc.DataSourceDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "datasource-routing")
public class DatasourceRoutingConfig {
    @Getter
    @Setter
    private Map<String, DataSourceDefinition> dataSourceDefinitions;

    @Setter
    @Getter
    private String dataSourceKeyName;
}
