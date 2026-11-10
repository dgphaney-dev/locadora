package br.ucb.locadora.modelo;

/**
 * Interface que define o contrato para entidades que possuem regras de tarifacao
 * de servicos adicionais (como seguro e descontos promocionais).
 * 
 * Atende ao requisito obrigatorio de Interfaces e Polimorfismo, sendo adotada
 * diretamente ou indiretamente pelas subclasses concretas da hierarquia.
 */
public interface Tarifavel {

    /**
     * Calcula o valor do seguro obrigatorio ou opcional com base no periodo de locacao.
     *
     * @param dias Quantidade de dias da locacao.
     * @return Valor total do seguro para o periodo.
     */
    double calcularTaxaSeguro(int dias);

    /**
     * Calcula o percentual ou valor de desconto promocional por tempo de permanencia.
     *
     * @param dias Quantidade de dias da locacao.
     * @return Percentual de desconto aplicavel (ex: 0.10 para 10%).
     */
    double calcularDescontoPromocional(int dias);
}
