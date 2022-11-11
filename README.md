# POC de Avaliação Filmes

POC feita com **Spring Boot 2.7.5** e **Java 17**. Documentação da API em http://localhost:8080/swagger-ui/index.html (versão *Swagger UI*) ou http://localhost:8080/v3/api-docs (versão *Open API 3.0*).
A API já nasce com 5 partidas realizadas por 2 jogadores.

## Autenticação
Autenticação do tipo **Basic Auth**, com os seguintes usuários possíveis:
* u: **jogador1** / s: **s1**
* u: **jogador2** / s: **s2**

## Passo 1: iniciar uma partida

Exemplo de requisição:
```sh
curl --location --request POST 'http://localhost:8080/avaliacoes-filmes' \
--header 'Authorization: Basic am9nYWRvcjI6czI=' \
--header 'Content-Type: application/json' \
--data-raw '{
  "tituloFilme1": "avengers",
  "tituloFilme2": "titanic"
}'
```

## Passo 2: finalizar uma partida

Exemplo de requisição:
```sh
curl --location --request PATCH 'http://localhost:8080/avaliacoes-filmes/1/2' \
--header 'Authorization: Basic am9nYWRvcjI6czI='
```

## Ranking

Para obter o ranking:
```sh
curl --location --request GET 'http://localhost:8080/avaliacoes-filmes/ranking' \
--header 'Authorization: Basic am9nYWRvcjE6czE='
```