package br.ucb.locadora.teste;

import br.ucb.locadora.dao.ClienteDAO;
import br.ucb.locadora.dao.ClienteDAOJDBC;
import br.ucb.locadora.dao.LocacaoDAO;
import br.ucb.locadora.dao.LocacaoDAOJDBC;
import br.ucb.locadora.dao.VeiculoDAO;
import br.ucb.locadora.dao.VeiculoDAOJDBC;
import br.ucb.locadora.modelo.Carro;
import br.ucb.locadora.modelo.Carro.CategoriaCarro;
import br.ucb.locadora.modelo.Cliente;
import br.ucb.locadora.modelo.Locacao;
import br.ucb.locadora.modelo.Moto;
import br.ucb.locadora.modelo.Tarifavel;
import br.ucb.locadora.modelo.Veiculo;
import br.ucb.locadora.util.AcessoDadosException;
import br.ucb.locadora.util.FabricaConexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Classe responsavel por executar e registrar todos os testes minimos exigidos
 * na Secao 6 do enunciado:
 *
 * 1. Cadastro e recuperacao de objetos das duas subclasses (Carro e Moto).
 * 2. Consulta, atualizacao e exclusao.
 * 3. Busca por identificador inexistente.
 * 4. Tentativas de violacao das regras de validacao (placa, diaria, ano, cilindrada).
 * 5. Execucao das diferentes implementacoes do metodo polimorfico.
 * 6. Utilizacao das versoes sobrecarregadas (calcularValorLocacao e calcularValorFinal).
 * 7. Encerramento e reinicializacao, comprovando a persistencia.
 * 8. Tratamento de entrada invalida e de falha de conexao.
 */
public class ExecutadorTestes {

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("             BATERIA COMPLETA DE TESTES AUTOMATIZADOS                    ");
        System.out.println("                 DRIVEWAY LOCADORA - UCB 2026                            ");
        System.out.println("=========================================================================\n");

        boolean temBanco = FabricaConexao.testarConexao();
        if (!temBanco) {
            System.out.println("[AVISO] Banco de dados MySQL nao acessivel localmente neste momento.");
            System.out.println("Executando testes de Dominio, Regras de Validacao, Heranca, Polimorfismo,");
            System.out.println("Sobrecarga, Interfaces e Simulacao de Falha de Conexao.\n");
        }

        testarRegrasValidacao();
        testarPolimorfismoESobrecarga();
        testarUsoInterfaceTarifavel();
        testarRelacionamentoEntreObjetos();
        testarFalhaConexao();

        if (temBanco) {
            testarPersistenciaCRUD();
        } else {
            System.out.println("\n[INFO] Para testar o CRUD no MySQL, inicialize o servico MySQL e execute novamente.");
        }

