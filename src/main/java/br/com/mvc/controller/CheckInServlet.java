package br.com.mvc.controller;

import br.com.mvc.model.CheckIn;
import br.com.mvc.model.Hospede;
import br.com.mvc.service.CheckInService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Cadastro de hospedagem em dois passos.
 *
 *  /checkin                 -> lista hospedagens
 *  /checkin?acao=novo       -> PASSO 1: dados pessoais do hospede
 *  POST acao=proximo        -> valida e leva para o PASSO 2
 *  /checkin?acao=voltar     -> volta ao PASSO 1 sem perder o que foi digitado
 *  POST acao=finalizar      -> grava hospede + hospedagem e ocupa o quarto
 *  /checkin?acao=checkout   -> finaliza a hospedagem e libera o quarto
 */
@WebServlet("/checkin")
public class CheckInServlet extends BaseServlet {

    private static final String LISTA = "/WEB-INF/jsp/checkin/lista.jsp";
    private static final String PASSO1 = "/WEB-INF/jsp/checkin/passo1.jsp";
    private static final String PASSO2 = "/WEB-INF/jsp/checkin/passo2.jsp";

    /** Guarda os dados do passo 1 enquanto o recepcionista escolhe o quarto. */
    private static final String SESSAO_HOSPEDE = "checkinHospede";

    private final CheckInService checkInService = new CheckInService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        switch (this.acao(req)) {
            case "novo" -> this.iniciar(req, resp);
            case "voltar" -> this.passo1(req, resp, this.hospedeDaSessao(req));
            case "checkout" -> this.checkOut(req, resp);
            case "cancelar" -> {
                this.limparSessao(req);
                this.redirect(req, resp, "/checkin");
            }
            default -> this.listar(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        switch (this.acao(req)) {
            case "proximo" -> this.proximo(req, resp);
            case "finalizar" -> this.finalizar(req, resp);
            default -> this.listar(req, resp);
        }
    }

    // ---------------------------------------------------------------- PASSO 1

    private void iniciar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.limparSessao(req);
        this.passo1(req, resp, null);
    }

    private void passo1(HttpServletRequest req, HttpServletResponse resp, Hospede hospede)
            throws ServletException, IOException {
        req.setAttribute("hospede", hospede);
        this.forward(req, resp, PASSO1);
    }

    /** Valida os dados pessoais e so entao mostra os quartos. */
    private void proximo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Hospede hospede = this.hospedeDoFormulario(req);

        try {
            this.checkInService.validarDadosHospede(hospede);
        } catch (Exception e) {
            req.setAttribute("erro", e.getMessage());
            this.passo1(req, resp, hospede);
            return;
        }

        req.getSession(true).setAttribute(SESSAO_HOSPEDE, hospede);
        this.passo2(req, resp, hospede);
    }

    // ---------------------------------------------------------------- PASSO 2

    private void passo2(HttpServletRequest req, HttpServletResponse resp, Hospede hospede)
            throws ServletException, IOException {

        req.setAttribute("hospede", hospede);
        req.setAttribute("quartos", this.checkInService.listarQuartosDisponiveis());
        req.setAttribute("formasPagamento", this.checkInService.listarFormasPagamento());
        this.forward(req, resp, PASSO2);
    }

    private void finalizar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Hospede hospede = this.hospedeDaSessao(req);
        if (hospede == null) {
            // Sessao expirou ou o usuario pulou o passo 1.
            req.setAttribute("erro", "Preencha os dados do hóspede novamente.");
            this.passo1(req, resp, null);
            return;
        }

        try {
            CheckIn checkIn = this.checkInService.realizarCheckIn(
                    hospede,
                    this.paramLong(req, "quartoId"),
                    this.paramInt(req, "quantidadeDias"),
                    this.param(req, "formaPagamento"));

            this.limparSessao(req);
            req.getSession(true).setAttribute("sucesso",
                    "Check-in realizado: " + checkIn.getHospedeNome()
                            + " no quarto " + checkIn.getQuartoNumero() + ".");
            this.redirect(req, resp, "/checkin");

        } catch (Exception e) {
            req.setAttribute("erro", e.getMessage());
            this.passo2(req, resp, hospede);
        }
    }

    // ------------------------------------------------------------ LISTA / SAIDA

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("sucesso") != null) {
            req.setAttribute("sucesso", session.getAttribute("sucesso"));
            session.removeAttribute("sucesso");
        }

        req.setAttribute("ativos", this.checkInService.listarAtivos());
        req.setAttribute("historico", this.checkInService.listarHistorico());
        this.forward(req, resp, LISTA);
    }

    private void checkOut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            this.checkInService.realizarCheckOut(this.paramLong(req, "id"));
            req.getSession(true).setAttribute("sucesso", "Check-out realizado e quarto liberado.");
            this.redirect(req, resp, "/checkin");
        } catch (Exception e) {
            req.setAttribute("erro", e.getMessage());
            this.listar(req, resp);
        }
    }

    // ---------------------------------------------------------------- apoio

    private Hospede hospedeDoFormulario(HttpServletRequest req) {
        Hospede hospede = new Hospede();
        hospede.setNome(this.param(req, "nome"));
        hospede.setCpf(this.param(req, "cpf"));
        hospede.setTelefone(this.param(req, "telefone"));
        hospede.setEmail(this.param(req, "email"));
        return hospede;
    }

    private Hospede hospedeDaSessao(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session == null) ? null : (Hospede) session.getAttribute(SESSAO_HOSPEDE);
    }

    private void limparSessao(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.removeAttribute(SESSAO_HOSPEDE);
        }
    }
}
