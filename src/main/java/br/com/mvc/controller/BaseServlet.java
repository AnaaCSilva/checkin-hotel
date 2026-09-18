package br.com.mvc.controller;

import br.com.mvc.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Base dos controllers: so helpers de rota/view.
 * Regra de negocio fica no Service.
 */
public abstract class BaseServlet extends HttpServlet {

    protected String acao(HttpServletRequest req) {
        String acao = req.getParameter("acao");
        return (acao == null || acao.isBlank()) ? "listar" : acao;
    }

    protected String param(HttpServletRequest req, String nome) {
        return req.getParameter(nome);
    }

    protected Long paramLong(HttpServletRequest req, String nome) {
        String valor = req.getParameter(nome);
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Integer paramInt(HttpServletRequest req, String nome) {
        String valor = req.getParameter(nome);
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Usuario usuarioLogado(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session == null) ? null : (Usuario) session.getAttribute("usuarioLogado");
    }

    protected void forward(HttpServletRequest req, HttpServletResponse resp, String jsp)
            throws ServletException, IOException {
        req.getRequestDispatcher(jsp).forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String caminho)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + caminho);
    }
}
