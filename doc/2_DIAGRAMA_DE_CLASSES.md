# 2. DIAGRAMA DE CLASSES E ESTRUTURA ORIENTADA A OBJETOS

## 2.1 Diagrama de Classes (Mermaid)

```mermaid
classDiagram
    direction TB

    %% Interface
    class Tarifavel {
        <<interface>>
        +calcularTaxaSeguro(dias: int) double
        +calcularDescontoPromocional(dias: int) double
    }

    %% Superclasse Abstrata
    class Veiculo {
        <<abstract>>
        -id: Integer
        -placa: String
        -marca: String
        -modelo: String
        -ano: int
        -valorDiaria: double
        -disponivel: boolean
        +Veiculo(id, placa, marca, modelo, ano, valorDiaria, disponivel)
        +Veiculo(placa, marca, modelo, ano, valorDiaria)
        +calcularValorLocacao(dias: int)* double
        +calcularValorLocacao(dias: int, incluirSeguro: boolean) double
        +aplicarReajusteDiaria(percentual: double) void
        +getDescricao() String
        +getTipo()* String
        +getters / setters com validacao()
    }

    %% Subclasses Concretas
    class Carro {
        -categoria: CategoriaCarro
        +Carro(id, placa, marca, modelo, ano, valorDiaria, disponivel, categoria)
        +Carro(placa, marca, modelo, ano, valorDiaria, categoria)
        +calcularValorLocacao(dias: int) double
        +calcularTaxaSeguro(dias: int) double
        +calcularDescontoPromocional(dias: int) double
        +getTipo() String
        +getCategoria() CategoriaCarro
        +setCategoria(categoria: CategoriaCarro) void
    }

    class Moto {
        -cilindrada: int
        +Moto(id, placa, marca, modelo, ano, valorDiaria, disponivel, cilindrada)
        +Moto(placa, marca, modelo, ano, valorDiaria, cilindrada)
        +calcularValorLocacao(dias: int) double
        +calcularTaxaSeguro(dias: int) double
        +calcularDescontoPromocional(dias: int) double
        +getTipo() String
        +getCilindrada() int
        +setCilindrada(cilindrada: int) void
    }

    %% Entidade Relacionada
    class Cliente {
        -id: Integer
        -nome: String
        -cpf: String
        -telefone: String
        -email: String
        +Cliente(id, nome, cpf, telefone, email)
        +Cliente(nome, cpf, telefone, email)
        +getCpfFormatado() String
        +getters / setters com validacao()
    }

    %% Entidade de Relacionamento
    class Locacao {
        -id: Integer
        -cliente: Cliente
        -veiculo: Veiculo
        -dataLocacao: LocalDate
        -diasPrevistos: int
        -valorTotal: double
        -status: String
        +Locacao(id, cliente, veiculo, dataLocacao, diasPrevistos, valorTotal, status)
        +Locacao(cliente, veiculo, diasPrevistos)
        +calcularValorFinal() double
        +calcularValorFinal(descontoExtra: double) double
        +finalizarLocacao() void
        +getResumo() String
    }

    %% Relacionamentos
    Tarifavel <|.. Veiculo : implementa
    Veiculo <|-- Carro : heranca
    Veiculo <|-- Moto : heranca
    Locacao "1" o-- "1" Cliente : associa
    Locacao "1" o-- "1" Veiculo : associa (polimorfico)
```

---

## 2.2 Representação Textual da Estrutura

```text
+-------------------------------------------------------------+
|                        <<interface>>                        |
|                          Tarifavel                          |
+-------------------------------------------------------------+
| + calcularTaxaSeguro(dias: int): double                     |
| + calcularDescontoPromocional(dias: int): double            |
+-------------------------------------------------------------+
                               ^
                               | implements
+-------------------------------------------------------------+
|                     <<abstract class>>                      |
|                           Veiculo                           |
+-------------------------------------------------------------+
| - id: Integer                                               |
| - placa: String                                             |
| - marca: String                                             |
| - modelo: String                                            |
| - ano: int                                                  |
| - valorDiaria: double                                       |
| - disponivel: boolean                                       |
+-------------------------------------------------------------+
| + calcularValorLocacao(dias: int): double [ABSTRATO]        |
| + calcularValorLocacao(dias: int, seguro: boolean): double  |
| + getTipo(): String [ABSTRATO]                              |
| + aplicarReajusteDiaria(percentual: double): void           |
| + getDescricao(): String                                    |
| + Getters e Setters com validacoes de estado                |
+-------------------------------------------------------------+
                 ^                           ^
                 | extends                   | extends
+-------------------------------+ +-------------------------------+
|             Carro             | |             Moto              |
+-------------------------------+ +-------------------------------+
| - categoria: CategoriaCarro   | | - cilindrada: int             |
+-------------------------------+ +-------------------------------+
| + calcularValorLocacao(): dbl | | + calcularValorLocacao(): dbl |
| + calcularTaxaSeguro(): dbl   | | + calcularTaxaSeguro(): dbl   |
| + calcularDescontoProm(): dbl | | + calcularDescontoProm(): dbl |
| + getTipo(): String           | | + getTipo(): String           |
+-------------------------------+ +-------------------------------+

+-------------------------------+
|            Cliente            |
+-------------------------------+
| - id: Integer                 |
| - nome: String                |
| - cpf: String                 |
| - telefone: String            |
| - email: String               |
+-------------------------------+
| + getCpfFormatado(): String   |
| + Getters e Setters validados |
+-------------------------------+
                 ^
                 | 1:N
+-------------------------------+
|            Locacao            |
+-------------------------------+
| - id: Integer                 |
| - cliente: Cliente            |
| - veiculo: Veiculo            |
| - dataLocacao: LocalDate      |
| - diasPrevistos: int          |
| - valorTotal: double          |
| - status: String              |
+-------------------------------+
| + calcularValorFinal(): dbl   |
| + calcularValorFinal(d): dbl  |
| + finalizarLocacao(): void    |
+-------------------------------+
```
