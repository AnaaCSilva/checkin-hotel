<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Novo check-in - Check-in Hotel</title>
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
        <h1>Novo check-in</h1>
    </div>

    <div class="card">
        <c:if test="${not empty erro}">
            <div class="alert alert-erro">${erro}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/checkin">
            <input type="hidden" name="acao" value="salvar">

            <div class="form-group">
                <label for="hospedeId">Hóspede</label>
                <select id="hospedeId" name="hospedeId" required>
                    <option value="">Selecione...</option>
                    <c:forEach var="h" items="${hospedes}">
                        <option value="${h.id}">${h.nome}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="quartoId">Quarto</label>
                <select id="quartoId" name="quartoId" required>
                    <option value="">Selecione...</option>
                    <c:forEach var="q" items="${quartos}">
                        <option value="${q.id}">${q.numero} - ${q.tipo}</option>
                    </c:forEach>
                </select>
            </div>

            <c:if test="${empty quartos}">
                <p class="empty">Nenhum quarto disponível no momento.</p>
            </c:if>

            <div class="actions">
                <button type="submit" class="btn">Realizar check-in</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/checkin">Cancelar</a>
            </div>
        </form>
    </div>
</main>
</body>
</html>
