# World Corruption Mod (1.20.1)

Minecraft 1.20.1 Forge 模组 — 世界腐化系统。

## 功能
- 新区块生成时 8% 概率随机腐化
- 5种腐化类型：蓝/黑/红/橙/灰
- 整区块三色异步闪烁
- 腐化区内实体变色

## 自动编译 (GitHub Actions)

1. Fork 本仓库或上传到你自己的 GitHub
2. 进入 **Actions** → **Build World Corruption Mod 1.20.1**
3. 点击 **Run workflow**
4. 等待 5-10 分钟
5. 下载 Artifacts 中的 jar

## 手动编译

需要 Java 17 + ForgeGradle 6.x

```bash
./gradlew build
```

## 安装

1. 安装 **Forge 1.20.1-47.1.0+**
2. 把 jar 放进 `.minecraft/mods/`
3. 启动游戏

## 要求

- Minecraft 1.20.1
- Forge 47.1.0+
- Java 17+
