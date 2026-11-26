package br.com.mvc.controller.api;

import br.com.mvc.exception.RecursoNaoEncontradoException;
import br.com.mvc.model.Quarto;
import br.com.mvc.service.QuartoService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * REST de quartos. Leitura liberada a qualquer usuario logado;
 * POST/PUT/DELETE so para Gerente (regra aplicada no AuthFilter).
 * GET /api/quartos?status=Disponível filtra por situacao.
 */
@WebServlet("/api/quartos/*")
public class QuartoApiController extends ApiBaseServlet {

    private final QuartoService quartoService = new QuartoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long id = this.idDaUrl(req);
            if (id == null) {
                String status = req.getParameter("status");
                this.json(resp, HttpServletResponse.SC_OK, (status == null || status.isBlank())
                        ? this.quartoService.listarTodos()
                        : this.quartoService.listarPorStatus(status));
                return;
            }
            Quarto quarto = this.quartoService.buscarPorId(id);
            if (quarto == null) {
                throw new RecursoNaoEncontradoException("Quarto não encontrado.");
            }
            this.json(resp, HttpServletResponse.SC_OK, quarto);
        } catch (Exception e) {
            this.tratarExcecao(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (this.idDaUrl(req) != null) {
                throw new IllegalArgumentException("POST não aceita id na URL. Use PUT para alterar.");
            }
            Quarto quarto = this.lerCorpo(req, Quarto.class);
            quarto.setId(null);
            this.quartoService.salvar(quarto);
            this.json(resp, HttpServletResponse.SC_CREATED,
                    this.quartoService.buscarPorNumero(quarto.getNumero()));
        } catch (Exception e) {
            this.tratarExcecao(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long id = this.idObrigatorio(req);
            Quarto quarto = this.lerCorpo(req, Quarto.class);
            quarto.setId(id);
            this.quartoService.salvar(quarto);
            this.json(resp, HttpServletResponse.SC_OK, this.quartoService.buscarPorId(id));
        } catch (Exception e) {
            this.tratarExcecao(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            this.quartoService.excluir(this.idObrigatorio(req));
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            this.tratarExcecao(resp, e);
        }
    }
}
