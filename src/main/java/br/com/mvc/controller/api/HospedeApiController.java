package br.com.mvc.controller.api;

import br.com.mvc.exception.RecursoNaoEncontradoException;
import br.com.mvc.model.Hospede;
import br.com.mvc.service.HospedeService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** REST de hospedes: GET/POST /api/hospedes e GET/PUT/DELETE /api/hospedes/{id}. */
@WebServlet("/api/hospedes/*")
public class HospedeApiController extends ApiBaseServlet {

    private final HospedeService hospedeService = new HospedeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long id = this.idDaUrl(req);
            if (id == null) {
                this.json(resp, HttpServletResponse.SC_OK, this.hospedeService.listar());
                return;
            }
            Hospede hospede = this.hospedeService.buscarPorId(id);
            if (hospede == null) {
                throw new RecursoNaoEncontradoException("Hóspede não encontrado.");
            }
            this.json(resp, HttpServletResponse.SC_OK, hospede);
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
            Hospede hospede = this.lerCorpo(req, Hospede.class);
            hospede.setId(null);
            this.hospedeService.salvar(hospede);
            // O Service normaliza o CPF (so digitos); com ele buscamos o registro criado, ja com id.
            this.json(resp, HttpServletResponse.SC_CREATED,
                    this.hospedeService.buscarPorCpf(hospede.getCpf()));
        } catch (Exception e) {
            this.tratarExcecao(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long id = this.idObrigatorio(req);
            Hospede hospede = this.lerCorpo(req, Hospede.class);
            hospede.setId(id);
            this.hospedeService.salvar(hospede);
            this.json(resp, HttpServletResponse.SC_OK, this.hospedeService.buscarPorId(id));
        } catch (Exception e) {
            this.tratarExcecao(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            this.hospedeService.deletar(this.idObrigatorio(req));
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            this.tratarExcecao(resp, e);
        }
    }
}
