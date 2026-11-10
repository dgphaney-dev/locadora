package br.ucb.locadora.modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Entidade de dominio que representa o Relacionamento entre Cliente e Veiculo.
 *
 * Conceitos aplicados:
 * - Relacionamento entre objetos: Associacao direta com Cliente e Veiculo (polimorfico).
 * - Encapsulamento: Validacoes de consistencia da transacao.
 * - Sobrecarga de metodos: calcularValorFinal com e sem desconto adicional.
 */
public class Locacao {

    private Integer id;
    private Cliente cliente;
    private Veiculo veiculo;
    private LocalDate dataLocacao;
    private int diasPrevistos;
    private double valorTotal;
    private String status; // "ATIVA" ou "FINALIZADA"

    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Locacao(Integer id, Cliente cliente, Veiculo veiculo, LocalDate dataLocacao, int diasPrevistos, double valorTotal, String status) {
        this.id = id;
        setCliente(cliente);
        setVeiculo(veiculo);
        setDataLocacao(dataLocacao != null ? dataLocacao : LocalDate.now());
        setDiasPrevistos(diasPrevistos);
        this.valorTotal = valorTotal > 0 ? valorTotal : calcularValorFinal();
        this.status = status != null ? status : "ATIVA";
    }

    public Locacao(Cliente cliente, Veiculo veiculo, int diasPrevistos) {
        this(null, cliente, veiculo, LocalDate.now(), diasPrevistos, 0.0, "ATIVA");
    }

    // =========================================================================
    // Sobrecarga de Metodos (Overloading)
    // =========================================================================

    /**
     * Calcula o valor final da locacao invocando o metodo polimorfico do veiculo
     * (com seguro obrigatorio incluso).
     */
    public double calcularValorFinal() {
        if (veiculo == null || diasPrevistos <= 0) return 0.0;
        return veiculo.calcularValorLocacao(diasPrevistos, true);
    }

    /**
     * Versao sobrecarregada que aplica um desconto extra em percentual (ex: cupom promocional).
     *
     * @param percentualDescontoExtra Percentual de desconto adicional (ex: 5 para 5%).
     * @return Valor final com o desconto aplicado.
     */
    public double calcularValorFinal(double percentualDescontoExtra) {
        double total = calcularValorFinal();
        if (percentualDescontoExtra > 0 && percentualDescontoExtra <= 50) {
            total = total * (1.0 - (percentualDescontoExtra / 100.0));
        }
        return total;
    }

    /**
     * Conclui o contrato de locacao e libera o veiculo para novas locacoes.
     */
    public void finalizarLocacao() {
        this.status = "FINALIZADA";
        if (this.veiculo != null) {
            this.veiculo.setDisponivel(true);
        }
    }

    // =========================================================================
    // Getters e Setters com Validacao
    // =========================================================================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("A locacao precisa de um cliente associado.");
        }
        this.cliente = cliente;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        if (veiculo == null) {
            throw new IllegalArgumentException("A locacao precisa de um veiculo associado.");
        }
        this.veiculo = veiculo;
    }

    public LocalDate getDataLocacao() {
        return dataLocacao;
    }

    public void setDataLocacao(LocalDate dataLocacao) {
        this.dataLocacao = Objects.requireNonNull(dataLocacao, "Data da locacao nao pode ser nula.");
    }

    public int getDiasPrevistos() {
        return diasPrevistos;
    }

    public void setDiasPrevistos(int diasPrevistos) {
        if (diasPrevistos <= 0) {
            throw new IllegalArgumentException("A quantidade de dias da locacao deve ser maior que zero: " + diasPrevistos);
        }
        this.diasPrevistos = diasPrevistos;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        if (valorTotal <= 0) {
            throw new IllegalArgumentException("O valor total da locacao deve ser maior que zero.");
        }
        this.valorTotal = valorTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResumo() {
        return String.format("Locacao #%d | Cliente: %s | Veiculo: %s (%s) | Dias: %d | Total: R$ %.2f | Status: %s | Data: %s",
                id != null ? id : 0,
                cliente.getNome(),
                veiculo.getModelo(),
                veiculo.getPlaca(),
                diasPrevistos,
                valorTotal,
                status,
                dataLocacao.format(FORMATADOR_DATA));
    }

    @Override
    public String toString() {
        return getResumo();
    }
}
