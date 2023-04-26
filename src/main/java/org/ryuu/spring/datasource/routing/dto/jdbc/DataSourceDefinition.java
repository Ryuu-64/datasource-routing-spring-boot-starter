package org.ryuu.spring.datasource.routing.dto.jdbc;

import lombok.Data;

@Data
public class DataSourceDefinition {
    private String driverClassName;

    private String url;

    private String username;

    private String password;
}
