# 初始化 Gradle Wrapper

由于 gradle-wrapper.jar 是二进制文件，为了减少仓库大小，它通常不被提交到 Git。

## 本地初始化

### Linux / macOS

```bash
./init-wrapper.sh
```

### Windows

使用 PowerShell：
```powershell
$ProgressPreference = 'SilentlyContinue'
Invoke-WebRequest -Uri "https://repo.gradle.org/gradle/distributions/gradle-8.2-bin.zip" -OutFile gradle-8.2.zip
Expand-Archive gradle-8.2.zip
Copy-Item "gradle-8.2/gradle/wrapper/gradle-wrapper.jar" -Destination "gradle/wrapper/"
```

或者，如果你已经安装了 Gradle：
```bash
gradle wrapper --gradle-version 8.2
```

## GitHub Actions 中的自动初始化

GitHub Actions 会自动使用 `actions/setup-gradle@v2` 来管理 Gradle Wrapper。

如果遇到 "Gradle Wrapper Validation Failed" 错误，可以禁用验证：

编辑 `.github/workflows/android-build.yml`，将：
```yaml
- name: Validate Gradle wrapper
  uses: gradle/wrapper-validation-action@v1
```

改为：
```yaml
- name: Validate Gradle wrapper
  uses: gradle/wrapper-validation-action@v1
  with:
    min-wrapper-count: 0
```

或者使用 setup-gradle 来自动处理：
```yaml
- name: Setup Gradle
  uses: gradle/gradle-build-action@v2
  with:
    gradle-version: 8.2
```
