
@echo off
chcp 65001 >nul
cls

echo.
echo ========================================
echo   🏆 复制销冠AI助手 - 启动程序
echo ========================================
echo.

REM 检查 Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误：未检测到 Java，请先安装 JDK 17+
    echo    下载地址：https://adoptium.net/
    pause
    exit /b 1
)

REM 检查 Node.js
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误：未检测到 Node.js，请先安装 Node.js
    echo    下载地址：https://nodejs.org/
    pause
    exit /b 1
)

echo [1/3] 检查后端依赖...
if not exist "target" (
    echo    首次运行，正在编译后端...
    call mvn clean compile
)

echo [2/3] 启动后端服务...
start "后端服务" cmd /k "title 复制销冠 - 后端服务 && echo 正在启动 Spring Boot... && mvn spring-boot:run"

echo    等待后端启动（10秒）...
timeout /t 10 /nobreak >nul

echo [3/3] 启动桌面客户端...
cd electron
if not exist "node_modules" (
    echo    首次运行，正在安装依赖...
    call npm install
)
start "桌面客户端" cmd /k "title 复制销冠 - 桌面客户端 && npm start"

echo.
echo ========================================
echo   ✅ 启动完成！
echo.
echo   📱 网页版: http://localhost:8080/index.html
echo   💻 客户端: 已打开新窗口
echo.
echo   提示：
echo   - 两个窗口都需要保持打开
echo   - 按 Ctrl+C 可停止后端服务
echo   - 关闭窗口可退出客户端
echo ========================================
echo.
pause