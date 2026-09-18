<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    Cabeçalho comum: <head>, barra superior e menu lateral.
    Fecha com layout/rodape.jsp.

    Parâmetros:
      titulo -> vai no <title> e no cabeçalho da seção
      ativo  -> qual item do menu fica destacado
                (inicio | checkin | hospedes | quartos | usuarios | perfis)

    ctx e ehGerente ficam em escopo de request, então a página que
    incluiu este arquivo também pode usá-los.
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request" />
<c:set var="usuario" value="${sessionScope.usuarioLogado}" scope="request" />
<c:set var="ehGerente" value="${usuario.perfil.nome eq 'Gerente'}" scope="request" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${param.titulo} · Check-in Hotel</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,500;9..144,600&family=Manrope:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/estilo.css">
</head>
<body>
<div class="pagina">

    <header class="topo">
        <div class="caixa-ola">
            <span class="marca">Check-in Hotel</span>
            <span class="saudacao">
                Olá, <strong>${usuario.nome}</strong>
                <span class="selo-perfil">${usuario.perfil.nome}</span>
            </span>
        </div>
        <a class="caixa-sair" href="${ctx}/logout">Sair</a>
    </header>

    <div class="corpo">

        <nav class="menu">
            <a class="nav-item ${param.ativo eq 'inicio' ? 'ativo' : ''}"
               href="${ctx}/home">Início</a>

            <a class="nav-item ${param.ativo eq 'checkin' ? 'ativo' : ''}"
               href="${ctx}/checkin">Hospedagens</a>

            <a class="nav-item ${param.ativo eq 'hospedes' ? 'ativo' : ''}"
               href="${ctx}/hospedes">Hóspedes</a>

            <c:if test="${ehGerente}">
                <span class="menu-titulo">Área do gerente</span>

                <a class="nav-item ${param.ativo eq 'quartos' ? 'ativo' : ''}"
                   href="${ctx}/quartos">Quartos</a>

                <a class="nav-item ${param.ativo eq 'usuarios' ? 'ativo' : ''}"
                   href="${ctx}/usuarios">Recepcionistas</a>

                <a class="nav-item ${param.ativo eq 'perfis' ? 'ativo' : ''}"
                   href="${ctx}/perfis">Perfis</a>
            </c:if>
        </nav>

        <main class="conteudo">
