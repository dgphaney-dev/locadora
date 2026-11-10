package br.ucb.locadora.modelo;

/**
 * Subclasse concreta de Veiculo representando uma Motocicleta no sistema de locacao.
 *
 * Conceitos aplicados:
 * - Heranca: Reutiliza caracteristicas comuns da superclasse Veiculo.
 * - Sobrescrita: Implementa regras de tarifacao e seguro proprias de motocicletas.
 * - Polimorfismo: Responde com regras distintas de Carro quando acionada pela superclasse ou interface.
 */
public class Moto extends Veiculo {

    private int cilindrada;

    public Moto(Integer id, String placa, String marca, String modelo, int ano, double valorDiaria, boolean disponivel, int cilindrada) {
        super(id, placa, marca, modelo, ano, valorDiaria, disponivel);
        setCilindrada(cilindrada);
    }

    public Moto(String placa, String marca, String modelo, int ano, double valorDiaria, int cilindrada) {
        super(placa, marca, modelo, ano, valorDiaria);
        setCilindrada(cilindrada);
    }

    public int getCilindrada() {
        return cilindrada;
    }

    /**
     * Regra de Validacao: Cilindrada da moto deve estar entre 50cc e 2500cc.
     */
    public void setCilindrada(int cilindrada) {
        if (cilindrada < 50 || cilindrada > 2500) {
            throw new IllegalArgumentException("Cilindrada invalida (" + cilindrada + " cc). Deve estar entre 50cc e 2500cc.");
        }
        this.cilindrada = cilindrada;
    }

    @Override
    public String getTipo() {
        return "Moto (" + cilindrada + " cc)";
    }

    /**
     * Sobrescrita do calculo de locacao para Moto:
     * Motos acima de 300cc possuem taxa de alta performance de R$ 30,00/dia.
     * Motos de ate 300cc possuem taxa reduzida de R$ 10,00/dia.
     * Aplica o desconto proporcional aos dias contratados.
     */
    @Override
    public double calcularValorLocacao(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("A quantidade de dias deve ser maior que zero: " + dias);
        }
        double taxaCilindrada = (cilindrada > 300) ? 30.0 : 10.0;
        double subtotal = (getValorDiaria() + taxaCilindrada) * dias;
        double desconto = calcularDescontoPromocional(dias);
        return subtotal * (1.0 - desconto);
    }

    /**
     * Sobrescrita do calculo de seguro para motocicletas:
     * Taxa de risco de R$ 28,00/dia para motos potentes (> 300cc) e R$ 14,00/dia para as demais.
     */
    @Override
    public double calcularTaxaSeguro(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("Dias deve ser maior que zero.");
        }
        double taxaDiaria = (cilindrada > 300) ? 28.0 : 14.0;
        return taxaDiaria * dias;
    }

    /**
     * Regra promocional para motos:
     * A partir de 10 dias: 12% de desconto.
     * A partir de 5 dias: 8% de desconto.
     */
    @Override
    public double calcularDescontoPromocional(int dias) {
        if (dias >= 10) {
            return 0.12;
        } else if (dias >= 5) {
            return 0.08;
        }
        return 0.0;
    }

    @Override
    public String getDescricao() {
        return String.format("[MOTO - %d cc] %s %s (%d) - Placa: %s - Diaria: R$ %.2f [%s]",
                cilindrada, getMarca(), getModelo(), getAno(), getPlaca(),
                getValorDiaria(), isDisponivel() ? "DISPONIVEL" : "LOCADO");
    }
}
