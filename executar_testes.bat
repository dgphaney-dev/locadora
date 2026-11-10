@echo off
setlocal
chcp 65001 > nul

if not exist bin\br\ucb\locadora\teste\ExecutadorTestes.class (
    echo [INFO] Binarios nao encontrados. Compilando o projeto primeiro...
    call compilar.bat
)

if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\jbr\bin\java.exe" (
    set JAVA_CMD="C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\jbr\bin\java.exe"
) else (
    set JAVA_CMD=java
)

echo Executando Bateria de Testes Automatizados...
%JAVA_CMD% -Dfile.encoding=UTF-8 -cp "bin;lib/*" br.ucb.locadora.teste.ExecutadorTestes
pause
