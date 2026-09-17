package br.com.mvc.controller;

import br.com.mvc.model.Quarto;
import br.com.mvc.service.QuartoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/quartos")
public class QuartoServlet extends BaseServlet {

    private static final String LISTA = "/WEB-INF/jsp/quartos/lista.jsp";
    private static final String FORM = "/WEB-INF/jsp/quartos/form.jsp";

    private final QuartoService quartoService = new QuartoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        switch (this.acao(req)) {
            case "novo" -> this.form(req, resp, null);
            case "editar" -> this.form(req, resp, this.quartoService.buscarPorId(this.paramLong(req, "id")));
            case "excluir" -> {
                try {
                    this.quartoService.excluir(this.paramLong(req, "id"));
                } catch (IllegalArgumentException e) {
                    req.setAttribute("erro", e.getMessage());
                    req.setAttribute("quartos", this.quartoService.listarTodos());
                    this.forward(req, resp, LISTA);
                    return;
                }
                this.redirect(req, resp, "/quartos");
            }
            default -> {
                req.setAttribute("quartos", this.quartoService.listarTodos());
                this.forward(req, resp, LISTA);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        Quarto quarto = this.fromRequest(req);

        try {
            this.quartoService.salvar(quarto);
            this.redirect(req, resp, "/quartos");
        } catch (IllegalArgumentException e) {
            req.setAttribute("erro", e.getMessage());
            this.form(req, resp, quarto);
        }
    }

    private void form(HttpServletRequest req, HttpServletResponse resp, Quarto quarto)
            throws ServletException, IOException {

        if ("editar".equals(this.acao(req)) && quarto == null) {
            this.redirect(req, resp, "/quartos");
            return;
        }

        req.setAttribute("quarto", quarto);
        this.forward(req, resp, FORM);
    }

    private Quarto fromRequest(HttpServletRequest req) {
        Quarto quarto = new Quarto();
        quarto.setId(this.paramLong(req, "id"));
        quarto.setNumero(this.param(req, "numero"));
        quarto.setTipo(this.param(req, "tipo"));
        quarto.setStatus(this.param(req, "status"));
        return quarto;
    }
}