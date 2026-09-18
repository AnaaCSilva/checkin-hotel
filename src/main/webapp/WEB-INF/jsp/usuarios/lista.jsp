<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/layout/cabecalho.jsp">
    <jsp:param name="titulo" value="Recepcionistas" />
    <jsp:param name="ativo" value="usuarios" />
</jsp:include>

<div class="secao-cabecalho">
    <div>
        <h2>Recepcionistas</h2>
        <p>Logins que podem acessar o sistema.</p>
    </div>
    <a class="botao" href="${ctx}/usuarios?acao=novo">+ Novo funcionário</a>
</div>

<c:if test="${not empty erro}">
    <div class="aviso aviso--erro">${erro}</div>
</c:if>

<c:choose>
    <c:when test="${empty usuarios}">
        <p class="vazio">Nenhum funcionário cadastrado.</p>
    </c:when>
    <c:otherwise>
        <div class="tabela-wrap">
            <table>
                <thead>
                <tr>
                    <th>#</th>
                    <th>Nome</th>
                    <th>Login</th>
                    <th>Perfil</th>
                    <th class="col-acoes">Ações</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="u" items="${usuarios}">
                    <tr>
                        <td>${u.id}</td>
                        <td><span class="principal">${u.nome}</span></td>
                        <td>${u.login}</td>
                        <td>
                            <span class="selo ${u.perfil.nome eq 'Gerente' ? 'selo--ocupado' : 'selo--neutro'}">
                                ${u.perfil.nome}
                            </span>
                        </td>
                        <td class="col-acoes">
                            <a class="link-acao" href="${ctx}/usuarios?acao=editar&id=${u.id}">Editar</a>
                            <a class="link-acao link-acao--perigo"
                               href="${ctx}/usuarios?acao=excluir&id=${u.id}"
                               onclick="return confirm('Excluir o acesso de ${u.nome}?');">Excluir</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/jsp/layout/rodape.jsp" />
