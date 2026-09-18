<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/layout/cabecalho.jsp">
    <jsp:param name="titulo" value="Hospedagens" />
    <jsp:param name="ativo" value="checkin" />
</jsp:include>

<div class="secao-cabecalho">
    <div>
        <h2>Hospedagens</h2>
        <p>Quem está no hotel agora e o histórico de entradas e saídas.</p>
    </div>
    <a class="botao" href="${ctx}/checkin?acao=novo">+ Cadastrar hóspede</a>
</div>

<c:if test="${not empty sucesso}">
    <div class="aviso aviso--ok">${sucesso}</div>
</c:if>
<c:if test="${not empty erro}">
    <div class="aviso aviso--erro">${erro}</div>
</c:if>

<h3>Hospedados agora</h3>
<c:choose>
    <c:when test="${empty ativos}">
        <p class="vazio">Nenhum hóspede no hotel neste momento.</p>
    </c:when>
    <c:otherwise>
        <div class="tabela-wrap">
            <table>
                <thead>
                <tr>
                    <th>Hóspede</th>
                    <th>Quarto</th>
                    <th>Dias</th>
                    <th>Pagamento</th>
                    <th>Entrada</th>
                    <th>Saída prevista</th>
                    <th class="col-acoes"></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="c" items="${ativos}">
                    <tr>
                        <td>
                            <span class="principal">${c.hospedeNome}</span>
                            <span class="secundario">CPF ${c.hospedeCpf}</span>
                        </td>
                        <td>
                            <span class="principal">${c.quartoNumero}</span>
                            <span class="secundario">${c.quartoTipo}</span>
                        </td>
                        <td>${c.quantidadeDias}</td>
                        <td>${c.formaPagamento}</td>
                        <td>${c.dataCheckinFormatada}</td>
                        <td>${c.dataPrevistaSaidaFormatada}</td>
                        <td class="col-acoes">
                            <a class="botao botao--claro botao--pequeno"
                               href="${ctx}/checkin?acao=checkout&id=${c.id}"
                               onclick="return confirm('Confirmar check-out do quarto ${c.quartoNumero}?');">
                                Check-out
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<h3>Histórico</h3>
<c:choose>
    <c:when test="${empty historico}">
        <p class="vazio">Nenhuma hospedagem registrada até agora.</p>
    </c:when>
    <c:otherwise>
        <div class="tabela-wrap">
            <table>
                <thead>
                <tr>
                    <th>Hóspede</th>
                    <th>Quarto</th>
                    <th>Entrada</th>
                    <th>Saída</th>
                    <th>Situação</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="c" items="${historico}">
                    <tr>
                        <td><span class="principal">${c.hospedeNome}</span></td>
                        <td>${c.quartoNumero}</td>
                        <td>${c.dataCheckinFormatada}</td>
                        <td>
                            <c:choose>
                                <c:when test="${c.ativo}">—</c:when>
                                <c:otherwise>${c.dataCheckoutFormatada}</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <span class="selo ${c.ativo ? 'selo--ok' : 'selo--neutro'}">
                                ${c.situacao}
                            </span>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/jsp/layout/rodape.jsp" />
