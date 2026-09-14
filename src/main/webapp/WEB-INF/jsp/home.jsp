<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Painel - Check-in Hotel</title>
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
        <h1>Painel Principal</h1>
    </div>

    <div class="grid-cards">
        <a class="menu-card" href="${pageContext.request.contextPath}/hospedes">
            <strong>Hóspedes</strong>
            <span>Listar, cadastrar, editar e excluir hóspedes.</span>
        </a>

        <a class="menu-card" href="${pageContext.request.contextPath}/quartos">
            <strong>Quartos</strong>
            <span>Listar, cadastrar, editar e gerenciar status dos quartos.</span>
        </a>

        <a class="menu-card" href="${pageContext.request.contextPath}/usuarios">
            <strong>Usuários</strong>
            <span>Listar, cadastrar, editar e excluir usuários.</span>
        </a>

        <a class="menu-card" href="${pageContext.request.contextPath}/perfis">
            <strong>Perfis</strong>
            <span>Listar, cadastrar, editar e excluir perfis de acesso.</span>
        </a>
    </div>
</main>
</body>
</html>