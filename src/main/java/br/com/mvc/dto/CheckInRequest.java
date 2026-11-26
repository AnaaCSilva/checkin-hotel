package br.com.mvc.dto;

import br.com.mvc.model.Hospede;

/**
 * Corpo JSON do POST /api/checkins.
 * Reune o que o recepcionista informa nos dois passos da tela:
 * dados do hospede + quarto, dias e forma de pagamento.
 */
public class CheckInRequest {

    private Hospede hospede;
    private Long quartoId;
    private Integer quantidadeDias;
    private String formaPagamento;

    public Hospede getHospede() { return hospede; }
    public Long getQuartoId() { return quartoId; }
    public Integer getQuantidadeDias() { return quantidadeDias; }
    public String getFormaPagamento() { return formaPagamento; }
}
