package br.ucb.locadora.modelo;

import java.time.Year;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Superclasse abstrata que representa um Veiculo generico no sistema de locacao.
 *
 * Conceitos aplicados:
 * - Abstracao: Define a estrutura comum e possui metodos abstratos e concretos.
 * - Encapsulamento: Atributos privados com getters/setters e regras de validacao rigorosas.
 * - Heranca: Serve como base para as subclasses Carro e Moto.
 * - Interface: Implementa Tarifavel, garantindo comportamento tarifario polimorfico.
 * - Sobrecarga: Metodo calcularValorLocacao sobrecarregado com diferentes assinaturas.
 */
public abstract class Veiculo implements Tarifavel {

    private Integer id;
    private String placa;
    private String marca;
    private String modelo;
    private int ano;
    private double valorDiaria;
    private boolean disponivel;

    // Padrao Mercosul e Tradicional brasileiro: ABC1234 ou ABC1D23
    private static final Pattern PADRAO_PLACA = Pattern.compile("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");

    /**
     * Construtor completo para inicializar objetos com todos os dados necessarios.
     */
    public Veiculo(Integer id, String placa, String marca, String modelo, int ano, double valorDiaria, boolean disponivel) {
        this.id = id;
        setPlaca(placa);
        setMarca(marca);
        setModelo(modelo);
        setAno(ano);
        setValorDiaria(valorDiaria);
        this.disponivel = disponivel;
    }

    /**
     * Construtor para novos veiculos (sem ID atribuido pelo banco de dados).
     */
    public Veiculo(String placa, String marca, String modelo, int ano, double valorDiaria) {
        this(null, placa, marca, modelo, ano, valorDiaria, true);
    }

    // =========================================================================
    // Metodos Abstratos (Obrigatorios em todas as subclasses concretas)
    // =========================================================================

    /**
     * Calcula o valor da locacao para uma quantidade de dias, aplicando as regras
     * especificas de cada categoria de veiculo (sobrescrito nas subclasses).
     *
     * @param dias Quantidade de dias da locacao.
     * @return Valor total da locacao.
     */
    public abstract double calcularValorLocacao(int dias);

    /**
     * Retorna a descricao textual do tipo de veiculo (ex: "Carro", "Moto").
     */
    public abstract String getTipo();

    // =========================================================================
    // Sobrecarga de Metodos (Overloading)
    // =========================================================================

    /**
     * Versao sobrecarregada que permite calcular a locacao incluindo ou nao o seguro opcional.
     *
     * @param dias Quantidade de dias da locacao.
     * @param incluirSeguro Se verdadeiro, inclui a taxa de seguro calculada via interface Tarifavel.
     * @return Valor total da locacao com ou sem seguro.
     */
    public double calcularValorLocacao(int dias, boolean incluirSeguro) {
        double valorBase = calcularValorLocacao(dias);
        if (incluirSeguro) {
            valorBase += calcularTaxaSeguro(dias);
        }
        return valorBase;
    }

    // =========================================================================
    // Metodos Concretos da Superclasse
    // =========================================================================

    /**
     * Metodo concreto que aplica um reajuste percentual no valor da diaria.
     *
     * @param percentual Percentual de reajuste (ex: 10 para +10%, -5 para desconto de 5%).
     */
    public void aplicarReajusteDiaria(double percentual) {
        double novoValor = this.valorDiaria * (1.0 + (percentual / 100.0));
        setValorDiaria(novoValor);
    }

    /**
     * Retorna uma descricao textual formatada do veiculo.
     */
    public String getDescricao() {
        return String.format("%s %s (%d) - Placa: %s - Diaria: R$ %.2f [%s]",
                marca, modelo, ano, placa, valorDiaria, disponivel ? "DISPONIVEL" : "LOCADO");
    }

    // =========================================================================
    // Encapsulamento: Getters e Setters com Regras de Validacao
    // =========================================================================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    /**
     * Regra de Validacao 1: A placa nao pode ser nula, deve possuir exatamente 7 caracteres
     * alfanumericos e atender ao formato brasileiro/Mercosul.
     */
    public void setPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("A placa do veiculo nao pode ser nula ou vazia.");
        }
        String placaFormatada = placa.trim().toUpperCase().replace("-", "");
        if (!PADRAO_PLACA.matcher(placaFormatada).matches()) {
            throw new IllegalArgumentException("Placa invalida: '" + placa + "'. Padrao esperado: ABC1234 ou ABC1D23.");
        }
        this.placa = placaFormatada;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        if (marca == null || marca.trim().isEmpty()) {
            throw new IllegalArgumentException("A marca do veiculo nao pode ser vazia.");
        }
        this.marca = marca.trim();
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new IllegalArgumentException("O modelo do veiculo nao pode ser vazio.");
        }
        this.modelo = modelo.trim();
    }

    public int getAno() {
        return ano;
    }

    /**
     * Regra de Validacao 2: O ano de fabricacao deve ser valido (entre 1990 e ano atual + 1).
     */
    public void setAno(int ano) {
        int anoLimite = Year.now().getValue() + 1;
        if (ano < 1990 || ano > anoLimite) {
            throw new IllegalArgumentException("Ano invalido (" + ano + "). Deve estar entre 1990 e " + anoLimite + ".");
        }
        this.ano = ano;
    }

    public double getValorDiaria() {
        return valorDiaria;
    }

    /**
     * Regra de Validacao 3: O valor da diaria deve ser estritamente positivo (> 0).
     */
    public void setValorDiaria(double valorDiaria) {
        if (valorDiaria <= 0) {
            throw new IllegalArgumentException("O valor da diaria deve ser maior que zero. Informado: R$ " + valorDiaria);
        }
        this.valorDiaria = valorDiaria;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Veiculo veiculo = (Veiculo) o;
        return Objects.equals(placa, veiculo.placa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placa);
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
