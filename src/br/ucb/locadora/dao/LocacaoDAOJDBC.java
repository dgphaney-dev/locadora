package br.ucb.locadora.dao;

import br.ucb.locadora.modelo.Cliente;
import br.ucb.locadora.modelo.Locacao;
import br.ucb.locadora.modelo.Veiculo;
import br.ucb.locadora.util.AcessoDadosException;
import br.ucb.locadora.util.FabricaConexao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LocacaoDAOJDBC implements LocacaoDAO {

    private final ClienteDAO clienteDAO;
    private final VeiculoDAO veiculoDAO;

    public LocacaoDAOJDBC() {
        this.clienteDAO = new ClienteDAOJDBC();
        this.veiculoDAO = new VeiculoDAOJDBC();
    }

    public LocacaoDAOJDBC(ClienteDAO clienteDAO, VeiculoDAO veiculoDAO) {
        this.clienteDAO = clienteDAO;
        this.veiculoDAO = veiculoDAO;
    }

    @Override
    public void inserir(Locacao locacao) {
        String sql = "INSERT INTO locacao (id_cliente, id_veiculo, data_locacao, dias_previstos, valor_total, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, locacao.getCliente().getId());
            ps.setInt(2, locacao.getVeiculo().getId());
            ps.setDate(3, Date.valueOf(locacao.getDataLocacao()));
            ps.setInt(4, locacao.getDiasPrevistos());
            ps.setDouble(5, locacao.getValorTotal());
            ps.setString(6, locacao.getStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    locacao.setId(rs.getInt(1));
                }
            }

            // Atualiza o veiculo no banco para DISPONIVEL = FALSE
            locacao.getVeiculo().setDisponivel(false);
            veiculoDAO.atualizar(locacao.getVeiculo());

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao registrar locacao: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Locacao locacao) {
        String sql = "UPDATE locacao SET id_cliente = ?, id_veiculo = ?, data_locacao = ?, dias_previstos = ?, " +
                     "valor_total = ?, status = ? WHERE id_locacao = ?";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, locacao.getCliente().getId());
            ps.setInt(2, locacao.getVeiculo().getId());
            ps.setDate(3, Date.valueOf(locacao.getDataLocacao()));
            ps.setInt(4, locacao.getDiasPrevistos());
            ps.setDouble(5, locacao.getValorTotal());
            ps.setString(6, locacao.getStatus());
            ps.setInt(7, locacao.getId());

            int linhas = ps.executeUpdate();
            if (linhas == 0) {
                throw new AcessoDadosException("Locacao ID " + locacao.getId() + " nao encontrada para atualizacao.");
            }

            // Se a locacao foi finalizada, atualiza o veiculo para disponivel
            if ("FINALIZADA".equalsIgnoreCase(locacao.getStatus())) {
                locacao.getVeiculo().setDisponivel(true);
                veiculoDAO.atualizar(locacao.getVeiculo());
            }

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao atualizar locacao: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM locacao WHERE id_locacao = ?";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas == 0) {
                throw new AcessoDadosException("Locacao ID " + id + " nao encontrada para exclusao.");
            }

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao excluir locacao: " + e.getMessage(), e);
        }
    }

    @Override
    public Locacao buscarPorId(int id) {
        String sql = "SELECT * FROM locacao WHERE id_locacao = ?";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construirDoResultSet(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao buscar locacao por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Locacao> listarTodas() {
        String sql = "SELECT * FROM locacao ORDER BY id_locacao DESC";
        List<Locacao> lista = new ArrayList<>();

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(construirDoResultSet(rs));
            }
            return lista;

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao listar locacoes: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Locacao> listarAtivas() {
        String sql = "SELECT * FROM locacao WHERE status = 'ATIVA' ORDER BY id_locacao DESC";
        List<Locacao> lista = new ArrayList<>();

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(construirDoResultSet(rs));
            }
            return lista;

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao listar locacoes ativas: " + e.getMessage(), e);
        }
    }

    @Override
    public double calcularFaturamentoTotal() {
        String sql = "SELECT COALESCE(SUM(valor_total), 0.0) FROM locacao";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao calcular faturamento: " + e.getMessage(), e);
        }
    }

    private Locacao construirDoResultSet(ResultSet rs) throws SQLException {
        int idLocacao = rs.getInt("id_locacao");
        int idCliente = rs.getInt("id_cliente");
        int idVeiculo = rs.getInt("id_veiculo");
        Date data = rs.getDate("data_locacao");
        int dias = rs.getInt("dias_previstos");
        double valorTotal = rs.getDouble("valor_total");
        String status = rs.getString("status");

        Cliente cliente = clienteDAO.buscarPorId(idCliente);
        Veiculo veiculo = veiculoDAO.buscarPorId(idVeiculo);

        return new Locacao(idLocacao, cliente, veiculo, data.toLocalDate(), dias, valorTotal, status);
    }
}
