<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Hospedes - Check-in Hotel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>
    <header class="topbar">
        <div class="container">
            <strong>Check-in Hotel</strong>
            <nav>
                <a href="${pageContext.request.contextPath}/home">Home</a>
                <a href="${pageContext.request.contextPath}/hospedes">Hospedes</a>
                <a href="${pageContext.request.contextPath}/usuarios">Usuarios</a>
                <a href="${pageContext.request.contextPath}/perfis">Perfis</a>
                <a href="${pageContext.request.contextPath}/logout">Sair</a>
            </nav>
        </div>
    </header>

    <main class="container">
        <div class="page-header">
            <h1>Hospedes</h1>
            <a class="btn" href="${pageContext.request.contextPath}/hospedes?acao=novo">Novo hospede</a>
        </div>

        <c:if test="${not empty erro}">
            <div class="alert alert-erro">${erro}</div>
        </c:if>

        <div class="table-wrap">
            <c:choose>
                <c:when test="${empty hospedes}">
                    <p class="empty">Nenhum hospede cadastrado.</p>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nome</th>
                            <th>CPF</th>
                            <th>Telefone</th>
                            <th>Email</th>
                            <th>Acoes</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="hospede" items="${hospedes}">
                            <tr>
                                <td>${hospede.id}</td>
                                <td>${hospede.nome}</td>
                                <td>${hospede.tipoDocumento}: ${hospede.numeroDocumento}</td>
                                <td>${hospede.telefone}</td>
                                <td>${hospede.email}</td>
                                <td class="links">
                                    <a href="${pageContext.request.contextPath}/hospedes?acao=editar&id=${hospede.id}">Editar</a>
                                    <a href="${pageContext.request.contextPath}/hospedes?acao=excluir&id=${hospede.id}"
                                    onclick="return confirm('Excluir este hospede?');">Excluir</a>
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