## 使用说明

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

  在`hzero-iam` 服务中一共有三个内置角色：

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

## 环境需求

- mysql 5.7+
- redis 3.0+
- 该项目是一个 Eureka Client 项目启动后需要注册到 `EurekaServer`，本地环境需要 `eureka-server`，线上环境需要使用 `go-register-server`

## 安装和启动步骤

- 运行 `eureka-server`，[代码库地址](https://github.com/choerodon/eureka-server.git)。
- 运行 `hzero-platform`，[代码库地址](https://github.com/choerodon/hzero-platform.git)。

- 拉取当前项目到本地

  ```sh
  git clone https://github.com/choerodon/hzero-iam.git
  ```

- 创建数据库，本地创建 `hzero_platform` 数据库和默认用户，示例如下：

  ```sql
  CREATE USER 'choerodon'@'%' IDENTIFIED BY "123456";
  CREATE DATABASE hzero_platform DEFAULT CHARACTER SET utf8;
  GRANT ALL PRIVILEGES ON hzero_platform.* TO choerodon@'%';
  FLUSH PRIVILEGES;
  ```

- 初始化 `base_service` 数据库，运行项目根目录下的 `init-mysql-database.sh`，该脚本默认初始化数据库的地址为 `localhost`，若有变更需要修改脚本文件

  ```sh
  sh init-database.sh
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