<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/layout/cabecalho.jsp">
    <jsp:param name="titulo" value="Cadastrar hóspede" />
    <jsp:param name="ativo" value="checkin" />
</jsp:include>

<div class="secao-cabecalho">
    <div>
        <h2>Cadastrar hóspede</h2>
        <p>Comece pelos dados pessoais de quem vai se hospedar.</p>
    </div>
</div>

<ol class="passos">
    <li class="ativo"><span>1</span> Dados pessoais</li>
    <li><span>2</span> Quarto e pagamento</li>
</ol>

<c:if test="${not empty erro}">
    <div class="aviso aviso--erro">${erro}</div>
</c:if>

<form class="formulario" method="post" action="${ctx}/checkin">
    <input type="hidden" name="acao" value="proximo">

    <label for="nome">Nome completo *</label>
    <input type="text" id="nome" name="nome" maxlength="150" required
           value="${hospede.nome}" autofocus>

    <label for="cpf">CPF *</label>
    <input type="text" id="cpf" name="cpf" maxlength="14" required
           placeholder="000.000.000-00" value="${hospede.cpfFormatado}">
    <p class="dica">Pode digitar com ou sem pontuação.</p>

    <label for="telefone">Telefone</label>
    <input type="text" id="telefone" name="telefone" maxlength="20"
           placeholder="(34) 99999-0000" value="${hospede.telefone}">

    <label for="email">E-mail</label>
    <input type="email" id="email" name="email" maxlength="150" value="${hospede.email}">

    <div class="acoes">
        <button class="botao" type="submit">Próximo</button>
        <a class="botao botao--claro" href="${ctx}/checkin?acao=cancelar">Cancelar</a>
    </div>
</form>

<jsp:include page="/WEB-INF/jsp/layout/rodape.jsp" />
