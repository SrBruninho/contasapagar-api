# Contas a Pagar API

## Descrição
API REST para gerenciamento de contas a pagar, permitindo realizar operações CRUD, alterar a situação da conta, obter informações e importar um lote de contas de um arquivo CSV.

## Tecnologias
- Java 17
- Spring Boot
- PostgreSQL
- Docker
- Docker Compose
- Flyway
- Spring Security

## Estrutura do Projeto
O projeto segue os princípios de Domain Driven Design (DDD)


### Explicação da Estrutura:
``` bash
Contas a Pagar API
├── src/
│   └── main/
│       ├── java/
│       │   └── com.srbruninho.contasapagar/
│       │       ├── api/
│       │       │   ├── controller/
│       │       │   ├── dto/
│       │       │   └── converter/
│       │       ├── domain/
│       │       │   ├── model/
│       │       │   └── services/
│       │       └── infrastructure/
│       │           ├── config/
│       │           ├── repositories/
│       │           └── exception/
│       └── resources/
│           ├── application.properties
│           └── sql/
├── docker-compose.yml
├── pom.xml
└── README.md

``` 
- **Contas a Pagar API**: Nome do projeto principal.

- **src/main/java/com.srbruninho.contasapagar/**: Diretório principal para o código-fonte Java.
    - **api/controller**: Controladores REST que definem os endpoints da API.
    - **api/dto**: DTOs (Data Transfer Objects) para representar dados nas requisições e respostas da API.
    - **api/converter**: Classes para conversão entre entidades de domínio e DTOs.
    - **domain/model**: Entidades que representam o modelo de dados da aplicação.
    - **domain/services**: Serviços que encapsulam a lógica de negócio da aplicação.
    - **infrastructure/config**: Configurações da aplicação, como beans do Spring e configurações de segurança.
    - **infrastructure/repositories**: Interfaces e classes de acesso a dados (repositórios).
    - **infrastructure/exception**: Classes para tratamento de exceções customizadas.

- **src/main/resources/**: Diretório principal para recursos da aplicação.
    - **application.properties**: Arquivo de configuração Spring Boot.
    - **sql/**: Scripts SQL para migrações de banco de dados com Flyway.

- **docker-compose.yml**: Arquivo para configuração do ambiente Docker com PostgreSQL.

- **pom.xml**: Arquivo de configuração do Maven com as dependências do projeto.

- **README.md**: Arquivo Markdown com a documentação do projeto.

Este formato utiliza uma representação visual com espaços e barras para indicar a hierarquia dos diretórios e subdiretórios. Ele é adequado para Markdown, mantendo a estrutura organizada e fácil de ser compreendida na documentação do seu projeto.





## Funcionalidades da API
Endpoints
- POST `/api/contas/create-account:` Cria uma nova conta a pagar.
- PUT `/api/contas/update-account/{id}:` Atualiza uma conta existente pelo ID.
- PUT `/api/contas/update-situacao/{id}:` Atualiza a situação de pagamento de uma conta.
- GET `/api/contas:` Retorna todas as contas paginadas.
- GET `/api/contas/filter/due-date/description/unpaid:` Filtra contas pendentes por data de vencimento e descrição.
- GET `/api/contas/filter/total-value/period/paid:` Obtém o valor total pago por período.
- GET `/api/contas/{id}:` Retorna uma conta pelo ID.
- DELETE `/api/contas/{id}:` Exclui uma conta pelo ID.
- POST `/api/contas/import-csv:` Importa um lote de contas a pagar de um arquivo CSV.

## Como Executar

git clone https://github.com/seu-usuario/contasapagar-api.git

cd contasapagar-api

docker-compose up


## SpringDoc

http://localhost:8080/swagger-ui.html

## Notas Adicionais
##### Certifique-se de ter o Docker e Docker Compose instalados.
##### Os dados são persistidos em um banco de dados PostgreSQL.
##### O Flyway é utilizado para migrações de banco de dados.


## Autenticação
A API utiliza autenticação Basic Authentication. 
Para acessar os endpoints protegidos, utilize as seguintes credenciais:

Usuário: contas
Senha: contas#2024