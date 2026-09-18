# rebuild.ps1 - recria o banco, compila o WAR e sobe tudo (Windows / PowerShell)
#
# Uso:
#   .\rebuild.ps1           -> build + sobe (mantem o banco atual)
#   .\rebuild.ps1 -Reset    -> APAGA o banco e roda o init.sql do zero
#
# Se o PowerShell bloquear o script:
#   Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass

param(
    [switch]$Reset
)

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

Write-Host "==> Conferindo arquivos do projeto"
foreach ($arquivo in @("pom.xml", "docker-compose.yml", "init.sql")) {
    if (-not (Test-Path $arquivo)) {
        Write-Error "$arquivo nao encontrado. Rode o script dentro da pasta checkin-hotel."
    }
}

if ($Reset) {
    Write-Host "==> Derrubando containers e APAGANDO o volume do MySQL"
    docker compose down -v
} else {
    Write-Host "==> Derrubando containers (banco preservado)"
    docker compose down
}

Write-Host "==> Compilando o WAR (mvn clean package)"
mvn -q clean package
if ($LASTEXITCODE -ne 0) { Write-Error "Falha no build do Maven." }

if (-not (Test-Path "deploy\mvc.war")) {
    Write-Error "deploy\mvc.war nao foi gerado. Veja os erros do Maven acima."
}

# O Tomcat so reexplode o WAR se a pasta antiga sair da frente.
Write-Host "==> Limpando a pasta explodida antiga"
if (Test-Path "deploy\mvc") { Remove-Item -Recurse -Force "deploy\mvc" }

Write-Host "==> Subindo MySQL e Tomcat"
docker compose up -d

Write-Host "==> Esperando o MySQL aceitar conexoes"
$ok = $false
for ($i = 1; $i -le 60; $i++) {
    docker exec mvc-java-mysql mysqladmin ping -h 127.0.0.1 -u root -proot --silent 2>$null | Out-Null
    if ($LASTEXITCODE -eq 0) { $ok = $true; Write-Host "    MySQL no ar."; break }
    Start-Sleep -Seconds 1
}
if (-not $ok) { Write-Error "MySQL nao respondeu em 60s. Veja: docker compose logs mysql" }

Write-Host "==> Conferindo os dados de teste"
docker exec mvc-java-mysql mysql -u mvc_user -pmvc123 mvc_java -e "SELECT login, perfil_id FROM usuarios;" 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "AVISO: tabela usuarios vazia ou inexistente. Rode: .\rebuild.ps1 -Reset"
}

Write-Host "==> Esperando o Tomcat publicar a aplicacao"
for ($i = 1; $i -le 60; $i++) {
    try {
        $resp = Invoke-WebRequest "http://localhost:8080/mvc/login" -UseBasicParsing -TimeoutSec 3
        if ($resp.StatusCode -eq 200) { Write-Host "    Aplicacao no ar."; break }
    } catch { }
    Start-Sleep -Seconds 1
}

Write-Host ""
Write-Host "Pronto."
Write-Host "  URL:   http://localhost:8080/mvc/login"
Write-Host "  Login: gerente / 123456"
Write-Host "  Logs:  docker compose logs -f tomcat"
