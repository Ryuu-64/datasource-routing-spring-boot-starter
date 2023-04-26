# datasource-routing-spring-boot-starter

基于 [`AbstractRoutingDataSource`](https://github.com/spring-projects/spring-framework/blob/main/spring-jdbc/src/main/java/org/springframework/jdbc/datasource/lookup/AbstractRoutingDataSource.java) 的数据源路由。

## 使用

配置数据源，并使用注解标记目标方法或类即可使用路由的数据源。

### 配置数据源

在 [`application.yml`](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config) 中进行配置，配置的前缀必须为 `datasource-routing`：

```yaml
datasource-routing:
  data-source-definitions:
    dataSourceKey1:
      url: jdbc:mysql://localhost:****/********
      username: ********
      password: ********
      driver-class-name: com.mysql.cj.jdbc.Driver
    dataSourceKey2:
      url: jdbc:mysql://localhost:****/********
      username: ********
      password: ********
      driver-class-name: com.mysql.cj.jdbc.Driver
```

### 设置上下文

设置上下文中的 `dataSourceKey`

```java
DataSourceContextHolder.getContext().setDataSourceKey(dataSourceKey);
```

注意，设置上下文并使用后，必须将其清除

```
DataSourceContextHolder.getContext().clearDataSourceKey();
```

#### 添加注解

可使用注解自动设置上下文，在需要数据源路由的方法上添加 `@DataSourceRouting`。

 `@DataSourceRouting` 有三种  `RoutingPolicy`。

1. `CONTEXT`

   上下文，从上下文中获取 key，而不是使用注解中的 key

   ```java
   @DataSourceRouting(policy = RoutingPolicy.CONTEXT)
   ```

   或

   ```java
   @DataSourceRouting
   ```

2. `FORCED`

   强制，任何情况下都使用注解中的 key

      ```java
   @DataSourceRouting(key = "dataSourceKey", policy = RoutingPolicy.FORCED)
      ```

3. `FALLBACK`

   备选，使用上下文中获取 key，只有在上下文中没有路由信息时才使用注解中的 key

   ```java
   @DataSourceRouting(key = "dataSourceKey", policy = RoutingPolicy.FALLBACK)
   ```

默认的 `@DataSourceRouting` 

```java
@DataSourceRouting
```

等同于：

```java
@DataSourceRouting(key = "none", policy = RoutingPolicy.CONTEXT)
```

`key` 为 [`application.yml`](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)  `data-source-definitions` 中 key：

```java
@Service
public class UserService {
    //...
    @DataSourceRouting(key = "dataSourceKey1")
    @Override
    public List<User> queryAll() {
    }
    //...
}
```

 `@DataSourceRouting` 可作用于类，被修饰的类中的所有方法都会使用动态数据源：

```java
@Service
@DataSourceRouting(key = "dataSourceKey1")
public class UserService {
    @Override
    public List<User> queryAll() {
    }
}
```

#### 其他方式

除使用 `@DataSourceRouting` 设置 `key`，还可以自行处理。在使用完 `key` 后一定要进行清除，避免读取错误数据和内存泄露。

以 Http Request 为例：

1. 编写一个 Filter 获取 Request 中的 `key` ：

   ```java
   @Component
   @AllArgsConstructor
   public class DataSourceRoutingFilter extends OncePerRequestFilter {
       private static final Logger logger = LoggerFactory.getLogger(DataSourceRoutingFilter.class);
   
       private final ObjectMapper objectMapper;
   
       private final DatasourceRoutingConfig datasourceRoutingConfig;
   
       @Override
       protected void doFilterInternal(
               HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
       ) throws ServletException, IOException {
           tryGetDataSourceKey(request);
           try {
               filterChain.doFilter(request, response);
           } finally {
               DataSourceContextHolder.getContext().clearDataSourceKey();
           }
       }
   
       private void tryGetDataSourceKey(HttpServletRequest request) {
           JsonNode requestBodyjsonNode;
           try {
               requestBodyjsonNode = objectMapper.readTree(request.getReader());
           } catch (IOException e) {
               logger.warn("Error when readTree from request reader.", e);
               return;
           }
           String dataSourceKeyName = datasourceRoutingConfig.getDataSourceKeyName();
           if (!requestBodyjsonNode.has(dataSourceKeyName)) {
               return;
           }
           JsonNode dataSourceKeyNode = requestBodyjsonNode.get(dataSourceKeyName);
           String dataSourceKey = dataSourceKeyNode.asText();
           if (ObjectUtils.isEmpty(dataSourceKey)) {
               return;
           }
   
           DataSourceContextHolder.getContext().setDataSourceKey(dataSourceKey);
       }
   }
   ```

2. 设置 filter bean：

   ```java
   @Configuration
   @AllArgsConstructor
   public class DataSourceRoutingFilterConfig {
       private DataSourceRoutingFilter dataSourceRoutingFilter;
   
       @Bean
       public FilterRegistrationBean<DataSourceRoutingFilter> DataSourceRoutingFilterRegistration() {
           FilterRegistrationBean<DataSourceRoutingFilter> registration = new FilterRegistrationBean<>();
           registration.setFilter(dataSourceRoutingFilter);
           registration.addUrlPatterns("/*");
           registration.setName("dataSourceRoutingFilter");
           return registration;
       }
   }
   ```

3. 设置  `@DataSourceRouting`：

   ```java
   @Service
   @DataSourceRouting
   public class UserService {
       @Override
       public List<User> queryAll() {
       }
   }
   ```

## 另请参阅

[`org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource`](https://github.com/spring-projects/spring-framework/blob/main/spring-jdbc/src/main/java/org/springframework/jdbc/datasource/lookup/AbstractRoutingDataSource.java)  
