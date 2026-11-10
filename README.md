# DriveWay Locadora - Sistema de Gestão de Locação de Veículos

Projeto prático desenvolvido para a disciplina de Programação Orientada a Objetos da Universidade Católica de Brasília (UCB).

---

## 1. Visão Geral do Sistema

O **DriveWay Locadora** é uma aplicação completa em Java que simula a operação de uma locadora de veículos, permitindo o gerenciamento integrado de frota, clientes e contratos de locação. O sistema foi projetado aplicando os princípios fundamentais da orientação a objetos e a persistência relacional em banco de dados MySQL via JDBC.

### Principais Destaques Técnicos:
- **Separação em Camadas:** Domínio (`modelo`), Acesso a Dados (`dao`), Utilitários (`util`), Apresentação (`visao`), Aplicação (`app`) e Testes (`teste`).
- **Mapeamento Objeto-Relacional:** Adoção do padrão *Single Table Inheritance* (Tabela Única) com coluna discriminadora para persistência eficiente da hierarquia de veículos.
- **Segurança e Confiabilidade:** Validações de domínio que impedem a instanciação de objetos em estados inconsistentes e controle transacional com tratamento de integridade referencial.
- **Persistência Segura:** Uso exclusivo de consultas parametrizadas com `PreparedStatement` e fechamento automático de recursos com `try-with-resources`.

---

## 2. Modelagem Orientada a Objetos

O sistema contempla todos os requisitos teóricos e práticos de POO:

- **Classes de Domínio:** 5 classes implementadas (`Veiculo`, `Carro`, `Moto`, `Cliente`, `Locacao`).
- **Abstração e Herança:** A superclasse `Veiculo` é abstrata, definindo o contrato de cálculo de diárias e provendo comportamentos reutilizáveis para as subclasses concretas `Carro` e `Moto`.
- **Interface e Polimorfismo:** A interface `Tarifavel` define o cálculo de seguros e descontos promocionais, adotada pela hierarquia de veículos e acionada de forma desacoplada. O processamento polimórfico permite iterar sobre coleções de veículos executando a regra de tarifação correspondente sem o uso de condicionais ou `instanceof`.
- **Sobrecarga (Overloading):** Métodos com mesmo identificador e assinaturas distintas presentes em `Veiculo` (`calcularValorLocacao`) e `Locacao` (`calcularValorFinal`).
- **Encapsulamento e Validações:** Atributos estritamente privados com acesso controlado por métodos. Entre as regras de validação ativas:
  - Formato de placa veicular no padrão brasileiro/Mercosul (`ABC1234` ou `ABC1D23`).
  - Valor de diária obrigatoriamente superior a zero.
  - Ano de fabricação compreendido entre 1990 e o ano atual + 1.
  - Cilindrada de motocicletas restrita entre 50cc e 2500cc.
  - CPF de clientes com validação de 11 dígitos numéricos.
- **Relacionamento entre Entidades:** A classe `Locacao` associa as entidades `Cliente` e `Veiculo`, refletindo a relação 1:N no modelo relacional.

---

## 3. Estrutura do Projeto

```text
projeto_locadora/
├── src/                                  # Código-fonte Java
│   └── br/ucb/locadora/
│       ├── modelo/                       # Entidades de negócio e interfaces
│       │   ├── Tarifavel.java            # Interface de tarifação
│       │   ├── Veiculo.java              # Superclasse abstrata
│       │   ├── Carro.java                # Subclasse concreta
│       │   ├── Moto.java                 # Subclasse concreta
│       │   ├── Cliente.java              # Entidade Cliente
│       │   └── Locacao.java              # Relacionamento Cliente e Veículo
│       ├── dao/                          # Camada de Persistência (Data Access Object)
│       │   ├── VeiculoDAO.java           # Interface de operações de Veículo
│       │   ├── VeiculoDAOJDBC.java       # Implementação JDBC para Veículo
│       │   ├── ClienteDAO.java           # Interface de operações de Cliente
│       │   ├── ClienteDAOJDBC.java       # Implementação JDBC para Cliente
│       │   ├── LocacaoDAO.java           # Interface de operações de Locação
│       │   └── LocacaoDAOJDBC.java       # Implementação JDBC para Locação
│       ├── util/                         # Utilitários de infraestrutura
│       │   ├── FabricaConexao.java       # Gerenciador de conexões JDBC
│       │   └── AcessoDadosException.java # Exceção encapsulada de banco
│       ├── visao/                        # Camada de Interface com o Usuário
│       │   └── MenuConsole.java          # Menu interativo em terminal
│       ├── app/                          # Ponto de entrada da aplicação
│       │   └── Principal.java            # Classe executável principal
│       └── teste/                        # Suíte de testes automatizados
│           └── ExecutadorTestes.java     # Verificação dos cenários mínimos
├── db/                                   # Scripts SQL
│   └── schema_e_dados.sql                # Criação do banco, tabelas e dados de teste
├── doc/                                  # Documentações detalhadas do trabalho
│   ├── 1_DESCRICAO_DO_SISTEMA.md         # Detalhamento de problemas e regras
│   ├── 2_DIAGRAMA_DE_CLASSES.md          # Diagramas estruturais (Mermaid e texto)
│   ├── 3_QUADRO_DE_APLICACAO_DOS_CONCEITOS.md # Mapeamento completo dos requisitos
│   ├── 4_INSTRUCOES_DE_EXECUCAO.md       # Guia passo a passo de configuração
│   └── 5_REGISTRO_DOS_TESTES.md          # Relatório e evidências dos testes
├── lib/                                  # Dependências externas (Driver JDBC MySQL)
├── db.properties                         # Configuração desacoplada de conexão
├── compilar.bat                          # Script de compilação para Windows
├── executar.bat                          # Script de inicialização da aplicação
└── executar_testes.bat                   # Script de execução da bateria de testes
```

