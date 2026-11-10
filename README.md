# DriveWay Locadora - Sistema de Gestão de Locação de Veículos
**Universidade Católica de Brasília (UCB) - Programação Orientada a Objetos**

---

## 📌 Visão Geral do Projeto
Este projeto foi desenvolvido como trabalho prático final da disciplina de **Programação Orientada a Objetos**, aplicando de forma completa, coerente e integrada os conceitos de:
- **Classes e Objetos**
- **Construtores e Encapsulamento com Validações Rígidas**
- **Herança e Abstração**
- **Interfaces e Polimorfismo Dinâmico e Estático (Sobrecarga e Sobrescrita)**
- **Relacionamentos entre Objetos (Composição / Associação)**
- **Persistência Completa (CRUD) em Banco de Dados MySQL via JDBC**
- **Tratamento de Exceções e Conexões Seguras com `try-with-resources`**

---

## 📁 Estrutura de Diretórios
```text
projeto_locadora/
├── src/                                  # Código-fonte organizado por pacotes
│   └── br/ucb/locadora/
│       ├── modelo/                       # Classes de domínio e interfaces
│       │   ├── Tarifavel.java            # Interface de tarifação
│       │   ├── Veiculo.java              # Superclasse abstrata
│       │   ├── Carro.java                # Subclasse concreta
│       │   ├── Moto.java                 # Subclasse concreta
│       │   ├── Cliente.java              # Entidade Cliente
│       │   └── Locacao.java              # Relacionamento Cliente + Veiculo
│       ├── dao/                          # Camada de Acesso a Dados (DAO)
│       │   ├── VeiculoDAO.java           # Interface DAO de Veiculo
│       │   ├── VeiculoDAOJDBC.java       # Implementação JDBC (Single Table Inheritance)
│       │   ├── ClienteDAO.java           # Interface DAO de Cliente
│       │   ├── ClienteDAOJDBC.java       # Implementação JDBC de Cliente
│       │   ├── LocacaoDAO.java           # Interface DAO de Locacao
│       │   └── LocacaoDAOJDBC.java       # Implementação JDBC de Locacao
│       ├── util/                         # Utilitários e Infraestrutura
│       │   ├── FabricaConexao.java       # ConnectionFactory (db.properties)
│       │   └── AcessoDadosException.java # Exceção personalizada de banco
│       ├── visao/                        # Camada de Apresentação
│       │   └── MenuConsole.java          # Menu interativo no Terminal
│       ├── app/                          # Ponto de entrada
│       │   └── Principal.java            # Classe executável principal
│       └── teste/                        # Bateria de testes automatizados
│           └── ExecutadorTestes.java     # Teste dos cenários mínimos exigidos
├── db/                                   # Scripts de Banco de Dados
│   └── schema_e_dados.sql                # DDL (tabelas, chaves) + DML (dados fictícios)
├── doc/                                  # Documentação completa exigida na entrega
│   ├── 1_DESCRICAO_DO_SISTEMA.md         # Problema, arquitetura e regras de negócio
│   ├── 2_DIAGRAMA_DE_CLASSES.md          # Diagrama Mermaid e representação textual
│   ├── 3_QUADRO_DE_APLICACAO_DOS_CONCEITOS.md # Mapeamento detalhado dos conceitos
│   ├── 4_INSTRUCOES_DE_EXECUCAO.md       # Como configurar e executar o projeto
│   └── 5_REGISTRO_DOS_TESTES.md          # Log e evidências dos testes realizados
├── lib/                                  # Pasta para o driver JDBC (mysql-connector-j)
├── db.properties                         # Arquivo de configuração da conexão MySQL
├── compilar.bat                          # Script de compilação automática no Windows
├── executar.bat                          # Script de execução do sistema interativo
└── executar_testes.bat                   # Script de execução dos testes automatizados
```

---

## 🚀 Como Executar em 3 Passos
1. **Configurar o Banco de Dados:**
   - Execute o script `db/schema_e_dados.sql` no seu MySQL (cria a base `locadora_db` e insere dados de teste).
   - Ajuste usuário e senha no arquivo `db.properties` se necessário.
2. **Colocar o Driver JDBC:**
   - Baixe o driver `mysql-connector-j-8.4.0.jar` e coloque na pasta `lib/`.
3. **Executar:**
   - Duplo clique em `executar.bat` para abrir o menu do sistema.
   - Duplo clique em `executar_testes.bat` para rodar os testes automatizados.
