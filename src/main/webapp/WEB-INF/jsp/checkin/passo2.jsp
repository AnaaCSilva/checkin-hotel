<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/layout/cabecalho.jsp">
    <jsp:param name="titulo" value="Escolha do quarto" />
    <jsp:param name="ativo" value="checkin" />
</jsp:include>

<div class="secao-cabecalho">
    <div>
        <h2>Escolha do quarto</h2>
        <p>Selecione um quarto livre e informe os dias e o pagamento.</p>
    </div>
</div>

<ol class="passos">
    <li class="feito"><span>1</span> Dados pessoais</li>
    <li class="ativo"><span>2</span> Quarto e pagamento</li>
</ol>

<div class="resumo">
    <strong>${hospede.nome}</strong>
    <span class="secundario">CPF ${hospede.cpfFormatado}</span>
    <a class="link-acao" href="${ctx}/checkin?acao=voltar">Editar dados</a>
</div>

<c:if test="${not empty erro}">
    <div class="aviso aviso--erro">${erro}</div>
</c:if>

<c:choose>
    <c:when test="${empty quartos}">
        <p class="vazio">
            Nenhum quarto disponível no momento. Faça um check-out ou peça ao
            gerente para liberar um quarto.
        </p>
        <div class="acoes">
            <a class="botao botao--claro" href="${ctx}/checkin">Voltar às hospedagens</a>
        </div>
    </c:when>

    <c:otherwise>
        <form method="post" action="${ctx}/checkin">
            <input type="hidden" name="acao" value="finalizar">

            <div class="quartos">
                <c:forEach var="q" items="${quartos}">
                    <label class="quarto">
                        <input type="radio" name="quartoId" value="${q.id}" required>
                        <span class="quarto-numero">Quarto ${q.numero}</span>
                        <span class="quarto-tipo">${q.tipo}</span>
                        <span class="selo selo--ok">${q.status}</span>
                    </label>
                </c:forEach>
            </div>

            <div class="formulario">
                <label for="quantidadeDias">Quantidade de dias *</label>
                <input type="number" id="quantidadeDias" name="quantidadeDias"
                       min="1" max="60" value="1" required>

                <label for="formaPagamento">Forma de pagamento *</label>
                <select id="formaPagamento" name="formaPagamento" required>
                    <option value="">Selecione...</option>
                    <c:forEach var="forma" items="${formasPagamento}">
                        <option value="${forma}">${forma}</option>
                    </c:forEach>
                </select>

                <div class="acoes">
                    <button class="botao" type="submit">Finalizar cadastro</button>
                    <a class="botao botao--claro" href="${ctx}/checkin?acao=voltar">Voltar</a>
                </div>
            </div>
        </form>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/jsp/layout/rodape.jsp" />
