<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/layout/cabecalho.jsp">
    <jsp:param name="titulo" value="Quartos" />
    <jsp:param name="ativo" value="quartos" />
</jsp:include>

<div class="secao-cabecalho">
    <div>
        <h2>Quartos</h2>
        <p>Número, tipo e disponibilidade de cada quarto do hotel.</p>
    </div>
    <a class="botao" href="${ctx}/quartos?acao=novo">+ Novo quarto</a>
</div>

<c:if test="${not empty erro}">
    <div class="aviso aviso--erro">${erro}</div>
</c:if>

<c:choose>
    <c:when test="${empty quartos}">
        <p class="vazio">Nenhum quarto cadastrado ainda.</p>
    </c:when>
    <c:otherwise>
        <div class="tabela-wrap">
            <table>
                <thead>
                <tr>
                    <th>#</th>
                    <th>Número</th>
                    <th>Tipo</th>
                    <th>Situação</th>
                    <th class="col-acoes">Ações</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="q" items="${quartos}">
                    <tr>
                        <td>${q.id}</td>
                        <td><span class="principal">${q.numero}</span></td>
                        <td>${q.tipo}</td>
                        <td>
                            <c:choose>
                                <c:when test="${q.status eq 'Disponível'}">
                                    <span class="selo selo--ok">${q.status}</span>
                                </c:when>
                                <c:when test="${q.status eq 'Ocupado'}">
                                    <span class="selo selo--ocupado">${q.status}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="selo selo--atencao">${q.status}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="col-acoes">
                            <a class="link-acao" href="${ctx}/quartos?acao=editar&id=${q.id}">Editar</a>
                            <a class="link-acao link-acao--perigo"
                               href="${ctx}/quartos?acao=excluir&id=${q.id}"
                               onclick="return confirm('Excluir o quarto ${q.numero}?');">Excluir</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/jsp/layout/rodape.jsp" />
