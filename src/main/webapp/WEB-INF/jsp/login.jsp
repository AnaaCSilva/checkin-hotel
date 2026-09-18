<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Acesso · Check-in Hotel</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,500;9..144,600&family=Manrope:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/estilo.css">
</head>
<body class="tela-login">

<div class="cartao-login">
    <h1>Check-in Hotel</h1>
    <p class="subtitulo">Área exclusiva de funcionários.</p>

    <c:if test="${not empty erro}">
        <div class="aviso aviso--erro">${erro}</div>
    </c:if>

    <form class="formulario" method="post" action="${ctx}/login">
        <label for="login">Login</label>
        <input type="text" id="login" name="login" required autofocus>

        <label for="senha">Senha</label>
        <input type="password" id="senha" name="senha" required>

        <button class="botao" type="submit">Entrar</button>
    </form>

    <p class="rodape-login">
        Cadastro de quartos e de novos recepcionistas fica na área restrita,
        liberada apenas para o login de gerente.
    </p>
</div>

</body>
</html>
