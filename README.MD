# Documentação da API - Projeto Destino

Este documento descreve os endpoints da API para o "Projeto Destino", detalhando as rotas de autenticação e as rotas de teste protegidas.

## Informações Gerais

### Autenticação

A autenticação é baseada em JSON Web Tokens (JWT).
1.  O usuário se registra (se for novo) ou faz login.
2.  O endpoint de login (`/api/auth/login`) retorna um `token` (Bearer Token).
3.  Para acessar rotas protegidas, o front-end deve enviar este token no cabeçalho (Header) `Authorization` de cada requisição.

**Exemplo de Cabeçalho (Header):**
`Authorization: Bearer <SEU_TOKEN_JWT_AQUI>`

### URL Base

Todas as rotas são relativas a uma URL base (ex: `http://localhost:8080`).

---

## 1. Endpoints de Autenticação

Controlador: `AuthController.java`
Caminho Base: `/api/auth`

### 1.1. Registrar Novo Usuário

Cria um novo usuário no sistema.

* **Endpoint:** `POST /api/auth/register`
* **Descrição:** Cadastra um novo usuário com o perfil padrão (`USUARIO`).
* **Corpo da Requisição (Request Body):** `(application/json)`

    ```json
    {
      "nome": "string",
      "cpf": "string",
      "email": "string (example@domain.com)",
      "telefone": "string",
      "senha": "string"
    }
    ```

* **Resposta de Sucesso (201 CREATED):**

    ```json
    {
      "error": false,
      "msg": "Usuário cadastrado com sucesso!"
    }
    ```

* **Respostas de Erro (400 BAD_REQUEST / 409 CONFLICT):**
    * Se a senha for inválida:
        ```json
        {
          "error": true,
          "msg": "A senha não atende aos requisitos de segurança..."
        }
        ```
    * Se o CPF ou E-mail já existir:
        ```json
        {
          "error": true,
          "msg": "Erro: usuário já existe!"
        }
        ```
      ou
        ```json
        {
          "error": true,
          "msg": "Erro: Email já está em uso"
        }
        ```

### 1.2. Autenticar (Login)

Autentica um usuário existente e retorna um token JWT.

* **Endpoint:** `POST /api/auth/login`
* **Descrição:** Valida as credenciais do usuário.
* **Corpo da Requisição (Request Body):** `(application/json)`

    ```json
    {
      "email": "string",
      "senha": "string"
    }
    ```

* **Resposta de Sucesso (200 OK):**

    ```json
    {
      "token": "Bearer eyJhbGciOiJIUzI1NiJ9...",
      "expiresIn": 3600000
    }
    ```

* **Resposta de Erro (401/403 UNAUTHORIZED):**
    * (Tratado pelo Spring Security, geralmente retorna um status 401 ou 403 sem corpo específico se as credenciais estiverem erradas).

---

## 2. Endpoints de Teste (Protegidos)

Controlador: `TestController.java`
Caminho Base: `/api`

### 2.1. Rota Pública

Rota acessível sem autenticação.

* **Endpoint:** `GET /api/publico`
* **Autenticação:** Nenhuma.
* **Resposta (Texto Simples):**
  `"Olá! Esta é uma rota pública."`

### 2.2. Rota Privada (Usuário)

Rota acessível por usuários com o perfil `ROLE_USUARIO`.

* **Endpoint:** `GET /api/privado/usuario`
* **Autenticação:** Obrigatória (Bearer Token).
* **Perfil Necessário:** `ROLE_USUARIO`
* **Resposta (Texto Simples):**
  `"Essa rota só usuário, funcionário e administrador tem acesso"`

### 2.3. Rota Privada (Funcionário)

Rota acessível por usuários com o perfil `ROLE_FUNCIONARIO`.

* **Endpoint:** `GET /api/privado/funcionario`
* **Autenticação:** Obrigatória (Bearer Token).
* **Perfil Necessário:** `ROLE_FUNCIONARIO`
* **Resposta (Texto Simples):**
  `"Informação Secreta. Você acessou com um token de 'funcionário' ou 'adm' válido!"`

### 2.4. Rota Privada (Administrador)

Rota acessível por usuários com o perfil `ROLE_ADMINISTRADOR`.

* **Endpoint:** `GET /api/privado/admin`
* **Autenticação:** Obrigatória (Bearer Token).
* **Perfil Necessário:** `ROLE_ADMINISTRADOR`
* **Resposta (Texto Simples):**
  `"Dados Ultra Secretos. Acesso concedido apenas para administradores!"`