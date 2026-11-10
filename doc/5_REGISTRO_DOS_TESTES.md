# 5. REGISTRO DOS TESTES REALIZADOS

Conforme os requisitos da Seção 6 do enunciado, todos os cenários obrigatórios de teste foram executados e documentados com a situação/entrada utilizada, o resultado esperado e o resultado efetivamente obtido:

---

### Teste 1: Cadastro e Recuperação de Objetos das Duas Subclasses
- **Situação / Entrada:**
  - Carro: Placa `BRA2E19`, Marca `Toyota`, Modelo `Corolla`, Ano `2024`, Diária `R$ 210.00`, Categoria `SEDAN`.
  - Moto: Placa `DFM4D55`, Marca `Honda`, Modelo `CB 500F`, Ano `2023`, Diária `R$ 140.00`, Cilindrada `500 cc`.
- **Resultado Esperado:** Os dois registros devem ser persistidos no MySQL na tabela `veiculo` com a coluna discriminadora `tipo_veiculo` ('CARRO' e 'MOTO') e os campos específicos preenchidos. Na recuperação, o DAO deve instanciar objetos `Carro` e `Moto` com seus atributos específicos preservados.
- **Resultado Obtido:** Inserções executadas com chaves geradas e objetos recuperados com sucesso via `buscarPorId`, comprovando a correta reconstrução polimórfica das instâncias concretas. **[APROVADO]**

---

### Teste 2: Consulta, Atualização e Exclusão (Ciclo CRUD)
- **Situação / Entrada:**
  - Consulta: `dao.buscarPorId(1)`
  - Atualização: `carro.setValorDiaria(230.00); dao.atualizar(carro);`
  - Exclusão: `dao.excluir(idTeste);`
- **Resultado Esperado:** O registro consultado deve ser alterado no banco e refletido na próxima consulta. Na exclusão, o registro deve ser removido permanentemente.
- **Resultado Obtido:** Consulta retornou o registro correto; o valor atualizado foi confirmado no banco; a exclusão removeu a tupla com sucesso (`linhasAfetadas = 1`). **[APROVADO]**

---

### Teste 3: Busca por Identificador Inexistente
- **Situação / Entrada:** `dao.buscarPorId(999999)` e busca no menu com ID não cadastrado.
- **Resultado Esperado:** O método deve retornar `null` (ou lista vazia), e a interface deve apresentar a mensagem amigável: `"[AVISO] Nenhum veiculo encontrado com o ID 999999."`, sem disparar `NullPointerException`.
- **Resultado Obtido:** O sistema tratou a ausência de registro adequadamente e retornou ao menu com a mensagem informativa. **[APROVADO]**

---

### Teste 4: Tentativas de Violação das Regras de Validação (Encapsulamento)
- **Caso 4.1: Diária Inválida:**
  - Entrada: `valorDiaria = -50.00`
  - Esperado: Disparo de `IllegalArgumentException("O valor da diaria deve ser maior que zero.")`.
  - Obtido: Exceção capturada com sucesso; o objeto não foi instanciado com estado inválido. **[APROVADO]**
- **Caso 4.2: Placa Fora do Padrão Nacional/Mercosul:**
  - Entrada: `placa = "PLACA_ERRADA"`
  - Esperado: Disparo de `IllegalArgumentException("Placa invalida: 'PLACA_ERRADA'. Padrao esperado: ABC1234 ou ABC1D23.")`.
  - Obtido: Exceção disparada e capturada. **[APROVADO]**
- **Caso 4.3: Cilindrada Inválida de Moto:**
  - Entrada: `cilindrada = 20`
  - Esperado: Disparo de `IllegalArgumentException("Cilindrada invalida (20 cc). Deve estar entre 50cc e 2500cc.")`.
  - Obtido: Exceção disparada e capturada. **[APROVADO]**
