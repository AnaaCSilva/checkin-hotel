<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quartos - Check-in Hotel</title>
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
                <a href="${pageContext.request.contextPath}/usuarios">Usuários</a>
                <a href="${pageContext.request.contextPath}/perfis">Perfis</a>
                <a href="${pageContext.request.contextPath}/logout">Sair</a>
            </nav>
        </div>
    </header>

    <main class="container">
        <div class="page-header">
            <h1>Quartos</h1>
            <a class="btn" href="${pageContext.request.contextPath}/quartos?acao=novo">Novo quarto</a>
        </div>

        <c:if test="${not empty erro}">
            <div class="alert alert-erro">${erro}</div>
        </c:if>

        <div class="table-wrap">
            <c:choose>
                <c:when test="${empty quartos}">
                    <p class="empty">Nenhum quarto cadastrado.</p>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Número</th>
                            <th>Tipo</th>
                            <th>Status</th>
                            <th>Ações</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="quarto" items="${quartos}">
                            <tr>
                                <td>${quarto.id}</td>
                                <td>${quarto.numero}</td>
                                <td>${quarto.tipo}</td>
                                <td>${quarto.status}</td>
                                <td class="links">
                                    <a href="${pageContext.request.contextPath}/quartos?acao=editar&id=${quarto.id}">Editar</a>
                                    <a href="${pageContext.request.contextPath}/quartos?acao=excluir&id=${quarto.id}"
                                       onclick="return confirm('Excluir este quarto?');">Excluir</a>
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