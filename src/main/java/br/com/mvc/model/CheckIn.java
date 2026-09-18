package br.com.mvc.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CheckIn {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Long id;
    private Long hospedeId;
    private Long quartoId;
    private Integer quantidadeDias;
    private String formaPagamento;
    private LocalDateTime dataCheckin;
    private LocalDateTime dataPrevistaSaida;
    private LocalDateTime dataCheckout;

    // Campos vindos do JOIN (nao existem na tabela checkin).
    private String hospedeNome;
    private String hospedeCpf;
    private String quartoNumero;
    private String quartoTipo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHospedeId() { return hospedeId; }
    public void setHospedeId(Long hospedeId) { this.hospedeId = hospedeId; }

    public Long getQuartoId() { return quartoId; }
    public void setQuartoId(Long quartoId) { this.quartoId = quartoId; }

    public Integer getQuantidadeDias() { return quantidadeDias; }
    public void setQuantidadeDias(Integer quantidadeDias) { this.quantidadeDias = quantidadeDias; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public LocalDateTime getDataCheckin() { return dataCheckin; }
    public void setDataCheckin(LocalDateTime dataCheckin) { this.dataCheckin = dataCheckin; }

    public LocalDateTime getDataPrevistaSaida() { return dataPrevistaSaida; }
    public void setDataPrevistaSaida(LocalDateTime dataPrevistaSaida) { this.dataPrevistaSaida = dataPrevistaSaida; }

    public LocalDateTime getDataCheckout() { return dataCheckout; }
    public void setDataCheckout(LocalDateTime dataCheckout) { this.dataCheckout = dataCheckout; }

    public String getHospedeNome() { return hospedeNome; }
    public void setHospedeNome(String hospedeNome) { this.hospedeNome = hospedeNome; }

    public String getHospedeCpf() { return hospedeCpf; }
    public void setHospedeCpf(String hospedeCpf) { this.hospedeCpf = hospedeCpf; }

    public String getQuartoNumero() { return quartoNumero; }
    public void setQuartoNumero(String quartoNumero) { this.quartoNumero = quartoNumero; }

    public String getQuartoTipo() { return quartoTipo; }
    public void setQuartoTipo(String quartoTipo) { this.quartoTipo = quartoTipo; }

    public boolean isAtivo() {
        return this.dataCheckout == null;
    }

    public String getSituacao() {
        return this.isAtivo() ? "Hospedado" : "Finalizado";
    }

    // O fmt do JSTL nao trabalha com LocalDateTime, entao formatamos aqui.
    public String getDataCheckinFormatada() {
        return (dataCheckin != null) ? dataCheckin.format(FORMATO) : "";
    }

    public String getDataPrevistaSaidaFormatada() {
        return (dataPrevistaSaida != null) ? dataPrevistaSaida.format(FORMATO) : "";
    }

    public String getDataCheckoutFormatada() {
        return (dataCheckout != null) ? dataCheckout.format(FORMATO) : "";
    }
}
