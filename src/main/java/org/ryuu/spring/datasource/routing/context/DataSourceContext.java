package org.ryuu.spring.datasource.routing.context;

public interface DataSourceContext {
    void setDataSourceKey(Object dataSourceKey);

    Object getDataSourceKey();

    void clearDataSourceKey();
}
