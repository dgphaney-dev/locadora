package br.ucb.locadora.dao;

import br.ucb.locadora.modelo.Cliente;
import br.ucb.locadora.util.AcessoDadosException;
import br.ucb.locadora.util.FabricaConexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOJDBC implements ClienteDAO {

    @Override
    public void inserir(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, cpf, telefone, email) VALUES (?, ?, ?, ?)";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());
            ps.setString(3, cliente.getTelefone());
            ps.setString(4, cliente.getEmail());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new AcessoDadosException("Ja existe um cliente cadastrado com o CPF informado.", e);
            }
            throw new AcessoDadosException("Erro ao inserir cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ?, cpf = ?, telefone = ?, email = ? WHERE id_cliente = ?";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());
            ps.setString(3, cliente.getTelefone());
            ps.setString(4, cliente.getEmail());
            ps.setInt(5, cliente.getId());

            int linhas = ps.executeUpdate();
            if (linhas == 0) {
                throw new AcessoDadosException("Cliente ID " + cliente.getId() + " nao encontrado.");
            }

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas == 0) {
                throw new AcessoDadosException("Cliente ID " + id + " nao encontrado para exclusao.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new AcessoDadosException("Nao e possivel excluir o cliente ID " + id + ", pois possui historico de locacoes.", e);
            }
            throw new AcessoDadosException("Erro ao excluir cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM cliente WHERE id_cliente = ?";

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                            rs.getInt("id_cliente"),
                            rs.getString("nome"),
                            rs.getString("cpf"),
                            rs.getString("telefone"),
                            rs.getString("email")
                    );
                }
            }
            return null;

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao buscar cliente por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Cliente buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM cliente WHERE cpf = ?";
        String cpfLimpo = cpf.replaceAll("\\D", "");

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cpfLimpo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                            rs.getInt("id_cliente"),
                            rs.getString("nome"),
                            rs.getString("cpf"),
                            rs.getString("telefone"),
                            rs.getString("email")
                    );
                }
            }
            return null;

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao buscar cliente por CPF: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        String sql = "SELECT * FROM cliente ORDER BY nome";
        List<Cliente> lista = new ArrayList<>();

        try (Connection con = FabricaConexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("telefone"),
                        rs.getString("email")
                ));
            }
            return lista;

        } catch (SQLException e) {
            throw new AcessoDadosException("Erro ao listar clientes: " + e.getMessage(), e);
        }
    }
}
