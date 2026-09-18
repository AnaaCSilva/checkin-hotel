<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/layout/cabecalho.jsp">
    <jsp:param name="titulo" value="Perfil" />
    <jsp:param name="ativo" value="perfis" />
</jsp:include>

<div class="secao-cabecalho">
    <div>
        <h2>
            <c:choose>
                <c:when test="${empty perfil.id}">Novo perfil</c:when>
                <c:otherwise>Editar perfil</c:otherwise>
            </c:choose>
        </h2>
        <p>O perfil chamado "Gerente" é o que libera a área restrita.</p>
    </div>
</div>

<c:if test="${not empty erro}">
    <div class="aviso aviso--erro">${erro}</div>
</c:if>

<form class="formulario" method="post" action="${ctx}/perfis">
    <input type="hidden" name="id" value="${perfil.id}">

    <label for="nome">Nome *</label>
    <input type="text" id="nome" name="nome" maxlength="100" required value="${perfil.nome}">

    <div class="acoes">
        <button class="botao" type="submit">Salvar</button>
        <a class="botao botao--claro" href="${ctx}/perfis">Cancelar</a>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/layout/rodape.jsp" />
