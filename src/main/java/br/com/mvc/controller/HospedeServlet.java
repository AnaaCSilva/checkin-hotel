package br.com.mvc.controller;

import br.com.mvc.model.Hospede;
import br.com.mvc.service.HospedeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/hospedes")
public class HospedeServlet extends BaseServlet {

    private static final String LISTA = "/WEB-INF/jsp/hospedes/lista.jsp";
    private static final String FORM = "/WEB-INF/jsp/hospedes/form.jsp";

    private final HospedeService hospedeService = new HospedeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        switch (this.acao(req)) {
            case "novo" -> this.form(req, resp, null);
            case "editar" -> this.form(req, resp, this.hospedeService.buscarPorId(this.paramLong(req, "id")));
            case "excluir" -> {
                try {
                    this.hospedeService.deletar(this.paramLong(req, "id"));
                } catch (IllegalArgumentException e) {
                    req.setAttribute("erro", e.getMessage());
                    req.setAttribute("hospedes", this.hospedeService.listar());
                    this.forward(req, resp, LISTA);
                    return;
                }
                this.redirect(req, resp, "/hospedes");
            }
            default -> {
                req.setAttribute("hospedes", this.hospedeService.listar());
                this.forward(req, resp, LISTA);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        Hospede hospede = this.fromRequest(req);

        try {
            this.hospedeService.salvar(hospede);
            this.redirect(req, resp, "/hospedes");
        } catch (IllegalArgumentException e) {
            req.setAttribute("erro", e.getMessage());
            this.form(req, resp, hospede);
        }
    }

    private void form(HttpServletRequest req, HttpServletResponse resp, Hospede hospede)
            throws ServletException, IOException {

        if ("editar".equals(this.acao(req)) && hospede == null) {
            this.redirect(req, resp, "/hospedes");
            return;
        }

        req.setAttribute("hospede", hospede);
        this.forward(req, resp, FORM);
    }

    private Hospede fromRequest(HttpServletRequest req) {
        Hospede hospede = new Hospede();
        hospede.setId(this.paramLong(req, "id"));
        hospede.setNome(this.param(req, "nome"));
        hospede.setTipoDocumento(this.param(req, "tipoDocumento"));
        hospede.setNumeroDocumento(this.param(req, "numeroDocumento"));
        hospede.setTelefone(this.param(req, "telefone"));
        hospede.setEmail(this.param(req, "email"));
        return hospede;
    }
}