package br.ucb.locadora.dao;

import br.ucb.locadora.modelo.Carro;
import br.ucb.locadora.modelo.Carro.CategoriaCarro;
import br.ucb.locadora.modelo.Moto;
import br.ucb.locadora.modelo.Veiculo;
import br.ucb.locadora.util.AcessoDadosException;
import br.ucb.locadora.util.FabricaConexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacao JDBC do VeiculoDAO utilizando MySQL.
 *
 * Aplica o padrao Single Table Inheritance para persistir a hierarquia de Veiculo
 * na tabela 'veiculo', utilizando uma coluna discriminadora 'tipo_veiculo'.
 */
public class VeiculoDAOJDBC implements VeiculoDAO {

    @Override
    public void inserir(Veiculo veiculo) {
        String sql = "INSERT INTO veiculo (placa, marca, modelo, ano, valor_diaria, disponivel, tipo_veiculo, categoria_carro, cilindrada_moto) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, veiculo.getPlaca());
            ps.setString(2, veiculo.getMarca());
            ps.setString(3, veiculo.getModelo());
            ps.setInt(4, veiculo.getAno());
            ps.setDouble(5, veiculo.getValorDiaria());
            ps.setBoolean(6, veiculo.isDisponivel());

            if (veiculo instanceof Carro) {
                Carro carro = (Carro) veiculo;
                ps.setString(7, "CARRO");
                ps.setString(8, carro.getCategoria().name());
                ps.setNull(9, java.sql.Types.INTEGER);
            } else if (veiculo instanceof Moto) {
                Moto moto = (Moto) veiculo;
                ps.setString(7, "MOTO");
                ps.setNull(8, java.sql.Types.VARCHAR);
                ps.setInt(9, moto.getCilindrada());
            } else {
                throw new IllegalArgumentException("Tipo de veiculo desconhecido para persistencia.");
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    veiculo.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Codigo de erro MySQL para chave duplicada
                throw new AcessoDadosException("Ja existe um veiculo cadastrado com a placa '" + veiculo.getPlaca() + "'.", e);
            }
            throw new AcessoDadosException("Erro ao inserir veiculo no banco de dados: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Veiculo veiculo) {
        String sql = "UPDATE veiculo SET placa = ?, marca = ?, modelo = ?, ano = ?, valor_diaria = ?, disponivel = ?, " +
                     "categoria_carro = ?, cilindrada_moto = ? WHERE id_veiculo = ?";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, veiculo.getPlaca());
            ps.setString(2, veiculo.getMarca());
            ps.setString(3, veiculo.getModelo());
            ps.setInt(4, veiculo.getAno());
            ps.setDouble(5, veiculo.getValorDiaria());
            ps.setBoolean(6, veiculo.isDisponivel());

            if (veiculo instanceof Carro) {
                Carro carro = (Carro) veiculo;
                ps.setString(7, carro.getCategoria().name());
                ps.setNull(8, java.sql.Types.INTEGER);
            } else if (veiculo instanceof Moto) {
                Moto moto = (Moto) veiculo;
                ps.setNull(7, java.sql.Types.VARCHAR);
                ps.setInt(8, moto.getCilindrada());
            }

            ps.setInt(9, veiculo.getId());

            int linhasAfetadas = ps.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new AcessoDadosException("Nenhum veiculo encontrado com o ID " + veiculo.getId() + " para atualizacao.");
            }

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao atualizar veiculo ID " + veiculo.getId() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM veiculo WHERE id_veiculo = ?";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new AcessoDadosException("Nenhum veiculo encontrado com o ID " + id + " para exclusao.");
            }

        } catch (SQLException e) {
            // MySQL erro 1451: Cannot delete or update a parent row: a foreign key constraint fails
            if (e.getErrorCode() == 1451) {
                throw new AcessoDadosException("Nao e possivel excluir o veiculo ID " + id +
                        ", pois existem locacoes vinculadas a ele. Exclua as locacoes antes ou marque-o como indisponivel.", e);
            }
            throw new AcessoDadosException("Erro ao excluir veiculo ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Veiculo buscarPorId(int id) {
        String sql = "SELECT * FROM veiculo WHERE id_veiculo = ?";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construirObjetoDoResultSet(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao consultar veiculo por ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Veiculo buscarPorPlaca(String placa) {
        String sql = "SELECT * FROM veiculo WHERE placa = ?";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, placa.trim().toUpperCase().replace("-", ""));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construirObjetoDoResultSet(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao consultar veiculo por placa '" + placa + "': " + e.getMessage(), e);
        }
    }

    @Override
    public List<Veiculo> pesquisarPorModelo(String termoModelo) {
        String sql = "SELECT * FROM veiculo WHERE LOWER(modelo) LIKE ? ORDER BY modelo";
        List<Veiculo> lista = new ArrayList<>();

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + termoModelo.trim().toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(construirObjetoDoResultSet(rs));
                }
            }
            return lista;

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao pesquisar veiculos por modelo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Veiculo> listarTodos() {
        String sql = "SELECT * FROM veiculo ORDER BY id_veiculo";
        List<Veiculo> lista = new ArrayList<>();

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(construirObjetoDoResultSet(rs));
            }
            return lista;

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao listar veiculos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Veiculo> listarDisponiveis() {
        String sql = "SELECT * FROM veiculo WHERE disponivel = TRUE ORDER BY id_veiculo";
        List<Veiculo> lista = new ArrayList<>();

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(construirObjetoDoResultSet(rs));
            }
            return lista;

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao listar veiculos disponiveis: " + e.getMessage(), e);
        }
    }

    @Override
    public int contarPorTipo(String tipo) {
        String sql = "SELECT COUNT(*) FROM veiculo WHERE tipo_veiculo = ?";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipo.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao contar veiculos por tipo: " + e.getMessage(), e);
        }
    }

    /**
     * Metodo auxiliar para reconstruir objetos concretos (Carro ou Moto)
     * a partir dos dados do ResultSet.
     */
    private Veiculo construirObjetoDoResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_veiculo");
        String placa = rs.getString("placa");
        String marca = rs.getString("marca");
        String modelo = rs.getString("modelo");
        int ano = rs.getInt("ano");
        double valorDiaria = rs.getDouble("valor_diaria");
        boolean disponivel = rs.getBoolean("disponivel");
        String tipo = rs.getString("tipo_veiculo");

        if ("CARRO".equalsIgnoreCase(tipo)) {
            String categoriaStr = rs.getString("categoria_carro");
            CategoriaCarro categoria = CategoriaCarro.valueOf(categoriaStr != null ? categoriaStr.toUpperCase() : "HATCH");
            return new Carro(id, placa, marca, modelo, ano, valorDiaria, disponivel, categoria);
        } else if ("MOTO".equalsIgnoreCase(tipo)) {
            int cilindrada = rs.getInt("cilindrada_moto");
            return new Moto(id, placa, marca, modelo, ano, valorDiaria, disponivel, cilindrada);
        } else {
            throw new AcessoDadosException("Tipo de veiculo desconhecido no banco: " + tipo);
        }
    }
}
