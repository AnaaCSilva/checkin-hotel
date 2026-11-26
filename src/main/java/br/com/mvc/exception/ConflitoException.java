package br.com.mvc.exception;

/**
 * Operacao em conflito com o estado atual (CPF repetido, quarto ocupado,
 * registro com historico). A API converte em HTTP 409.
 */
public class ConflitoException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
