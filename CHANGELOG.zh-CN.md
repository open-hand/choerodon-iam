# Changelog

这个项目的所有显著变化都将被记录在这个文件中。

# [0.19.3] - 2019-11-11

## 后端

### 新增

- `OAUTH_ACCESS_TOKEN` 表添加了关于列 `AUTHENTICATION_ID` 的索引

### 修改

- 初始化权限，删除权限时级联删除角色权限
- 优化了`CHECKPERMISSION` 的查询

### 移除

- 移除了依赖`low-code-sdk`