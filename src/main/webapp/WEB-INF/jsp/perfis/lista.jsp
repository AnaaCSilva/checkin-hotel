<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/layout/cabecalho.jsp">
    <jsp:param name="titulo" value="Perfis" />
    <jsp:param name="ativo" value="perfis" />
</jsp:include>

<div class="secao-cabecalho">
    <div>
        <h2>Perfis de acesso</h2>
        <p>Gerente vê a área restrita; os demais perfis, não.</p>
    </div>
    <a class="botao" href="${ctx}/perfis?acao=novo">+ Novo perfil</a>
</div>

<c:if test="${not empty erro}">
    <div class="aviso aviso--erro">${erro}</div>
</c:if>

<c:choose>
    <c:when test="${empty perfis}">
        <p class="vazio">Nenhum perfil cadastrado.</p>
    </c:when>
    <c:otherwise>
        <div class="tabela-wrap">
            <table>
                <thead>
                <tr>
                    <th>#</th>
                    <th>Nome</th>
                    <th class="col-acoes">Ações</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="p" items="${perfis}">
                    <tr>
                        <td>${p.id}</td>
                        <td><span class="principal">${p.nome}</span></td>
                        <td class="col-acoes">
                            <a class="link-acao" href="${ctx}/perfis?acao=editar&id=${p.id}">Editar</a>
                            <a class="link-acao link-acao--perigo"
                               href="${ctx}/perfis?acao=excluir&id=${p.id}"
                               onclick="return confirm('Excluir o perfil ${p.nome}?');">Excluir</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/jsp/layout/rodape.jsp" />
