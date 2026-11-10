package br.ucb.locadora.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Fabrica de conexoes JDBC para o banco de dados MySQL.
 *
 * Atende ao requisito de permitir a configuracao da conexao sem depender de caminhos
 * ou credenciais exclusivos do computador em que foi desenvolvido.
 * Busca as credenciais no arquivo db.properties e, na sua ausencia, utiliza
 * parametros padrao convencionais de desenvolvimento local.
 */
public class FabricaConexao {

    private static final String ARQUIVO_CONFIG = "db.properties";
    private static final Properties PROPRIEDADES = new Properties();

    private static String url;
    private static String usuario;
    private static String senha;

    static {
        carregarConfiguracoes();
    }

    private static void carregarConfiguracoes() {
        boolean carregou = false;

        // 1. Tenta carregar do diretorio de execucao corrente
        File arquivoLocal = new File(ARQUIVO_CONFIG);
        if (arquivoLocal.exists()) {
            try (FileInputStream fis = new FileInputStream(arquivoLocal)) {
                PROPRIEDADES.load(fis);
                carregou = true;
            } catch (IOException e) {
                System.err.println("Aviso: Falha ao ler db.properties local. Usando padroes.");
            }
        }

        // 2. Se nao encontrou no diretorio local, tenta via ClassLoader
        if (!carregou) {
            try (InputStream is = FabricaConexao.class.getClassLoader().getResourceAsStream(ARQUIVO_CONFIG)) {
                if (is != null) {
                    PROPRIEDADES.load(is);
                    carregou = true;
                }
            } catch (IOException ignored) {}
        }

        // Define valores com fallbacks seguros
        url = PROPRIEDADES.getProperty("db.url", "jdbc:mysql://localhost:3306/locadora_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8");
        usuario = PROPRIEDADES.getProperty("db.usuario", "root");
        senha = PROPRIEDADES.getProperty("db.senha", "");

        // Carrega o driver JDBC explicitamente
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Em versoes antigas do conector:
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ignored) {
                System.err.println("Aviso: Driver JDBC do MySQL nao encontrado no classpath.");
            }
        }
    }

    /**
     * Obtem uma conexao ativa com o banco de dados MySQL.
     *
     * @return Conexao JDBC ativa.
     * @throws AcessoDadosException caso ocorra falha de conexao ou driver indisponivel.
     */
    public static Connection getConexao() throws AcessoDadosException {
        try {
            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException e) {
            throw new AcessoDadosException("Nao foi possivel conectar ao banco de dados MySQL em '" + url +
                    "'. Verifique se o servico MySQL esta rodando e as credenciais em '" + ARQUIVO_CONFIG + "' estao corretas. Detalhes: " + e.getMessage(), e);
        }
    }

    /**
     * Testa se a conexao com o banco esta disponivel.
     */
    public static boolean testarConexao() {
        try (Connection con = getConexao()) {
            return con != null && !con.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUrl() {
        return url;
    }

    public static String getUsuario() {
        return usuario;
    }
}