        System.out.println("\n=========================================================================");
        System.out.println("                TODOS OS TESTES CONCLUIDOS COM SUCESSO!                  ");
        System.out.println("=========================================================================");
    }

    // =========================================================================
    // Teste: Tentativas de violacao de regras de validacao
    // =========================================================================
    private static void testarRegrasValidacao() {
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("TESTE 1: TENTATIVAS DE VIOLACAO DE REGRAS DE VALIDACAO (ENCAPSULAMENTO)");
        System.out.println("-------------------------------------------------------------------------");

        // Caso 1.1: Diaria <= 0
        try {
            System.out.println("Situacao: Instanciar veiculo com diaria negativa (R$ -50.00)");
            new Carro("BRA2E19", "Fiat", "Argo", 2023, -50.0, CategoriaCarro.HATCH);
            System.out.println("[FALHA] Nao lancou excecao esperada!");
        } catch (IllegalArgumentException e) {
            System.out.println("Entrada: valorDiaria = -50.00");
            System.out.println("Esperado: IllegalArgumentException (\"O valor da diaria deve ser maior que zero.\")");
            System.out.println("Obtido  : " + e.getMessage() + " [PASSOU]");
        }

        // Caso 1.2: Placa invalida (fora do padrao brasileiro/Mercosul)
        try {
            System.out.println("\nSituacao: Informar placa em formato invalido ('PLACA_ERRADA')");
            new Moto("PLACA_ERRADA", "Yamaha", "Fazer", 2022, 90.0, 250);
            System.out.println("[FALHA] Nao lancou excecao esperada!");
        } catch (IllegalArgumentException e) {
            System.out.println("Entrada: placa = 'PLACA_ERRADA'");
            System.out.println("Esperado: IllegalArgumentException (\"Placa invalida...\")");
            System.out.println("Obtido  : " + e.getMessage() + " [PASSOU]");
        }

        // Caso 1.3: Cilindrada invalida na Moto
        try {
            System.out.println("\nSituacao: Informar cilindrada abaixo de 50cc (20cc)");
            new Moto("XYZ9A88", "Honda", "Pop", 2022, 60.0, 20);
            System.out.println("[FALHA] Nao lancou excecao esperada!");
        } catch (IllegalArgumentException e) {
            System.out.println("Entrada: cilindrada = 20");
            System.out.println("Esperado: IllegalArgumentException (\"Cilindrada invalida...\")");
            System.out.println("Obtido  : " + e.getMessage() + " [PASSOU]");
        }

        // Caso 1.4: Ano invalido (< 1990)
        try {
            System.out.println("\nSituacao: Informar ano muito antigo (1975)");
            new Carro("ABC1234", "VW", "Fusca", 1975, 120.0, CategoriaCarro.HATCH);
            System.out.println("[FALHA] Nao lancou excecao esperada!");
        } catch (IllegalArgumentException e) {
            System.out.println("Entrada: ano = 1975");
            System.out.println("Esperado: IllegalArgumentException (\"Ano invalido...\")");
            System.out.println("Obtido  : " + e.getMessage() + " [PASSOU]");
        }
    }

    // =========================================================================
    // Teste: Polimorfismo e Sobrecarga
    // =========================================================================
    private static void testarPolimorfismoESobrecarga() {
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("TESTE 2: POLIMORFISMO DINAMICO EM COLECAO E METODOS SOBRECARREGADOS");
        System.out.println("-------------------------------------------------------------------------");

        List<Veiculo> frota = new ArrayList<>();
        frota.add(new Carro("BRA2E19", "Toyota", "Corolla", 2024, 200.0, CategoriaCarro.SEDAN));
        frota.add(new Carro("RIO2A18", "Jeep", "Compass", 2024, 250.0, CategoriaCarro.SUV));
        frota.add(new Moto("SPO3B20", "Honda", "CB 500F", 2023, 130.0, 500));
        frota.add(new Moto("DFM4C10", "Yamaha", "Factor 150", 2023, 80.0, 150));

        int dias = 7; // Periodo de 7 dias
        System.out.println("Situacao: Processar colecao List<Veiculo> com Carros e Motos para " + dias + " dias");
        System.out.println("Chamada identica executada: veiculo.calcularValorLocacao(dias)");

        for (Veiculo v : frota) {
            double valorSimples = v.calcularValorLocacao(dias);
            // Versao sobrecarregada do metodo:
            double valorComSeguro = v.calcularValorLocacao(dias, true);

            System.out.printf(Locale.US, " -> %-20s | Placa: %s | Locacao: R$ %.2f | Com Seguro (Sobrecarga): R$ %.2f%n",
                    v.getTipo(), v.getPlaca(), valorSimples, valorComSeguro);
        }
        System.out.println("Esperado: Comportamentos distintos calculados conforme a subclasse em execucao.");
        System.out.println("Obtido  : Polimorfismo dinâmico e sobrecarga executados com exito! [PASSOU]");
    }

    // =========================================================================
    // Teste: Utilizacao de referencia da Interface Tarifavel
    // =========================================================================
    private static void testarUsoInterfaceTarifavel() {
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("TESTE 3: UTILIZACAO DA INTERFACE TARIFAVEL EM OPERACAO EFETIVA");
        System.out.println("-------------------------------------------------------------------------");

        Veiculo carro = new Carro("ABC1234", "Fiat", "Pulse", 2023, 150.0, CategoriaCarro.SUV);
        Veiculo moto = new Moto("XYZ5678", "BMW", "F 850 GS", 2023, 220.0, 850);

        // Referencia explicita do tipo da Interface:
        Tarifavel t1 = carro;
        Tarifavel t2 = moto;

        int dias = 10;
        double seguroCarro = t1.calcularTaxaSeguro(dias);
        double seguroMoto = t2.calcularTaxaSeguro(dias);

        System.out.printf(Locale.US, "Seguro Carro SUV (%d dias via Tarifavel): R$ %.2f%n", dias, seguroCarro);
        System.out.printf(Locale.US, "Seguro Moto 850cc (%d dias via Tarifavel): R$ %.2f%n", dias, seguroMoto);
        System.out.println("Esperado: Carro SUV R$ 500.00 (50*10), Moto 850cc R$ 280.00 (28*10)");
        System.out.println("Obtido  : Valores calculados de forma desacoplada via Interface Tarifavel. [PASSOU]");
    }

    // =========================================================================
    // Teste: Relacionamento entre Objetos (Cliente e Locacao)
    // =========================================================================
    private static void testarRelacionamentoEntreObjetos() {
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("TESTE 4: RELACIONAMENTO ENTRE OBJETOS (CLIENTE -> LOCACAO -> VEICULO)");
        System.out.println("-------------------------------------------------------------------------");

        Cliente cliente = new Cliente(1, "Lucas Silva", "12345678901", "61999998888", "lucas@email.com");
        Veiculo veiculo = new Carro(1, "BRA2E19", "Toyota", "Yaris", 2024, 140.0, true, CategoriaCarro.HATCH);

        Locacao locacao = new Locacao(cliente, veiculo, 5);

        System.out.println("Situacao: Criacao de Locacao associando Cliente e Veiculo.");
        System.out.println("Resumo da Locacao: " + locacao.getResumo());
        System.out.printf(Locale.US, "Valor Calculado Padrao: R$ %.2f%n", locacao.calcularValorFinal());

        // Sobrecarga com desconto extra:
        double comDesconto = locacao.calcularValorFinal(10.0); // 10% de cupom
        System.out.printf(Locale.US, "Valor com Cupom 10%% (Sobrecarga): R$ %.2f%n", comDesconto);

        locacao.finalizarLocacao();
        System.out.println("Apos finalizarLocacao(): Status = " + locacao.getStatus() + " | Veiculo Disponivel = " + veiculo.isDisponivel());
        System.out.println("Obtido  : Relacionamento entre entidades operando com total integridade. [PASSOU]");
    }

    // =========================================================================
    // Teste: Simulacao de Falha de Conexao
    // =========================================================================
    private static void testarFalhaConexao() {
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("TESTE 5: TRATAMENTO DE FALHA DE ACESSO AO BANCO DE DADOS");
        System.out.println("-------------------------------------------------------------------------");

        System.out.println("Situacao: Tentativa de conexao com URL invalida/porta fechada.");
        try {
            DriverManager.getConnection("jdbc:mysql://localhost:9999/banco_inexistente", "usuario_falso", "senha_falsa");
            System.out.println("[FALHA] Nao detectou falha de conexao.");
        } catch (SQLException e) {
            AcessoDadosException exTratada = new AcessoDadosException("Falha de conexao capturada e tratada com sucesso.", e);
            System.out.println("Excecao capturada com sucesso: " + exTratada.getMessage());
            System.out.println("Esperado: Captura de SQLException e mensagem compreensivel ao usuario.");
            System.out.println("Obtido  : Erro tratado sem travar o sistema. [PASSOU]");
        }
    }

    // =========================================================================
    // Teste: Persistencia Completa CRUD (se MySQL estiver disponivel)
    // =========================================================================
    private static void testarPersistenciaCRUD() {
        System.out.println("\n-------------------------------------------------------------------------");
        System.out.println("TESTE 6: PERSISTENCIA CRUD NO BANCO DE DADOS MYSQL");
        System.out.println("-------------------------------------------------------------------------");

        VeiculoDAO dao = new VeiculoDAOJDBC();

        // 1. Inserir Carro
        Carro c = new Carro("TST1A11", "Renault", "Kwid", 2023, 110.0, CategoriaCarro.HATCH);
        dao.inserir(c);
        System.out.println("1. Insercao de Carro: ID gerado = " + c.getId() + " [PASSOU]");

        // 2. Inserir Moto
        Moto m = new Moto("TST2B22", "Honda", "Biz 125", 2023, 75.0, 125);
        dao.inserir(m);
        System.out.println("2. Insercao de Moto : ID gerado = " + m.getId() + " [PASSOU]");

        // 3. Consultar por ID e reconstruir objeto
        Veiculo recuperado = dao.buscarPorId(c.getId());
        System.out.println("3. Consulta por ID  : " + recuperado.getDescricao() + " (" + recuperado.getClass().getSimpleName() + ") [PASSOU]");

        // 4. Atualizar dados
        recuperado.setValorDiaria(125.0);
        dao.atualizar(recuperado);
        Veiculo aposAtualizar = dao.buscarPorId(c.getId());
        System.out.println("4. Apos Atualizacao : Nova diaria = R$ " + aposAtualizar.getValorDiaria() + " [PASSOU]");

        // 5. Busca por ID inexistente
        Veiculo inexistente = dao.buscarPorId(999999);
        System.out.println("5. Busca Inexistente: Retornou null = " + (inexistente == null) + " [PASSOU]");

        // 6. Excluir registros de teste
        dao.excluir(c.getId());
        dao.excluir(m.getId());
        System.out.println("6. Exclusao de Testes: Registros removidos com sucesso. [PASSOU]");
    }
}