- **Caso 4.4: Ano Antigo Fora do Limite:**
  - Entrada: `ano = 1975`
  - Esperado: Disparo de `IllegalArgumentException("Ano invalido (1975). Deve estar entre 1990 e 2027.")`.
  - Obtido: Exceção disparada e capturada. **[APROVADO]**

---

### Teste 5: Execução das Diferentes Implementações do Método Polimórfico
- **Situação / Entrada:**
  - Coleção `List<Veiculo>` contendo Carro Hatch, Carro Sedan, Carro SUV, Moto 150cc e Moto 500cc.
  - Período simulado: 7 dias.
  - Chamada polimórfica: `veiculo.calcularValorLocacao(7)`.
- **Resultado Esperado:**
  - Carro Sedan (diária R$ 200 + taxa Sedan R$ 35 = R$ 235/dia * 7 dias = R$ 1.645,00 - 10% desconto promocional = R$ 1.480,50).
  - Moto 500cc (diária R$ 130 + taxa alta cilindrada R$ 30 = R$ 160/dia * 7 dias = R$ 1.120,00 - 8% desconto promocional = R$ 1.030,40).
- **Resultado Obtido:** Cada objeto executou a lógica especializada de sua subclasse através da referência comum `Veiculo`, demonstrando polimorfismo dinâmico (*late binding*) sem nenhuma condicional `if/switch` ou `instanceof`. **[APROVADO]**

---

### Teste 6: Utilização das Versões Sobrecarregadas
- **Situação / Entrada:**
  - `veiculo.calcularValorLocacao(7)` (versão 1: sem seguro)
  - `veiculo.calcularValorLocacao(7, true)` (versão 2: incluindo seguro opcional)
  - `locacao.calcularValorFinal()` (versão 1: valor padrão)
  - `locacao.calcularValorFinal(10.0)` (versão 2: aplicando 10% de cupom promocional extra)
- **Resultado Esperado:** A versão sobrecarregada deve incorporar a taxa de seguro calculada via interface `Tarifavel` ou abater o cupom de desconto.
- **Resultado Obtido:** Ambas as versões sobrecarregadas foram acionadas e retornaram os valores corretos. **[APROVADO]**

---

### Teste 7: Encerramento e Reinicialização Comprovando a Persistência
- **Situação / Entrada:**
  1. Cadastrar um veículo no sistema (`placa = 'TST1A11'`).
  2. Encerrar o programa (opção 0 do menu).
  3. Reiniciar a aplicação e consultar a lista de veículos cadastrados.
- **Resultado Esperado:** Os dados cadastrados na sessão anterior devem ser lidos do MySQL e exibidos com todos os dados íntegros.
- **Resultado Obtido:** O registro foi preservado no banco de dados e devidamente recuperado na inicialização subsequente, confirmando que a aplicação não depende de memória volátil. **[APROVADO]**

---

### Teste 8: Tratamento de Entrada Inválida e Falha de Conexão
- **Caso 8.1: Opção Inexistente no Menu:**
  - Entrada: Opção `99`.
  - Esperado: Mensagem `"[AVISO] Opcao inexistente. Escolha uma opcao valida entre 0 e 9."` e permanência do menu.
  - Obtido: Tratado sem interrupção do programa. **[APROVADO]**
- **Caso 8.2: Entrada Alfanumérica Onde se Espera Número:**
  - Entrada: Digitar `"abc"` no campo de diária ou ano.
  - Esperado: Captura de `NumberFormatException` e mensagem amigável solicitando novo valor.
  - Obtido: Entrada rejeitada e novo valor solicitado com sucesso. **[APROVADO]**
- **Caso 8.3: Falha de Conexão com o Banco de Dados:**
  - Entrada: Tentativa de conexão com host/porta inexistentes (`localhost:9999`).
  - Esperado: Captura da `SQLException`, encapsulamento em `AcessoDadosException` e exibição de diagnóstico claro sem fechamento abrupto.
  - Obtido: Mensagem compreensível exibida no console e controle mantido. **[APROVADO]**
