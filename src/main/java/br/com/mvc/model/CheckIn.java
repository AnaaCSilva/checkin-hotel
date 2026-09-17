package br.com.mvc.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CheckIn {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Long id;
    private Long hospedeId;
    private Long quartoId;
    private LocalDateTime dataCheckin;
    private LocalDateTime dataCheckout;

    // Campos de apoio para a listagem (vem do JOIN, nao existem na tabela checkin).
    private String hospedeNome;
    private String quartoNumero;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHospedeId() { return hospedeId; }
    public void setHospedeId(Long hospedeId) { this.hospedeId = hospedeId; }

    public Long getQuartoId() { return quartoId; }
    public void setQuartoId(Long quartoId) { this.quartoId = quartoId; }

    public LocalDateTime getDataCheckin() { return dataCheckin; }
    public void setDataCheckin(LocalDateTime dataCheckin) { this.dataCheckin = dataCheckin; }

    public LocalDateTime getDataCheckout() { return dataCheckout; }
    public void setDataCheckout(LocalDateTime dataCheckout) { this.dataCheckout = dataCheckout; }

    public String getHospedeNome() { return hospedeNome; }
    public void setHospedeNome(String hospedeNome) { this.hospedeNome = hospedeNome; }

    public String getQuartoNumero() { return quartoNumero; }
    public void setQuartoNumero(String quartoNumero) { this.quartoNumero = quartoNumero; }

    public boolean isAtivo() {
        return this.dataCheckout == null;
    }

    // Getters prontos para exibir no JSP (o fmt do JSTL nao trabalha com LocalDateTime).
    public String getDataCheckinFormatada() {
        return (dataCheckin != null) ? dataCheckin.format(FORMATO) : "";
    }

    public String getDataCheckoutFormatada() {
        return (dataCheckout != null) ? dataCheckout.format(FORMATO) : "";
    }
}
