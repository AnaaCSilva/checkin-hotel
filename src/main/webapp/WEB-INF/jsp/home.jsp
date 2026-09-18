<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/layout/cabecalho.jsp">
    <jsp:param name="titulo" value="Início" />
    <jsp:param name="ativo" value="inicio" />
</jsp:include>

<div class="secao-cabecalho">
    <div>
        <h2>Bem-vinda, ${usuario.nome}</h2>
        <p>O que você quer fazer agora?</p>
    </div>
    <a class="botao" href="${ctx}/checkin?acao=novo">+ Cadastrar hóspede</a>
</div>

<div class="atalhos">
    <a class="atalho atalho--destaque" href="${ctx}/checkin?acao=novo">
        <strong>Cadastrar hóspede</strong>
        <span>Dados pessoais, escolha do quarto e forma de pagamento.</span>
    </a>

    <a class="atalho" href="${ctx}/checkin">
        <strong>Hospedagens</strong>
        <span>Quem está no hotel agora e registro de check-out.</span>
    </a>

    <a class="atalho" href="${ctx}/hospedes">
        <strong>Hóspedes</strong>
        <span>Consultar e corrigir cadastros já feitos.</span>
    </a>
</div>

<c:if test="${ehGerente}">
    <h3>Área do gerente</h3>
    <div class="atalhos">
        <a class="atalho" href="${ctx}/quartos">
            <strong>Quartos</strong>
            <span>Cadastrar quartos, tipo e disponibilidade.</span>
        </a>

        <a class="atalho" href="${ctx}/usuarios">
            <strong>Recepcionistas</strong>
            <span>Adicionar e gerenciar os logins dos funcionários.</span>
        </a>
    </div>
</c:if>

<jsp:include page="/WEB-INF/jsp/layout/rodape.jsp" />
