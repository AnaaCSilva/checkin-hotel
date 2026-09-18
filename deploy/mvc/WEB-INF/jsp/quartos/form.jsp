<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>
        <c:choose>
            <c:when test="${empty quarto.id}">Novo quarto</c:when>
            <c:otherwise>Editar quarto</c:otherwise>
        </c:choose>
        - Check-in Hotel
    </title>
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
        <h1>
            <c:choose>
                <c:when test="${empty quarto.id}">Novo quarto</c:when>
                <c:otherwise>Editar quarto</c:otherwise>
            </c:choose>
        </h1>
    </div>

    <div class="card">
        <c:if test="${not empty erro}">
            <div class="alert alert-erro">${erro}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/quartos">
            <input type="hidden" name="acao" value="salvar">
            <input type="hidden" name="id" value="${quarto.id}">

            <div class="form-group">
                <label for="numero">Número</label>
                <input type="text" id="numero" name="numero" value="${quarto.numero}" required>
            </div>

            <div class="form-group">
                <label for="tipo">Tipo</label>
                <select id="tipo" name="tipo" required>
                    <option value="">Selecione...</option>
                    <option value="Solteiro" ${quarto.tipo == 'Solteiro' ? 'selected' : ''}>Solteiro</option>
                    <option value="Casal" ${quarto.tipo == 'Casal' ? 'selected' : ''}>Casal</option>
                    <option value="Luxo" ${quarto.tipo == 'Luxo' ? 'selected' : ''}>Luxo</option>
                    <option value="Suíte" ${quarto.tipo == 'Suíte' ? 'selected' : ''}>Suíte</option>
                </select>
            </div>

            <div class="form-group">
                <label for="status">Status</label>
                <select id="status" name="status" required>
                    <option value="Disponível" ${empty quarto.status || quarto.status == 'Disponível' ? 'selected' : ''}>Disponível</option>
                    <option value="Ocupado" ${quarto.status == 'Ocupado' ? 'selected' : ''}>Ocupado</option>
                    <option value="Manutenção" ${quarto.status == 'Manutenção' ? 'selected' : ''}>Manutenção</option>
                </select>
            </div>

            <div class="actions">
                <button type="submit" class="btn">Salvar</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/quartos">Cancelar</a>
            </div>
        </form>
    </div>
</main>
</body>
</html>