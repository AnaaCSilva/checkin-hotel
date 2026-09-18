package br.com.mvc.controller;

import br.com.mvc.model.Usuario;
import br.com.mvc.service.PerfilService;
import br.com.mvc.service.UsuarioService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Area restrita: cadastro de recepcionistas.
 * O AuthFilter ja garante que so o Gerente chega ate aqui.
 */
@WebServlet("/usuarios")
public class UsuarioServlet extends BaseServlet {

    private static final String LISTA = "/WEB-INF/jsp/usuarios/lista.jsp";
    private static final String FORM = "/WEB-INF/jsp/usuarios/form.jsp";

    private final UsuarioService usuarioService = new UsuarioService();
    private final PerfilService perfilService = new PerfilService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        switch (this.acao(req)) {
            case "novo" -> this.form(req, resp, null);
            case "editar" -> this.form(req, resp, this.usuarioService.buscarPorId(this.paramLong(req, "id")));
            case "excluir" -> this.excluir(req, resp);
            default -> this.listar(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        if ("excluir".equals(this.acao(req))) {
            this.excluir(req, resp);
            return;
        }

        Usuario usuario = this.fromRequest(req);

        try {
            this.usuarioService.salvar(usuario);
            this.redirect(req, resp, "/usuarios");
        } catch (Exception e) {
            req.setAttribute("erro", e.getMessage());
            this.form(req, resp, usuario);
        }
    }

    /** Aceita exclusao por link (GET) e por formulario (POST). */
    private void excluir(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            this.usuarioService.deletar(this.paramLong(req, "id"), this.usuarioLogado(req));
            this.redirect(req, resp, "/usuarios");
        } catch (Exception e) {
            req.setAttribute("erro", e.getMessage());
            this.listar(req, resp);
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("usuarios", this.usuarioService.listar());
        this.forward(req, resp, LISTA);
    }

    private void form(HttpServletRequest req, HttpServletResponse resp, Usuario usuario)
            throws ServletException, IOException {

        if ("editar".equals(this.acao(req)) && usuario == null) {
            this.redirect(req, resp, "/usuarios");
            return;
        }

        req.setAttribute("usuario", usuario);
        req.setAttribute("perfis", this.perfilService.listar());
        this.forward(req, resp, FORM);
    }

    private Usuario fromRequest(HttpServletRequest req) {
        Usuario usuario = new Usuario();
        usuario.setId(this.paramLong(req, "id"));
        usuario.setNome(this.param(req, "nome"));
        usuario.setLogin(this.param(req, "login"));

        String senha = this.param(req, "senha");
        if (senha != null && !senha.isBlank()) {
            usuario.setSenha(senha);
        }

        usuario.setPerfilId(this.paramLong(req, "perfilId"));
        return usuario;
    }
}
