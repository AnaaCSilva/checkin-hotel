package br.com.mvc.controller;

import br.com.mvc.service.CheckInService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/checkin")
public class CheckInServlet extends BaseServlet {

    private static final String LISTA = "/WEB-INF/jsp/checkin/lista.jsp";
    private static final String FORM = "/WEB-INF/jsp/checkin/form.jsp";

    private final CheckInService checkInService = new CheckInService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        switch (this.acao(req)) {
            case "novo" -> this.form(req, resp);
            case "checkout" -> this.checkOut(req, resp);
            default -> this.listar(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        if ("salvar".equals(this.acao(req))) {
            this.salvar(req, resp);
            return;
        }

        this.doGet(req, resp);
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("historico", this.checkInService.listarHistorico());
        this.forward(req, resp, LISTA);
    }

    private void form(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("hospedes", this.checkInService.listarHospedes());
        req.setAttribute("quartos", this.checkInService.listarQuartosDisponiveis());
        this.forward(req, resp, FORM);
    }

    private void salvar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long hospedeId = this.paramLong(req, "hospedeId");
        Long quartoId = this.paramLong(req, "quartoId");

        try {
            this.checkInService.realizarCheckIn(hospedeId, quartoId);
            this.redirect(req, resp, "/checkin");
        } catch (Exception e) {
            req.setAttribute("erro", e.getMessage());
            this.form(req, resp);
        }
    }

    private void checkOut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            this.checkInService.realizarCheckOut(this.paramLong(req, "id"));
            this.redirect(req, resp, "/checkin");
        } catch (Exception e) {
            req.setAttribute("erro", e.getMessage());
            this.listar(req, resp);
        }
    }
}
