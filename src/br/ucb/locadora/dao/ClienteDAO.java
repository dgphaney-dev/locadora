package br.ucb.locadora.dao;

import br.ucb.locadora.modelo.Cliente;
import java.util.List;

/**
 * Interface DAO para operacoes de persistencia da entidade Cliente.
 */
public interface ClienteDAO {

    void inserir(Cliente cliente);

    void atualizar(Cliente cliente);

    void excluir(int id);

    Cliente buscarPorId(int id);

    Cliente buscarPorCpf(String cpf);

    List<Cliente> listarTodos();
}
