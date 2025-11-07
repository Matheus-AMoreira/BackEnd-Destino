# Documentação da API - Projeto Destino

Este documento descreve os endpoints da API para o "Projeto Destino", detalhando as rotas de autenticação e as rotas de teste protegidas.

# 🔒 Configurando HTTPS Local no Spring Boot com JDK (keytool)

Este guia detalha como gerar um certificado autoassinado usando o `keytool` do JDK, configurar sua aplicação Spring Boot para usar HTTPS e instalar o certificado no Windows para remover os avisos de segurança do navegador (Chrome, Edge, etc.).

## 1. Pré-requisitos

* **Java Development Kit (JDK):** O `keytool` faz parte do JDK.
* **Variável PATH:** Para executar o `keytool` diretamente, certifique-se de que o diretório `bin` do seu JDK (`C:\Program Files\Java\jdk-<versão>\bin`) esteja adicionado à variável de ambiente **`PATH`** do Windows.

## 2. Geração do Certificado (`keystore.p12`)

Navegue até o diretório `src/main/resources` do seu projeto Spring Boot e execute o comando abaixo. Ele gera um par de chaves e o armazena no arquivo `keystore.p12`.

O comando é totalmente **não interativo** e inclui o crucial **`Subject Alternative Name (SAN)`** para compatibilidade com navegadores modernos.

# Execute no diretório src/main/resources
- Esse comando cria o certificado, depois é so clicar nele e seguir as instruções só lembre de colocar no repositório "Autoridades de Certificação Raiz Confiáveis"
- **Importante:** Substitua `minha_senha_segura` por uma senha forte de sua escolha.
> keytool -genkeypair -alias springboot -keyalg RSA -keysize 2048 -dname "CN=localhost, OU=TI, O=MinhaEmpresa, L=SaoPaulo, ST=SP, C=BR" -storetype PKCS12 -keystore keystore.p12 -storepass minha_senha_segura -keypass minha_senha_segura -validity 3650 -ext "SAN=dns:localhost,ip:127.0.0.1,ip:::1"

## Informações Gerais

### Autenticação

A autenticação é baseada em JSON Web Tokens (JWT).

1.  O usuário se registra (se for novo) ou faz login.
2.  O endpoint de login (`/api/auth/login`) retorna um seta um cookie.
3.  Para acessar rotas protegidas, o front-end deve enviar cookie que possui o jwt no cabeçalho de cada requisição.

### URL Base

Todas as rotas são relativas a uma URL base (ex: `http://localhost:8080`).

---

## 1. Endpoints de Autenticação

Controlador: `AuthController.java`
Caminho Base: `/api/auth`

### 1.1. Registrar Novo Usuário

Cria um novo usuário no sistema.

- **Endpoint:** `POST /api/auth/r/singup`
- **Descrição:** Cadastra um novo usuário com o perfil padrão (`USUARIO`).
- **Corpo da Requisição (Request Body):** `(application/json)`

  ```json
  {
    "nome": "string",
    "cpf": "string",
    "email": "string (example@domain.com)",
    "telefone": "string",
    "senha": "string"
  }
  ```

- **Resposta de Sucesso (201 CREATED):**

  ```json
  {
    "error": false,
    "msg": "Usuário cadastrado com sucesso!"
  }
  ```

- **Respostas de Erro (400 BAD_REQUEST / 409 CONFLICT):**
  - Se a senha for inválida:
    ```json
    {
      "error": true,
      "msg": "A senha não atende aos requisitos de segurança..."
    }
    ```
  - Se o CPF ou E-mail já existir:
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

- **Endpoint:** `POST /api/auth/login`
- **Descrição:** Valida as credenciais do usuário.
- **Corpo da Requisição (Request Body):** `(application/json)`

  ```json
  {
    "email": "string",
    "senha": "string"
  }
  ```

- **Resposta de Sucesso (200 OK):**

  ```json
  {
    "error": false,
    "message": "Login realizado com sucesso.",
    "userInfo": {
      "id": "UUID",
      "name": "Nome do usuáro"
    }
  }
  ```

- **Resposta de Erro (401/403 UNAUTHORIZED):**
  - (Tratado pelo Spring Security, geralmente retorna um status 401 ou 403 sem corpo específico se as credenciais estiverem erradas).

---

### 1.3. Buscar usuários inválidos

Busca por usuários que ainda não foram validados.

- **Endpoint:** `Get /api/auth/usuarios/invalidos`
- **Descrição:** Busca os usuários que estão com o campo valido = false.
- **Resposta de Sucesso (200 OK):** Devolve uma lista de todos os usuários que atendem a condição

### 1.4. Autenticar (Login)

Authentica o usuário.

- **Endpoint:** `Patch /api/auth/usuarios/validar/{id}`
- **Descrição:** altera a coluna de válido do usuário para true através do id que é passo pela URL.
- **Resposta de Sucesso (200 OK):** Usuário atualizado com sucesso!
- **Resposta de Erro (401/403 UNAUTHORIZED):** Não existe usuario com id: " + id

---

### 1.5 Logout
Limpa os cookies do usuário

- **Endpoint:** `Patch /api/auth/usuarios/logout`
- **Resposta de Sucesso (200 OK):** Logout realizado!
- 
## 2. Endpoints de Teste (Protegidos)

Controlador: `TestController.java`
Caminho Base: `/api`

### 2.1. Rota Pública

Rota acessível sem autenticação.

- **Endpoint:** `GET /api/publico`
- **Autenticação:** Nenhuma.
- **Resposta (Texto Simples):**
  `"Olá! Esta é uma rota pública."`

### 2.2. Rota Privada (Usuário)

Rota acessível por usuários com o perfil `ROLE_USUARIO`.

- **Endpoint:** `GET /api/privado/usuario`
- **Autenticação:** Obrigatória (Bearer Token).
- **Perfil Necessário:** `ROLE_USUARIO`
- **Resposta (Texto Simples):**
  `"Essa rota só usuário, funcionário e administrador tem acesso"`

### 2.3. Rota Privada (Funcionário)

Rota acessível por usuários com o perfil `ROLE_FUNCIONARIO`.

- **Endpoint:** `GET /api/privado/funcionario`
- **Autenticação:** Obrigatória (Bearer Token).
- **Perfil Necessário:** `ROLE_FUNCIONARIO`
- **Resposta (Texto Simples):**
  `"Informação Secreta. Você acessou com um token de 'funcionário' ou 'adm' válido!"`

### 2.4. Rota Privada (Administrador)

Rota acessível por usuários com o perfil `ROLE_ADMINISTRADOR`.

- **Endpoint:** `GET /api/privado/admin`
- **Autenticação:** Obrigatória (Bearer Token).
- **Perfil Necessário:** `ROLE_ADMINISTRADOR`
- **Resposta (Texto Simples):**
  `"Dados Ultra Secretos. Acesso concedido apenas para administradores!"`
