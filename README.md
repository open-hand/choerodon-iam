# choerodon-iam
用户身份管理服务

## Introduction
权限管理服务，平台统一的权限体系架构。此服务是对[hzero-iam](https://github.com/open-hand/hzero-iam.git)的二开，定制化开发了项目相关的功能，包括用户项目权限等。

## Documentation
- 更多详情请参考`hzero-iam`[中文文档](http://open.hand-china.com/document-center/doc/application/10038/10150?doc_id=4900)


## Features
- 租户管理：租户定义及管理
- 角色管理：支持SaaS的多级角色管理体系，支持一键下分权限和回收权限
- 菜单管理：标准菜单管理、多前端菜单管理，维护权限集和API权限
- 用户管理：多租户用户管理，可给用户分配角色、分配客户端、分配数据权限等
- 配置管理：三方应用、客户端、域名等配置的维护管理
- 权限管理：用于配置业务单据类型以及可控制的权限维度基础数据
- 项目管理：项目定义及管理

## Architecture

* 基础架构

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/6657a3b9adb14deda7032726558bcf65/image.png)

* 多级管理员体系

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/12b076e089744736ab210942b2bd9fa8/image.png)


## Dependencies


* 服务依赖

```xml
<dependency>
    <groupId>org.hzero</groupId>
    <artifactId>hzero-iam-saas</artifactId>
    <version>${hzero.service.version}</version>
</dependency>

```
## Data initialization

- 使用`hzero_platform` 数据库，如果在`hzero_platform` 已经创建过该库，可忽略步骤2

- 创建数据库，本地创建 `hzero_platform` 数据库和默认用户，示例如下：

  ```sql
  CREATE USER 'choerodon'@'%' IDENTIFIED BY "123456";
  CREATE DATABASE hzero_platform DEFAULT CHARACTER SET utf8;
  GRANT ALL PRIVILEGES ON hzero_platform.* TO choerodon@'%';
  FLUSH PRIVILEGES;
  ```
  
- 初始化 `hzero_platform` 数据库，运行项目根目录下的 `init-database.sh`，该脚本默认初始化数据库的地址为 `localhost`，若有变更需要修改脚本文件

  ```sh
  sh init-database.sh
  ```
  
## Changelog

- [更新日志](./CHANGELOG.zh-CN.md)


## Contributing

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建Issue讨论新特性或者变更。

Copyright (c) 2020-present, CHOERODON
