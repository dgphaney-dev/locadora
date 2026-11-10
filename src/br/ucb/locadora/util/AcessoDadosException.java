package br.ucb.locadora.util;

/**
 * Excecao personalizada para encapsular falhas de comunicacao, execucao SQL
 * e integridade referencial com o banco de dados.
 */
public class AcessoDadosException extends RuntimeException {

    public AcessoDadosException(String mensagem) {
        super(mensagem);
    }

    public AcessoDadosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
