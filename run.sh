#!/bin/bash

# Planar Cube 游戏启动脚本

echo "启动 Planar Cube 游戏..."

# 检查Java版本
JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt "17" ]; then
    echo "错误: 需要Java 17或更高版本"
    echo "当前Java版本: $(java -version 2>&1 | head -1)"
    exit 1
fi

# 检查Maven
if ! command -v mvn &> /dev/null; then
    echo "警告: Maven未找到，尝试直接运行JAR文件..."
    if [ -f "target/planar-cube-1.0.0.jar" ]; then
        java -jar target/planar-cube-1.0.0.jar
        exit 0
    else
        echo "错误: 未找到JAR文件，请先构建项目"
        exit 1
    fi
fi

# 检查是否需要构建
if [ ! -f "target/planar-cube-1.0.0.jar" ]; then
    echo "构建项目..."
    mvn clean package
    if [ $? -ne 0 ]; then
        echo "构建失败"
        exit 1
    fi
fi

# 运行游戏
echo "启动游戏..."
java -jar target/planar-cube-1.0.0.jar

echo "游戏已退出"