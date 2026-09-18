<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/layout/cabecalho.jsp">
    <jsp:param name="titulo" value="Editar hóspede" />
    <jsp:param name="ativo" value="hospedes" />
</jsp:include>

<div class="secao-cabecalho">
    <div>
        <h2>
            <c:choose>
                <c:when test="${empty hospede.id}">Novo hóspede</c:when>
                <c:otherwise>Editar hóspede</c:otherwise>
            </c:choose>
        </h2>
        <p>Os dados ficam salvos para os próximos check-ins deste CPF.</p>
    </div>
</div>

<c:if test="${not empty erro}">
    <div class="aviso aviso--erro">${erro}</div>
</c:if>

<form class="formulario" method="post" action="${ctx}/hospedes">
    <input type="hidden" name="id" value="${hospede.id}">

    <label for="nome">Nome completo *</label>
    <input type="text" id="nome" name="nome" maxlength="150" required value="${hospede.nome}">

    <label for="cpf">CPF *</label>
    <input type="text" id="cpf" name="cpf" maxlength="14" required
           placeholder="000.000.000-00" value="${hospede.cpfFormatado}">

    <label for="telefone">Telefone</label>
    <input type="text" id="telefone" name="telefone" maxlength="20" value="${hospede.telefone}">

    <label for="email">E-mail</label>
    <input type="email" id="email" name="email" maxlength="150" value="${hospede.email}">

    <div class="acoes">
        <button class="botao" type="submit">Salvar</button>
        <a class="botao botao--claro" href="${ctx}/hospedes">Cancelar</a>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/layout/rodape.jsp" />
