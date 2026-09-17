package br.com.mvc.filter;

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
        // Inicializacao do filtro, se necessaria
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        boolean isLoginPage = path.equals("/login") || path.equals("/login.jsp");
        boolean isStaticResource = path.startsWith("/css/")
                                || path.startsWith("/js/")
                                || path.startsWith("/images/")
                                || path.startsWith("/assets/")
                                || path.endsWith(".css")
                                || path.endsWith(".js")
                                || path.endsWith(".png")
                                || path.endsWith(".jpg");

        HttpSession session = httpRequest.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("usuarioLogado") != null);

        if (!isStaticResource) {
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setDateHeader("Expires", 0);
        }

        if (loggedIn || isLoginPage || isStaticResource) {
            chain.doFilter(request, response);
        } else {
            boolean isAjax = "XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With"));

            if (isAjax) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            }
        }
    }

    @Override
    public void destroy() {
        // Limpeza de recursos, se necessaria
    }
}