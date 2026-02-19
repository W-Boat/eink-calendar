# Gradle Wrapper ClassNotFoundException 解决方案

## 问题诊断

GitHub Actions 构建失败，错误信息：
```
Could not find or load main class org.gradle.wrapper.GradleWrapperMain
Caused by: java.lang.ClassNotFoundException: org.gradle.wrapper.GradleWrapperMain
Error: Process completed with exit code 1
```

这个错误表示 `gradle-wrapper.jar` 文件缺失或无法访问。

## 根本原因

1. gradle-wrapper.jar 不在版本控制中
2. Gradle Wrapper 初始化步骤不正确
3. 旧的 gradle-build-action 无法正确处理 wrapper

## 官方解决方案

使用 **gradle/actions/setup-gradle@v3** - Gradle 官方推荐的现代解决方案。

### 工作原理

`gradle/actions/setup-gradle@v3` 自动处理：
- gradle-wrapper.jar 的检测和验证
- Gradle 分发版本的缓存
- Wrapper 校验和验证（安全检查）
- 依赖解析缓存
- 无需手动 JAR 初始化

### GitHub Actions 配置

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          lfs: true  # 支持 Git LFS

      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: gradle

      # 使用官方 Gradle Actions
      - uses: gradle/actions/setup-gradle@v3
        with:
          gradle-version: wrapper  # 使用项目的 wrapper 版本

      # 现在可以正常运行 Gradle
      - run: ./gradlew build
```

## 关键改进

1. **移除手动权限修复**
   - 不再需要 `chmod +x ./gradlew`
   - setup-gradle 自动处理

2. **自动 JAR 验证**
   - setup-gradle 自动检测 gradle-wrapper.jar
   - 验证 wrapper 完整性
   - 无需手动下载或生成

3. **Git LFS 支持**
   - 添加 `lfs: true` 到 checkout
   - 正确处理可能存储在 Git LFS 中的二进制文件

4. **简化配置**
   - `gradle-version: wrapper` 使用项目配置的版本
   - 自动缓存 Gradle 分发版本
   - 无需在工作流中指定版本号

## 本项目的变更

### 工作流更新

从：
```yaml
uses: gradle/gradle-build-action@v2
with:
  gradle-version: 8.2
run: chmod +x ./gradlew
```

改为：
```yaml
uses: gradle/actions/setup-gradle@v3
with:
  gradle-version: wrapper
# 不需要 chmod 步骤
```

### Checkout 更新

从：
```yaml
- uses: actions/checkout@v4
```

改为：
```yaml
- uses: actions/checkout@v4
  with:
    lfs: true
```

## 为什么 gradle-wrapper.jar 通常不在 Git 中

1. **历史原因** - 早期的 Gradle 建议不提交 JAR
2. **文件大小** - gradle-wrapper.jar 约 5MB
3. **自动生成** - 可以在本地重新生成

但现代最佳实践是将 wrapper 文件提交到 Git，以确保：
- CI/CD 环境一致
- 离线构建支持
- 版本控制完整性

## 相关资源

- [Gradle 官方 Actions](https://github.com/gradle/actions)
- [Setup Gradle 文档](https://github.com/gradle/actions/blob/main/docs/setup-gradle.md)
- [Gradle Wrapper 文档](https://docs.gradle.org/current/userguide/gradle_wrapper.html)
- [GitHub Actions 中的 Gradle](https://docs.gradle.org/current/userguide/github-actions.html)

## 测试新工作流

下一次推送到主分支时，GitHub Actions 会：
1. 自动下载并缓存 Gradle 8.2
2. 验证 gradle-wrapper.jar 完整性
3. 正确执行 ./gradlew 命令
4. 缓存依赖以加快后续构建

预期成功，不再出现 ClassNotFoundException。
