<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Check-in - Check-in Hotel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>
    <header class="topbar">
        <div class="container">
            <strong>Check-in Hotel</strong>
            <nav>
                <a href="${pageContext.request.contextPath}/home">Home</a>
                <a href="${pageContext.request.contextPath}/hospedes">Hóspedes</a>
                <a href="${pageContext.request.contextPath}/quartos">Quartos</a>
                <a href="${pageContext.request.contextPath}/checkin">Check-in</a>
                <a href="${pageContext.request.contextPath}/usuarios">Usuários</a>
                <a href="${pageContext.request.contextPath}/perfis">Perfis</a>
                <a href="${pageContext.request.contextPath}/logout">Sair</a>
            </nav>
        </div>
    </header>

    <main class="container">
        <div class="page-header">
            <h1>Hospedagens</h1>
            <a class="btn" href="${pageContext.request.contextPath}/checkin?acao=novo">Novo check-in</a>
        </div>

        <c:if test="${not empty erro}">
            <div class="alert alert-erro">${erro}</div>
        </c:if>

        <div class="table-wrap">
            <c:choose>
                <c:when test="${empty historico}">
                    <p class="empty">Nenhuma hospedagem registrada.</p>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Hóspede</th>
                            <th>Quarto</th>
                            <th>Check-in</th>
                            <th>Check-out</th>
                            <th>Situação</th>
                            <th>Ações</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="item" items="${historico}">
                            <tr>
                                <td>${item.id}</td>
                                <td>${item.hospedeNome}</td>
                                <td>${item.quartoNumero}</td>
                                <td>${item.dataCheckinFormatada}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.ativo}">-</c:when>
                                        <c:otherwise>${item.dataCheckoutFormatada}</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.ativo}">Em andamento</c:when>
                                        <c:otherwise>Finalizada</c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="links">
                                    <c:if test="${item.ativo}">
                                        <a href="${pageContext.request.contextPath}/checkin?acao=checkout&id=${item.id}"
                                           onclick="return confirm('Confirmar check-out?');">Check-out</a>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</body>
</html>
