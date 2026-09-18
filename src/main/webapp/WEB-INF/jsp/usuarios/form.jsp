<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/layout/cabecalho.jsp">
    <jsp:param name="titulo" value="Funcionário" />
    <jsp:param name="ativo" value="usuarios" />
</jsp:include>

<div class="secao-cabecalho">
    <div>
        <h2>
            <c:choose>
                <c:when test="${empty usuario.id}">Novo funcionário</c:when>
                <c:otherwise>Editar ${usuario.nome}</c:otherwise>
            </c:choose>
        </h2>
        <p>O perfil define o que a pessoa enxerga no sistema.</p>
    </div>
</div>

<c:if test="${not empty erro}">
    <div class="aviso aviso--erro">${erro}</div>
</c:if>

<form class="formulario" method="post" action="${ctx}/usuarios">
    <input type="hidden" name="id" value="${usuario.id}">

    <label for="nome">Nome *</label>
    <input type="text" id="nome" name="nome" maxlength="150" required value="${usuario.nome}">

    <label for="login">Login *</label>
    <input type="text" id="login" name="login" maxlength="100" required value="${usuario.login}">

    <label for="senha">Senha <c:if test="${empty usuario.id}">*</c:if></label>
    <input type="password" id="senha" name="senha" minlength="6"
           <c:if test="${empty usuario.id}">required</c:if>>
    <p class="dica">
        Mínimo de 6 caracteres.
        <c:if test="${not empty usuario.id}">Deixe em branco para manter a senha atual.</c:if>
    </p>

    <label for="perfilId">Perfil *</label>
    <select id="perfilId" name="perfilId" required>
        <option value="">Selecione...</option>
        <c:forEach var="p" items="${perfis}">
            <option value="${p.id}" ${usuario.perfilId eq p.id ? 'selected' : ''}>${p.nome}</option>
        </c:forEach>
    </select>

    <div class="acoes">
        <button class="botao" type="submit">Salvar</button>
        <a class="botao botao--claro" href="${ctx}/usuarios">Cancelar</a>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/layout/rodape.jsp" />
