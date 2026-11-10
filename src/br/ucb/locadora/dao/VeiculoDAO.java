package br.ucb.locadora.dao;

import br.ucb.locadora.modelo.Veiculo;
import java.util.List;

/**
 * Interface DAO que define as operacoes de persistencia para a hierarquia Veiculo.
 */
public interface VeiculoDAO {

    /**
     * Insere um novo veiculo (Carro ou Moto) no banco de dados.
     */
    void inserir(Veiculo veiculo);

    /**
     * Atualiza os dados de um veiculo existente.
     */
    void atualizar(Veiculo veiculo);

    /**
     * Remove um veiculo pelo seu identificador, respeitando integridade referencial.
     */
    void excluir(int id);

    /**
     * Consulta um veiculo pelo seu identificador unico.
     */
    Veiculo buscarPorId(int id);

    /**
     * Consulta um veiculo pela sua placa.
     */
    Veiculo buscarPorPlaca(String placa);

    /**
     * Pesquisa veiculos cujo modelo contenha o termo informado.
     */
    List<Veiculo> pesquisarPorModelo(String termoModelo);

    /**
     * Lista todos os veiculos cadastrados (reconstruindo Carro e Moto).
     */
    List<Veiculo> listarTodos();

    /**
     * Lista apenas veiculos disponiveis para locacao.
     */
    List<Veiculo> listarDisponiveis();

    /**
     * Retorna a quantidade de veiculos agrupados por tipo.
     */
    int contarPorTipo(String tipo);
}
