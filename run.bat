@echo off
REM Planar Cube 游戏启动脚本（Windows）

echo 启动 Planar Cube 游戏...

REM 检查Java版本
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION=%%g
)
set JAVA_VERSION=%JAVA_VERSION:"=%
for /f "tokens=1 delims=." %%a in ("%JAVA_VERSION%") do set JAVA_MAJOR=%%a

if %JAVA_MAJOR% LSS 17 (
    echo 错误: 需要Java 17或更高版本
    echo 当前Java版本: %JAVA_VERSION%
    pause
    exit /b 1
)

REM 检查Maven
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo 警告: Maven未找到，尝试直接运行JAR文件...
    if exist "target\planar-cube-1.0.0.jar" (
        java -jar target\planar-cube-1.0.0.jar
        pause
        exit /b 0
    ) else (
        echo 错误: 未找到JAR文件，请先构建项目
        pause
        exit /b 1
    )
)

REM 检查是否需要构建
if not exist "target\planar-cube-1.0.0.jar" (
    echo 构建项目...
    call mvn clean package
    if %ERRORLEVEL% neq 0 (
        echo 构建失败
        pause
        exit /b 1
    )
)

REM 运行游戏
echo 启动游戏...
java -jar target\planar-cube-1.0.0.jar

echo 游戏已退出
pause