package br.com.mvc.controller.api;

import br.com.mvc.config.JsonConfig;
import br.com.mvc.exception.ConflitoException;
import br.com.mvc.exception.RecursoNaoEncontradoException;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * Base dos controllers REST.
 * Cuida so de HTTP/JSON: ler a URL, ler o corpo, escrever a resposta e
 * traduzir excecoes do Service em codigos de status. Regra de negocio fica no Service.
 */
public abstract class ApiBaseServlet extends HttpServlet {

    /** Partes da URL depois do mapeamento. /api/checkins/5/checkout -> ["5", "checkout"]. */
    protected String[] segmentos(HttpServletRequest req) {
        String info = req.getPathInfo();
        if (info == null || info.equals("/")) {
            return new String[0];
        }
        return info.substring(1).split("/");
    }

    protected Long paraId(String texto) {
        try {
            return Long.valueOf(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Id invalido na URL.");
        }
    }

    /** Id de /recurso/{id}. Devolve null para /recurso. */
    protected Long idDaUrl(HttpServletRequest req) {
        String[] partes = this.segmentos(req);
        if (partes.length == 0) {
            return null;
        }
        if (partes.length > 1) {
            throw new RecursoNaoEncontradoException("Rota nao encontrada.");
        }
        return this.paraId(partes[0]);
    }

    protected Long idObrigatorio(HttpServletRequest req) {
        Long id = this.idDaUrl(req);
        if (id == null) {
            throw new IllegalArgumentException("Informe o id na URL.");
        }
        return id;
    }

    protected <T> T lerCorpo(HttpServletRequest req, Class<T> tipo) throws IOException {
        try {
            T corpo = JsonConfig.GSON.fromJson(req.getReader(), tipo);
            if (corpo == null) {
                throw new IllegalArgumentException("Corpo JSON obrigatorio.");
            }
            return corpo;
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("JSON invalido.");
        }
    }

    protected void json(HttpServletResponse resp, int status, Object corpo) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        if (corpo != null) {
            resp.getWriter().write(JsonConfig.GSON.toJson(corpo));
        }
    }

    protected void erro(HttpServletResponse resp, int status, String mensagem) throws IOException {
        this.json(resp, status, Map.of("erro", mensagem == null ? "Erro inesperado." : mensagem));
    }

    /**
     * Excecao -> status HTTP. A ordem importa: 404 e 409 sao filhas de
     * IllegalArgumentException, entao precisam ser testadas antes dela.
     */
    protected void tratarExcecao(HttpServletResponse resp, Exception e) throws IOException {
        if (e instanceof RecursoNaoEncontradoException) {
            this.erro(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } else if (e instanceof ConflitoException || e instanceof IllegalStateException) {
            this.erro(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } else if (e instanceof IllegalArgumentException) {
            this.erro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } else {
            this.getServletContext().log("Erro na API", e);
            this.erro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno do servidor.");
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        super.service(req, resp);
    }
}