---

## 4. Funcionalidades do Sistema

O programa disponibiliza um menu interativo no terminal que contempla:

1. **Cadastrar Veículo:** Registro de novos Carros (Hatch, Sedan, SUV) e Motocicletas (baixa e alta cilindrada).
2. **Listar Veículos:** Exibição completa da frota, identificando tipo, placa, diária base e disponibilidade.
3. **Consultar Veículo por ID:** Busca direta pelo identificador primário.
4. **Pesquisar Veículos:** Filtro por placa exata ou correspondência parcial por modelo.
5. **Atualizar Veículo:** Modificação de atributos cadastrais respeitando as regras de validação.
6. **Excluir Veículo:** Remoção de veículos mantendo integridade com relação a contratos vinculados.
7. **Simulador de Locação de Frota:** Demonstração prática de polimorfismo dinâmico e uso da interface `Tarifavel` para cotação em lote.
8. **Relatório Resumido:** Indicadores consolidados de faturamento, ocupação da frota e médias financeiras.
9. **Gestão de Clientes e Locações:** Cadastro de clientes, abertura de contratos e devolução com liberação de veículo.

---

## 5. Instruções de Configuração e Execução

### Requisitos Mínimos:
- Java JDK 8 ou superior (código compilado com compatibilidade `--release 8`).
- MySQL Server 8.0 ou superior (ou MariaDB).
- Driver `mysql-connector-j-8.x.jar` na pasta `lib/`.

### Passo a Passo:

1. **Banco de Dados:**
   Execute o script `db/schema_e_dados.sql` em seu gerenciador MySQL para criar o banco de dados `locadora_db` e carregar a massa de dados inicial.

2. **Configuração da Conexão:**
   Ajuste as credenciais de acesso no arquivo `db.properties` na raiz do projeto:
   ```properties
   db.url=jdbc:mysql://localhost:3306/locadora_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8
   db.usuario=root
   db.senha=sua_senha_aqui
   ```

3. **Execução:**
   - Para abrir o menu interativo: execute `executar.bat` ou utilize o terminal:
     ```cmd
     java -cp "bin;lib/*" br.ucb.locadora.app.Principal
     ```
   - Para rodar a suíte de testes: execute `executar_testes.bat` ou utilize o terminal:
     ```cmd
     java -cp "bin;lib/*" br.ucb.locadora.teste.ExecutadorTestes
     ```

---

## 6. Documentação Complementar

A pasta `doc/` contém os arquivos complementares exigidos na entrega:

- **1_DESCRICAO_DO_SISTEMA.md:** Descrição formal do problema, arquitetura e regras de negócio.
- **2_DIAGRAMA_DE_CLASSES.md:** Diagrama de classes UML em notação Mermaid e formato textual ASCII.
- **3_QUADRO_DE_APLICACAO_DOS_CONCEITOS.md:** Tabela detalhada indicando as classes e métodos onde cada conceito orientado a objetos foi aplicado.
- **4_INSTRUCOES_DE_EXECUCAO.md:** Manual detalhado para instalação, configuração do banco e execução manual.
- **5_REGISTRO_DOS_TESTES.md:** Documentação dos 8 cenários de teste exigidos com entradas, resultados esperados e obtidos.
