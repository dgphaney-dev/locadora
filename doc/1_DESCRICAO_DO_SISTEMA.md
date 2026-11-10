# 1. DESCRIÇÃO DO SISTEMA - DRIVEWAY LOCADORA

## 1.1 Contexto e Problema
O setor de locação de veículos demanda agilidade na gestão da frota, controle rigoroso de disponibilidades e regras de tarifação personalizadas por categoria de veículo (automóveis de passeio, veículos executivos, utilitários esportivos e motocicletas). 

O sistema **DriveWay Locadora** foi desenvolvido em Java para solucionar a gestão unificada de uma locadora moderna, permitindo cadastrar clientes, gerenciar veículos de diferentes naturezas (Carros e Motocicletas), calcular locações com regras de precificação e seguros específicas, e realizar contratos de locação vinculando clientes a veículos.

A persistência dos dados é realizada no banco de dados relacional **MySQL** utilizando a API nativa **JDBC**, garantindo integridade referencial, preservação das transações e reconstrução correta dos objetos em memória.

---

## 1.2 Entidades e Arquitetura do Sistema
O sistema adota uma arquitetura em camadas estruturada em pacotes:
- `br.ucb.locadora.modelo`: Entidades de negócio e interfaces (`Veiculo`, `Carro`, `Moto`, `Cliente`, `Locacao`, `Tarifavel`).
- `br.ucb.locadora.dao`: Interfaces e implementações JDBC (`VeiculoDAO`, `VeiculoDAOJDBC`, `ClienteDAO`, `ClienteDAOJDBC`, `LocacaoDAO`, `LocacaoDAOJDBC`).
- `br.ucb.locadora.util`: Fábrica de conexões (`FabricaConexao`) e tratamento de exceções (`AcessoDadosException`).
- `br.ucb.locadora.visao`: Interface interativa via terminal (`MenuConsole`).
- `br.ucb.locadora.app`: Ponto de entrada (`Principal`).
- `br.ucb.locadora.teste`: Bateria automatizada de validações e testes (`ExecutadorTestes`).

---

## 1.3 Principais Regras de Negócio e Validações
1. **Regras de Validação de Veículos (Encapsulamento):**
   - **Placa:** Não pode ser nula ou vazia e deve obedecer estritamente ao padrão brasileiro (convencional `ABC1234` ou Mercosul `ABC1D23`).
   - **Valor da Diária Base:** Deve ser estritamente superior a zero (`valorDiaria > 0`).
   - **Ano de Fabricação:** Deve ser entre 1990 e o ano corrente + 1.
   - **Cilindrada (Moto):** Deve estar compreendida entre 50cc e 2.500cc.
   - **Categoria (Carro):** Deve ser uma das opções válidas (`HATCH`, `SEDAN`, `SUV`).

2. **Regras de Tarifação Polimórfica:**
   - **Carro:** 
     - Diária efetiva = Diária base + Taxa de categoria (`HATCH`: +R$ 0, `SEDAN`: +R$ 35, `SUV`: +R$ 70).
     - Desconto progressivo: 10% para contratos $\ge$ 7 dias; 15% para contratos $\ge$ 15 dias.
     - Seguro diário (via interface `Tarifavel`): R$ 20 (Hatch), R$ 35 (Sedan), R$ 50 (SUV).
   - **Moto:**
     - Diária efetiva = Diária base + Taxa de potência (`cilindrada > 300cc`: +R$ 30/dia; `<= 300cc`: +R$ 10/dia).
     - Desconto progressivo: 8% para contratos $\ge$ 5 dias; 12% para contratos $\ge$ 10 dias.
     - Seguro diário (via interface `Tarifavel`): R$ 28/dia para alta cilindrada e R$ 14/dia para as demais.

3. **Regras de Locação e Integridade:**
   - Ao registrar uma locação para um veículo, sua situação passa automaticamente para `LOCADO` (`disponivel = FALSE`).
   - Ao finalizar a locação, o veículo é liberado (`disponivel = TRUE`).
   - Não é permitida a exclusão de veículos ou clientes com contratos de locação vinculados no banco de dados (restrição de chave estrangeira `ON DELETE RESTRICT`).

---

## 1.4 Estratégia de Mapeamento Objeto-Relacional
Para persistir a hierarquia de herança (`Veiculo` $\rightarrow$ `Carro`, `Moto`), adotou-se o padrão **Single Table Inheritance (Tabela Única)**:
- Toda a hierarquia é mapeada na tabela `veiculo`.
- A coluna `tipo_veiculo` atua como discriminador (`'CARRO'` ou `'MOTO'`).
- As colunas específicas (`categoria_carro` e `cilindrada_moto`) armazenam os atributos particulares de cada subclasse.
- **Vantagens:** Alto desempenho (elimina junções SQL `JOIN`), simplicidade na implementação JDBC e suporte nativo a consultas polimórficas.
