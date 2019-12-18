# Base Service

## 主要功能

- 角色管理
- 菜单管理
- 用户管理
- 租户管理
- 权限刷新
- 单据权限管理

## 功能介绍

- 角色

  将角色分配给用户时，角色相关标签将发送到 `devops` 进行处理，相应的角色将分配给 gitlab。

  在`base-service` 服务中一共有三个内置角色：

  - 平台管理员：拥有平台全局布局的所有权限；

  - 组织管理员：拥有单个组织的组织布局的所有权限；

  - 项目所有者：拥有单个项目的项目布局的所有权限。

- User

  服务初始化之后，admin 用户将被内置。admin 用户拥有所有的平台范围权限，即包括所有组织和所有项目的所有权限。

  每当创建、修改和删除用户时将会导致发送相应的事件，同时 `gitlab` 将会同步该事件以执行相应的操作。

- Privilege

  服务中的所有接口都是通过 `@Permission` 注解去声明权限。通过 `eureka-server`  和 `manager-service`  的处理，所有服务的权限信息将自动刷入数据库以生效。

  `@Permission` 注解可以将接口为如下几种类型：

  - 公共访问，即无需登录即可访问
  - 登录访问，即需要登录后才可以访问
  - 全局层接口
  - 组织层接口
  - 项目级接口

- Organization

  服务初始化之后，`operational organization` 组织是被内置的，同时 admin 用户拥有该组织的所有权限。

- Client

  内置客户端的添加、删除和修改是组织层的接口，对应于通过 `oauth-server` 登录所需的 `客户端`。

- Password policy

## 服务配置

- `application.yml`

  ```yaml
  spring:
    datasource:
      url: jdbc:mysql://localhost/base_service?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true
      username: choerodon
      password: 123456
    servlet: #设置上传文件最大为10M
      multipart:
        max-file-size: 10MB # 单个文件最大上传大小
        max-request-size: 10MB # 总上传文件最大上传大小
    redis:
      host: localhost
      port: 6379
      database: 1
    mvc:
      static-path-pattern: /**
    resources:
      static-locations: classpath:/static,classpath:/public,classpath:/resources,classpath:/META-INF/resources,file:/dist
  choerodon:
    gateway:
      url: http://api.example.choerodon.com
    organization:
      link:
        complete: http://localhost:8080/#/organization/register-organization
    category:
      enabled: false # 是否开启项目/组织类型控制
    devops:
      message: true
    eureka:
      event:
        max-cache-size: 300
        retry-time: 5
        retry-interval: 3
        skip-services: config**, **register-server, **gateway**, zipkin**, hystrix**, oauth**
    saga:
      consumer:
        enabled: true # 启动消费端
        thread-num: 2 # saga消息消费线程池大小
        max-poll-size: 200 # 每次拉取消息最大数量
        poll-interval-ms: 1000 # 拉取间隔，默认1000毫秒
    schedule:
      consumer:
        enabled: true # 启用任务调度消费端
        thread-num: 1 # 任务调度消费线程数
        poll-interval-ms: 1000 # 拉取间隔，默认1000毫秒
    cleanPermission: false
  eureka:
    instance:
      preferIpAddress: true
      leaseRenewalIntervalInSeconds: 10
      leaseExpirationDurationInSeconds: 30
      metadata-map:
        VERSION: v1
    client:
      serviceUrl:
        defaultZone: ${EUREKA_DEFAULT_ZONE:http://localhost:8000/eureka/}
      registryFetchIntervalSeconds: 10
  hystrix:
    command:
      default:
        execution:
          isolation:
            thread:
              timeoutInMilliseconds: 15000
  ribbon:
    ReadTimeout: 5000
    ConnectTimeout: 5000
  file-service:
    ribbon:
      ReadTimeout: 60000
      ConnectTimeout: 60000
  notify-service:
    ribbon:
      ReadTimeout: 15000
      ConnectTimeout: 15000
  mybatis:
    mapperLocations: classpath*:/mapper/*.xml
    configuration: # 数据库下划线转驼峰配置
      mapUnderscoreToCamelCase: true
  db:
    type: mysql
  ```

- `bootstrap.yml`

  ```yaml
  server:
    port: 8030
  spring:
    application:
      name: base-service
    cloud:
      config:
        failFast: true
        retry:
          maxAttempts: 6
          multiplier: 1.5
          maxInterval: 2000
        uri: localhost:8010
        enabled: false
  management:
    endpoint:
      health:
        show-details: ALWAYS
    server:
      port: 8031
    endpoints:
      web:
        exposure:
          include: '*'
  ```

## 环境需求

- mysql 5.6+
- redis 3.0+
- 该项目是一个 Eureka Client 项目启动后需要注册到 `EurekaServer`，本地环境需要 `eureka-server`，线上环境需要使用 `go-register-server`

## 安装和启动步骤

- 运行 `eureka-server`，[代码库地址](https://github.com/choerodon/eureka-server.git)。

- 拉取当前项目到本地

  ```sh
  git clone https://github.com/choerodon/base-service.git
  ```

- 创建数据库，本地创建 `base_service` 数据库和默认用户，示例如下：

  ```sql
  CREATE USER 'choerodon'@'%' IDENTIFIED BY "123456";
  CREATE DATABASE base_service DEFAULT CHARACTER SET utf8;
  GRANT ALL PRIVILEGES ON base_service.* TO choerodon@'%';
  FLUSH PRIVILEGES;
  ```

- 初始化 `base_service` 数据库，运行项目根目录下的 `init-mysql-database.sh`，该脚本默认初始化数据库的地址为 `localhost`，若有变更需要修改脚本文件

  ```sh
  sh init-mysql-database.sh
  ```

- 本地启动redis-server

- 启动项目，项目根目录下执行如下命令：

  ```sh
   mvn spring-boot:run
  ```
  
## 更新日志

- [更新日志](./CHANGELOG.zh-CN.md)


## 如何参与

欢迎参与我们的项目，了解更多有关如何[参与贡献](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md)的信息。