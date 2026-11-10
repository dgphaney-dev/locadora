package br.ucb.locadora.dao;

import br.ucb.locadora.modelo.Locacao;
import java.util.List;

/**
 * Interface DAO para persistencia e gerenciamento de Locacoes.
 */
public interface LocacaoDAO {

    void inserir(Locacao locacao);

    void atualizar(Locacao locacao);

    void excluir(int id);

    Locacao buscarPorId(int id);

    List<Locacao> listarTodas();

    List<Locacao> listarAtivas();

    double calcularFaturamentoTotal();
}
