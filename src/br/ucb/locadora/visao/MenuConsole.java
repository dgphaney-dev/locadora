package br.ucb.locadora.visao;

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

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Interface textual com o usuario via Console/Terminal.
 *
 * Implementa o menu interativo com todas as 9 funcionalidades exigidas,
 * com tratamento de excecoes e validacoes para manter o programa em execucao.
 */
public class MenuConsole {

    private final Scanner scanner;
    private final VeiculoDAO veiculoDAO;
    private final ClienteDAO clienteDAO;
    private final LocacaoDAO locacaoDAO;

    public MenuConsole() {
        this.scanner = new Scanner(System.in);
        this.veiculoDAO = new VeiculoDAOJDBC();
        this.clienteDAO = new ClienteDAOJDBC();
        this.locacaoDAO = new LocacaoDAOJDBC();
    }

    public void iniciar() {
        int opcao = -1;
        while (opcao != 0) {
            exibirMenuPrincipal();
            opcao = lerInteiro("Escolha uma opcao: ");
            System.out.println();

            try {
                switch (opcao) {
                    case 1:
                        cadastrarVeiculo();
                        break;
                    case 2:
                        listarVeiculos();
                        break;
                    case 3:
                        consultarPorId();
                        break;
                    case 4:
                        pesquisarPorModeloOuPlaca();
                        break;
                    case 5:
                        atualizarVeiculo();
                        break;
                    case 6:
                        excluirVeiculo();
                        break;
                    case 7:
                        executarSimuladorPolimorfico();
                        break;
                    case 8:
                        exibirRelatorioResumido();
                        break;
                    case 9:
                        menuClientesELocacoes();
                        break;
                    case 0:
                        System.out.println("Encerrando o sistema DriveWay Locadora. Ate logo!");
                        break;
                    default:
                        System.out.println("[AVISO] Opcao inexistente. Escolha uma opcao valida entre 0 e 9.");
                }
            } catch (AcessoDadosException e) {
                System.err.println("[ERRO DE BANCO DE DADOS] " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("[REGRA DE NEGOCIO VIOLADA] " + e.getMessage());
            } catch (Exception e) {
                System.err.println("[ERRO INESPERADO] " + e.getMessage());
            }

            if (opcao != 0) {
                pressionarEnterParaContinuar();
            }
        }
    }

    private void exibirMenuPrincipal() {
        System.out.println("=========================================================================");
        System.out.println("               DRIVEWAY LOCADORA - SISTEMA DE GESTAO                     ");
        System.out.println("=========================================================================");
        System.out.println(" 1. Cadastrar Veiculo (Carro ou Moto)");
        System.out.println(" 2. Listar Veiculos (identificando tipos e disponibilidade)");
        System.out.println(" 3. Consultar Veiculo por ID");
        System.out.println(" 4. Pesquisar Veiculos por Placa ou Modelo");
        System.out.println(" 5. Atualizar Dados do Veiculo");
        System.out.println(" 6. Excluir Veiculo (respeitando relacionamentos)");
        System.out.println(" 7. Demonstracao de Polimorfismo (Simulador de Locacao de Frota)");
        System.out.println(" 8. Relatorio Resumido de Indicadores");
        System.out.println(" 9. Gerenciar Clientes e Locacoes");
        System.out.println(" 0. Encerrar o Programa");
        System.out.println("=========================================================================");
    }

    // =========================================================================
    // 1. Cadastrar Veiculo (Carro ou Moto)
    // =========================================================================
    private void cadastrarVeiculo() {
        System.out.println("--- CADASTRAR NOVO VEICULO ---");
        System.out.println("Tipo de veiculo:");
        System.out.println("1 - Carro");
        System.out.println("2 - Moto");
        int tipo = lerInteiro("Selecione o tipo (1 ou 2): ");

        if (tipo != 1 && tipo != 2) {
            System.out.println("[ERRO] Tipo de veiculo invalido. Operacao cancelada.");
            return;
        }

        System.out.print("Placa (ex: ABC1234 ou ABC1D23): ");
        String placa = scanner.nextLine().trim();

        System.out.print("Marca (ex: Toyota, Honda, Fiat): ");
        String marca = scanner.nextLine().trim();

        System.out.print("Modelo (ex: Corolla, Civic, CG 160): ");
        String modelo = scanner.nextLine().trim();

        int ano = lerInteiro("Ano de fabricacao (1990 a 2027): ");
        double valorDiaria = lerDouble("Valor da diaria base (R$): ");

        Veiculo veiculo;
        if (tipo == 1) {
            System.out.println("Categorias de Carro disponiveis:");
            System.out.println("1 - HATCH (adicional R$ 0/dia)");
            System.out.println("2 - SEDAN (adicional R$ 35/dia)");
            System.out.println("3 - SUV   (adicional R$ 70/dia)");
            int catOpcao = lerInteiro("Escolha a categoria (1, 2 ou 3): ");
            CategoriaCarro categoria;
            switch (catOpcao) {
                case 2:
                    categoria = CategoriaCarro.SEDAN;
                    break;
                case 3:
                    categoria = CategoriaCarro.SUV;
                    break;
                default:
                    categoria = CategoriaCarro.HATCH;
                    break;
            }
            veiculo = new Carro(placa, marca, modelo, ano, valorDiaria, categoria);
        } else {
            int cilindrada = lerInteiro("Cilindrada do motor em cc (50 a 2500): ");
            veiculo = new Moto(placa, marca, modelo, ano, valorDiaria, cilindrada);
        }

        veiculoDAO.inserir(veiculo);
        System.out.println("[SUCESSO] " + veiculo.getTipo() + " cadastrado com sucesso! ID gerado: " + veiculo.getId());
    }

    // =========================================================================
    // 2. Listar Veiculos
    // =========================================================================
    private void listarVeiculos() {
        System.out.println("--- LISTA DE VEICULOS CADASTRADOS ---");
        List<Veiculo> veiculos = veiculoDAO.listarTodos();

        if (veiculos.isEmpty()) {
            System.out.println("[AVISO] Nenhum veiculo cadastrado no momento.");
            return;
        }

        System.out.printf(Locale.US, "%-4s | %-8s | %-12s | %-16s | %-4s | %-18s | %-12s | %s%n",
                "ID", "PLACA", "MARCA", "MODELO", "ANO", "TIPO / DETALHES", "DIARIA BASE", "STATUS");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (Veiculo v : veiculos) {
            System.out.printf(Locale.US, "%-4d | %-8s | %-12s | %-16s | %-4d | %-18s | R$ %9.2f | %s%n",
                    v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(), v.getAno(),
                    v.getTipo(), v.getValorDiaria(), v.isDisponivel() ? "DISPONIVEL" : "LOCADO");
        }
    }

    // =========================================================================
    // 3. Consultar por ID
    // =========================================================================
    private void consultarPorId() {
        System.out.println("--- CONSULTAR VEICULO POR ID ---");
        int id = lerInteiro("Informe o ID do veiculo: ");
        Veiculo v = veiculoDAO.buscarPorId(id);

        if (v == null) {
            System.out.println("[AVISO] Nenhum veiculo encontrado com o ID " + id + ".");
            return;
        }

        exibirDetalhesVeiculo(v);
    }

    // =========================================================================
    // 4. Pesquisar por Placa ou Modelo
    // =========================================================================
    private void pesquisarPorModeloOuPlaca() {
        System.out.println("--- PESQUISAR VEICULOS ---");
        System.out.println("1 - Pesquisar por Placa exata");
        System.out.println("2 - Pesquisar por Modelo (termo parcial)");
        int op = lerInteiro("Opcao: ");

        if (op == 1) {
            System.out.print("Informe a Placa: ");
            String placa = scanner.nextLine().trim();
            Veiculo v = veiculoDAO.buscarPorPlaca(placa);
            if (v != null) {
                exibirDetalhesVeiculo(v);
            } else {
                System.out.println("[AVISO] Nenhum veiculo encontrado com a placa '" + placa + "'.");
            }
        } else if (op == 2) {
            System.out.print("Informe o termo do Modelo: ");
            String termo = scanner.nextLine().trim();
            List<Veiculo> resultados = veiculoDAO.pesquisarPorModelo(termo);
            if (resultados.isEmpty()) {
                System.out.println("[AVISO] Nenhum veiculo encontrado contendo '" + termo + "' no modelo.");
            } else {
                System.out.println("Veiculos encontrados (" + resultados.size() + "):");
                for (Veiculo v : resultados) {
                    System.out.println(" - " + v.getDescricao());
                }
            }
        } else {
            System.out.println("[AVISO] Opcao de busca invalida.");
        }
    }

    // =========================================================================
    // 5. Atualizar Dados do Veiculo
    // =========================================================================
    private void atualizarVeiculo() {
        System.out.println("--- ATUALIZAR VEICULO ---");
        int id = lerInteiro("Informe o ID do veiculo a ser atualizado: ");
        Veiculo v = veiculoDAO.buscarPorId(id);

        if (v == null) {
            System.out.println("[AVISO] Veiculo ID " + id + " nao encontrado.");
            return;
        }

        System.out.println("Dados atuais: " + v.getDescricao());
        System.out.print("Nova Placa (ENTER para manter '" + v.getPlaca() + "'): ");
        String novaPlaca = scanner.nextLine().trim();
        if (!novaPlaca.isEmpty()) {
            v.setPlaca(novaPlaca);
        }

        System.out.print("Nova Marca (ENTER para manter '" + v.getMarca() + "'): ");
        String novaMarca = scanner.nextLine().trim();
        if (!novaMarca.isEmpty()) {
            v.setMarca(novaMarca);
        }

        System.out.print("Novo Modelo (ENTER para manter '" + v.getModelo() + "'): ");
        String novoModelo = scanner.nextLine().trim();
        if (!novoModelo.isEmpty()) {
            v.setModelo(novoModelo);
        }

        System.out.print("Novo Ano (ENTER para manter '" + v.getAno() + "'): ");
        String anoStr = scanner.nextLine().trim();
        if (!anoStr.isEmpty()) {
            v.setAno(Integer.parseInt(anoStr));
        }

        System.out.print("Nova Diaria Base (ENTER para manter R$ " + v.getValorDiaria() + "): ");
        String diariaStr = scanner.nextLine().trim();
        if (!diariaStr.isEmpty()) {
            v.setValorDiaria(Double.parseDouble(diariaStr.replace(",", ".")));
        }

        if (v instanceof Carro) {
            Carro carro = (Carro) v;
            System.out.println("Categoria atual: " + carro.getCategoria().name());
            System.out.print("Nova Categoria (1-HATCH, 2-SEDAN, 3-SUV, ENTER para manter): ");
            String catStr = scanner.nextLine().trim();
            if (!catStr.isEmpty()) {
                switch (catStr) {
                    case "1":
                        carro.setCategoria(CategoriaCarro.HATCH);
                        break;
                    case "2":
                        carro.setCategoria(CategoriaCarro.SEDAN);
                        break;
                    case "3":
                        carro.setCategoria(CategoriaCarro.SUV);
                        break;
                }
            }
        } else if (v instanceof Moto) {
            Moto moto = (Moto) v;
            System.out.print("Nova Cilindrada (ENTER para manter " + moto.getCilindrada() + " cc): ");
            String cilStr = scanner.nextLine().trim();
            if (!cilStr.isEmpty()) {
                moto.setCilindrada(Integer.parseInt(cilStr));
            }
        }

        veiculoDAO.atualizar(v);
        System.out.println("[SUCESSO] Veiculo ID " + id + " atualizado com sucesso no banco de dados!");
    }

    // =========================================================================
    // 6. Excluir Veiculo (respeitando relacionamentos)
    // =========================================================================
    private void excluirVeiculo() {
        System.out.println("--- EXCLUIR VEICULO ---");
        int id = lerInteiro("Informe o ID do veiculo a ser excluido: ");
        Veiculo v = veiculoDAO.buscarPorId(id);

        if (v == null) {
            System.out.println("[AVISO] Veiculo ID " + id + " nao encontrado.");
            return;
        }

        System.out.println("Veiculo selecionado: " + v.getDescricao());
        System.out.print("Tem certeza que deseja excluir este veiculo? (S/N): ");
        String confirma = scanner.nextLine().trim();

        if (confirma.equalsIgnoreCase("S")) {
            veiculoDAO.excluir(id);
            System.out.println("[SUCESSO] Veiculo ID " + id + " excluido com sucesso!");
        } else {
            System.out.println("Operacao cancelada pelo usuario.");
        }
    }

    // =========================================================================
    // 7. Demonstracao de Polimorfismo (Simulador de Locacao de Frota)
    // =========================================================================
    private void executarSimuladorPolimorfico() {
        System.out.println("=========================================================================");
        System.out.println("         SIMULADOR POLIMORFICO DE COTACAO E SEGURO DE FROTA              ");
        System.out.println("=========================================================================");
        System.out.println("Este modulo demonstra POLIMORFISMO DINAMICO processando uma colecao");
        System.out.println("de objetos das duas subclasses (Carro e Moto) com a mesma chamada de");
        System.out.println("metodo e demonstrando a utilizacao da interface Tarifavel.");
        System.out.println("=========================================================================");

        List<Veiculo> frota = veiculoDAO.listarTodos();
        if (frota.isEmpty()) {
            System.out.println("[AVISO] Nao ha veiculos cadastrados para a simulacao.");
            return;
        }

        int dias = lerInteiro("Informe o periodo de locacao em dias para a simulacao (ex: 7): ");
        if (dias <= 0) {
            System.out.println("[ERRO] Quantidade de dias deve ser maior que zero.");
            return;
        }

        System.out.print("Deseja incluir o seguro tarifario calculado via interface Tarifavel? (S/N): ");
        boolean comSeguro = scanner.nextLine().trim().equalsIgnoreCase("S");

        double totalFrota = 0.0;
        double totalSeguros = 0.0;

        System.out.println("\n--- RESULTADO DA SIMULACAO POLIMORFICA PARA " + dias + " DIAS ---");
        System.out.printf(Locale.US, "%-4s | %-18s | %-10s | %-12s | %-12s | %-12s%n",
                "ID", "TIPO / VEICULO", "PLACA", "LOCACAO", "SEGURO (INT)", "VALOR TOTAL");
        System.out.println("--------------------------------------------------------------------------------");

        for (Veiculo v : frota) {
            // 1. Chamada polimorfica a partir da superclasse abstrata Veiculo:
            // O comportamento do calculo varia se for Carro ou Moto em tempo de execucao (Late Binding).
            double valorLocacao = v.calcularValorLocacao(dias);

            // 2. Utilizacao da referencia da interface Tarifavel em uma operacao efetiva do sistema:
            Tarifavel tarifavel = v;
            double valorSeguro = tarifavel.calcularTaxaSeguro(dias);
            double descontoPercentual = tarifavel.calcularDescontoPromocional(dias);

            double valorFinalItem = comSeguro ? (valorLocacao + valorSeguro) : valorLocacao;
            totalFrota += valorFinalItem;
            totalSeguros += valorSeguro;

            System.out.printf(Locale.US, "%-4d | %-18s | %-10s | R$ %9.2f | R$ %9.2f | R$ %9.2f (desc: %.0f%%)%n",
                    v.getId(), v.getTipo(), v.getPlaca(), valorLocacao, valorSeguro, valorFinalItem, descontoPercentual * 100);
        }

        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf(Locale.US, "TOTAL GERAL DA FROTA SIMULADA: R$ %.2f (Total de Seguros: R$ %.2f)%n",
                totalFrota, totalSeguros);
    }

    // =========================================================================
    // 8. Relatorio Resumido de Indicadores
    // =========================================================================
    private void exibirRelatorioResumido() {
        System.out.println("=========================================================================");
        System.out.println("                 RELATORIO RESUMIDO DA LOCADORA                          ");
        System.out.println("=========================================================================");

        List<Veiculo> todos = veiculoDAO.listarTodos();
        int totalVeiculos = todos.size();
        int qtdCarros = veiculoDAO.contarPorTipo("CARRO");
        int qtdMotos = veiculoDAO.contarPorTipo("MOTO");

        long disponiveis = todos.stream().filter(Veiculo::isDisponivel).count();
        long locados = totalVeiculos - disponiveis;

        double somaDiarias = todos.stream().mapToDouble(Veiculo::getValorDiaria).sum();
        double mediaDiaria = totalVeiculos > 0 ? (somaDiarias / totalVeiculos) : 0.0;
        double faturamento = locacaoDAO.calcularFaturamentoTotal();

        System.out.println(" Total de Veiculos Cadastrados : " + totalVeiculos);
        System.out.println(" - Quantidade de Carros       : " + qtdCarros);
        System.out.println(" - Quantidade de Motocicletas : " + qtdMotos);
        System.out.println("-------------------------------------------------------------------------");
        System.out.println(" Status da Frota:");
        System.out.printf(" - Disponiveis para Locacao   : %d (%.1f%%)%n", disponiveis, totalVeiculos > 0 ? (disponiveis * 100.0 / totalVeiculos) : 0.0);
        System.out.printf(" - Atualmente Locados         : %d (%.1f%%)%n", locados, totalVeiculos > 0 ? (locados * 100.0 / totalVeiculos) : 0.0);
        System.out.println("-------------------------------------------------------------------------");
        System.out.printf(Locale.US, " Valor Medio da Diaria Base   : R$ %.2f%n", mediaDiaria);
        System.out.printf(Locale.US, " Faturamento Total Historico  : R$ %.2f%n", faturamento);
        System.out.println("=========================================================================");
    }

    // =========================================================================
    // 9. Gerenciador de Clientes e Locacoes
    // =========================================================================
    private void menuClientesELocacoes() {
        System.out.println("--- GESTAO DE CLIENTES E LOCACOES ---");
        System.out.println("1 - Cadastrar Cliente");
        System.out.println("2 - Listar Clientes");
        System.out.println("3 - Criar Nova Locacao (Relacionamento Cliente + Veiculo)");
        System.out.println("4 - Listar Locacoes");
        System.out.println("5 - Finalizar Locacao / Devolver Veiculo");
        int op = lerInteiro("Escolha a opcao: ");

        switch (op) {
            case 1: {
                System.out.print("Nome do Cliente: ");
                String nome = scanner.nextLine().trim();
                System.out.print("CPF (11 digitos): ");
                String cpf = scanner.nextLine().trim();
                System.out.print("Telefone: ");
                String tel = scanner.nextLine().trim();
                System.out.print("E-mail: ");
                String email = scanner.nextLine().trim();
                Cliente c = new Cliente(nome, cpf, tel, email);
                clienteDAO.inserir(c);
                System.out.println("[SUCESSO] Cliente cadastrado com ID: " + c.getId());
                break;
            }
            case 2: {
                List<Cliente> clientes = clienteDAO.listarTodos();
                if (clientes.isEmpty()) {
                    System.out.println("[AVISO] Nenhum cliente cadastrado.");
                } else {
                    for (Cliente c : clientes) {
                        System.out.println(" - " + c);
                    }
                }
                break;
            }
            case 3: {
                System.out.println("Selecione um cliente:");
                List<Cliente> clientes = clienteDAO.listarTodos();
                if (clientes.isEmpty()) {
                    System.out.println("[ERRO] Cadastre um cliente antes de realizar uma locacao.");
                    return;
                }
                for (Cliente c : clientes) {
                    System.out.println("ID [" + c.getId() + "] " + c.getNome() + " (CPF: " + c.getCpfFormatado() + ")");
                }
                int idCli = lerInteiro("Informe o ID do cliente: ");
                Cliente cli = clienteDAO.buscarPorId(idCli);
                if (cli == null) {
                    System.out.println("[ERRO] Cliente nao encontrado.");
                    return;
                }

                System.out.println("Selecione um veiculo disponivel:");
                List<Veiculo> disponiveis = veiculoDAO.listarDisponiveis();
                if (disponiveis.isEmpty()) {
                    System.out.println("[ERRO] Nao ha veiculos disponiveis no momento.");
                    return;
                }
                for (Veiculo v : disponiveis) {
                    System.out.println("ID [" + v.getId() + "] " + v.getDescricao());
                }
                int idVeic = lerInteiro("Informe o ID do veiculo: ");
                Veiculo veic = veiculoDAO.buscarPorId(idVeic);
                if (veic == null || !veic.isDisponivel()) {
                    System.out.println("[ERRO] Veiculo invalido ou indisponivel.");
                    return;
                }

                int dias = lerInteiro("Quantidade de dias da locacao: ");
                Locacao loc = new Locacao(cli, veic, dias);

                System.out.print("Possui cupom de desconto promocional adicional (%)? (0 para nenhum): ");
                double descExtra = lerDouble("");
                if (descExtra > 0) {
                    // Utilizacao da versao sobrecarregada do metodo calcularValorFinal:
                    loc.setValorTotal(loc.calcularValorFinal(descExtra));
                }

                locacaoDAO.inserir(loc);
                System.out.println("[SUCESSO] Locacao registrada com sucesso! ID: " + loc.getId() +
                        " | Valor Total: R$ " + String.format(Locale.US, "%.2f", loc.getValorTotal()));
                break;
            }
            case 4: {
                List<Locacao> locacoes = locacaoDAO.listarTodas();
                if (locacoes.isEmpty()) {
                    System.out.println("[AVISO] Nenhuma locacao registrada.");
                } else {
                    for (Locacao l : locacoes) {
                        System.out.println(l);
                    }
                }
                break;
            }
            case 5: {
                List<Locacao> ativas = locacaoDAO.listarAtivas();
                if (ativas.isEmpty()) {
                    System.out.println("[AVISO] Nao ha locacoes ativas no momento.");
                    return;
                }
                for (Locacao l : ativas) {
                    System.out.println(l);
                }
                int idLoc = lerInteiro("Informe o ID da locacao para finalizar/devolver: ");
                Locacao loc = locacaoDAO.buscarPorId(idLoc);
                if (loc == null || !"ATIVA".equalsIgnoreCase(loc.getStatus())) {
                    System.out.println("[ERRO] Locacao inexistente ou ja finalizada.");
                    return;
                }
                loc.finalizarLocacao();
                locacaoDAO.atualizar(loc);
                System.out.println("[SUCESSO] Locacao #" + idLoc + " finalizada! Veiculo '" + loc.getVeiculo().getPlaca() + "' liberado para novas locacoes.");
                break;
            }
            default:
                System.out.println("[AVISO] Opcao invalida.");
                break;
        }
    }

    // =========================================================================
    // Metodos Auxiliares
    // =========================================================================
    private void exibirDetalhesVeiculo(Veiculo v) {
        System.out.println("-------------------------------------------------------------------------");
        System.out.println(" ID            : " + v.getId());
        System.out.println(" Tipo          : " + v.getTipo());
        System.out.println(" Marca / Modelo: " + v.getMarca() + " " + v.getModelo());
        System.out.println(" Ano           : " + v.getAno());
        System.out.println(" Placa         : " + v.getPlaca());
        System.out.printf(Locale.US, " Diaria Base   : R$ %.2f%n", v.getValorDiaria());
        System.out.println(" Situacao      : " + (v.isDisponivel() ? "DISPONIVEL" : "LOCADO"));
        if (v instanceof Carro) {
            Carro c = (Carro) v;
            System.out.println(" Categoria     : " + c.getCategoria().name() + " (+R$ " + c.getCategoria().getTaxaAdicionalDiaria() + "/dia)");
        } else if (v instanceof Moto) {
            Moto m = (Moto) v;
            System.out.println(" Cilindrada    : " + m.getCilindrada() + " cc");
        }
        System.out.println("-------------------------------------------------------------------------");
    }

    private int lerInteiro(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String linha = scanner.nextLine().trim();
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("[AVISO] Entrada invalida. Digite um numero inteiro.");
            }
        }
    }

    private double lerDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String linha = scanner.nextLine().trim().replace(",", ".");
                return Double.parseDouble(linha);
            } catch (NumberFormatException e) {
                System.out.println("[AVISO] Entrada invalida. Digite um valor numerico valido.");
            }
        }
    }

    private void pressionarEnterParaContinuar() {
        System.out.print("\nPressione ENTER para voltar ao menu principal...");
        scanner.nextLine();
        System.out.println();
    }
}
