<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/jsp/layout/cabecalho.jsp">
    <jsp:param name="titulo" value="Área restrita" />
    <jsp:param name="ativo" value="" />
</jsp:include>

<div class="bloqueio">
    <h2>Área restrita</h2>
    <p>
        Esta parte do sistema é exclusiva do gerente. Peça para ele entrar
        com o login dele para cadastrar quartos ou novos recepcionistas.
    </p>
    <a class="botao" href="${ctx}/home">Voltar ao início</a>
</div>

<jsp:include page="/WEB-INF/jsp/layout/rodape.jsp" />
