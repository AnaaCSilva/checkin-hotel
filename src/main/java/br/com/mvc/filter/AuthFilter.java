package br.com.mvc.filter;

import br.com.mvc.model.Usuario;
import br.com.mvc.service.UsuarioService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

/**
 * Controla o acesso ao sistema:
 *
 * 1) sem login -> so a tela /login e os arquivos de css/js
 * 2) com login -> telas do dia a dia (check-in, hospedes)
 * 3) AREA RESTRITA (quartos, recepcionistas e perfis) -> somente perfil Gerente
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    /** Rotas que so o gerente enxerga. */
    private static final Set<String> ROTAS_GERENTE = Set.of("/usuarios", "/perfis", "/quartos");

    private static final String ACESSO_NEGADO = "/WEB-INF/jsp/acesso-negado.jsp";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nada a inicializar
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        boolean paginaLogin = path.equals("/login") || path.equals("/login.jsp");
        boolean recursoEstatico = path.startsWith("/css/")
                                || path.startsWith("/js/")
                                || path.startsWith("/images/")
                                || path.endsWith(".css")
                                || path.endsWith(".js")
                                || path.endsWith(".png")
                                || path.endsWith(".jpg");

        if (!recursoEstatico) {
            resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            resp.setHeader("Pragma", "no-cache");
            resp.setDateHeader("Expires", 0);
        }

        if (paginaLogin || recursoEstatico) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        Usuario usuario = (session == null) ? null : (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            if ("XMLHttpRequest".equals(req.getHeader("X-Requested-With"))) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                resp.sendRedirect(req.getContextPath() + "/login");
            }
            return;
        }

        if (ROTAS_GERENTE.contains(path) && !UsuarioService.ehGerente(usuario)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.getRequestDispatcher(ACESSO_NEGADO).forward(req, resp);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // nada a liberar
    }
}
