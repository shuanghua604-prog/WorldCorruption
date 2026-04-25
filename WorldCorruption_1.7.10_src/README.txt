
========================================
World Corruption 模组 - 1.7.10 Forge
========================================

【功能】
- 新区块生成时 8% 概率随机腐化
- 5种腐化类型：蓝/黑/红/橙/灰
- 整区块三色异步闪烁
- 腐化区内实体变红/黑
- 服务端-客户端数据同步

【文件结构】
WorldCorruption_1.7.10_src.zip
├── build.gradle          <- Gradle构建配置
├── mcmod.info            <- 模组信息
└── com/corruption/
    ├── CorruptionMod.java           <- 主类
    ├── CorruptionColors.java        <- 颜色配置
    ├── ChunkCorruptionData.java     <- 服务端区块数据
    ├── ChunkCorruptionClient.java   <- 客户端数据
    ├── CorruptionEventHandler.java  <- 区块加载事件
    ├── CorruptionRenderHandler.java <- 区块渲染劫持
    ├── EntityRenderCorruption.java  <- 实体渲染劫持
    └── CorruptionSyncPacket.java    <- 网络同步包

【编译方法】
1. 解压 zip 到 Forge 1.7.10 MDK 的目录下
   （替换 src/main/java/ 和 src/main/resources/ 内容）

2. 确保 Forge MDK 已配置好（build.gradle 中的 minecraft.version 要匹配）

3. 运行:
   ./gradlew build

4. 生成的 jar 在 build/libs/ 目录下

【已知问题】
- 渲染覆盖层使用简单平面，可能被地形遮挡
- 实体渲染劫持可能与其他模组冲突
- 需要进一步测试网络同步的稳定性

========================================
