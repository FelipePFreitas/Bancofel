# BancoFel API 🏦

O dinheiro evoluiu. A forma de integrar também. Bancofel: a API que acelera o seu ecossistema financeiro.

O **BancoFel** é uma API RESTful de nível profissional que simula o ecossistema de um banco digital moderno. O sistema gerencia o cadastro de clientes, a abertura e controle de contas bancárias, histórico de transações e integrações para operações como PIX.

---

## 🚀 Funcionalidades Principais (Escopo do Sistema)

* **Gestão de Clientes (CRUD)**: Cadastro completo de usuários contendo dados pessoais (como CPF único e data de nascimento) e informações de endereço estruturadas.
* **Controle de Contas**: Vínculo dinâmico onde um cliente pode possuir múltiplas contas (Relacionamento Um-para-Muitos), com controle rigoroso de saldos decimais precisos e identificadores técnicos.
* **Histórico de Transações**: Registro imutável de movimentações financeiras (Depósitos, Saques, Transferências e Pix) atrelado a carimbos de data/hora para auditoria e geração de extratos.
* **Segurança de Credenciais**: Arquitetura que isola strings de conexão, usuários e senhas do banco de dados por meio de injeção via variáveis de ambiente, impossibilitando o vazamento de segredos no código-fonte.

---

## 🛠️ Tecnologias e Dependências Utilizadas

O ecossistema do projeto foi construído utilizando a stack mais moderna e demandada pelo mercado Java corporativo, além de uma interface web em Angular:

### Back-end (Java)
- **Linguagem:** Java 17+  
- **Framework / Runtime:** Spring Boot (3.x / compatível com 4.x)
- **Módulos principais:** Spring Web (REST), Spring Data JPA
- **ORM:** Hibernate (dialectos configurados para MySQL)
- **Banco de Dados:** MySQL (driver presente)
- **Produtividade / Utilitários:** Lombok, Spring Boot DevTools
- **Validação:** Spring Boot Starter Validation (Jakarta Bean Validation)
- **Documentação API:** springdoc-openapi / Swagger
- **Build / Execução:** Maven (pom.xml) e Maven Wrapper (`mvnw`, `mvnw.cmd`, `.mvn/`)
- **Testes:** pasta `src/test` (JUnit / frameworks de teste padrão do ecossistema Spring)

### Front-end
- **Framework:** Angular (aplicação em `bancofel/frontend`)
- **Runtime / Package Manager:** Node.js + npm (scripts no README do frontend)
- **Observação:** O frontend depende de pacotes do ecossistema npm (há pacotes como Algolia listados entre dependências).

### Ferramentas e integrações relevantes
- **Maven Wrapper** (`mvnw`) — garante build independente do Maven local.
- **CORS** configurado para permitir comunicação com o frontend local (`http://localhost:4200`).
- **Algolia** — presença de pacotes do Algolia no `node_modules` do frontend (cliente de busca JS).
- **Arquivos auxiliares:** `.gitignore`, `.gitattributes`.

---

## Como rodar (mínimo necessário)

Backend (no diretório raiz `bancofel`):

```bash
# Linux / macOS
cd bancofel
./mvnw clean spring-boot:run

# Windows (Powershell / CMD)
cd bancofel
mvnw.cmd clean spring-boot:run
```

Variáveis de ambiente comuns:
- SPRING_DATASOURCE_URL (ex.: jdbc:mysql://localhost:3306/bancofel)
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- SPRING_PROFILES_ACTIVE (opcional)

Frontend (dentro de `bancofel/frontend`):

```bash
cd bancofel/frontend
npm install
npm start
# Abra: http://localhost:4200
```

O back-end por padrão escuta em `http://localhost:8080` e o front-end em `http://localhost:4200`. A API já expõe documentação interativa via Swagger/OpenAPI (springdoc) quando a aplicação estiver rodando.

---

## Estrutura principal do repositório

```text
./
  README.md                (este arquivo)
  bancofel/                (aplicação Java + Maven)
    mvnw, mvnw.cmd, .mvn/  (Maven Wrapper)
    pom.xml                (dependências e build)
    src/
      main/
        java/...           (código-fonte Java)
        resources/         (configurações, application.properties)
      test/                (testes)
    frontend/              (aplicação Angular)
      README.md
      package.json / node_modules/
```
