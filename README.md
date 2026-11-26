# Check-in Hotel — API REST (Trabalho API T2)

API RESTful em Java (Servlets Jakarta + JDBC) com MySQL, organizada em camadas
**Controller → Service → DAO**. Ela convive com a interface web (JSP) já existente:
as duas usam os **mesmos Services e DAOs**, então as regras de negócio valem para ambas.

> Documentação do sistema web (layout, fluxo de check-in, perfis): veja o [LEIA-ME.md](LEIA-ME.md).

## Recursos

| Recurso | Operações | Observação |
|---|---|---|
| Hóspedes (`/api/hospedes`) | CRUD completo | CPF validado (dígitos verificadores) e único |
| Quartos (`/api/quartos`) | CRUD completo | Escrita somente para **Gerente** |
| Hospedagens (`/api/checkins`) | Listar, buscar, **check-in**, **check-out** | Sem PUT/DELETE: o histórico não pode ser apagado |

## Arquitetura

```
src/main/java/br/com/mvc/
├── controller/api/  ApiBaseServlet, HospedeApiController, QuartoApiController, CheckInApiController
│                    (HTTP e JSON: lê URL/corpo, escolhe o status da resposta)
├── service/         HospedeService, QuartoService, CheckInService   (regras de negócio)
├── dao/             HospedeDAO, QuartoDAO, CheckInDAO               (SQL via JDBC)
├── model/           Hospede, Quarto, CheckIn
├── dto/             CheckInRequest                                  (corpo do POST /api/checkins)
├── exception/       RecursoNaoEncontradoException (404), ConflitoException (409)
├── filter/          AuthFilter                                      (login e perfil)
└── config/          MysqlSingleton (banco), JsonConfig (Gson)
```

Fluxo de uma requisição: `AuthFilter` → `Controller` → `Service` → `DAO` → MySQL.

## Como executar

```bash
./rebuild.sh --reset     # Linux/WSL   (Windows: .\rebuild.ps1 -Reset)
```

Ou manualmente:

```bash
docker compose down -v
mvn clean package
docker compose up -d
```

Base URL: `http://localhost:8080/mvc`

## Autenticação e permissões

A API usa a **mesma sessão** do sistema web. Antes de qualquer chamada, faça login
(o Postman e o `curl -c/-b` guardam o cookie `JSESSIONID` automaticamente):

```
POST /login
Content-Type: application/x-www-form-urlencoded

login=gerente&senha=gerente123
```

| Situação | Resposta |
|---|---|
| Sem login em `/api/*` | `401` `{"erro": "..."}` |
| Recepcionista em `POST/PUT/DELETE /api/quartos` | `403` `{"erro": "..."}` |

| Perfil | Hóspedes | Hospedagens | Quartos (GET) | Quartos (POST/PUT/DELETE) |
|---|---|---|---|---|
| Gerente (`gerente` / `gerente123`) | ✅ | ✅ | ✅ | ✅ |
| Recepcionista (`recepcao1` / `123456`) | ✅ | ✅ | ✅ | ❌ 403 |

## Hóspedes

| Método | Rota | Descrição | Sucesso | Erros |
|---|---|---|---|---|
| GET | `/api/hospedes` | Lista todos | 200 | 401 |
| GET | `/api/hospedes/{id}` | Busca por id | 200 | 400, 404 |
| POST | `/api/hospedes` | Cria | 201 | 400, 409 |
| PUT | `/api/hospedes/{id}` | Atualiza (substitui todos os campos) | 200 | 400, 404, 409 |
| DELETE | `/api/hospedes/{id}` | Exclui | 204 | 404, 409 |

Corpo (POST/PUT):

```json
{
  "nome": "Maria Lima",
  "cpf": "529.982.247-25",
  "telefone": "(34) 99999-0003",
  "email": "maria@email.com"
}
```

Regras: nome (mín. 3 caracteres) e CPF obrigatórios; CPF com dígitos verificadores válidos
(aceita com ou sem pontuação, grava só dígitos); CPF único (**409**); não exclui hóspede com
hospedagens registradas (**409**).

Resposta de exemplo (201):

```json
{ "id": 3, "nome": "Maria Lima", "cpf": "52998224725", "telefone": "(34) 99999-0003", "email": "maria@email.com" }
```

## Quartos

