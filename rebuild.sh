#!/usr/bin/env bash
#
# rebuild.sh - recria o banco, compila o WAR e sobe tudo.
#
# Uso:
#   ./rebuild.sh          -> build + sobe (mantem o banco atual)
#   ./rebuild.sh --reset  -> APAGA o banco e roda o init.sql do zero
#
# Rode sempre a partir da pasta checkin-hotel/

set -euo pipefail

cd "$(dirname "$0")"

RESET=0
if [ "${1:-}" = "--reset" ]; then
    RESET=1
fi

echo "==> Conferindo arquivos do projeto"
for arquivo in pom.xml docker-compose.yml init.sql; do
    if [ ! -f "$arquivo" ]; then
        echo "ERRO: $arquivo nao encontrado. Rode o script dentro da pasta checkin-hotel." >&2
        exit 1
    fi
done

if [ "$RESET" -eq 1 ]; then
    echo "==> Derrubando containers e APAGANDO o volume do MySQL"
    docker compose down -v
else
    echo "==> Derrubando containers (banco preservado)"
    docker compose down
fi

echo "==> Compilando o WAR (mvn clean package)"
mvn -q clean package

if [ ! -f deploy/mvc.war ]; then
    echo "ERRO: deploy/mvc.war nao foi gerado. Veja os erros do Maven acima." >&2
    exit 1
fi

# O Tomcat so reexplode o WAR se a pasta antiga sair da frente.
echo "==> Limpando a pasta explodida antiga"
rm -rf deploy/mvc

echo "==> Subindo MySQL e Tomcat"
docker compose up -d

echo "==> Esperando o MySQL aceitar conexoes"
for i in $(seq 1 60); do
    if docker exec mvc-java-mysql mysqladmin ping -h 127.0.0.1 -u root -proot --silent >/dev/null 2>&1; then
        echo "    MySQL no ar."
        break
    fi
    if [ "$i" -eq 60 ]; then
        echo "ERRO: MySQL nao respondeu em 60s. Veja: docker compose logs mysql" >&2
        exit 1
    fi
    sleep 1
done

echo "==> Conferindo os dados de teste"
docker exec mvc-java-mysql mysql -u mvc_user -pmvc123 mvc_java \
    -e "SELECT login, perfil_id FROM usuarios;" 2>/dev/null \
    || echo "AVISO: tabela usuarios vazia ou inexistente. Rode: ./rebuild.sh --reset"

echo "==> Esperando o Tomcat publicar a aplicacao"
for i in $(seq 1 60); do
    codigo=$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/mvc/login || echo 000)
    if [ "$codigo" = "200" ]; then
        echo "    Aplicacao no ar."
        break
    fi
    if [ "$i" -eq 60 ]; then
        echo "AVISO: Tomcat ainda nao respondeu. Veja: docker compose logs -f tomcat"
    fi
    sleep 1
done

echo
echo "Pronto."
echo "  URL:   http://localhost:8080/mvc/login"
echo "  Login: gerente / 123456"
echo "  Logs:  docker compose logs -f tomcat"
