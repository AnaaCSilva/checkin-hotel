<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/layout/cabecalho.jsp">
    <jsp:param name="titulo" value="Hóspedes" />
    <jsp:param name="ativo" value="hospedes" />
</jsp:include>

<div class="secao-cabecalho">
    <div>
        <h2>Hóspedes</h2>
        <p>Todo mundo que já passou pelo hotel.</p>
    </div>
    <a class="botao" href="${ctx}/checkin?acao=novo">+ Novo check-in</a>
</div>

<c:if test="${not empty erro}">
    <div class="aviso aviso--erro">${erro}</div>
</c:if>

<c:choose>
    <c:when test="${empty hospedes}">
        <p class="vazio">Nenhum hóspede cadastrado ainda.</p>
    </c:when>
    <c:otherwise>
        <div class="tabela-wrap">
            <table>
                <thead>
                <tr>
                    <th>#</th>
                    <th>Nome</th>
                    <th>CPF</th>
                    <th>Contato</th>
                    <th class="col-acoes">Ações</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="h" items="${hospedes}">
                    <tr>
                        <td>${h.id}</td>
                        <td><span class="principal">${h.nome}</span></td>
                        <td>${h.cpfFormatado}</td>
                        <td>
                            ${h.telefone}
                            <span class="secundario">${h.email}</span>
                        </td>
                        <td class="col-acoes">
                            <a class="link-acao" href="${ctx}/hospedes?acao=editar&id=${h.id}">Editar</a>
                            <a class="link-acao link-acao--perigo"
                               href="${ctx}/hospedes?acao=excluir&id=${h.id}"
                               onclick="return confirm('Excluir o hóspede ${h.nome}?');">Excluir</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/jsp/layout/rodape.jsp" />
