
@echo off
chcp 65001 >nul
cls

echo.
echo ========================================
echo   🏆 复制销冠AI助手 - 打包工具
echo ========================================
echo.

echo 请选择要执行的操作：
echo.
echo [1] 打包后端 JAR
echo [2] 打包客户端 EXE
echo [3] 全部打包
echo [0] 退出
echo.
set /p choice=请输入选项 (0-3):

if "%choice%"=="1" goto package-backend
if "%choice%"=="2" goto package-client
if "%choice%"=="3" goto package-all
if "%choice%"=="0" exit /b 0

echo 无效选项
pause
exit /b 1

:package-backend
echo.
echo ========================================
echo   正在打包后端...
echo ========================================
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ❌ 后端打包失败
    pause
    exit /b 1
)
echo ✅ 后端打包完成：target\demo-0.0.1-SNAPSHOT.jar
echo.
pause
exit /b 0

:package-client
echo.
echo ========================================
echo   正在打包客户端...
echo ========================================
cd electron
if not exist "node_modules" (
    echo 正在安装依赖...
    call npm install
)
echo 正在生成安装包...
call npm run build:win
if %errorlevel% neq 0 (
    echo ❌ 客户端打包失败
    pause
    exit /b 1
)
echo ✅ 客户端打包完成：electron\dist\1.0.0\
cd ..
echo.
pause
exit /b 0

:package-all
echo.
echo ========================================
echo   正在打包全部内容...
echo ========================================
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ❌ 后端打包失败
    pause
    exit /b 1
)
echo ✅ 后端打包完成

cd electron
if not exist "node_modules" (
    echo 正在安装依赖...
    call npm install
)
echo 正在生成安装包...
call npm run build:win
if %errorlevel% neq 0 (
    echo ❌ 客户端打包失败
    pause
    exit /b 1
)
cd ..
echo ✅ 客户端打包完成

echo.
echo ========================================
echo   🎉 全部打包完成！
echo.
echo   📦 后端: target\demo-0.0.1-SNAPSHOT.jar
echo   📦 客户端: electron\dist\1.0.0\
echo ========================================
echo.
pause
exit /b 0