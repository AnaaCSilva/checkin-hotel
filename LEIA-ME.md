# Check-in Hotel

Sistema de check-in de hotel em Java (Servlets + JSP), MySQL e Tomcat via Docker.

---

## 1. Como atualizar a sua pasta

Esta pasta é o projeto inteiro. Copie por cima da sua `checkin-hotel`,
substituindo os arquivos. Os únicos arquivos removidos são:

```
src/main/webapp/css/estilo-extra.css           (virou parte do estilo.css)
src/main/webapp/WEB-INF/jsp/layout/menu.jsp    (virou cabecalho.jsp + rodape.jsp)
```

Se eles ainda existirem aí, apague na mão — senão ficam sobrando no Git.

## 2. Subir para o Git

```bash
cd checkin-hotel

git status                 # confira o que mudou antes de qualquer coisa
git add -A                 # o -A também registra os arquivos apagados
git commit -m "Check-in em dois passos, area do gerente e novo layout"
git push
```

Se o `git status` mostrar `target/` ou `deploy/`, acrescente no `.gitignore`:

```
target/
deploy/
```

## 3. Rodar no Docker

```bash
docker compose down -v     # o -v apaga o banco: obrigatório desta vez
mvn clean package
docker compose up -d
```

O `-v` é necessário porque o banco mudou: `hospedes` agora tem `cpf` e
`checkin` ganhou `quantidade_dias`, `forma_pagamento` e `data_prevista_saida`.
Nas próximas vezes, rode sem `-v` para não perder os cadastros.

Ou use os scripts prontos: `./rebuild.sh --reset` (Linux/WSL) ou
`.\rebuild.ps1 -Reset` (Windows). Eles fazem os três passos e ainda
conferem se os dados iniciais entraram no banco.

Site: http://localhost:8080/mvc/login

| Login     | Senha      | Perfil        | Área do gerente |
|-----------|------------|---------------|-----------------|
| gerente   | gerente123 | Gerente       | sim             |
| recepcao1 | 123456     | Recepcionista | não             |

---

## 4. Como o sistema funciona

1. O funcionário entra em `/login`.
2. **Cadastrar hóspede** abre o passo 1: nome, CPF, telefone e e-mail.
   O CPF é validado pelos dígitos verificadores antes de seguir.
3. **Próximo** guarda os dados na sessão e abre o passo 2: apenas os quartos
   `Disponível`, mais quantidade de dias (1 a 60) e forma de pagamento.
4. **Finalizar cadastro** grava o hóspede, abre a hospedagem, calcula a saída
   prevista (entrada + dias) e marca o quarto como `Ocupado`.
5. Em **Hospedagens**, o botão **Check-out** encerra a estadia e devolve o
   quarto para `Disponível`.

### Área restrita

`/quartos`, `/usuarios` e `/perfis` passam pelo `AuthFilter` e exigem perfil
Gerente. Quem não é gerente não vê os links no menu e, se digitar a URL,
cai na tela de acesso negado.

### Regras que ficam no Service

- CPF obrigatório, único e validado.
- CPF repetido reaproveita o cadastro em vez de duplicar.
- Não hospeda em quarto que não esteja `Disponível`.
- Nem dois check-ins abertos no mesmo quarto, nem dois para o mesmo hóspede.
- Dias entre 1 e 60; forma de pagamento só entre as opções válidas.
- Quarto em `Manutenção` não volta para `Disponível` no check-out.
- Não exclui hóspede com hospedagem registrada.
- Não exclui o último gerente nem o próprio usuário logado.
- Senha em branco na edição mantém a senha atual.

---

## 5. Layout

Design em rosa pastel, com barra superior, menu lateral e cartão de conteúdo.

**Toda página usa o mesmo layout:**

```jsp
<jsp:include page="/WEB-INF/jsp/layout/cabecalho.jsp">
    <jsp:param name="titulo" value="Hospedagens" />
    <jsp:param name="ativo"  value="checkin" />
</jsp:include>

    ... conteúdo da página ...

<jsp:include page="/WEB-INF/jsp/layout/rodape.jsp" />
```

- `titulo` aparece na aba do navegador.
- `ativo` diz qual item do menu fica destacado
  (`inicio`, `checkin`, `hospedes`, `quartos`, `usuarios`, `perfis`).
- O `cabecalho.jsp` já deixa `${ctx}` (caminho da aplicação),
  `${usuario}` e `${ehGerente}` prontos para a página usar.

Para mudar o menu do sistema inteiro, edite só `layout/cabecalho.jsp`.

**As cores estão todas no topo do `css/estilo.css`**, em variáveis:

```css
--rose-700: #8C4566;   /* botões, links, menu */
--rose-100: #F6D9E3;   /* fundos suaves, selos */
--cream:    #FFF8F5;   /* fundo dos campos e cartões internos */
```

Trocar o rosa por outra cor é mexer só nessas linhas.

As fontes (Fraunces e Manrope) vêm do Google Fonts, então precisam de
internet. Sem conexão, o navegador cai no Georgia e no Segoe UI e o
layout continua funcionando.

---

## 6. O que ainda dá para melhorar

- A senha é guardada em texto puro (aceitável em trabalho de faculdade,
  não em produção).
- O sistema não calcula valor de diária nem fecha conta no check-out.
- Ninguém é avisado quando o hóspede passa da data prevista de saída.
