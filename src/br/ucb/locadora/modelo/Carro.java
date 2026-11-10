package br.ucb.locadora.modelo;

/**
 * Subclasse concreta de Veiculo representando um Carro no sistema de locacao.
 *
 * Conceitos aplicados:
 * - Heranca: Herda atributos e metodos comuns de Veiculo.
 * - Sobrescrita: Sobrescreve calcularValorLocacao, calcularTaxaSeguro e calcularDescontoPromocional.
 * - Polimorfismo: Apresenta comportamento de calculo e seguro especifico para carros.
 */
public class Carro extends Veiculo {

    public enum CategoriaCarro {
        HATCH(0.0, 20.0),
        SEDAN(35.0, 35.0),
        SUV(70.0, 50.0);

        private final double taxaAdicionalDiaria;
        private final double seguroDiario;

        CategoriaCarro(double taxaAdicionalDiaria, double seguroDiario) {
            this.taxaAdicionalDiaria = taxaAdicionalDiaria;
            this.seguroDiario = seguroDiario;
        }

        public double getTaxaAdicionalDiaria() {
            return taxaAdicionalDiaria;
        }

        public double getSeguroDiario() {
            return seguroDiario;
        }
    }

    private CategoriaCarro categoria;

    public Carro(Integer id, String placa, String marca, String modelo, int ano, double valorDiaria, boolean disponivel, CategoriaCarro categoria) {
        super(id, placa, marca, modelo, ano, valorDiaria, disponivel);
        setCategoria(categoria);
    }

    public Carro(String placa, String marca, String modelo, int ano, double valorDiaria, CategoriaCarro categoria) {
        super(placa, marca, modelo, ano, valorDiaria);
        setCategoria(categoria);
    }

    public CategoriaCarro getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaCarro categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("A categoria do carro nao pode ser nula (opcoes: HATCH, SEDAN, SUV).");
        }
        this.categoria = categoria;
    }

    @Override
    public String getTipo() {
        return "Carro (" + categoria.name() + ")";
    }

    /**
     * Sobrescrita do calculo de locacao para Carro:
     * Custo diario = (Valor da Diaria Base + Taxa Adicional da Categoria) * dias.
     * Aplica o desconto promocional conforme a duracao do contrato.
     */
    @Override
    public double calcularValorLocacao(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("A quantidade de dias deve ser maior que zero: " + dias);
        }
        double diariaEfetiva = getValorDiaria() + categoria.getTaxaAdicionalDiaria();
        double subtotal = diariaEfetiva * dias;
        double desconto = calcularDescontoPromocional(dias);
        return subtotal * (1.0 - desconto);
    }

    /**
     * Sobrescrita do calculo do seguro diario especifico para automoveis.
     */
    @Override
    public double calcularTaxaSeguro(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("Dias deve ser maior que zero.");
        }
        return categoria.getSeguroDiario() * dias;
    }

    /**
     * Regra promocional para carros:
     * A partir de 15 dias: 15% de desconto.
     * A partir de 7 dias: 10% de desconto.
     */
    @Override
    public double calcularDescontoPromocional(int dias) {
        if (dias >= 15) {
            return 0.15;
        } else if (dias >= 7) {
            return 0.10;
        }
        return 0.0;
    }

    @Override
    public String getDescricao() {
        return String.format("[CARRO - %s] %s %s (%d) - Placa: %s - Diaria: R$ %.2f (+R$ %.2f cat) [%s]",
                categoria.name(), getMarca(), getModelo(), getAno(), getPlaca(),
                getValorDiaria(), categoria.getTaxaAdicionalDiaria(), isDisponivel() ? "DISPONIVEL" : "LOCADO");
    }
}
