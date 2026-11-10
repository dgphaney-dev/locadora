package br.ucb.locadora.app;

import br.ucb.locadora.util.FabricaConexao;
import br.ucb.locadora.visao.MenuConsole;

/**
 * Ponto de entrada (Main) do Sistema DriveWay Locadora.
 */
public class Principal {

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("                 INICIALIZANDO DRIVEWAY LOCADORA                         ");
        System.out.println("=========================================================================");
        System.out.println("Verificando conectividade com o banco de dados MySQL...");

        if (FabricaConexao.testarConexao()) {
            System.out.println("[OK] Conexao com o MySQL estabelecida com sucesso!");
        } else {
            System.err.println("[ATENCAO] Nao foi possivel conectar ao MySQL em '" + FabricaConexao.getUrl() + "'.");
            System.err.println("Certifique-se de que o MySQL esta em execucao e o banco 'locadora_db' foi criado.");
            System.err.println("Voce pode configurar usuario/senha no arquivo 'db.properties'.");
            System.err.println("O sistema sera iniciado para demonstracao do menu.");
        }
        System.out.println();

        MenuConsole menu = new MenuConsole();
        menu.iniciar();
    }
}
