#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
World Corruption 模组自动编译脚本
用法：把此脚本和 com/ 文件夹放在同一目录，双击运行
"""

import os
import urllib.request
import zipfile
import subprocess
import shutil
import sys
import time

# ========== 配置 ==========
MOD_NAME = "WorldCorruption"
FORGE_VERSION = "1.7.10-10.13.4.1614"
MDK_URL = f"https://maven.minecraftforge.net/net/minecraftforge/forge/{FORGE_VERSION}/forge-{FORGE_VERSION}-src.zip"
MDK_FILE = "forge_mdk.zip"
MDK_DIR = "forge_mdk"
# ==========================

def print_step(n, text):
    print(f"\n{'='*50}")
    print(f"步骤 {n}: {text}")
    print('='*50)

def check_java():
    """检查是否有 Java"""
    try:
        result = subprocess.run(["java", "-version"], capture_output=True, text=True)
        if result.returncode == 0:
            print("✓ Java 已安装")
            return True
    except:
        pass
    print("✗ 找不到 Java。请安装 Java 8 (JRE 或 JDK)")
    print("  下载地址: https://www.java.com/download")
    return False

def download_mdk():
    """下载 Forge MDK"""
    if os.path.exists(MDK_FILE):
        print("Forge MDK 已下载，跳过")
        return True

    print(f"正在下载 Forge {FORGE_VERSION} MDK...")
    print(f"地址: {MDK_URL}")
    print("大小约 20MB，请等待...")

    try:
        # 显示下载进度
        def report(block_num, block_size, total_size):
            downloaded = block_num * block_size
            percent = min(100, downloaded * 100 // total_size)
            sys.stdout.write(f"\r  进度: {percent}% ({downloaded//1024}KB / {total_size//1024}KB)")
            sys.stdout.flush()

        urllib.request.urlretrieve(MDK_URL, MDK_FILE, reporthook=report)
        print("\n✓ 下载完成")
        return True
    except Exception as e:
        print(f"\n✗ 下载失败: {e}")
        print("请手动下载:")
        print(f"  1. 访问 https://files.minecraftforge.net/net/minecraftforge/forge/index_1.7.10.html")
        print(f"  2. 点击 'Download Recommended' 下方的 'Src'")
        print(f"  3. 把下载的 zip 重命名为 {MDK_FILE} 放在此目录")
        return False

def extract_mdk():
    """解压 MDK"""
    if os.path.exists(MDK_DIR):
        print("清理旧目录...")
        shutil.rmtree(MDK_DIR)

    print("解压 Forge MDK...")
    with zipfile.ZipFile(MDK_FILE, 'r') as z:
        z.extractall(MDK_DIR)
    print("✓ 解压完成")

def inject_source():
    """注入模组源码"""
    print("检查源码文件...")

    # 检查必要文件
    required = [
        "com/corruption/CorruptionMod.java",
        "com/corruption/CorruptionColors.java",
        "com/corruption/ChunkCorruptionData.java",
        "com/corruption/ChunkCorruptionClient.java",
        "com/corruption/CorruptionEventHandler.java",
        "com/corruption/CorruptionRenderHandler.java",
        "com/corruption/EntityRenderCorruption.java",
        "com/corruption/CorruptionSyncPacket.java",
    ]

    for f in required:
        if not os.path.exists(f):
            print(f"✗ 缺少文件: {f}")
            print("请确保此脚本和 com/ 文件夹在同一目录")
            return False

    print("✓ 所有源码文件齐全")

    # 创建目录
    java_dir = os.path.join(MDK_DIR, "src/main/java")
    res_dir = os.path.join(MDK_DIR, "src/main/resources")
    os.makedirs(java_dir, exist_ok=True)
    os.makedirs(res_dir, exist_ok=True)

    # 复制源码
    print("复制 Java 源码...")
    dst_com = os.path.join(java_dir, "com")
    if os.path.exists(dst_com):
        shutil.rmtree(dst_com)
    shutil.copytree("com", dst_com)

    # 复制 mcmod.info
    if os.path.exists("mcmod.info"):
        print("复制 mcmod.info...")
        shutil.copy("mcmod.info", res_dir)

    # 复制 build.gradle
    if os.path.exists("build.gradle"):
        print("复制 build.gradle...")
        shutil.copy("build.gradle", MDK_DIR)

    print("✓ 源码注入完成")
    return True

def build():
    """编译"""
    print("开始编译模组...")
    print("第一次编译会自动下载 Gradle（约 50MB），请耐心等待 5-10 分钟...")
    print("如果卡住超过 15 分钟，按 Ctrl+C 取消重试")

    os.chdir(MDK_DIR)

    gradlew = "gradlew.bat" if os.name == 'nt' else "./gradlew"

    try:
        # 给 gradlew 执行权限（Linux/Mac）
        if os.name != 'nt' and os.path.exists("gradlew"):
            os.chmod("gradlew", 0o755)

        # 运行编译
        result = subprocess.run(
            [gradlew, "build"],
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            shell=(os.name == 'nt')
        )

        print(result.stdout)

        if result.returncode != 0:
            print("\n✗ 编译失败")
            print("常见原因:")
            print("  - Java 版本不对（需要 Java 8）")
            print("  - 网络问题（Gradle 下载失败）")
            print("  - 源码有语法错误")
            return False

        print("\n✓ 编译成功!")
        return True

    except FileNotFoundError:
        print(f"✗ 找不到 {gradlew}")
        print("尝试用系统 Gradle...")
        try:
            result = subprocess.run(["gradle", "build"], capture_output=True, text=True)
            print(result.stdout)
            return result.returncode == 0
        except:
            print("✗ 也找不到系统 Gradle")
            return False

def copy_jar():
    """复制输出的 jar"""
    os.chdir("..")
    libs_dir = os.path.join(MDK_DIR, "build/libs")

    if not os.path.exists(libs_dir):
        print("✗ 找不到编译输出目录")
        return False

    found = False
    for f in os.listdir(libs_dir):
        if f.endswith(".jar") and not f.endswith("-sources.jar") and not f.endswith("-dev.jar"):
            src = os.path.join(libs_dir, f)
            dst = os.path.join(os.getcwd(), f)
            shutil.copy(src, dst)
            print(f"\n{'='*50}")
            print(f"✓ 模组编译完成!")
            print(f"{'='*50}")
            print(f"文件名: {f}")
            print(f"位置: {os.path.abspath(dst)}")
            print(f"\n使用方法:")
            print(f"  1. 确保已安装 Forge 1.7.10")
            print(f"  2. 把 {f} 复制到 .minecraft/mods/")
            print(f"  3. 启动游戏，进入世界，探索新地形")
            print(f"\n效果:")
            print(f"  - 8% 的新区块会随机变色（蓝/黑/橙/灰）")
            print(f"  - 颜色会三色异步闪烁")
            print(f"  - 腐化区内的生物变红/黑")
            found = True
            break

    if not found:
        print("✗ 找不到编译输出的 jar 文件")
        return False

    return True

def main():
    print("="*50)
    print("World Corruption 模组自动编译器")
    print("="*50)
    print("此脚本会自动下载 Forge MDK 并编译模组")
    print()

    # 步骤 1: 检查 Java
    print_step(1, "检查 Java 环境")
    if not check_java():
        sys.exit(1)

    # 步骤 2: 下载 MDK
    print_step(2, "下载 Forge MDK")
    if not download_mdk():
        sys.exit(1)

    # 步骤 3: 解压
    print_step(3, "解压 Forge MDK")
    extract_mdk()

    # 步骤 4: 注入源码
    print_step(4, "注入模组源码")
    if not inject_source():
        sys.exit(1)

    # 步骤 5: 编译
    print_step(5, "编译模组")
    if not build():
        sys.exit(1)

    # 步骤 6: 复制 jar
    print_step(6, "提取 jar 文件")
    if not copy_jar():
        sys.exit(1)

    print("\n按 Enter 键退出...")
    input()

if __name__ == "__main__":
    main()
