<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/layout/cabecalho.jsp">
    <jsp:param name="titulo" value="Quarto" />
    <jsp:param name="ativo" value="quartos" />
</jsp:include>

<div class="secao-cabecalho">
    <div>
        <h2>
            <c:choose>
                <c:when test="${empty quarto.id}">Novo quarto</c:when>
                <c:otherwise>Editar quarto ${quarto.numero}</c:otherwise>
            </c:choose>
        </h2>
        <p>Quartos marcados como disponíveis aparecem no check-in.</p>
    </div>
</div>

<c:if test="${not empty erro}">
    <div class="aviso aviso--erro">${erro}</div>
</c:if>

<form class="formulario" method="post" action="${ctx}/quartos">
    <input type="hidden" name="id" value="${quarto.id}">

    <label for="numero">Número *</label>
    <input type="text" id="numero" name="numero" maxlength="10" required value="${quarto.numero}">

    <label for="tipo">Tipo *</label>
    <select id="tipo" name="tipo" required>
        <option value="Solteiro" ${quarto.tipo eq 'Solteiro' ? 'selected' : ''}>Solteiro</option>
        <option value="Casal"    ${quarto.tipo eq 'Casal'    ? 'selected' : ''}>Casal</option>
        <option value="Suíte"    ${quarto.tipo eq 'Suíte'    ? 'selected' : ''}>Suíte</option>
    </select>

    <label for="status">Situação *</label>
    <select id="status" name="status" required>
        <option value="Disponível" ${empty quarto.status or quarto.status eq 'Disponível' ? 'selected' : ''}>Disponível</option>
        <option value="Ocupado"    ${quarto.status eq 'Ocupado'    ? 'selected' : ''}>Ocupado</option>
        <option value="Manutenção" ${quarto.status eq 'Manutenção' ? 'selected' : ''}>Manutenção</option>
    </select>
    <p class="dica">A situação muda sozinha no check-in e no check-out.</p>

    <div class="acoes">
        <button class="botao" type="submit">Salvar</button>
        <a class="botao botao--claro" href="${ctx}/quartos">Cancelar</a>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/layout/rodape.jsp" />
