package br.com.mvc.exception;

/**
 * Registro inexistente. A API converte em HTTP 404.
 * Estende IllegalArgumentException para que as telas JSP, que ja capturam
 * essa excecao, continuem funcionando sem alteracao.
 */
public class RecursoNaoEncontradoException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