| Método | Rota | Descrição | Sucesso | Erros |
|---|---|---|---|---|
| GET | `/api/quartos` | Lista todos (`?status=Disponível` filtra) | 200 | 400 |
| GET | `/api/quartos/{id}` | Busca por id | 200 | 400, 404 |
| POST | `/api/quartos` | Cria (**Gerente**) | 201 | 400, 403, 409 |
| PUT | `/api/quartos/{id}` | Atualiza (**Gerente**) | 200 | 400, 403, 404, 409 |
| DELETE | `/api/quartos/{id}` | Exclui (**Gerente**) | 204 | 403, 404, 409 |

Corpo (POST/PUT):

```json
{ "numero": "301", "tipo": "Casal", "status": "Disponível" }
```

Regras: número e tipo obrigatórios; número único (**409**); `status` ∈ `Disponível`, `Ocupado`,
`Manutenção` (padrão `Disponível` ao criar; se omitido no PUT, mantém o atual); não exclui quarto
**ocupado** nem quarto com **hospedagens registradas** (**409**).

## Hospedagens (check-in / check-out)

| Método | Rota | Descrição | Sucesso | Erros |
|---|---|---|---|---|
| GET | `/api/checkins` | Histórico completo (`?ativos=true` só as em aberto) | 200 | 401 |
| GET | `/api/checkins/{id}` | Busca uma hospedagem | 200 | 400, 404 |
| POST | `/api/checkins` | **Realiza o check-in** | 201 | 400, 409 |
| POST | `/api/checkins/{id}/checkout` | **Realiza o check-out** | 200 | 400, 404, 409 |

Corpo do check-in:

```json
{
  "hospede": { "nome": "Maria Lima", "cpf": "529.982.247-25", "telefone": "(34) 99999-0003", "email": "maria@email.com" },
  "quartoId": 1,
  "quantidadeDias": 3,
  "formaPagamento": "Pix"
}
```

Formas de pagamento válidas: `Dinheiro`, `Pix`, `Cartão de Débito`, `Cartão de Crédito`.

Regras: dias entre 1 e 60; só hospeda em quarto `Disponível` (**409**); um check-in aberto por
quarto e por hóspede (**409**); CPF já cadastrado reaproveita o hóspede; a saída prevista é a
entrada + dias e o quarto passa para `Ocupado`. No check-out o quarto volta para `Disponível`
(exceto se estiver em `Manutenção`); check-out repetido retorna **409**.

Resposta de exemplo (201):

```json
{
  "id": 1, "hospedeId": 3, "quartoId": 1, "quantidadeDias": 3, "formaPagamento": "Pix",
  "dataCheckin": "2026-11-23T10:15:30", "dataPrevistaSaida": "2026-11-26T10:15:30",
  "hospedeNome": "Maria Lima", "hospedeCpf": "52998224725",
  "quartoNumero": "101", "quartoTipo": "Solteiro"
}
```

`dataCheckout` só aparece depois do check-out. Métodos não previstos (ex.: `DELETE /api/checkins/1`)
retornam `405`.

## Códigos de erro

| Status | Quando |
|---|---|
| 400 | Validação, JSON inválido, id inválido ou campo obrigatório ausente |
| 401 | Não autenticado |
| 403 | Perfil sem permissão |
| 404 | Registro ou rota não encontrada |
| 409 | Conflito com o estado atual (CPF/número repetido, quarto indisponível ou ocupado, registro com histórico, check-out repetido) |
| 500 | Erro interno |

Todos os erros seguem o formato `{"erro": "mensagem"}`.

## Exemplos com curl

```bash
BASE=http://localhost:8080/mvc

# login (guarda o cookie de sessão)
curl -c c.txt -L -d "login=gerente&senha=gerente123" $BASE/login -o /dev/null

# hóspedes
curl -b c.txt $BASE/api/hospedes
curl -b c.txt -X POST -H "Content-Type: application/json" \
  -d '{"nome":"Maria Lima","cpf":"529.982.247-25"}' $BASE/api/hospedes

# quartos disponíveis
curl -b c.txt "$BASE/api/quartos?status=Dispon%C3%ADvel"

# check-in e check-out
curl -b c.txt -X POST -H "Content-Type: application/json" \
  -d '{"hospede":{"nome":"Maria Lima","cpf":"529.982.247-25"},"quartoId":1,"quantidadeDias":3,"formaPagamento":"Pix"}' \
  $BASE/api/checkins
curl -b c.txt -X POST $BASE/api/checkins/1/checkout
```

Também há uma coleção do Postman pronta em [`docs/postman_collection.json`](docs/postman_collection.json)
(importe no Postman, rode **Login** primeiro e depois as demais requisições).

## Equipe

- Nome 1 — responsável por: ...
- Nome 2 — responsável por: ...
