# 4. INSTRUÇÕES DE EXECUÇÃO E CONFIGURAÇÃO

## 4.1 Requisitos do Ambiente
- **Java Development Kit (JDK):** Versão 17 ou superior (compatível também com JDK 8+ com compilação `--release 8`).
- **Banco de Dados:** MySQL Server versão 8.0 ou superior (ou MariaDB).
- **Driver JDBC:** `mysql-connector-j-8.x.jar` (disponibilizado na pasta `lib/`).

---

## 4.2 Configuração do Banco de Dados MySQL
1. Abra seu cliente MySQL favorito (MySQL Workbench, DBeaver, HeidiSQL ou linha de comando `mysql -u root -p`).
2. Abra e execute o script localizado em:
   ```text
   db/schema_e_dados.sql
   ```
   *Este script cria automaticamente o banco `locadora_db`, todas as tabelas com chaves primárias e estrangeiras, além de inserir registros fictícios de Carros, Motos, Clientes e Locações para teste imediato.*

---

## 4.3 Configuração da Conexão (`db.properties`)
O arquivo `db.properties` está localizado na raiz do projeto e permite configurar o acesso ao banco em qualquer máquina sem alterar nenhuma linha de código Java:

```properties
db.url=jdbc:mysql://localhost:3306/locadora_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8
db.usuario=root
db.senha=sua_senha_aqui
```

> **Nota:** Caso o seu MySQL não tenha senha configurada para o usuário `root`, deixe `db.senha=` em branco. Se o MySQL estiver rodando em outra porta (ex: 3307), basta ajustar na URL.

---

## 4.4 Disponibilização do Driver JDBC
O projeto requer o driver JDBC oficial da Oracle (`mysql-connector-j`).
1. O arquivo `.jar` do conector deve ficar na pasta `lib/`, por exemplo:
   ```text
   lib/mysql-connector-j-8.4.0.jar
   ```
2. O driver pode ser baixado gratuitamente do repositório oficial Maven Central:
   - [Download MySQL Connector/J no Maven Central](https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar)

---

## 4.5 Como Compilar e Executar no Windows (Scripts Prontos)

O projeto já inclui scripts automatizados na raiz:

### 1. Compilar todo o projeto:
Dê um duplo clique ou execute no terminal:
```powershell
.\compilar.bat
```

### 2. Executar o Sistema Interativo (Menu Console):
```powershell
.\executar.bat
```

### 3. Executar a Bateria Completa de Testes Automatizados:
```powershell
.\executar_testes.bat
```

---

## 4.6 Como Compilar e Executar Manualmente via Linha de Comando

```powershell
# Compilacao:
javac -cp "lib/*" -d bin -sourcepath src src/br/ucb/locadora/app/Principal.java src/br/ucb/locadora/teste/ExecutadorTestes.java

# Copiar configuracao para o diretorio de execucao:
copy db.properties bin\

# Execucao do Sistema:
java -cp "bin;lib/*" br.ucb.locadora.app.Principal

# Execucao dos Testes:
java -cp "bin;lib/*" br.ucb.locadora.teste.ExecutadorTestes
```
