@echo off
setlocal
chcp 65001 > nul
echo =========================================================================
echo             COMPILANDO PROJETO DRIVEWAY LOCADORA (UCB 2026)             
echo =========================================================================

REM Procura o compilador javac no PATH ou no JBR do IntelliJ
where javac >nul 2>nul
if %errorlevel% equ 0 (
    set JAVAC_CMD=javac
) else if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\jbr\bin\javac.exe" (
    set JAVAC_CMD="C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\jbr\bin\javac.exe"
) else (
    echo [ERRO] O compilador javac nao foi encontrado no PATH do sistema.
    pause
    exit /b 1
)

if not exist bin mkdir bin

REM Monta o classpath com todas as bibliotecas da pasta lib
set CLASSPATH_LIBS=lib/*

echo Compilando fontes Java compativeis com Java 8+...
%JAVAC_CMD% --release 8 -encoding UTF-8 -cp "%CLASSPATH_LIBS%" -d bin -sourcepath src ^
  src/br/ucb/locadora/modelo/*.java ^
  src/br/ucb/locadora/util/*.java ^
  src/br/ucb/locadora/dao/*.java ^
  src/br/ucb/locadora/visao/*.java ^
  src/br/ucb/locadora/app/*.java ^
  src/br/ucb/locadora/teste/*.java

if %errorlevel% neq 0 (
    echo.
    echo [ERRO] Falha durante a compilacao.
    pause
    exit /b %errorlevel%
)

REM Copia o arquivo de configuracao para o diretorio bin
copy db.properties bin\ > nul

echo.
echo [SUCESSO] Projeto compilado com exito! Classes geradas na pasta 'bin'.
echo.
pause
