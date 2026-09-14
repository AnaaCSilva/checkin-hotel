package com.hotel.filter;

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

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização do filtro, se necessária
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // 1. Definição de recursos públicos e arquivos estáticos
        boolean isLoginPage = path.equals("/login.jsp") || path.equals("/login") || path.equals("/autenticar");
        boolean isStaticResource = path.startsWith("/css/") 
                                || path.startsWith("/js/") 
                                || path.startsWith("/images/") 
                                || path.startsWith("/assets/")
                                || path.endsWith(".css")
                                || path.endsWith(".js")
                                || path.endsWith(".png")
                                || path.endsWith(".jpg");

        // 2. Validação segura da sessão (evita instanciar nova sessão caso não exista)
        HttpSession session = httpRequest.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("usuario") != null);

        // 3. Aplicação de cabeçalhos HTTP para desativar o cache do navegador em rotas protegidas
        if (!isStaticResource) {
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
            httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0
            httpResponse.setDateHeader("Expires", 0); // Proxies
        }

        // 4. Lógica de controle de acesso
        if (loggedIn || isLoginPage || isStaticResource) {
            chain.doFilter(request, response);
        } else {
            // Tratamento especial para requisições assíncronas (AJAX / Fetch)
            boolean isAjax = "XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With"));

            if (isAjax) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            }
        }
    }

    @Override
    public void destroy() {
        // Limpeza de recursos, se necessária
    }
}