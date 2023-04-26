package org.ryuu.spring.datasource.routing.context;

public class ThreadLocalDataSourceContext implements DataSourceContext {
    private static final ThreadLocal<Object> DATA_SOURCE_KEY = new ThreadLocal<>();

    @Override
    public void setDataSourceKey(Object dataSourceKey) {
        DATA_SOURCE_KEY.set(dataSourceKey);
    }

    @Override
    public Object getDataSourceKey() {
        return DATA_SOURCE_KEY.get();
    }

    @Override
    public void clearDataSourceKey() {
        DATA_SOURCE_KEY.remove();
    }
}
